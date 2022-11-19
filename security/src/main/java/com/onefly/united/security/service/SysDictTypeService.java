package com.onefly.united.security.service;

import com.onefly.united.common.page.PageData;
import com.onefly.united.common.service.BaseService;
import com.onefly.united.security.dto.SysDictTypeDTO;
import com.onefly.united.security.entity.DictType;
import com.onefly.united.security.entity.SysDictTypeEntity;

import java.util.List;
import java.util.Map;

/**
 * 数据字典
 *
 * @author Mark Rundon
 */
public interface SysDictTypeService extends BaseService<SysDictTypeEntity> {

    PageData<SysDictTypeDTO> page(Map<String, Object> params);

    SysDictTypeDTO get(Long id);

    void save(SysDictTypeDTO dto);

    void update(SysDictTypeDTO dto);

    void delete(Long[] ids);

    /**
     * 获取所有字典
     */
    List<DictType> getAllList();

}