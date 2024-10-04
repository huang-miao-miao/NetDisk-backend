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
import io.minio.RemoveObjectArgs;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("file")
public class FileController {
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private FileService fileService;
    private String[] video = {"mp4", "avi", "rmvb", "mkv", "mov", "MP4", "AVI", "RMVB", "MKV", "MOV"};
    private String[] music = {"mp3", "wav", "wma", "mp2", "flac", "midi", "ra", "ape", "AAC", "CDA", "MP3", "WAV", "WMA", "MP2", "FLAC", "MIDI", "RA", "APE", "AAC", "CDA"};
    private String[] image = {"jpeg", "jpg", "png", "gif", "bmp", "dds", "psd", "pdt", "webp", "xmp", "svg", "tiff", "JPEG", "JPG", "PNG", "GIF", "BMP", "DDS", "PSD", "PDT", "WEBP", "XMP", "SVG", "TIFF"};
    private String[] pdf = {"PDF", "PPTX", "pdf", "pptx"};
    private String[] word = {"DOCX", "docx"};
    private String[] excel = {"XLSX", "xlsx"};
    @PostMapping("createfolder")
    public Result CreateFolder(@RequestBody FileVo fileVo){
        File file = new File();
        Date date = new Date();
        DateTime time = new DateTime(date);
        String simpleUUID = IdUtil.simpleUUID();
        file.setFileId(simpleUUID);
        file.setFileName(fileVo.getFileName());
        file.setStatus(0);
        file.setPId(fileVo.getFileId());
        file.setUserId(fileVo.getUserId());
        file.setFolderType(0);
        file.setUpdatetime(time);
        if(fileVo.getFileId().equals("1")){
            file.setFilePath(fileVo.getFileName());
        }else {
            File byId = fileService.getById(fileVo.getFileId());
            file.setFilePath(byId.getFilePath()+"/"+fileVo.getFileName());
        }
        fileService.save(file);
        return Result.ok("新建文件夹成功");
    }
    @DeleteMapping("deletefile")
    public Result deleteFile(@RequestParam List<String> deletefile){
        deletefile.forEach(file -> {
            LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
            fileLambdaQueryWrapper.eq(File::getFileId,file);
            File one = fileService.getOne(fileLambdaQueryWrapper);
            if(one.getFolderType()==0){
                fileService.removeById(one.getFileId());
                return;
            }
            try {
                minioClient.removeObject(RemoveObjectArgs
                        .builder()
                        .bucket("netdisk")
                        .object(one.getFilePath())
                        .build());
                fileService.removeById(one.getFileId());
            } catch (Exception e) {
                throw new RuntimeException(String.format("minio 删除对象失败，【%s】", e.getMessage()));
            }
        });
        return Result.ok("已删除");
    }
    @PostMapping("checkfile")
    public Result checkFile(String fileMd5,String Filename,Long FileSize,String pid,String userId){
        String extName = FileUtil.extName(Filename);
        System.out.println(extName);
        LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fileLambdaQueryWrapper.eq(File::getFileMd5,fileMd5);
        File one = fileService.getOne(fileLambdaQueryWrapper);
        if(one==null){
            String simpleUUID = IdUtil.simpleUUID();
            Date date = new Date();
            DateTime time = new DateTime(date);
            File file1 = new File();
            file1.setFileId(simpleUUID);
            file1.setUserId(userId);
            file1.setFileName(Filename);
            file1.setFileSize(FileSize);
            file1.setFolderType(1);
            file1.setPId(pid);
            filesetcategory(file1,extName);
            file1.setFileMd5(fileMd5);
            file1.setStatus(1);
            file1.setUpdatetime(time);
            if(pid.equals("1")){
                file1.setFilePath(Filename);
            }else {
                file1.setFilePath(pid+"/"+Filename);
            }

            fileService.save(file1);
            return Result.ok("文件尚未上传");
        }
        if(one.getStatus()==1){
            return Result.ok("文件处理中");
        }
        return Result.ok("文件已上传");
    }
    @PostMapping("FileList")
    public Result get_file_list(@RequestBody FileVo fileVo){
        LambdaQueryWrapper<File> QueryWrapper = new LambdaQueryWrapper<>();
        QueryWrapper.eq(File::getUserId,fileVo.getUserId())
                .eq(File::getPId,fileVo.getFileId())
                .eq(fileVo.getFileCategory()!=null,File::getFileCategory,fileVo.getFileCategory())
                .eq(File::getStatus,0);
        List<File> list = fileService.list(QueryWrapper);
        List<FileVo> fileList = new ArrayList<>();
        list.forEach(file -> {
            FileVo fileVo1 = new FileVo();
            BeanUtils.copyProperties(file,fileVo1);
            fileList.add(fileVo1);
        });
        return Result.ok(fileList);
    }
    private File filesetcategory(File file,String extName){
        if(Arrays.asList(video).contains(extName)){
            file.setFileCategory(1);
            return file;
        }
        if(Arrays.asList(music).contains(extName)){
            file.setFileCategory(2);
            return file;
        }
        if(Arrays.asList(image).contains(extName)){
            file.setFileCategory(3);
            return file;
        }
        if(Arrays.asList(word).contains(extName)||Arrays.asList(pdf).contains(extName)||Arrays.asList(excel).contains(extName)){
            file.setFileCategory(4);
            return file;
        }
        file.setFileCategory(5);
        return file;
    }
}
