package com.zheng.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName file
 */
@TableName(value ="file")
@Data
public class File implements Serializable {
    @TableId(value = "file_id")
    private String fileId;

    private String userId;

    private String fileName;

    private Long fileSize;

    private Integer folderType;

    private String pId;

    private Integer fileCategory;

    private String fileMd5;

    private Integer status;

    private Date updatetime;

    private String filePath;

    private static final long serialVersionUID = 1L;
}