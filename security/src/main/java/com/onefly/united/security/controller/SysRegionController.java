package com.onefly.united.security.controller;

import com.onefly.united.common.exception.ErrorCode;
import com.onefly.united.common.exception.RenException;
import com.onefly.united.common.utils.Result;
import com.onefly.united.common.validator.AssertUtils;
import com.onefly.united.common.validator.ValidatorUtils;
import com.onefly.united.common.validator.group.AddGroup;
import com.onefly.united.common.validator.group.DefaultGroup;
import com.onefly.united.common.validator.group.UpdateGroup;
import com.onefly.united.security.dto.SysRegionDTO;
import com.onefly.united.security.dto.region.RegionProvince;
import com.onefly.united.security.service.SysRegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 行政区域
 * 
 * @author Mark Rundon
 */
@RestController
@RequestMapping("/api/sys/region")
@Api(tags="行政区域")
public class SysRegionController {
	@Autowired
	private SysRegionService sysRegionService;

	@GetMapping("list")
	@ApiOperation("列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "pid", value = "上级ID", paramType = "query", dataType="String")
	})
	@PreAuthorize("hasAuthority('sys:region:list')")
	public Result<List<SysRegionDTO>> list(@RequestParam Map<String, Object> params){
		List<SysRegionDTO> list = sysRegionService.list(params);

		return new Result<List<SysRegionDTO>>().ok(list);
	}

	@GetMapping("tree")
	@ApiOperation("树形数据")
	public Result<List<Map<String, Object>>> tree(){
		List<Map<String, Object>> list = sysRegionService.getTreeList();

		return new Result<List<Map<String, Object>>>().ok(list);
	}

	@GetMapping("{id}")
	@ApiOperation("信息")
	@PreAuthorize("hasAuthority('sys:region:info')")
	public Result<SysRegionDTO> get(@PathVariable("id") Long id){
		SysRegionDTO data = sysRegionService.get(id);

		return new Result<SysRegionDTO>().ok(data);
	}

	@PostMapping
	@ApiOperation("保存")
	////@LogOperation("部署")("保存")
	@PreAuthorize("hasAuthority('sys:region:save')")
	public Result save(@RequestBody SysRegionDTO dto){
		//效验数据
		ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

		sysRegionService.save(dto);

		return new Result();
	}

	@PutMapping
	@ApiOperation("修改")
	////@LogOperation("部署")("修改")
	@PreAuthorize("hasAuthority('sys:region:update')")
	public Result update(@RequestBody SysRegionDTO dto){
		//效验数据
		ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

		sysRegionService.update(dto);

		return new Result();
	}

	@DeleteMapping("{id}")
	@ApiOperation("删除")
	////@LogOperation("部署")("删除")
	@PreAuthorize("hasAuthority('sys:region:delete')")
	public Result delete(@PathVariable("id") Long id){
		//效验数据
		AssertUtils.isNull(id, "id");

		int count = sysRegionService.getCountByPid(id);
		if(count > 0){
			throw new RenException(ErrorCode.REGION_SUB_DELETE_ERROR);
		}

		sysRegionService.delete(id);

		return new Result();
	}

	@GetMapping("region")
	@ApiOperation("地区列表")
	@ApiImplicitParam(name = "threeLevel", value = "是否显示3级   true显示   false不显示", paramType = "query", dataType="boolean")
	public Result<List<RegionProvince>> region(@RequestParam(value = "threeLevel", defaultValue = "true") boolean threeLevel){
		List<RegionProvince> list = sysRegionService.getRegion(threeLevel);

		return new Result<List<RegionProvince>>().ok(list);
	}
	
}