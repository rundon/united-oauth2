package com.onefly.united.security.service;

import com.onefly.united.common.service.BaseService;
import com.onefly.united.security.dto.SysRegionDTO;
import com.onefly.united.security.dto.region.RegionProvince;
import com.onefly.united.security.entity.SysRegionEntity;

import java.util.List;
import java.util.Map;

/**
 * 行政区域
 * 
 * @author Mark Rundon
 */
public interface SysRegionService extends BaseService<SysRegionEntity> {

	List<SysRegionDTO> list(Map<String, Object> params);

	List<Map<String, Object>> getTreeList();

	SysRegionDTO get(Long id);

	void save(SysRegionDTO dto);

	void update(SysRegionDTO dto);

	void delete(Long id);

	int getCountByPid(Long pid);

	List<RegionProvince> getRegion(boolean threeLevel);
}