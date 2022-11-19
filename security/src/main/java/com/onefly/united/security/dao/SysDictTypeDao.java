package com.onefly.united.security.dao;

import com.onefly.united.common.dao.BaseDao;
import com.onefly.united.security.entity.DictType;
import com.onefly.united.security.entity.SysDictTypeEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 字典类型
 *
 * @author Mark Rundon
 */
@Mapper
public interface SysDictTypeDao extends BaseDao<SysDictTypeEntity> {

    /**
     * 字典类型列表
     */
    List<DictType> getDictTypeList();

}
