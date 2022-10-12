package com.onefly.united.oauth2.service;

import com.onefly.united.common.exception.RenException;
import com.onefly.united.common.utils.Result;
import com.onefly.united.oauth2.domain.SysUserDTO;
import com.onefly.united.oauth2.web.Oauth2Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysUserService {
    @Autowired
    private Oauth2Client oauth2Client;

    public SysUserDTO get(Long id) {
        Result<SysUserDTO> result = oauth2Client.loadSysUser(id);
        if (!result.success()) {
            throw new RenException(result.getMsg());
        }
        return result.getData();
    }

    /**
     * 根据部门ID,查询用户Id列表
     */
    public List<Long> getUserIdListByDeptId(List<Long> deptIdList) {
        Result<List<Long>> result = oauth2Client.getUserIdListByDeptId(deptIdList);
        if (!result.success()) {
            throw new RenException(result.getMsg());
        }
        return result.getData();
    }
}
