package com.ljh.securitydemo.mapper;

import com.ljh.securitydemo.entity.User;

public interface UserMapper {
    User getUserByUsername(String Username);
}
