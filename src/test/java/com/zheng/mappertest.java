package com.zheng;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zheng.pojo.File;
import com.zheng.pojo.vo.FileVo;
import com.zheng.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class mappertest {
    @Autowired
    private FileService fileService;
    @Test
    public void list_file(){
        FileVo fileVo = new FileVo();
        fileVo.setFileId("1");
        fileVo.setUserId("1");
        LambdaQueryWrapper<File> QueryWrapper = new LambdaQueryWrapper<>();
        QueryWrapper.eq(File::getUserId,fileVo.getUserId())
                .eq(File::getPId,fileVo.getFileId());
        List<File> list = fileService.list(QueryWrapper);
        System.out.println(list);
    }
}
