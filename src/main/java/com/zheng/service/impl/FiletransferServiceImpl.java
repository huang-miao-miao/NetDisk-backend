package com.zheng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zheng.mapper.FileMapper;
import com.zheng.pojo.File;
import com.zheng.pojo.Result;
import com.zheng.service.FileService;
import com.zheng.service.FiletransferService;
import org.springframework.beans.factory.annotation.Autowired;

public class FiletransferServiceImpl implements FiletransferService {
//    @Autowired
//    private FileService fileService;
    @Override
    public Result checkChunk(String fileMd5, int chunk, String filename) {
        return null;
    }

    @Override
    public Result checkFile(String fileMd5) {
        LambdaQueryWrapper<File> QueryWrapper = new LambdaQueryWrapper<>();
        QueryWrapper.eq(File::getFileMd5,fileMd5);
        return null;
    }
}
