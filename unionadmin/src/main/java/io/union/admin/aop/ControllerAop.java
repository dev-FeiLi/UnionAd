package io.union.admin.aop;

import io.union.admin.entity.SysManage;
import io.union.admin.entity.SysOperateLog;
import io.union.admin.schedule.OperateSaveJob;
import io.union.admin.util.ApplicationConstant;
import io.union.admin.util.ToolUtil;
import net.sf.json.JSONObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * Created by Administrator on 2017/4/18.
 */
@Aspect
@Configuration
public class ControllerAop {
    final static Logger LOG = LoggerFactory.getLogger(ControllerAop.class);

    private ThreadLocal<SysOperateLog> local = new ThreadLocal<>();
    @Autowired
    private OperateSaveJob operateSaveJob;

    @Pointcut("execution(* io.union.admin.controller.*Controller.*(..))")
    public void excudeService() {
    }

    @Before("excudeService()")
    public void before(JoinPoint joinPoint) {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
            HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
            String uri = request.getRequestURI();
            Enumeration<String> paramNames = request.getParameterNames();
            Map<String, String> parameters = new HashMap<>();
            while (paramNames.hasMoreElements()) {
                String key = paramNames.nextElement();
                parameters.put(key, request.getParameter(key));
            }
            String params = "", ip = ToolUtil.obtainRequestIp(request);
            if (parameters.size() > 0) {
                params = JSONObject.fromObject(parameters).toString();
            } else {
                Object[] args = joinPoint.getArgs();
                for (Object arg : args) {
                    if (arg instanceof LinkedHashMap) {
                        params = JSONObject.fromObject(arg).toString();
                    }
                }
            }
            String result = "操作时的传值是：" + params;

            SysOperateLog operateLog = new SysOperateLog();
            operateLog.setOptTagValue(result);
            operateLog.setOptUrl(uri);
            operateLog.setOptIp(ip);
            operateLog.setOptStart(System.currentTimeMillis());
            operateLog.setOptAddTime(new Date());

            SysManage manage = (SysManage) session.getAttribute(ApplicationConstant.SESSION_USER_CONTEXT);
            if (null != manage) {
                operateLog.setOptManId(manage.getManId());
                operateLog.setOptAccount(manage.getManAccount());
            } else {
                operateLog.setOptManId(0L);
                operateLog.setOptAccount("");
            }
            local.set(operateLog);
        } catch (Exception e) {
            LOG.error("aop before error: ", e);
        }
    }

    @After("excudeService()")
    public void after(JoinPoint joinPoint) {
        try {
            SysOperateLog operateLog = local.get();
            if (null != operateLog) {
                Long start = operateLog.getOptStart(), end = System.currentTimeMillis();
                Long total = end - start;
                operateLog.setOptEnd(end);
                operateLog.setOptSecond(total);
                //sysOperateLogService.save(operateLog);
                operateSaveJob.addOperate(operateLog);
            }
        } catch (Exception e) {
            LOG.error("aop after error: ", e);
        }
    }

    @AfterThrowing(value = "excudeService()", throwing = "throwable")
    public void error(JoinPoint joinPoint, Throwable throwable) {
        try {
            SysOperateLog operateLog = local.get();
            if (null != operateLog) {
                Long start = operateLog.getOptStart(), end = System.currentTimeMillis();
                Long total = end - start;
                String result = operateLog.getOptTagValue();
                result += ", 发生异常：" + throwable.getMessage();
                operateLog.setOptEnd(end);
                operateLog.setOptSecond(total);
                operateLog.setOptTagValue(result);
                //sysOperateLogService.save(operateLog);
                operateSaveJob.addOperate(operateLog);
            }
        } catch (Exception e) {
            LOG.error("aop throw error: ", e);
        }
    }
}
