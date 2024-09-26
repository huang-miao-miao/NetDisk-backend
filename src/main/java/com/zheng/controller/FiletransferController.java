package com.zheng.controller;


import io.minio.MinioClient;
import io.minio.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("file")
public class FiletransferController {
    @Autowired
    private MinioClient minioClient;
    @PostMapping("upload")
    public Result Upload_file(MultipartFile file){
        String originalFilename = file.getOriginalFilename();

    }
}
