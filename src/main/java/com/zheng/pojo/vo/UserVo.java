package com.zheng.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;


@TableName(value ="user")
@Data
public class UserVo implements Serializable {
    private String phone;

    private String password;
}
