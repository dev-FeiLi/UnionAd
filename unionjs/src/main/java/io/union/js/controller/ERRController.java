package io.union.js.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2017/7/20.
 */
@RestController
@EnableAutoConfiguration
@Scope("prototype")
public class ERRController {

    @RequestMapping(value = "/error", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView errorPage(HttpServletResponse response) {
        ModelAndView mav = new ModelAndView();
        String page = "error";
        int status = response.getStatus();
        page = page + "/" + status;
        mav.setViewName(page);
        return mav;
    }
}
