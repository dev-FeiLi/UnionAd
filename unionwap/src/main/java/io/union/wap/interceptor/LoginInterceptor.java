package io.union.wap.interceptor;

import io.union.wap.common.utils.ApplicationConstant;
import io.union.wap.entity.AdsUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        try {
            HttpSession session = httpServletRequest.getSession();
            // 从后台点击ID直接进入会员后台
            String reffer = httpServletRequest.getHeader("Referer");
            if (null != reffer && reffer.startsWith("http://b.dianyoo.cn/")) {
                String id = httpServletRequest.getParameter("uid");
                if (null != id && !"".equals(id)) {
                    Long uid = new Long(id);
                    AdsUser curuser = new AdsUser();
                    curuser.setUid(uid);
                    if (null != curuser) {
                        session.setAttribute(ApplicationConstant.SESSION_USER_CONTEXT, curuser);
                    }
                }
            }
            // 废话注释
            Object object = session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            if (null == object) {
                httpServletResponse.setStatus(HttpServletResponse.SC_FOUND);
                httpServletResponse.sendRedirect("/login");
            } else {
                return true;
            }
        } catch (Exception e) {
            logger.error("pre interceptor error: ", e);
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
