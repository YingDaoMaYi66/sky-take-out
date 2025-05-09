package com.sky.controller.admin;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜皮管理controller
 */
@RestController()
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 新增菜品
     * @param dishDTO 菜品DTO
     * @return 返回响应类
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品日志:{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        //清理缓存数据
        String key="dish_"+dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO 菜品分页查询DTO
     * @return 返回分页查询结果
     */
    @GetMapping("/page")
    @ApiOperation("分页查询菜品")
    public Result<PageResult>page(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询菜品日志:{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除菜品
     * @param ids  菜品id
     * @return 返回响应状态
     */
    @DeleteMapping
    @ApiOperation("菜品批量删除")
    public Result delete(@RequestParam  List<Long> ids){
        log.info("菜品批量上删除:{}", ids);
        dishService.deleteBatch(ids);


        //将所有的菜品清理缓存清理掉，所有的dish_开头的key
        cleanCache("dish_*");

        return Result.success();
    }
    /**
     * 根据id查询菜品
     * @param id 菜品id
     * @return 菜品
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品 ")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品:{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }
    /**
     * 修改菜品
     * @param dishDTO 菜品DTO
     * @return 返回响应类
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品:{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);

        //将所有的菜品清理缓存清理掉，所有的dish_开头的key
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据菜品分类id查询菜品
     * @param categoryId 菜品分类id
     * @return 菜品
     */
    @GetMapping("/list")
    @ApiOperation("根据菜品分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId) {
        log.info("查询菜品列表:{}", categoryId);
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 菜品起售与停售
     * @param status 菜品状态
     * @param id 菜品id
     * @return 返回响应状态码
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售与停售")
    public Result<String> stratOrStop(@PathVariable Integer status, Long id) {
        log.info("菜品状态修改:{},{}", status,id);
        dishService.stratOrStop(status, id);
        //将所有的菜品清理缓存清理掉，所有的dish_开头的key
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return Result.success();
    }

    /**
     * 清理缓存数据
     * @param pattern 缓存key的匹配规则
     */
    private void cleanCache(String pattern) {
        //清理缓存数据
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

}
