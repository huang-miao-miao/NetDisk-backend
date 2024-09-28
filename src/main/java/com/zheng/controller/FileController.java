package com.zheng.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
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
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("file")
public class FileController {
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private FileService fileService;
    @PostMapping("checkfile")
    public Result checkFile(String fileMd5,String Filename,Long FileSize,String pid){
        LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fileLambdaQueryWrapper.eq(File::getFileMd5,fileMd5);
        File one = fileService.getOne(fileLambdaQueryWrapper);
        if(one==null){
            String simpleUUID = IdUtil.simpleUUID();
            Date date = new Date();
            DateTime time = new DateTime(date);
            File file1 = new File();
            file1.setFileId(simpleUUID);
            file1.setUserId("1");
            file1.setFileName(Filename);
            file1.setFileSize(FileSize);
            file1.setFolderType(1);
            file1.setPId(pid);
            file1.setFileCategory(1);
            file1.setFileMd5(fileMd5);
            file1.setStatus(0);
            file1.setUpdatetime(time);
            if(pid.equals("1")){
                file1.setFilePath(Filename);
            }else {
                file1.setFilePath(pid+"/"+Filename);
            }
            fileService.save(file1);
            return Result.ok("文件尚未上传");
        }
        if(one.getStatus().equals("1")){
            return Result.ok("文件处理中");
        }
        return Result.ok("文件已上传");
    }
    @PostMapping("FileList")
    public Result get_file_list(@RequestBody FileVo fileVo){
        LambdaQueryWrapper<File> QueryWrapper = new LambdaQueryWrapper<>();
        QueryWrapper.eq(File::getUserId,fileVo.getUserId())
                .eq(File::getPId,fileVo.getFileId())
                .eq(fileVo.getFileCategory()!=null,File::getFileCategory,fileVo.getFileCategory());
        List<File> list = fileService.list(QueryWrapper);
        List<FileVo> fileList = new ArrayList<>();
        list.forEach(file -> {
            FileVo fileVo1 = new FileVo();
            BeanUtils.copyProperties(file,fileVo1);
            fileList.add(fileVo1);
        });
        return Result.ok(fileList);
    }
}
