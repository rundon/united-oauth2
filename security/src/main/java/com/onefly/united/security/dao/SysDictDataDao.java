package com.onefly.united.security.dao;

import com.onefly.united.common.dao.BaseDao;
import com.onefly.united.security.entity.DictData;
import com.onefly.united.security.entity.SysDictDataEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 字典数据
 *
 * @author Mark Rundon
 */
@Mapper
public interface SysDictDataDao extends BaseDao<SysDictDataEntity> {

    /**
     * 字典数据列表
     */
    List<DictData> getDictDataList();
}
