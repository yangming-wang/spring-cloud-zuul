package com.imooc.user.controller;

import com.imooc.user.VO.ResultVO;
import com.imooc.user.constant.CookieConstant;
import com.imooc.user.constant.RedisConstant;
import com.imooc.user.dataobject.UserInfo;
import com.imooc.user.enums.ResultEnum;
import com.imooc.user.enums.RoleEnum;
import com.imooc.user.service.UserService;
import com.imooc.user.utils.CookieUtil;
import com.imooc.user.utils.ResultVOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 买家登陆
     * @param openid
     * @param response
     * @return
     */
    @GetMapping("/buyer")
    public ResultVO buyer(@RequestParam("openid") String openid,HttpServletResponse response){
        //1. openid和数据库里的数据匹配
            UserInfo userInfo=userService.findByOpenid(openid);
            if (userInfo==null){
                return ResultVOUtil.error(ResultEnum.LOGIN_FAIL);
            }
        //2. 判断角色
            if (RoleEnum.BUYER.getCode()!=userInfo.getRole()){
                return ResultVOUtil.error(ResultEnum.ROLE_ERROR);
            }
        //3.cookie里设置openid=abc  第四个参数是过期时间
        CookieUtil.set(response, CookieConstant.OPENID,openid,CookieConstant.expire);
            return ResultVOUtil.success();
    }

    @GetMapping("/seller")
    public ResultVO seller(@RequestParam("openid") String openid, HttpServletResponse response, HttpServletRequest request){
        //判断用户是否已经登录
        Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
        if (cookie != null &&
                !StringUtils.isEmpty(stringRedisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_TEMPLATE, cookie.getValue())))) {
            return ResultVOUtil.success();
        }
        //1. openid和数据库里的数据匹配
        UserInfo userInfo=userService.findByOpenid(openid);
        if (userInfo==null){
            return ResultVOUtil.error(ResultEnum.LOGIN_FAIL);
        }
        //2. 判断角色
        if (RoleEnum.SELLER.getCode()!=userInfo.getRole()){
            return ResultVOUtil.error(ResultEnum.ROLE_ERROR);
        }
        //3.redis设置key=UUID,value=xyz
        String token= UUID.randomUUID().toString();
        Integer expire=CookieConstant.expire;
        stringRedisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_TEMPLATE,token),
                openid,
                expire,
                TimeUnit.SECONDS);  //TimeUnit.SECONDS  时间等待单位  线程等待
        //4.cookie里设置token=UUID
        CookieUtil.set(response, CookieConstant.TOKEN,token,CookieConstant.expire);
        return ResultVOUtil.success();
    }
}
