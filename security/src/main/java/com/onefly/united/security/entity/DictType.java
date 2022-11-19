
package com.onefly.united.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 字典类型
 *
 * @author Mark Rundon
 */
@Data
public class DictType {
    @JsonIgnore
    private Long id;
    private String dictType;
    private List<DictData> dataList = new ArrayList<>();
}
