package com.onefly.united.oauth2.factory;

import com.onefly.united.common.utils.Result;
import com.onefly.united.oauth2.domain.SysUserDTO;
import com.onefly.united.oauth2.web.Oauth2Client;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RemoteOauth2FallbackFactory implements FallbackFactory<Oauth2Client> {

    @Override
    public Oauth2Client create(Throwable cause) {
        log.error("权限服务调用失败:{}", cause.getMessage());
        return new Oauth2Client() {

            @Override
            public Result<SysUserDTO> loadSysUser(Long id) {
                return new Result().error("获取用户信息失败:" + cause.getMessage());
            }

            @Override
            public Result<List<Long>> getUserIdListByDeptId(List<Long> deptIdList) {
                return new Result().error("查询用户id失败:" + cause.getMessage());
            }

            @Override
            public Result<List<Long>> getRoleIdList(Long userId) {
                return new Result().error("查询用户角色失败:" + cause.getMessage());
            }

            @Override
            public Result<Integer> updateValueByCode(String paramCode, String paramValue) {
                return new Result().error("更新value失败:" + cause.getMessage());
            }

            @Override
            public Result<String> getValueObject(String paramCode) {
                return new Result().error("查询参数编码失败:" + cause.getMessage());
            }
        };
    }
}
