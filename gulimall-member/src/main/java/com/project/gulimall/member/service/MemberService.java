package com.project.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.common.utils.PageUtils;
import com.project.gulimall.member.entity.MemberEntity;
import com.project.gulimall.member.exception.PhoneExistException;
import com.project.gulimall.member.exception.UserNameExistException;
import com.project.gulimall.member.vo.MemberRegisterVo;

import java.util.Map;

/**
 * 会员
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegisterVo memberRegisterVo);

    // 检查邮箱唯一性
    void checkPhoneUnique(String phone) throws PhoneExistException;

    // 检查用户名唯一性
    void checkUserNameUnique(String username) throws UserNameExistException;

}

