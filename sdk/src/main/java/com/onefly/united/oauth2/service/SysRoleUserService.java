package com.onefly.united.oauth2.service;

import com.onefly.united.oauth2.web.Oauth2Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysRoleUserService {
    @Autowired
    private Oauth2Client oauth2Client;

    /**
     * 角色ID列表
     *
     * @param userId 用户ID
     */
    public List<Long> getRoleIdList(Long userId) {
        return oauth2Client.getRoleIdList(userId);
    }
}
