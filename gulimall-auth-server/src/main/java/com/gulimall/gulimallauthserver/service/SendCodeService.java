package com.gulimall.gulimallauthserver.service;

import javax.validation.constraints.NotEmpty;

public interface SendCodeService {
    boolean sendCode(String phone);

    boolean checkCode(String phone, String code);
}
