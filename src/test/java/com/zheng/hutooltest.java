package com.zheng;

import cn.hutool.core.date.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class hutooltest {
    @Test
    public void DateTest(){
        Date date = new Date();
        DateTime time = new DateTime(date);
        System.out.println(time);
    }
}
