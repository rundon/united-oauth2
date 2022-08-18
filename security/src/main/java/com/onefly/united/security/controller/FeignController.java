package com.onefly.united.security.controller;

import com.onefly.united.security.dto.SysUserDTO;
import com.onefly.united.security.service.SysParamsService;
import com.onefly.united.security.service.SysRoleUserService;
import com.onefly.united.security.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sys")
public class FeignController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleUserService sysRoleUserService;

    @Autowired
    private SysParamsService sysParamsService;

    @GetMapping("user")
    public SysUserDTO get(@RequestParam(value = "id", required = true) Long id) {
        SysUserDTO data = sysUserService.get(id);
        //用户角色列表
        List<Long> roleIdList = sysRoleUserService.getRoleIdList(id);
        data.setRoleIdList(roleIdList);
        return data;
    }

    @PostMapping("user/deptId")
    public List<Long> getUserIdListByDeptId(@RequestBody List<Long> deptIdList) {
        return sysUserService.getUserIdListByDeptId(deptIdList);
    }

    @GetMapping("user/role")
    public List<Long> getRoleIdList(@RequestParam(value = "userId", required = true) Long userId) {
        return sysRoleUserService.getRoleIdList(userId);
    }

    @PutMapping("paramsCode")
    public Integer updateValueByCode(@RequestParam(value = "paramCode", required = true) String paramCode, @RequestParam(value = "paramValue", required = true) String paramValue) {
        return sysParamsService.updateValueByCode(paramCode, paramValue);
    }

    @GetMapping("paramsCode")
    public String getValueObject(@RequestParam(value = "paramCode", required = true) String paramCode) {
        return sysParamsService.getValue(paramCode);
    }
}
