package com.ljh.securitydemo.service;

import com.ljh.securitydemo.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface LoginService {
    //    登录功能
    String Login(String username, String password);

    //      通过名字获取用户
    User getUserByUsername(String username);

    //  通过名字获取用户信息
    UserDetails loadUserByUsername(String username);
}
