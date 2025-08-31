package com.gulimall.gulimallauthserver.service;

import com.project.common.domain.vo.MemberResponseVo;

public interface GitHubOAuthService {
    MemberResponseVo loginOrRegister(String code);
}
