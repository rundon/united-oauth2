/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.onefly.united.security.service.impl;

import com.onefly.united.common.user.UserDetail;
import com.onefly.united.security.dao.SysMenuDao;
import com.onefly.united.security.dao.SysRoleDataScopeDao;
import com.onefly.united.security.dao.SysUserDao;
import com.onefly.united.security.entity.SysUserEntity;
import com.onefly.united.security.enums.SuperAdminEnum;
import com.onefly.united.security.service.ShiroService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ShiroServiceImpl implements ShiroService {
    @Autowired
    private SysMenuDao sysMenuDao;
    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private SysRoleDataScopeDao sysRoleDataScopeDao;

    @Override
    public Set<String> getUserPermissions(UserDetail user) {
        return loadUserPermissions(user.getSuperAdmin(), user.getId());
    }

    @Override
    public Set<String> loadUserPermissions(Integer superAdmin, Long id) {
        //系统管理员，拥有最高权限
        List<String> permissionsList;
        if (superAdmin == SuperAdminEnum.YES.value()) {
            permissionsList = sysMenuDao.getPermissionsList();
        } else {
            permissionsList = sysMenuDao.getUserPermissionsList(id);
        }

        //用户权限列表
        Set<String> permsSet = new HashSet<>();
        for (String permissions : permissionsList) {
            if (StringUtils.isBlank(permissions)) {
                continue;
            }
            permsSet.addAll(Arrays.asList(permissions.trim().split(",")));
        }

        return permsSet;
    }

    @Override
    public SysUserEntity getUser(Long userId) {
        return sysUserDao.selectById(userId);
    }

    @Override
    public List<Long> getDataScopeList(Long userId) {
        return sysRoleDataScopeDao.getDataScopeList(userId);
    }
}