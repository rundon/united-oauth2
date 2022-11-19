package com.onefly.united.security.controller;

import com.onefly.united.common.constant.Constant;
import com.onefly.united.common.exception.ErrorCode;
import com.onefly.united.common.page.PageData;
import com.onefly.united.common.user.SecurityUser;
import com.onefly.united.common.user.UserDetail;
import com.onefly.united.common.utils.ConvertUtils;
import com.onefly.united.common.utils.ExcelUtils;
import com.onefly.united.common.utils.Result;
import com.onefly.united.common.validator.AssertUtils;
import com.onefly.united.common.validator.ValidatorUtils;
import com.onefly.united.common.validator.group.AddGroup;
import com.onefly.united.common.validator.group.DefaultGroup;
import com.onefly.united.common.validator.group.UpdateGroup;
import com.onefly.united.security.dto.PasswordDTO;
import com.onefly.united.security.dto.SysUserDTO;
import com.onefly.united.security.dto.SysUserExcel;
import com.onefly.united.security.service.SysRoleUserService;
import com.onefly.united.security.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 用户管理
 *
 * @author Mark Rundon
 */
@RestController
@RequestMapping("/api/sys/user")
@Api(tags = "用户管理")
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleUserService sysRoleUserService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "gender", value = "性别", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "deptId", value = "部门ID", paramType = "query", dataType = "String")
    })

    @PreAuthorize("hasAuthority('sys:user:page')")
    public Result<PageData<SysUserDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<SysUserDTO> page = sysUserService.page(params);

        return new Result<PageData<SysUserDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @PreAuthorize("hasAuthority('sys:user:info')")
    public Result<SysUserDTO> get(@PathVariable("id") Long id) {
        SysUserDTO data = sysUserService.get(id);

        //用户角色列表
        List<Long> roleIdList = sysRoleUserService.getRoleIdList(id);
        data.setRoleIdList(roleIdList);

        return new Result<SysUserDTO>().ok(data);
    }

    @GetMapping("info")
    @ApiOperation("登录用户信息")
    public Result<SysUserDTO> info() {
        SysUserDTO data = ConvertUtils.sourceToTarget(SecurityUser.getUser(), SysUserDTO.class);
        return new Result<SysUserDTO>().ok(data);
    }

    @PutMapping("password")
    @ApiOperation("修改密码")
    ////@LogOperation("部署")("修改密码")
    public Result password(@RequestBody PasswordDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto);

        UserDetail user = SecurityUser.getUser();
        //原密码不正确
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return new Result().error(ErrorCode.PASSWORD_ERROR);
        }

        sysUserService.updatePassword(user.getId(), dto.getNewPassword());

        return new Result();
    }

    @PostMapping
    @ApiOperation("保存")
    ////@LogOperation("部署")("保存")
    @PreAuthorize("hasAuthority('sys:user:save')")
    public Result save(@RequestBody SysUserDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        sysUserService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    ////@LogOperation("部署")("修改")
    @PreAuthorize("hasAuthority('sys:user:update')")
    public Result update(@RequestBody SysUserDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        sysUserService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    ////@LogOperation("部署")("删除")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        sysUserService.deleteBatchIds(Arrays.asList(ids));

        return new Result();
    }

    @GetMapping("export")
    @ApiOperation("导出")
    ////@LogOperation("部署")("导出")
    @PreAuthorize("hasAuthority('sys:user:export')")
    @ApiImplicitParam(name = "username", value = "用户名", paramType = "query", dataType = "String")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<SysUserDTO> list = sysUserService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, SysUserExcel.class);
    }
}