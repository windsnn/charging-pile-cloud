package com.trick.backend.common.utils;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.*;
import com.aliyun.oss.common.comm.Protocol;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.ObjectMetadata;
import com.google.gson.Gson;
import com.trick.backend.common.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class AliyunOssUtil {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.region}")
    private String region;

    private OSS ossClient;

    @PostConstruct
    public void initClient() throws com.aliyuncs.exceptions.ClientException {
        EnvironmentVariableCredentialsProvider credentialsProvider =
                CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();

        ClientBuilderConfiguration config = new ClientBuilderConfiguration();
        config.setSignatureVersion(SignVersion.V4);

        this.ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        log.info("OSS 客户端初始化成功");
    }

    /**
     * 通用文件上传方法
     *
     * @param file      Spring的MultipartFile对象，上传的文件
     * @param directory 上传到OSS的目录，例如 "avatars/", "fault-images/"
     * @return 返回上传成功后文件的公网访问URL
     */
    public String upload(MultipartFile file, String directory) throws IOException {

        // 1. 生成唯一的文件名，避免覆盖
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String uniqueFileName = UUID.randomUUID() + fileExtension;

        // 2. 拼接完整的文件路径
        String objectName = directory + uniqueFileName;

        // 3. 获取文件输入流
        try (InputStream inputStream = file.getInputStream()) {

            // 4. 创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // 5. 执行上传
            ossClient.putObject(bucketName, objectName, inputStream, metadata);
        }

        // 6. 拼接并返回公网访问URL
        String point = endpoint.substring("https://".length());
        return "https://" + bucketName + "." + point + "/" + objectName;
    }

    /**
     * 删除文件
     *
     * @param objectName 完整的文件路径，如 "avatars/uuid-generated.jpg"
     */
    public void delete(String objectName) {
        ossClient.deleteObject(bucketName, objectName);
    }

    @PreDestroy
    public void destroyClient() {
        if (ossClient != null) {
            ossClient.shutdown();
            log.info("OSS 客户端已关闭");
        }
    }
}