package com.maoyan.service;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.maoyan.constant.UserConstant;
import com.maoyan.entity.User;
import com.maoyan.mapper.UserMapper;
import com.maoyan.vo.UserVO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    public UserVO register(String username, String password, String phone, String nickname) {
        if (lambdaQuery().eq(User::getUsername, username).count() > 0) {
            throw new IllegalArgumentException(UserConstant.USERNAME_EXISTS);
        }
        if (lambdaQuery().eq(User::getPhone, phone).count() > 0) {
            throw new IllegalArgumentException(UserConstant.PHONE_REGISTERED);
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setPhone(phone);
        user.setNickname(nickname);
        try {
            save(user);
        } catch (DuplicateKeyException exception) {
            throw new IllegalArgumentException(UserConstant.ACCOUNT_REGISTERED);
        }
        return UserVO.of(getById(user.getId()));
    }

    public UserVO login(String username, String password) {
        User user = lambdaQuery().eq(User::getUsername, username).one();
        if (user == null || !password.equals(user.getPassword())) {
            throw new IllegalArgumentException(UserConstant.LOGIN_FAILED);
        }
        return UserVO.of(user);
    }
}
