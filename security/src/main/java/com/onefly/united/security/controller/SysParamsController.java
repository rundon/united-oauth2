package com.onefly.united.security.controller;

import com.onefly.united.common.constant.Constant;
import com.onefly.united.common.page.PageData;
import com.onefly.united.common.utils.ExcelUtils;
import com.onefly.united.common.utils.Result;
import com.onefly.united.common.validator.AssertUtils;
import com.onefly.united.common.validator.ValidatorUtils;
import com.onefly.united.common.validator.group.AddGroup;
import com.onefly.united.common.validator.group.DefaultGroup;
import com.onefly.united.common.validator.group.UpdateGroup;
import com.onefly.united.security.dto.SysParamsDTO;
import com.onefly.united.security.dto.SysParamsExcel;
import com.onefly.united.security.service.SysParamsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 参数管理
 *
 * @author Mark Rundon
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/sys/params")
@Api(tags="参数管理")
public class SysParamsController {
    @Autowired
    private SysParamsService sysParamsService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = "paramCode", value = "参数编码", paramType = "query", dataType="String")
    })
    @PreAuthorize("hasAuthority('sys:params:page')")
    public Result<PageData<SysParamsDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<SysParamsDTO> page = sysParamsService.page(params);

        return new Result<PageData<SysParamsDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @PreAuthorize("hasAuthority('sys:params:info')")
    public Result<SysParamsDTO> get(@PathVariable("id") Long id){
        SysParamsDTO data = sysParamsService.get(id);

        return new Result<SysParamsDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    ////@LogOperation("部署")("保存")
    @PreAuthorize("hasAuthority('sys:params:save')")
    public Result save(@RequestBody SysParamsDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        sysParamsService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    ////@LogOperation("部署")("修改")
    @PreAuthorize("hasAuthority('sys:params:update')")
    public Result update(@RequestBody SysParamsDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        sysParamsService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    ////@LogOperation("部署")("删除")
    @PreAuthorize("hasAuthority('sys:params:delete')")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        sysParamsService.delete(ids);

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    ////@LogOperation("部署")("导出")
    @PreAuthorize("hasAuthority('sys:params:export')")
    @ApiImplicitParam(name = "paramCode", value = "参数编码", paramType = "query", dataType="String")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<SysParamsDTO> list = sysParamsService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, SysParamsExcel.class);
    }

}