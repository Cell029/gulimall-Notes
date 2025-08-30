package com.gulimall.gulimallauthserver.feign;

import com.gulimall.gulimallauthserver.domain.vo.GitHubUserInfoVo;
import com.gulimall.gulimallauthserver.domain.vo.UserLoginVo;
import com.project.common.domain.vo.UserRegisterVo;
import com.project.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-member")
public interface MemberFeignService {
    @PostMapping("/member/member/register")
    R register(@RequestBody UserRegisterVo userRegisterVo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo uerLoginVo);

    @PostMapping("/member/member/oauth/login")
    R oauthLogin(@RequestBody GitHubUserInfoVo gitHubUserInfoVo);
}
