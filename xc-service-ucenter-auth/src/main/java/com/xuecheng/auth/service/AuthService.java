package com.xuecheng.auth.service;

import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * 描述:
 * Auth Service
 *
 * @author XueGuCheng
 * @create 2020-03-08 12:11
 */
@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    @Value("${auth.tokenValiditySeconds}")
    int tokenValiditySeconds;//token存储到redis的过期时间
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    LoadBalancerClient loadBalancerClient;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    //认证方法
    public AuthToken login(String username,String password,String clientId,String clientSerret){
        //申请令牌

    }

    //认证方法
    private AuthToken applyToken(String username,String password,String clientId,String clientSerret){
        //选中认证服务的地址
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        if (serviceInstance == null) {
            LOGGER.error("choose an auth instance fail");
            //抛异常
        }
        //获取令牌的url
        String path = serviceInstance.getUri().toString() + "/auth/oauth/token";

        //定义body
        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        //授权方式
        formData.add("grant_type","password");
        //账号
        formData.add("username",username);
        //密码
        formData.add("password", password);
        //定义头
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization",httpbasic(clientId,clientSerret));
        //指定 restTemplate当遇到400或401响应时候也不要抛出异常，也要正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
                //当响应的值为400或401时候也要正常响应，不要抛出异常
                if (response.getStatusCode().equals(400) && response.getStatusCode().equals(401)){
                    super.handleError(response);
                }
            }
        });
        Map map = null;
        try{
            //http请求spring security的申请令牌接口
            ResponseEntity<Map> mapResponseEntity = restTemplate.exchange(path, HttpMethod.POST, new HttpEntity<MultiValueMap<String, String>>(formData, header), Map.class);
            map = mapResponseEntity.getBody();
        }catch (RestClientException e){
            e.printStackTrace();
            LOGGER.error("request oauth_token_password error: {}",e.getMessage());
            //抛异常
        }
        //jti是jwt令牌的唯一标识作为用户身份令牌
        if (map == null || map.get("access_token") == null || map.get("refresh_token") == null || map.get("jti") == null ){
            //抛异常
        }
        AuthToken authToken = new AuthToken();
        //访问令牌(jwt)
        String jwt_token = (String) map.get("access_token");
        //刷新令牌(jwt)
        String refresh_token = (String) map.get("refresh_token");
        //jti，作为用户的身份标识
        String access_token = (String) map.get("jti");
        authToken.setJwt_token(jwt_token);
        authToken.setAccess_token(access_token);
        authToken.setRefresh_token(refresh_token);

        return authToken;

    }

    //获取httpbasic认证串
    private String httpbasic(String clientId,String clientSecret){
        //将客户端id和客户端密码拼接，按“客户端id:客户端密码”
        String string = clientId + ":" + clientSecret;
        //进行base64编码
        byte[] encode = Base64.encode(string.getBytes());
        return "Basic" + new String(encode);
    }


}
