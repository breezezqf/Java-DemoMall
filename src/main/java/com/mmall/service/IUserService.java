package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

public interface IUserService {

    /**
     * 登录.登录
     * @param username
     * @param password
     * @return
     */
    ServerResponse<User> login(String username, String password);

    /**
     * 登录.注册
     * @param user
     * @return
     */
    ServerResponse<String> register(User user);

    /**
     * 登录.效验用户名和邮箱
     * @param str
     * @param type
     * @return
     */
    ServerResponse<String> checkValid(String str, String type);


    /**
     * 登录.忘记密码
     * @param username
     */
    ServerResponse selectQuestion(String username);


    /**
     * 登录.效验问题答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    ServerResponse<String> checkAnswer(String username, String question, String answer);

}
