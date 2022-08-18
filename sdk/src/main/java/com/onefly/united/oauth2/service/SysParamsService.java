package com.onefly.united.oauth2.service;

import com.alibaba.fastjson.JSON;
import com.onefly.united.common.exception.ErrorCode;
import com.onefly.united.common.exception.RenException;
import com.onefly.united.oauth2.web.Oauth2Client;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysParamsService {

    @Autowired
    private Oauth2Client oauth2Client;

    /**
     * 根据参数编码，获取value的Object对象
     *
     * @param paramCode 参数编码
     * @param clazz     Object对象
     */
    public <T> T getValueObject(String paramCode, Class<T> clazz) {
        String paramValue = oauth2Client.getValueObject(paramCode);
        if (StringUtils.isNotBlank(paramValue)) {
            return JSON.parseObject(paramValue, clazz);
        }
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RenException(ErrorCode.PARAMS_GET_ERROR);
        }
    }

    /**
     * 根据参数编码，更新value
     *
     * @param paramCode  参数编码
     * @param paramValue 参数值
     */
    public int updateValueByCode(String paramCode, String paramValue) {
        return oauth2Client.updateValueByCode(paramCode, paramValue);
    }
}
