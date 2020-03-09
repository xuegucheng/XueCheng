package com.xuecheng.auth.controller;

import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

/**
 * 描述:
 * Auth Controller
 *
 * @author XueGuCheng
 * @create 2020-03-08 12:46
 */
@RestController
public class AuthController  implements AuthControllerApi {

    @Value("${auth.clientId}")
    String clientId;
    @Value("${auth.clientSecret}")
    String clientSecret;
    @Value("${auth.cookieDomain}")
    String cookieDomain;
    @Value("${auth.cookieMaxAge}")
    int cookieMaxAge;
    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;

    @Autowired
    AuthService authService;



    @Override
    @PostMapping("/userlogin")
    public LoginResult login(LoginResult loginResult) {
        //校验账号是否输入
        if (loginResult == null || StringUtils.isEmpty(loginResult.getUsername())){
            //抛异常
        }
        //校验密码是否输入
        if (loginResult == null || StringUtils.isEmpty(loginResult.getPassword())){
            //抛异常
        }
        AuthToken authToken = authService.login(loginResult.getUsername(), loginResult.getPassword(), clientId, clientSecret);
        //将令牌写入cookie
        //访问token
        String access_token = authToken.getAccess_token();
        //将访问令牌存储到cookie

        return null;
    }

    //将令牌保存到cookie
    private void saveCookie(String token){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        //添加cookie 认证令牌，最后一个参数设置为false，表示允许浏览器获取
        CookieUtil.addCookie(response,cookieDomain,"/","uid",token,cookieMaxAge,false);
    }

    @Override
    @PostMapping("/userlogout")
    public ResponseResult logout() {
        return null;
    }
}
