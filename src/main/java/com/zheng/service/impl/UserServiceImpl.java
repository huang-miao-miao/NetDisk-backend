package com.zheng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zheng.pojo.User;
import com.zheng.service.UserService;
import com.zheng.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author Z2823
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-09-25 20:52:19
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




