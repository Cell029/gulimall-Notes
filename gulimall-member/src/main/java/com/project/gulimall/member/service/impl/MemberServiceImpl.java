package com.project.gulimall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.project.gulimall.member.dao.MemberLevelDao;
import com.project.gulimall.member.entity.MemberLevelEntity;
import com.project.gulimall.member.exception.PhoneExistException;
import com.project.gulimall.member.exception.UserNameExistException;
import com.project.gulimall.member.vo.MemberRegisterVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        memberEntity.setMobile(memberRegisterVo.getPhone());
        // 密码进行加密存储
        memberEntity.setPassword(memberRegisterVo.getPassword());
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

}