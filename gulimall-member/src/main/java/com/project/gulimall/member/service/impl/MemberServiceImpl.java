package com.project.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.project.gulimall.member.dao.MemberLevelDao;
import com.project.gulimall.member.entity.MemberLevelEntity;
import com.project.gulimall.member.exception.PhoneExistException;
import com.project.gulimall.member.exception.UserNameExistException;
import com.project.gulimall.member.vo.MemberGitHubUserInfoVo;
import com.project.gulimall.member.vo.MemberLoginVo;
import com.project.gulimall.member.vo.MemberRegisterVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.utils.PageUtils;
import com.project.common.utils.Query;

import com.project.gulimall.member.dao.MemberDao;
import com.project.gulimall.member.entity.MemberEntity;
import com.project.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelDao memberLevelDao;
    @Autowired
    private MemberDao memberDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegisterVo memberRegisterVo) {
        MemberEntity memberEntity = new MemberEntity();
        // 检查用户名和手机号是否唯一，为了让 Controller 感知，可以使用异常机制
        checkPhoneUnique(memberRegisterVo.getPhone());
        checkUserNameUnique(memberRegisterVo.getUsername());
        memberEntity.setUsername(memberRegisterVo.getUsername());
        memberEntity.setNickname(memberRegisterVo.getUsername());
        memberEntity.setMobile(memberRegisterVo.getPhone());

        // 密码进行加密存储
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(memberRegisterVo.getPassword());
        memberEntity.setPassword(encode);

        // 获取会员等级对应的 id
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(memberLevelEntity.getId());

        save(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) {
        Long mobile = memberDao.selectCount(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getMobile, phone));
        if (mobile > 0L) {
            throw new PhoneExistException();
        }

    }

    @Override
    public void checkUserNameUnique(String username) {
        Long username1 = memberDao.selectCount(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getUsername, username));
        if (username1 > 0L) {
            throw new UserNameExistException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo memberLoginVo) {
        String loginacct = memberLoginVo.getLoginacct();
        String password = memberLoginVo.getPassword();
        MemberEntity memberEntity = getOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getUsername, loginacct).or().eq(MemberEntity::getMobile, loginacct));
        if (memberEntity == null) {
            // 登录失败
            return null;
        } else {
            String passwordDb = memberEntity.getPassword();
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            boolean matches = bCryptPasswordEncoder.matches(password, passwordDb);
            if (matches) {
                return memberEntity;
            } else {
                return null;
            }
        }
    }

    @Override
    public MemberEntity oauthLogin(MemberGitHubUserInfoVo memberGitHubUserInfoVo) {
        String gitHubId = String.valueOf(memberGitHubUserInfoVo.getId());
        String githubLogin = memberGitHubUserInfoVo.getLogin();
        MemberEntity one = getOne(new LambdaQueryWrapper<MemberEntity>().eq(MemberEntity::getUsername, gitHubId));
        if (one == null) {
            // 如果没查到就注册
            MemberEntity memberEntity = new MemberEntity();
            memberEntity.setUsername(gitHubId);
            // 获取会员等级对应的 id
            MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();
            memberEntity.setLevelId(memberLevelEntity.getId());
            memberEntity.setNickname(githubLogin);
            memberEntity.setCreateTime(new Date());
            save(memberEntity);
            return memberEntity;
        } else {
            return one;
        }
    }

}