package com.onefly.united.security.service;


import com.onefly.united.common.page.PageData;
import com.onefly.united.common.service.BaseService;
import com.onefly.united.security.dto.SysRoleDTO;
import com.onefly.united.security.entity.SysRoleEntity;

import java.util.List;
import java.util.Map;


/**
 * 角色
 * 
 * @author Mark Rundon
 */
public interface SysRoleService extends BaseService<SysRoleEntity> {

	PageData<SysRoleDTO> page(Map<String, Object> params);

	List<SysRoleDTO> list(Map<String, Object> params);

	SysRoleDTO get(Long id);

	void save(SysRoleDTO dto);

	void update(SysRoleDTO dto);

	void delete(Long[] ids);

}
