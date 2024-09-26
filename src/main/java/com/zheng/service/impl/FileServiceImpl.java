package com.zheng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zheng.pojo.File;
import com.zheng.service.FileService;
import com.zheng.mapper.FileMapper;
import org.springframework.stereotype.Service;

/**
* @author Z2823
* @description 针对表【file】的数据库操作Service实现
* @createDate 2024-09-26 16:52:39
*/
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File>
    implements FileService{

}




