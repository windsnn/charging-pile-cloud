package com.trick.user.controller.wechat;

import com.trick.common.exception.BusinessException;
import com.trick.common.result.Result;
import com.trick.common.utils.ThreadLocalUtil;
import com.trick.user.utils.AliyunOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/wx/files/upload")
public class FilesUploadController {
    @Autowired
    private AliyunOssUtil aliyunOssUtil;

    @PostMapping
    public Result<Map<String, String>> uploadFile(
            @RequestParam MultipartFile file,
            @RequestParam String type) {

        // 线程获取userId
        int userId = ThreadLocalUtil.getUserId();

        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        String directory = switch (type) {
            case "avatar" -> "avatars/" + userId + "/";
            case "fault" -> "fault-images/" + userId + "/";
            default -> "others/" + userId + "/";
        };

        try {
            String url = aliyunOssUtil.upload(file, directory);
            Map<String, String> response = new HashMap<>();
            response.put("url", url);
            log.info("文件上传成功");
            return Result.success(response);

        } catch (IOException e) {
            throw new BusinessException("文件上传失败");
        }
    }
}
