package com.zheng.service;

import com.zheng.pojo.Result;
import org.springframework.stereotype.Service;

@Service
public interface FiletransferService {
    Result checkChunk(String fileMd5, int chunk, String filename);

    Result checkFile(String fileMd5);
}
