package com.zheng.service;

import com.zheng.pojo.File;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zheng.pojo.Result;

/**
* @author Z2823
* @description 针对表【file】的数据库操作Service
* @createDate 2024-09-26 16:52:39
*/
public interface FileService extends IService<File> {

    Result merge(String md5, Integer chunkTotal, String filename,String pid);
}
