package com.onefly.united.oauth2.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ICaptchaProvider {
    String PARAMETER_NAME = "_captcha_version";

    /**
     * 是否支持当前的请求验证
     *
     * @param request 请求对象
     * @return 返回是否支持该请求
     */
    boolean support(HttpServletRequest request);

    /**
     * 生成验证码
     *
     * @param request  当前请求
     * @param response 当前响应
     * @throws IOException
     */
    void generate(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 检查验证码
     *
     * @param request  当前请求
     * @param response 当前响应
     * @return 返回验证是否成功
     * @throws IOException
     */
    boolean check(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
