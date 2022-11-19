package com.onefly.united.security.dao;

import com.onefly.united.common.dao.BaseDao;
import com.onefly.united.security.entity.SysLanguageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 国际化
 * 
 * @author Mark Rundon
 */
@Mapper
public interface SysLanguageDao extends BaseDao<SysLanguageEntity> {

    SysLanguageEntity getLanguage(SysLanguageEntity entity);

    void updateLanguage(SysLanguageEntity entity);

    /**
     * 删除国际化
     * @param tableName   表名
     * @param tableId     表主键
     */
    void deleteLanguage(@Param("tableName") String tableName, @Param("tableId") Long tableId);
}