package com.onefly.united.oauth2.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户管理
 *
 * @author Mark Rundon
 * @since 1.0.0
 */
@Data

public class SysUserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	private Long id;

	private String username;


	private String password;

	private String realName;

	private String headUrl;

	private Integer gender;

	private String email;

	private String mobile;


	private Long deptId;


	private Integer status;

	private Date createDate;


	private Integer superAdmin;

	private List<Long> roleIdList;

	private String deptName;

}