package com.onefly.united.oauth2.web;

import com.onefly.united.oauth2.domain.SysUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "UNITED.SSOPANDA")
public interface Oauth2Client {

    @GetMapping("/api/sys/user")
    SysUserDTO loadSysUser(@RequestParam(value = "id", required = true) Long id);

    @PostMapping("/api/sys/user/deptId")
    List<Long> getUserIdListByDeptId(@RequestBody List<Long> deptIdList);

    @GetMapping("/api/sys/user/role")
    List<Long> getRoleIdList(@RequestParam(value = "userId", required = true) Long userId);

    @PutMapping("/api/sys/paramsCode")
    Integer updateValueByCode(@RequestParam(value = "paramCode", required = true) String paramCode, @RequestParam(value = "paramValue", required = true) String paramValue);

    @GetMapping("/api/sys/paramsCode")
    String getValueObject(@RequestParam(value = "paramCode", required = true) String paramCode);
}
