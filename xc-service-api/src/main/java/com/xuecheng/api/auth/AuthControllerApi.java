package com.xuecheng.api.auth;

import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;

@Api(value = "用户认证",description = "用户认证接口")
public interface AuthControllerApi {

    //登录
    public LoginResult login(LoginResult loginResult);

    //退出
    public ResponseResult logout();

}
