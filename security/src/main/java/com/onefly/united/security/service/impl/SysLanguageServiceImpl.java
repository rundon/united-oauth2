package com.onefly.united.security.service.impl;

import com.onefly.united.common.service.impl.BaseServiceImpl;
import com.onefly.united.security.dao.SysLanguageDao;
import com.onefly.united.security.entity.SysLanguageEntity;
import com.onefly.united.security.service.SysLanguageService;
import org.springframework.stereotype.Service;

/**
 * 国际化
 *
 * @author Mark Rundon
 */
@Service
public class SysLanguageServiceImpl extends BaseServiceImpl<SysLanguageDao, SysLanguageEntity> implements SysLanguageService {

    @Override
    public void saveOrUpdate(String tableName, Long tableId, String fieldName, String fieldValue, String language) {
        SysLanguageEntity entity = new SysLanguageEntity();
        entity.setTableName(tableName);
        entity.setTableId(tableId);
        entity.setFieldName(fieldName);
        entity.setFieldValue(fieldValue);
        entity.setLanguage(language);

        //判断是否有数据
        if(baseDao.getLanguage(entity) == null){
            baseDao.insert(entity);
        }else {
            baseDao.updateLanguage(entity);
        }
    }

    @Override
    public void deleteLanguage(String tableName, Long tableId) {
        baseDao.deleteLanguage(tableName, tableId);
    }
}