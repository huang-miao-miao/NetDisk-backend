package com.zheng.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    @TableId(value = "userid",type = IdType.AUTO)
    private String userid;

    private String username;

    private String phone;

    private String password;

    private String email;

    private String sex;

    private String birthday;

    private static final long serialVersionUID = 1L;
}