package com.onefly.united.web;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UnitedController {

    @RequestMapping({"/"})
    ModelAndView index() {
        return new ModelAndView("index");
    }

    @GetMapping({"/api/public/user"})
    @ResponseBody
    Authentication user() {
        Authentication result = SecurityContextHolder.getContext().getAuthentication();
        if (result instanceof AnonymousAuthenticationToken) {
            result.setAuthenticated(false);
        }
        return result;
    }

}
