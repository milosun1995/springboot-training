package com.training.admin.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 工具类：生成/解析/校验登录 Token（HS512 对称签名）。
 * 在登录成功后生成 Token，并在过滤器或业务中校验其有效性。
 */
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        // HS512 算法要求密钥至少 64 字节 (512 位)
        // 如果密钥长度不足，使用 Keys.secretKeyFor 生成安全密钥
        if (keyBytes.length < 64) {
            throw new IllegalArgumentException(
                "JWT secret key must be at least 64 bytes (512 bits) for HS512 algorithm. " +
                "Current key length: " + keyBytes.length + " bytes. " +
                "Please update jwt.secret in application.yml to a key with at least 64 bytes."
            );
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public String generateToken(String username) {
        // 将用户名放入 subject，并设置过期时间；payload 简洁，权限列表在进阶章节再加入
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

