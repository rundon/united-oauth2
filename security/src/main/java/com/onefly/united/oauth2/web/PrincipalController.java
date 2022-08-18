/*
 * Copyright (c) 2018. utaka and/or its affiliates.
 */

package com.onefly.united.oauth2.web;

import com.onefly.united.common.user.SecurityUser;
import com.onefly.united.common.user.UserDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;


@RestController
public class PrincipalController {


    /**
     * 获取当前用户信息
     *
     * @param principal 当前用户，不需要前端传递，由系统统一控制
     * @return 返回当前用户{@link User}的详细信息
     */
    @GetMapping("/api/user")
    UserDetail current(Principal principal) {

        UserDetail userDetail = SecurityUser.getUser();
        return userDetail;
    }

}
