package com.onefly.united.oauth2.common;

import com.alibaba.fastjson.JSON;
import com.onefly.united.common.utils.Result;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class ErrorCode {

    /**
     * 用户名或者密码错误
     */
    public static final String BAD_CREDENTIALS = "bad_credentials";

    /**
     * 找不到对应的对象
     */
    public static final String TEL_AND_EMAIL_IS_EMPTY = "oauth2_tel_and_emal_is_empty";

    /**
     * 找不到对应的对象
     */
    public static final String PARTITION_BY_NOT_FOUND = "oauth2_partition_by_not_found";

    /**
     * 找不到对应的对象
     */
    public static final String ENTITY_NOT_FOUND = "oauth2_entity_not_found";

    /**
     * 验证码发送失败
     */
    public static final String CAPTCHA_SEND_ERROR = "oauth2_captcha_send_error";

    /**
     * 获取验证错误
     */
    public static final String ILLIGAL_CAPTCHA_CODE = "验证码不正确";

    /**
     * 验证码校验失败
     */
    public static final String CAPTCHA_CHECK_ERROR = "oauth2_captcha_check_error";

    /**
     * GOOGLE验证码校验失败
     */
    public static final String CAPTCHA_GOOGLE_CHECK_ERROR = "oauth2_captcha_google_check_error";

    /**
     * 账号不存在
     */
    public static final String ACCOUNT_NOT_FOUND = "oauth2_account_not_found";

    /**
     * 非法域名
     */
    public static final String ILLEGAL_DOMAIN = "oauth2_illegal_domain";

    /**
     * 非法格式
     */
    public static final String ILLEGAL_FORMAT = "oauth2_illegal_format";


    /**
     * 验证失败
     */
    public static final String VALIDATION_FAILS = "oauth2_validation_fails";


    /**
     * 超过限制
     */
    public static final String EXCEED_THE_LIMIT = "oauth2_exceed_the_limit";

    /**
     * 发送错误
     * @param response
     * @param status
     * @param exception
     * @throws IOException
     */
    public static void sendError(HttpServletResponse response, int status, int code, String exception) throws IOException {
        response.setHeader("Content-type", "text/html;charset=UTF-8");  //这句话的意思，是告诉servlet用UTF-8转码，而不是用默认的ISO8859
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(JSON.toJSONString(new Result().error(code, exception)));
    }
}
