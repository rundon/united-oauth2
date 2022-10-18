package com.onefly.united.oauth2.web;

import com.google.common.collect.ImmutableMap;
import com.onefly.united.common.user.SecurityUser;
import com.onefly.united.common.user.UserDetail;
import com.onefly.united.common.utils.Result;
import com.onefly.united.oauth2.config.MyUserApprovalHandler;
import com.onefly.united.oauth2.service.impl.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

/**
 * @author rundon
 */
@Slf4j
@Controller
public class LoginController {
    private final RequestCache requestCache = new HttpSessionRequestCache();

    /**
     * 您的验证码是{code}。如非本人操作，请忽略本短信
     */
    @Value("${oauth2.login.verify.template:tmp_00000001}")
    private String templateId;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private MyUserApprovalHandler userApprovalHandler;


    @GetMapping({"/login/info", "/api/oauth2/login"})
    public ResponseEntity login(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.ok(ImmutableMap.of("authenticated", false));
        } else {
            return ResponseEntity.ok(authentication);
        }

    }

    @PostMapping(path = {"/login/verifying", "/api/oauth2/login/verifying"})
    public ResponseEntity check(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {
        UserDetail user = SecurityUser.getUser();
        String message = null;
        boolean successful = false;
        if (user != null) {
            successful = true;
        }
//        if (!successful) {
//            Optional<Strategy> strategy = customerAdapter.findStrategy(user.getCode());
//            message = strategy
//                    .map(item -> checkStrategy(item, user, captchaCode, googleCode))
//                    .orElse("");
//            successful = Strings.isNullOrEmpty(message);
//
//        }

        if (!userApprovalHandler.setVerified(authentication, successful)) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Result().error(message));
        }

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        ImmutableMap<String, ? extends Serializable> map = null;
        if (savedRequest != null && savedRequest.getRedirectUrl().indexOf("oauth") > 0) {
            clearAuthenticationAttributes(request);
            // Use the DefaultSavedRequest URL
            map = ImmutableMap.of(
                    "code", 302,
                    "location", savedRequest.getRedirectUrl()
            );
        }
        return ResponseEntity.ok().body(map);

    }

    private final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

}
