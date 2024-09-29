package com.zheng.service.impl;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zheng.pojo.File;
import com.zheng.pojo.Result;
import com.zheng.service.FileService;
import com.zheng.mapper.FileMapper;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
    public Result merge(String md5, Integer chunkTotal, String filename, String pid) {
        // 获取所有分块
        List<Item> itemList = getChunkList(md5);
        String extName = FileUtil.extName(filename);
        // 合并文件
        List<ComposeSource> composeSourceList = new ArrayList<>();
        for (Item item : itemList) {
            composeSourceList.add(ComposeSource.builder()
                    .bucket("netdisk")
                    .object(item.objectName())
                    .build());
        }
        try {
            if(!pid.equals("1")){
                LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
                fileLambdaQueryWrapper.eq(File::getFileId,pid);
                File one = fileMapper.selectOne(fileLambdaQueryWrapper);
                filename = one.getFilePath()+"/"+filename;
            }
            minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket("netdisk")
                            .object(filename)
                            .sources(composeSourceList).build());
            deletechunkfolder(md5);
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
    public void deletechunkfolder(String md5) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String prefix = "chunks/" + md5 + "/"; // 注意文件夹名后需要加'/'
        Iterable<io.minio.Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket("netdisk")
                .prefix(prefix)
                .recursive(true) // 设置为true以包含子文件夹中的对象
                .build());
        for (io.minio.Result<Item> result : results) {
            Item deleteitem = result.get();
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket("netdisk")
                    .object(deleteitem.objectName())
                    .build());
        }
    }
}




