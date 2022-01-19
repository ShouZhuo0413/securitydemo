package com.ljh.securitydemo.util;

/*
 * 用于生成jwt的工具类
 * */

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {
    //    playload主题
    private static final String CLAIMS_KEY_USERNAME = "sub";
    //    playload创建时间
    private static final String CLAIMS_CREATED_TIME = "created";

    //    用来解密的秘钥
    @Value("${jwt.secret}")
    private String secret;

    //     token的持续时间
    @Value("${jwt.expiration}")
    private Long expiration;

    //     token头部
    @Value("${jwt.tokenHead}")  // 固定赋值
    private String tokenHead;

    //    生成token
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    //    生成token过期时间
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + 1000 * expiration);
    }

    //    从token中拿到负载
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.out.println("jwt格式验证失败");
        }
        return claims;
    }

    //    从token中获取用户姓名
    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    //    验证token是否有效
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    //    验证token是否过期
    private boolean isTokenExpired(String token) {
        Date expirationDate = getExpirationDate(token);
        return expirationDate.before(new Date());
    }

    //    获取过期时间
    public Date getExpirationDate(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    //    根据用户信息生成token
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIMS_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIMS_CREATED_TIME, new Date());
        return generateToken(claims);
    }

    /*
     * 当原来的token没过期时可以刷新
     * */
    public String refreshHeadToken(String oldToken) {
        if (StrUtil.isEmpty(oldToken)) {
            return null;
        }
        String token = oldToken.substring(tokenHead.length());
        if (StrUtil.isEmpty(token)) {
            return null;
        }
        //  token校验不通过
        Claims claims = getClaimsFromToken(token);
        if (claims == null) {
            return null;
        }
        //  token过期 不能刷新
        if (isTokenExpired(token)) {
            return null;
        }
        if (tokenRefreshJustBefore(token, 30 * 60)) {
            return token;
        } else {
            claims.put(CLAIMS_CREATED_TIME, new Date());
            return generateToken(claims);
        }
    }

    /*
     * 判断token在指定时间内是否刚刚刷新过
     * */
    private boolean tokenRefreshJustBefore(String token, int time) {
        Claims claims = getClaimsFromToken(token);
        Date created = claims.get(CLAIMS_CREATED_TIME, Date.class);
        Date refreshDate = new Date();
        if (refreshDate.after(created) && refreshDate.before(DateUtil.offsetSecond(created, time))) {
            return true;
        }
        return false;
    }
}
