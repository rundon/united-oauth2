/*
 * Copyright (c) 2018. utaka and/or its affiliates.
 */

package com.onefly.united.oauth2.service.impl;

import com.google.common.base.Strings;
import com.onefly.united.common.exception.RenException;
import com.onefly.united.oauth2.captcha.CaptchaStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailMessage;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 验证码处理类
 * <p>
 * 使用步骤如下：
 * 1、先调用 {@link CaptchaService#generate()} 生成 {@code token}
 * 2、然后把根据拿到的{@code token}组织内容，调用发送短信或者邮件接口
 * 2.1、发送短信{@link CaptchaService send(String, String, Map, String, String, long, TimeUnit)}
 * 3、根据用户输入的内容调用 {@link CaptchaService#check(String, String)} ()} 进行校验
 *
 * @author rundon
 */
@Slf4j
@Component
public class CaptchaService {
    /**
     * 验证码发送失败
     */
    public static final String CAPTCHA_SEND_ERROR = "oauth2_captcha_send_error";
    public static final  String REGEX="^\\w+((-\\w+)|(\\.\\w+))*@\\w+(\\.\\w{2,3}){1,3}$";
    private final CaptchaStore store;

    private final int timeoutMinutes;

    @Autowired
    public CaptchaService(
                          CaptchaStore store,
                          @Value("${oauth2.captcha.timeout:5}") int timeoutMinutes) {
        this.store = store;
        this.timeoutMinutes = timeoutMinutes;
    }

    /**
     * 生成6位纯数字的随机数
     */
    public String generate() {
        Random random = new Random(System.currentTimeMillis());
        return Strings.padStart(String.valueOf(random.nextInt(1000000)), 6, '0');
    }


    /**
     * 校验验证码是否正确
     *
     * @param telOrEmail 之前发送验证码的账号
     * @param actual     前端用户输入的验证码
     * @return 返回是否校验正确
     */
    public boolean check(String telOrEmail, String actual) {
        Optional<String> code = store.get(telOrEmail);



        if (Strings.isNullOrEmpty(actual) || !code.isPresent()) {
            return false;
        }
        if (!actual.equals(code.get())) {
            return false;
        }
        store.reset(telOrEmail);
        return true;
    }

    /**
     * 发送短信验证码
     *
     * @param token       调用{@link CaptchaService#generate()}方法生成的随机数
     * @param telOrEmail  发送目标，手机或者邮箱地址
     * @param telOrEmail  手机号码
     * @param params      模板参数
     * @param templateId  已备案的模板编号
     * @param signatureId 已备案的签名
     * @return 是否发送成功
     */
    public String send(String token, String telOrEmail, Map<String, String> params,
                       String templateId, String signatureId, String partitionBy) {
        return this.send(token, telOrEmail, params, templateId, signatureId, partitionBy, "zh", timeoutMinutes, TimeUnit.MINUTES);
    }

    /**
     * 发送短信验证码
     *
     * @param token       调用{@link CaptchaService#generate()}方法生成的随机数
     * @param telOrEmail  发送目标，手机或者邮箱地址
     * @param telOrEmail  手机号码
     * @param params      模板参数
     * @param templateId  已备案的模板编号
     * @param signatureId 已备案的签名
     * @return 是否发送成功
     */
    public String send(String token, String telOrEmail, Map<String, String> params,
                       String templateId, String signatureId, String partitionBy, String language) {
        return this.send(token, telOrEmail, params, templateId, signatureId, partitionBy, language, timeoutMinutes, TimeUnit.MINUTES);
    }

    /**
     * 发送短信验证码
     *
     * @param token       调用{@link CaptchaService#generate()}方法生成的随机数
     * @param telOrEmail  发送目标，手机或者邮箱地址
     * @param params      模板参数
     * @param templateId  已备案的模板编号
     * @param signatureId 已备案的签名
     * @param partitionBy 交易所ID
     * @param language    语言
     * @param timeout     超时时长
     * @param unit        超时时长单位
     * @return 是否发送成功
     */
    public String send(String token, String telOrEmail, Map<String, String> params,
                       String templateId, String signatureId, String partitionBy, String language,
                       long timeout, TimeUnit unit) {
//        try {
//            if ( Pattern.compile(REGEX).matcher(telOrEmail).matches()) {
//                return this.send(
//                        token,
//                        telOrEmail,
//                        MailMessage.of(partitionBy, telOrEmail, templateId, language, params),
//                        timeout, unit
//                );
//            } else {
//                return this.send(
//                        token,
//                        telOrEmail,
//                        SmsMessage.of(telOrEmail, params, templateId, signatureId).partitionBy(partitionBy),
//                        timeout, unit
//                );
//            }
//        } catch (Exception e) {
//            log.error(CAPTCHA_SEND_ERROR, e);
//            throw new RenException(CAPTCHA_SEND_ERROR, e);
//        }
         return null;
    }

    /**
     * 发送短信验证码
     *
     * @param token   调用{@link CaptchaService#generate()}方法生成的随机数
     * @param mobile  发送目标，手机或者邮箱地址
     * @param message 短信
     * @param timeout 超时时长
     * @param unit    超时时长单位
     * @return 是否发送成功
     */
   // private String send(String token, String mobile, SmsMessage message, long timeout, TimeUnit unit) {
//        long timestamp = store.getTimeout(mobile);
//        String result = "ok";
//        if ((System.currentTimeMillis() - timestamp) < unit.toMillis(timeout)) {
//            return result;
//        }
//        result = this.smsSender.send(message);
//        store.save(mobile, token, timeout, unit);
//        return result;

   // }


    /**
     * 发送邮件验证码
     *
     * @param token   调用{@link CaptchaService#generate()}方法生成的随机数
     * @param email   发送目标，手机或者邮箱地址
     * @param message 邮件
     * @param timeout 超时时长
     * @param unit    超时时长单位
     * @return 是否发送成功
     */
    //private String send(String token, String email, MailMessage message, long timeout, TimeUnit unit) {
// /       long timestamp = store.getTimeout(email);
//        String result = "ok";
//        if ((System.currentTimeMillis() - timestamp) < unit.toMillis(timeout)) {
//            return result;
//        }
//        result = this.mailSender.send(message);
//        store.save(email, token, timeout, unit);
//        return result;
    //}
}
