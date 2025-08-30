package com.gulimall.gulimallauthserver.service;

import com.gulimall.gulimallauthserver.domain.vo.GitHubUserInfoVo;
import com.gulimall.gulimallauthserver.domain.vo.MemberResponseVo;

public interface GitHubOAuthService {
    MemberResponseVo loginOrRegister(String code);
}
