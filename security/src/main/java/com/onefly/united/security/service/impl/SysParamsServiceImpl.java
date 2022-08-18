/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.onefly.united.security.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.onefly.united.common.constant.Constant;
import com.onefly.united.common.exception.ErrorCode;
import com.onefly.united.common.exception.RenException;
import com.onefly.united.common.page.PageData;
import com.onefly.united.common.service.impl.BaseServiceImpl;
import com.onefly.united.common.utils.ConvertUtils;
import com.onefly.united.security.constant.CacheConstant;
import com.onefly.united.security.dao.SysParamsDao;
import com.onefly.united.security.dto.SysParamsDTO;
import com.onefly.united.security.entity.SysParamsEntity;
import com.onefly.united.security.service.SysParamsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 参数管理
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@Service
@CacheConfig(cacheNames = CacheConstant.CACHE_PARAMS)
public class SysParamsServiceImpl extends BaseServiceImpl<SysParamsDao, SysParamsEntity> implements SysParamsService {

    @Override
    public PageData<SysParamsDTO> page(Map<String, Object> params) {
        IPage<SysParamsEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                getWrapper(params)
        );

        return getPageData(page, SysParamsDTO.class);
    }

    @Override
    public List<SysParamsDTO> list(Map<String, Object> params) {
        List<SysParamsEntity> entityList = baseDao.selectList(getWrapper(params));

        return ConvertUtils.sourceToTarget(entityList, SysParamsDTO.class);
    }

    private QueryWrapper<SysParamsEntity> getWrapper(Map<String, Object> params) {
        String paramCode = (String) params.get("paramCode");

        QueryWrapper<SysParamsEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("param_type", 1);
        wrapper.like(StringUtils.isNotBlank(paramCode), "param_code", paramCode);

        return wrapper;
    }

    @Override
    public SysParamsDTO get(Long id) {
        SysParamsEntity entity = baseDao.selectById(id);

        return ConvertUtils.sourceToTarget(entity, SysParamsDTO.class);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void save(SysParamsDTO dto) {
        SysParamsEntity entity = ConvertUtils.sourceToTarget(dto, SysParamsEntity.class);
        insert(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(SysParamsDTO dto) {
        SysParamsEntity entity = ConvertUtils.sourceToTarget(dto, SysParamsEntity.class);
        updateById(entity);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long[] ids) {
        //删除Redis数据
        List<String> paramCodeList = baseDao.getParamCodeList(ids);
        String[] paramCodes = paramCodeList.toArray(new String[paramCodeList.size()]);
        //删除
        deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    @Cacheable(key = "'sysParams:'+ #paramCode")
    public String getValue(String paramCode) {
        String paramValue = baseDao.getValueByCode(paramCode);
        return paramValue;
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public int updateValueByCode(String paramCode, String paramValue) {
        int count = baseDao.updateValueByCode(paramCode, paramValue);
        return count;
    }

}