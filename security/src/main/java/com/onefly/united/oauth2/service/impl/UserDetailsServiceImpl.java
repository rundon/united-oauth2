package com.onefly.united.oauth2.service.impl;

import com.onefly.united.common.user.UserDetail;
import com.onefly.united.security.dto.SysUserDTO;
import com.onefly.united.security.service.ShiroService;
import com.onefly.united.security.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ShiroService shiroService;

    /**
     * 登录验证
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUserDTO sysUserDTO = sysUserService.getByUsername(username);
        if (sysUserDTO == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        //获取用户对应的部门数据权限
        List<Long> deptIdList = shiroService.getDataScopeList(sysUserDTO.getId());
        Set<String> authoritiesStr = shiroService.loadUserPermissions(sysUserDTO.getSuperAdmin(), sysUserDTO.getId());
        String[] roles = new String[authoritiesStr.size()];
        UserDetail ud = new UserDetail(
                username,
                sysUserDTO.getPassword(),
                true,
                true, true, true,
                AuthorityUtils.createAuthorityList(authoritiesStr.toArray(roles)), sysUserDTO.getId()
                , sysUserDTO.getRealName(), sysUserDTO.getHeadUrl(), sysUserDTO.getGender(), sysUserDTO.getEmail(), sysUserDTO.getMobile(),
                sysUserDTO.getDeptId(), sysUserDTO.getStatus(), sysUserDTO.getSuperAdmin(), deptIdList);
        return ud;
    }
}
