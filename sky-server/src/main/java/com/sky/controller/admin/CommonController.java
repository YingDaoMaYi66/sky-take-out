package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@RequestMapping("/admin/common")
@Api("通用接口")
@Slf4j
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    /**
     * 文件上传
     * @param file 文件
     * @return 返回响应类
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file.getOriginalFilename());
        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        //截取原始文件名称
        String extension = null;
        if (originalFilename != null) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        //构建文件名称
        String objectName = UUID.randomUUID().toString() + extension;
        //文件的请求路径
        try {
            String filepath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filepath);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e);
        }
        //提示文件上传失败
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
