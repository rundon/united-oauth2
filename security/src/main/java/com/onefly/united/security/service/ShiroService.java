package com.onefly.united.security.service;

import com.onefly.united.common.user.UserDetail;
import com.onefly.united.security.entity.SysUserEntity;

import java.util.List;
import java.util.Set;

/**
 * shiro相关接口
 *
 * @author Mark Rundon
 */
public interface ShiroService {
    /**
     * 获取用户权限列表
     */
    Set<String> getUserPermissions(UserDetail user);

    /**
     * 获取用户权限列表 SysUserDTO
     */
    Set<String> loadUserPermissions(Integer superAdmin, Long id);

    /**
     * 根据用户ID，查询用户
     * @param userId
     */
    SysUserEntity getUser(Long userId);

    /**
     * 获取用户对应的部门数据权限
     * @param userId  用户ID
     * @return 返回部门ID列表
     */
    List<Long> getDataScopeList(Long userId);
}
