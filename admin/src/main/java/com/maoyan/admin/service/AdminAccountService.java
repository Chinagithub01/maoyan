package com.maoyan.admin.service;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.maoyan.constant.AdminConstant;
import com.maoyan.entity.Admin;
import com.maoyan.entity.AdminSession;
import com.maoyan.mapper.AdminMapper;
import com.maoyan.mapper.AdminSessionMapper;
import com.maoyan.vo.AdminLoginVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class AdminAccountService extends ServiceImpl<AdminMapper, Admin> {
    private final AdminSessionMapper adminSessionMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminAccountService(AdminSessionMapper adminSessionMapper) {
        this.adminSessionMapper = adminSessionMapper;
    }

    public void seed() {
        if (lambdaQuery().eq(Admin::getUsername, AdminConstant.INITIAL_USERNAME).count() > 0) {
            return;
        }
        Admin admin = new Admin();
        admin.setUsername(AdminConstant.INITIAL_USERNAME);
        admin.setPassword(passwordEncoder.encode(AdminConstant.INITIAL_PASSWORD));
        admin.setNickname(AdminConstant.INITIAL_NICKNAME);
        admin.setStatus(AdminConstant.ENABLED);
        save(admin);
    }

    public AdminLoginVO login(String username, String password) {
        Admin admin = lambdaQuery().eq(Admin::getUsername, username).one();
        if (admin == null || admin.getPassword() == null || !passwordEncoder.matches(password, admin.getPassword())) {
            throw new IllegalArgumentException(AdminConstant.LOGIN_FAILED);
        }
        if (admin.getStatus() == null || admin.getStatus() != AdminConstant.ENABLED) {
            throw new IllegalArgumentException(AdminConstant.DISABLED);
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        AdminSession session = new AdminSession();
        session.setAdminId(admin.getId());
        session.setTokenHash(sha256(token));
        session.setExpireTime(LocalDateTime.now().plusHours(AdminConstant.TOKEN_HOURS));
        adminSessionMapper.insert(session);
        return new AdminLoginVO(token, admin.getId(), admin.getUsername(), admin.getNickname());
    }

    public Admin authenticate(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(AdminConstant.LOGIN_REQUIRED);
        }
        AdminSession stored = adminSessionMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdminSession>()
                        .eq(AdminSession::getTokenHash, sha256(token)));
        if (stored == null || stored.getExpireTime() == null || stored.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(AdminConstant.LOGIN_REQUIRED);
        }
        Admin admin = getById(stored.getAdminId());
        if (admin == null || admin.getStatus() == null || admin.getStatus() != AdminConstant.ENABLED) {
            throw new IllegalArgumentException(AdminConstant.LOGIN_REQUIRED);
        }
        return admin;
    }

    private String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("无法计算令牌摘要", exception);
        }
    }
}
