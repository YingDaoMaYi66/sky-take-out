package com.sky.controller.admin;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.sky.dto.CategoryDTO;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Api(tags = "分类相关接口")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     *
     * @param categoryDTO 分类dto
     * @return 返回响应类
     **/
    @PostMapping
    @ApiOperation("新增分类")
    public Result<String> save(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类：{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 分类分页查询
     *
     * @param categoryPageQueryDTO 分类分页查询DTO 页码，每页记录数，分类名称，分类类型
     * @return object
     */
    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Object page(CategoryPageQueryDTO categoryPageQueryDTO, Integer type) {
        if (categoryPageQueryDTO.getPage() > 0 && categoryPageQueryDTO.getPageSize() > 0) {
            log.info("分页查询：{}", categoryPageQueryDTO);
            PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
            return Result.success(pageResult);
        } else {
            List<Category> list = categoryService.list(type);
            return Result.success(list);
        }

    }

    /**
     * 删除分类
     *
     * @param id 分类dto
     * @return 响应类
     **/
    @DeleteMapping
    @ApiOperation("删除分类")
    public Result<String> deleteBy(Long id) {
        log.info("删除分类：{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改分类
     *
     * @param categoryDTO 分类dto
     * @return 响应类
     **/

    @PostMapping("/update")
    @ApiOperation("修改分类")
    public Result<String> update(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类：{}", categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用、禁用分类")
    public Result<String> startOrStop(@PathVariable("status") Integer status, Long id) {
        log.info("启用、禁用分类：{}", id);
        categoryService.startOrStop(status, id);
        return Result.success();
    }



    /**
     * 根据类型查询分类
     * @param type 分类类型
     * @return 返回数据列表
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> list(Integer type){
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}


