package com.gulimall.gulimallauthserver.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.gulimall.gulimallauthserver.domain.vo.GitHubUserInfoVo;
import com.gulimall.gulimallauthserver.domain.vo.MemberResponseVo;
import com.gulimall.gulimallauthserver.excetpion.OAuthException;
import com.gulimall.gulimallauthserver.feign.MemberFeignService;
import com.gulimall.gulimallauthserver.service.GitHubOAuthService;
import com.project.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service("gitHubOAuthServiceImpl")
public class GitHubOAuthServiceImpl implements GitHubOAuthService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MemberFeignService memberFeignService;

    private final String clientId = "Ov23licJNbDcKWBNbiLL";
    private final String clientSecret = "59566de00376668b9059feba62dce653aedf717d";
    private final String redirectUri = "http://auth.gulimall.com/oauth/github/success";

    @Override
    public MemberResponseVo loginOrRegister(String code) throws OAuthException {
        String accessToken = getAccessToken(code);
        GitHubUserInfoVo userInfo = getUserInfo(accessToken);
        R r = memberFeignService.oauthLogin(userInfo);
        if (r.getCode() == 0) {
            MemberResponseVo memberEntity = r.getData("memberEntity", new TypeReference<MemberResponseVo>() {
            });
            return memberEntity;
        }
        return null;
    }

    public GitHubUserInfoVo getUserInfo(String accessToken) throws OAuthException {
        String url = "https://api.github.com/user";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        Map<String, Object> body = response.getBody();
        if (body != null && body.get("id") != null) {
            GitHubUserInfoVo gitHubUserInfoVo = new GitHubUserInfoVo();
            gitHubUserInfoVo.setId(Long.parseLong(body.get("id").toString()));
            gitHubUserInfoVo.setLogin(body.get("login").toString());
            return gitHubUserInfoVo;
        } else {
            throw new OAuthException("获取 GitHub 用户信息失败");
        }
    }

    public String getAccessToken(String code) throws OAuthException {
        String url = "https://github.com/login/oauth/access_token";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, String> body = new HashMap<>();
        body.put("client_id", clientId);
        body.put("client_secret", clientSecret);
        body.put("code", code);
        body.put("redirect_uri", redirectUri);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        Map<String, Object> resp = response.getBody();
        if (resp != null && resp.get("access_token") != null) {
            return resp.get("access_token").toString();
        }else {
            throw new OAuthException("获取 AccessToken 失败");
        }
    }

}
