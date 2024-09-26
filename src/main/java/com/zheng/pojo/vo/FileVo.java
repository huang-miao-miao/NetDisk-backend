package com.zheng.pojo.vo;

import lombok.Data;

import java.util.Date;

@Data
public class FileVo {
    private String fileId;

    private String userId;

    private String fileName;

    private Long fileSize;

    private Integer folderType;

    private Integer fileCategory;

    private Date updatetime;
}
