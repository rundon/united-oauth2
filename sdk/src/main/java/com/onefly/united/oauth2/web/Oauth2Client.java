package com.onefly.united.oauth2.web;

import com.onefly.united.common.utils.Result;
import com.onefly.united.oauth2.domain.SysUserDTO;
import com.onefly.united.oauth2.factory.RemoteOauth2FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "UNITED.SSOPANDA", fallbackFactory = RemoteOauth2FallbackFactory.class)
public interface Oauth2Client {

    @GetMapping("/api/sys/user")
    Result<SysUserDTO> loadSysUser(@RequestParam(value = "id", required = true) Long id);

    @PostMapping("/api/sys/user/deptId")
    Result<List<Long>> getUserIdListByDeptId(@RequestBody List<Long> deptIdList);

    @GetMapping("/api/sys/user/role")
    Result<List<Long>> getRoleIdList(@RequestParam(value = "userId", required = true) Long userId);

    @PutMapping("/api/sys/paramsCode")
    Result<Integer> updateValueByCode(@RequestParam(value = "paramCode", required = true) String paramCode,
                                      @RequestParam(value = "paramValue", required = true) String paramValue);

    @GetMapping("/api/sys/paramsCode")
    Result<String> getValueObject(@RequestParam(value = "paramCode", required = true) String paramCode);
}
