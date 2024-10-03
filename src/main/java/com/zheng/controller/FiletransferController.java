package com.zheng.controller;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.unit.DataUnit;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zheng.pojo.File;
import com.zheng.pojo.Result;
import com.zheng.service.FileService;
import com.zheng.service.FiletransferService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@RestController
@RequestMapping("file")
public class FiletransferController {
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private FileService fileService;
//    @Autowired
//    private FiletransferService filetransferService;
    @PostMapping("checkchunk")
    public Result checkChunk(@RequestParam("fileMd5") String fileMd5, @RequestParam("chunk") int chunk) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean exist = true;
        try {
            StatObjectResponse netdisk = minioClient.statObject(StatObjectArgs.builder().bucket("netdisk")
                    .object("chunks/" + fileMd5 + "/" + chunk).build());
        }catch (Exception e){
            exist = false;
        }
        if(exist==true){
            return Result.ok("分块已存在");
        }
        return Result.ok("分块未上传");
    }

    @PostMapping("uploadchunk")
    public Result UploadChunk(MultipartFile file,@RequestParam("fileMd5") String fileMd5,@RequestParam("chunkNumber") int chunkNumber) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(file.getBytes());
        String str = Convert.toStr(chunkNumber);
        minioClient.putObject(PutObjectArgs.builder()
                .bucket("netdisk")
                .object("chunks/" + fileMd5 + "/"+str)
                .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                .build());
        return Result.ok("上传成功");
    }


    @PostMapping("upload")
    public Result Upload_file(@RequestParam("file") MultipartFile file, @RequestParam("pid") String pid,@RequestParam("fileMd5") String fileMd5) throws Exception {
        String originalFilename = file.getOriginalFilename();
        byte[] bytes = file.getBytes();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        LambdaQueryWrapper<File> fileLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        fileLambdaQueryWrapper1.eq(File::getFileMd5,fileMd5);
        File one1 = fileService.getOne(fileLambdaQueryWrapper1);
        if(!pid.equals("1")){
            LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
            fileLambdaQueryWrapper.eq(File::getFileId,pid);
            File one = fileService.getOne(fileLambdaQueryWrapper);
            originalFilename = one.getFilePath()+"/"+originalFilename;
        }
        minioClient.putObject(PutObjectArgs.builder()
                .bucket("netdisk")
                .object(originalFilename)
                .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                .build());
        one1.setStatus(0);
        fileService.updateById(one1);
        return Result.ok("上传成功");
    }
    @PostMapping("merge")
    public Result merge(@RequestParam("fileMd5") String fileMd5,
                        @RequestParam("chunkCount") Integer chunkCount,
                        @RequestParam("filename") String filename,
                        @RequestParam("pid") String pid) {
        return fileService.merge(fileMd5, chunkCount, filename,pid);
    }
    @PostMapping("download")
    public Result download_file( String fileId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fileLambdaQueryWrapper.eq(File::getFileId,fileId);
        File file = fileService.getOne(fileLambdaQueryWrapper);
        String test1 = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs
                .builder()
                .bucket("netdisk")
                .object(file.getFilePath())
                .method(Method.GET)
                .build());
        System.out.println(test1);
        return Result.ok(test1);
    }
}
