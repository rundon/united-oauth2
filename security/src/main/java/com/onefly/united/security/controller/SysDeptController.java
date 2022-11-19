package com.onefly.united.security.controller;

import com.onefly.united.common.utils.Result;
import com.onefly.united.common.validator.AssertUtils;
import com.onefly.united.common.validator.ValidatorUtils;
import com.onefly.united.common.validator.group.AddGroup;
import com.onefly.united.common.validator.group.DefaultGroup;
import com.onefly.united.common.validator.group.UpdateGroup;
import com.onefly.united.security.dto.SysDeptDTO;
import com.onefly.united.security.service.SysDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;

/**
 * 部门管理
 * 
 * @author Mark Rundon
 */
@RestController
@RequestMapping("/api/sys/dept")
@Api(tags="部门管理")
public class SysDeptController {
	@Autowired
	private SysDeptService sysDeptService;

	@GetMapping("list")
	@ApiOperation("列表")
	@PreAuthorize("hasAuthority('sys:dept:list')")
	public Result<List<SysDeptDTO>> list(){
		List<SysDeptDTO> list = sysDeptService.list(new HashMap<>(1));

		return new Result<List<SysDeptDTO>>().ok(list);
	}

	@GetMapping("{id}")
	@ApiOperation("信息")
	@PreAuthorize("hasAuthority('sys:dept:info')")
	public Result<SysDeptDTO> get(@PathVariable("id") Long id){
		SysDeptDTO data = sysDeptService.get(id);

		return new Result<SysDeptDTO>().ok(data);
	}

	@PostMapping
	@ApiOperation("保存")
	////@LogOperation("部署")("保存")
	@PreAuthorize("hasAuthority('sys:dept:save')")
	public Result save(@RequestBody SysDeptDTO dto){
		//效验数据
		ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

		sysDeptService.save(dto);

		return new Result();
	}

	@PutMapping
	@ApiOperation("修改")
	////@LogOperation("部署")("修改")
	@PreAuthorize("hasAuthority('sys:dept:update')")
	public Result update(@RequestBody SysDeptDTO dto){
		//效验数据
		ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

		sysDeptService.update(dto);

		return new Result();
	}

	@DeleteMapping("{id}")
	@ApiOperation("删除")
	////@LogOperation("部署")("删除")
	@PreAuthorize("hasAuthority('sys:dept:delete')")
	public Result delete(@PathVariable("id") Long id){
		//效验数据
		AssertUtils.isNull(id, "id");

		sysDeptService.delete(id);

		return new Result();
	}
	
}