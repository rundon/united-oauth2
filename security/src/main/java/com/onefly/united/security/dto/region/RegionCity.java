
package com.onefly.united.security.dto.region;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 市
 *
 * @author Mark Rundon
 */
@ApiModel(value = "市")
@Data
@EqualsAndHashCode(callSuper = true)
public class RegionCity extends Region {
    @ApiModelProperty(value = "区、县列表")
    private List<Region> counties = new ArrayList<>();
}
