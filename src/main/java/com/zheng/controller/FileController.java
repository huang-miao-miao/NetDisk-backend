package com.zheng.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zheng.pojo.File;
import com.zheng.pojo.Result;
import com.zheng.pojo.vo.FileVo;
import com.zheng.service.FileService;
import io.minio.MinioClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("file")
public class FileController {
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private FileService fileService;
    @PostMapping("FileList")
    public Result get_file_list(@RequestBody FileVo fileVo){
        LambdaQueryWrapper<File> QueryWrapper = new LambdaQueryWrapper<>();
        QueryWrapper.eq(File::getUserId,fileVo.getUserId())
                .eq(File::getPId,fileVo.getFileId());
        List<File> list = fileService.list(QueryWrapper);
        List<FileVo> fileList = new ArrayList<>();
        list.forEach(file -> {
            FileVo fileVo1 = new FileVo();
            System.out.println(file);
            BeanUtils.copyProperties(file,fileVo1);
            System.out.println(fileVo1);
            fileList.add(fileVo1);
        });
        return Result.ok(fileList);
    }
}
