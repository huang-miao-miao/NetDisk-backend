package com.zheng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zheng.pojo.File;
import com.zheng.pojo.Result;
import com.zheng.service.FileService;
import com.zheng.mapper.FileMapper;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author Z2823
* @description 针对表【file】的数据库操作Service实现
* @createDate 2024-09-26 16:52:39
*/
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File>
    implements FileService{
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private MinioClient minioClient;

    @Override
    public Result merge(String md5, Integer chunkTotal, String fileSuffix) {
        // 获取所有分块
        List<Item> itemList = getChunkList(md5);

        // 合并文件
        List<ComposeSource> composeSourceList = new ArrayList<>();
        for (Item item : itemList) {
            composeSourceList.add(ComposeSource.builder()
                    .bucket("netdisk")
                    .object(item.objectName())
                    .build());
        }
        try {
            minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket("netdisk")
                            .object("files/" + md5 + "." +fileSuffix)
                            .sources(composeSourceList).build());
        } catch (Exception e) {
            throw new RuntimeException(String.format("minio 合并对象失败，【%s】", e.getMessage()));
        }
        return Result.ok("分块合并完成");
    }
    private List<Item> getChunkList(String md5) {
        // 获取所有分片
        Iterable<io.minio.Result<Item>> resultIterable =
                minioClient.listObjects(ListObjectsArgs.builder()
                        .bucket("netdisk")
                        .prefix("chunks/" + md5 + "/")
                        .recursive(false) // 是否递归查询
                        .build());

        List<Item> itemList = new ArrayList<>(); // 分块

        for (io.minio.Result<Item> itemResult : resultIterable) {
            try {
                itemList.add(itemResult.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        // 分片文件排序
        itemList.sort((o1, o2) -> {
            String o1Name = o1.objectName();
            String o2Name = o2.objectName();
            int o1Index = Integer.parseInt(o1Name.substring(o1Name.lastIndexOf("/") + 1));
            int o2Index = Integer.parseInt(o2Name.substring(o2Name.lastIndexOf("/") + 1));
            return o1Index - o2Index;
        });
        return itemList;
    }
}




