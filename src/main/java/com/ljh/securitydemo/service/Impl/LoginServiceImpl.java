package com.ljh.securitydemo.service.Impl;

import com.ljh.securitydemo.common.CommonResult;
import com.ljh.securitydemo.common.ResultCode;
import com.ljh.securitydemo.entity.MyUserDetails;
import com.ljh.securitydemo.entity.User;
import com.ljh.securitydemo.mapper.UserMapper;
import com.ljh.securitydemo.service.LoginService;
import com.ljh.securitydemo.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserMapper userMapper;

    @Override
    public String Login(String username, String password) {
        String token = null;
        try {
            UserDetails userDetails = loadUserByUsername(username);
//            这里的密码校验逻辑简化了，如果是正常开发需要进行加密
            if (!password.equals(userDetails.getPassword())) {
                CommonResult.failed(ResultCode.FAILED, "密码错误，请重新确认");
            }
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (Exception e) {
            CommonResult.failed(ResultCode.VALIDATE_FAILED,"登陆异常，请联系管理员");
        }
        return token;
    }

    @Override
    public User getUserByUsername(String username) {
        System.out.println("取出");
        return userMapper.getUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        //获取用户信息
        User user = getUserByUsername(username);
        if (user != null) {
//           这里应该是用户权限赋值逻辑
            return new MyUserDetails(user);
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }

}
