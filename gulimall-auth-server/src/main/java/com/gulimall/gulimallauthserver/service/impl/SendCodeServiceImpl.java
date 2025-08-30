package com.gulimall.gulimallauthserver.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.gulimall.gulimallauthserver.service.SendCodeService;
import com.project.common.constant.PhoneCodeConstant;
import com.project.common.exception.BizCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("sendCodeService")
public class SendCodeServiceImpl implements SendCodeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean sendCode(String phone) {
        String redisCode = stringRedisTemplate.opsForValue().get(PhoneCodeConstant.LOGIN_CODE_KEY + phone);
        if (redisCode != null) {
            long time = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - time < 60 * 1000) {
                return false;
            }
        }
        String code = RandomUtil.randomNumbers(6) + "_" + System.currentTimeMillis();
        // 保存验证码到 redis
        stringRedisTemplate.opsForValue().set(PhoneCodeConstant.LOGIN_CODE_KEY + phone, code, PhoneCodeConstant.LOGIN_CODE_TTL, TimeUnit.MINUTES);
        // 防止同一个 phone 在 60s 内再次返送验证码
        log.debug("发送短信验证码成功，验证码：{}", code.split("_")[0]);
        return true;
    }

    @Override
    public boolean checkCode(String phone, String code) {
        String redisCode = stringRedisTemplate.opsForValue().get(PhoneCodeConstant.LOGIN_CODE_KEY + phone);
        if (redisCode == null) {
            return false;
        } else {
            if (code.equals(redisCode.split("_")[0])) {
                // 验证码正确则删除验证码，确保只能使用一次
                stringRedisTemplate.delete(PhoneCodeConstant.LOGIN_CODE_KEY + phone);
                // 进行注册
                return true;
            } else {
                return false;
            }
        }
    }
}
