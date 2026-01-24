package com.zjtec.travel.controller;

import com.zjtec.travel.constant.Const;
import com.zjtec.travel.domain.User;
import com.zjtec.travel.service.UserService;
import com.zjtec.travel.vo.LoginVo;
import com.zjtec.travel.vo.RedirectVo;
import com.zjtec.travel.vo.ResMsg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

  @Autowired
  private UserService userService;

  @Autowired
  public HttpSession session;

  /**
   * 登录功能
   * @param vo
   * @return
   */
  @RequestMapping(value = "/signin")
  @ResponseBody
  public ResMsg signIn(@RequestBody LoginVo vo) {
    ResMsg resMsg = new ResMsg();
    String captcha=(String)session.getAttribute(Const.SESSION_KEY_CAPTCHA);
    if(captcha==null || !captcha.equalsIgnoreCase(vo.getCaptcha())){
      resMsg.setErrcode("4");
      resMsg.setErrmsg("验证码不正确");
      return resMsg;
    }
    //完善登录功能

    if(StringUtils.hasLength(vo.getUsername()) && StringUtils.hasLength(vo.getPassword())&&StringUtils.hasLength(vo.getCaptcha())) {
            String username = vo.getUsername();
          User dbuser = userService.getUserByUsername(username);
            if (dbuser != null) {
                String password = dbuser.getPassword();
                if (password.equals(vo.getPassword())) {
                   resMsg.setErrcode("0");
                    resMsg.setErrmsg("登录成功");
          if (Const.USER_ROLE_ADMIN.equals(dbuser.getRole())) {
            resMsg.setResult(new RedirectVo("/dashboard"));
          } else if (Const.USER_ROLE_MEMBER.equals(dbuser.getRole())) {
            resMsg.setResult(new RedirectVo("/mine"));
          }
          //登录成功，将用户信息保存到session中
          session.setAttribute(Const.SESSION_KEY_USER, dbuser);
          session.setAttribute(Const.SESSION_KEY_USERNAME, dbuser.getUsername());
          session.setAttribute(Const.SESSION_KEY_USER_ROLE, dbuser.getRole());
        } else {
          resMsg.setErrcode("1");
          resMsg.setErrmsg("用户名或者密码不正确");
        }
      } else {
        resMsg.setErrcode("3");
        resMsg.setErrmsg("用户名不存在");
      }
    }
    else{
      resMsg.setErrcode("2");
      resMsg.setErrmsg("用户名或密码不能为空");
    }
    return resMsg;
  }

  /**
   * 退出/注销功能
   * @param map
   * @return
   */
  @RequestMapping(value = "/logout")
  public String logout(ModelMap map) {
    session.removeAttribute(Const.SESSION_KEY_USER);
    session.removeAttribute(Const.SESSION_KEY_USERNAME);
    session.removeAttribute(Const.SESSION_KEY_USER_ROLE);
    map.put("msg", "成功退出，点击跳转链接跳转到首页");
    map.put("redirect","/");
    map.put("title","用户退出");
    return "msg";
  }
}
