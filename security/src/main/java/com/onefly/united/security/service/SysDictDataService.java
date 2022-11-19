package com.onefly.united.security.service;

import com.onefly.united.common.page.PageData;
import com.onefly.united.common.service.BaseService;
import com.onefly.united.security.dto.SysDictDataDTO;
import com.onefly.united.security.entity.SysDictDataEntity;

import java.util.Map;

/**
 * 数据字典
 *
 * @author Mark Rundon
 */
public interface SysDictDataService extends BaseService<SysDictDataEntity> {

    PageData<SysDictDataDTO> page(Map<String, Object> params);

    SysDictDataDTO get(Long id);

    void save(SysDictDataDTO dto);

    void update(SysDictDataDTO dto);

    void delete(Long[] ids);

}