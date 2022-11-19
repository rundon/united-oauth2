package com.onefly.united.security.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 国际化
 * 
 * @author Mark Rundon
 */
@Data
@TableName("sys_language")
public class SysLanguageEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 表名
	 */
	private String tableName;
	/**
	 * 表主键
	 */
	private Long tableId;
	/**
	 * 字段名
	 */
	private String fieldName;
	/**
	 * 字段值
	 */
	private String fieldValue;
	/**
	 * 语言
	 */
	private String language;

}