package com.training.admin.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        // HS512 算法要求密钥至少 64 字节 (512 位)
        if (keyBytes.length < 64) {
            throw new IllegalArgumentException(
                "JWT secret key must be at least 64 bytes (512 bits) for HS512 algorithm. " +
                "Current key length: " + keyBytes.length + " bytes. " +
                "Please update jwt.secret in application.yml to a key with at least 64 bytes."
            );
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * 生成 JWT Token（包含权限列表）
     * @param username 用户名
     * @param permissions 权限编码列表
     */
    public String generateToken(String username, List<String> permissions) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setSubject(username)
                .claim("permissions", permissions)  // 将权限列表存入 JWT
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * 生成 JWT Token（兼容旧版本，无权限）
     */
    public String generateToken(String username) {
        return generateToken(username, List.of());
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    
    /**
     * 从 Token 中提取权限列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getPermissionsFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        Object permissions = claims.get("permissions");
        if (permissions instanceof List) {
            return (List<String>) permissions;
        }
        return List.of();
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

