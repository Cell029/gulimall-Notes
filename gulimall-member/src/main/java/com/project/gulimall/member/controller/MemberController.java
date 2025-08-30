package com.project.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.project.common.exception.BizCodeEnum;
import com.project.gulimall.member.exception.PhoneExistException;
import com.project.gulimall.member.exception.UserNameExistException;
import com.project.gulimall.member.vo.MemberGitHubUserInfoVo;
import com.project.gulimall.member.vo.MemberLoginVo;
import com.project.gulimall.member.vo.MemberRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.project.gulimall.member.entity.MemberEntity;
import com.project.gulimall.member.service.MemberService;
import com.project.common.utils.PageUtils;
import com.project.common.utils.R;



/**
 * 会员
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public R register(@RequestBody MemberRegisterVo memberRegisterVo){
        try {
            memberService.regist(memberRegisterVo);
        } catch (PhoneExistException e) {
            return R.error(BizCodeEnum.PHONE_EXIT_EXCEPTION.getCode(), BizCodeEnum.PHONE_EXIT_EXCEPTION.getMsg());
        } catch (UserNameExistException e) {
            return R.error(BizCodeEnum.USER_EXIT_EXCEPTION.getCode(), BizCodeEnum.USER_EXIT_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo memberLoginVo) {
        MemberEntity memberEntity = memberService.login(memberLoginVo);
        if (memberEntity != null) {
            return R.ok();
        } else {
            return R.error(BizCodeEnum.LOGINACCT_PASSWORD_INVALID_EXCEPTION.getCode(), BizCodeEnum.LOGINACCT_PASSWORD_INVALID_EXCEPTION.getMsg());
        }
    }

    /**
     * 社交账号登录
     */
    @PostMapping("/oauth/login")
    public R oauthLogin(@RequestBody MemberGitHubUserInfoVo memberGitHubUserInfoVo) {
        MemberEntity memberEntity = memberService.oauthLogin(memberGitHubUserInfoVo);
        return R.ok().put("memberEntity", memberEntity);
    }

}
