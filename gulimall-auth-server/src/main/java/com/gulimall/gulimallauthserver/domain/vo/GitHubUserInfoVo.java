package com.gulimall.gulimallauthserver.domain.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GitHubUserInfoVo {
    private Long id;
    private String login;
}
