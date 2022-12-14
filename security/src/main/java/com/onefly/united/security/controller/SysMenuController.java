package com.onefly.united.security.controller;

import com.onefly.united.common.exception.ErrorCode;
import com.onefly.united.common.user.SecurityUser;
import com.onefly.united.common.user.UserDetail;
import com.onefly.united.common.utils.Result;
import com.onefly.united.common.validator.AssertUtils;
import com.onefly.united.common.validator.ValidatorUtils;
import com.onefly.united.common.validator.group.DefaultGroup;
import com.onefly.united.security.dto.SysMenuDTO;
import com.onefly.united.security.enums.MenuTypeEnum;
import com.onefly.united.security.service.ShiroService;
import com.onefly.united.security.service.SysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜单管理
 *
 * @author Mark Rundon
 */
@RestController
@RequestMapping("/api/sys/menu")
@Api(tags = "菜单管理")
public class SysMenuController {
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private ShiroService shiroService;

    @GetMapping("nav")
    @ApiOperation("导航")
    public Result<List<SysMenuDTO>> nav() {
        UserDetail user = SecurityUser.getUser();
        List<SysMenuDTO> list = sysMenuService.getUserMenuList(user, MenuTypeEnum.MENU.value());
        return new Result<List<SysMenuDTO>>().ok(list);
    }

    @GetMapping("permissions")
    @ApiOperation("权限标识")
    public Result<Set<String>> permissions() {
        UserDetail user = SecurityUser.getUser();
        Set<String> set = shiroService.getUserPermissions(user);

        return new Result<Set<String>>().ok(set);
    }

    @GetMapping("list")
    @ApiOperation("列表")
    @ApiImplicitParam(name = "type", value = "菜单类型 0：菜单 1：按钮  null：全部", paramType = "query", dataType = "int")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result<List<SysMenuDTO>> list(Integer type) {
        List<SysMenuDTO> list = sysMenuService.getAllMenuList(type);

        return new Result<List<SysMenuDTO>>().ok(list);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @PreAuthorize("hasAuthority('sys:menu:info')")
    public Result<SysMenuDTO> get(@PathVariable("id") Long id) {
        SysMenuDTO data = sysMenuService.get(id);

        return new Result<SysMenuDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    ////@LogOperation("部署")("保存")
    @PreAuthorize("hasAuthority('sys:menu:save')")
    public Result save(@RequestBody SysMenuDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, DefaultGroup.class);

        sysMenuService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    ////@LogOperation("部署")("修改")
    @PreAuthorize("hasAuthority('sys:menu:update')")
    public Result update(@RequestBody SysMenuDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, DefaultGroup.class);

        sysMenuService.update(dto);

        return new Result();
    }

    @DeleteMapping("{id}")
    @ApiOperation("删除")
    ////@LogOperation("部署")("删除")
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    public Result delete(@PathVariable("id") Long id) {
        //效验数据
        AssertUtils.isNull(id, "id");

        //判断是否有子菜单或按钮
        List<SysMenuDTO> list = sysMenuService.getListPid(id);
        if (list.size() > 0) {
            return new Result().error(ErrorCode.SUB_MENU_EXIST);
        }

        sysMenuService.delete(id);

        return new Result();
    }

    @GetMapping("select")
    @ApiOperation("角色菜单权限")
    @PreAuthorize("hasAuthority('sys:menu:select')")
    public Result<List<SysMenuDTO>> select() {
        UserDetail user = SecurityUser.getUser();
        List<SysMenuDTO> list = sysMenuService.getUserMenuList(user, null);

        return new Result<List<SysMenuDTO>>().ok(list);
    }
}