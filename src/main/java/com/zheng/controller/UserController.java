package com.zheng.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zheng.common.TokenUtil;
import com.zheng.pojo.Result;
import com.zheng.pojo.User;
import com.zheng.pojo.vo.UserVo;
import com.zheng.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("login")
    private Result User_Login(@RequestBody UserVo userVo){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,userVo.getPhone());
        User user = userService.getOne(queryWrapper);
        if(user.getPassword().equals(userVo.getPassword())){
            String token = TokenUtil.sign(user.getUsername(),user.getUserid());
            return Result.ok(token);
        }else {
            return Result.fail("密码错误");
        }
    }
}
