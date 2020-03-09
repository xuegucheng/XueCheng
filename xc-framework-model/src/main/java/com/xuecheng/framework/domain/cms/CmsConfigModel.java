package com.xuecheng.framework.domain.cms;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

/**
 * Created by admin on 2018/2/6.
 */
@Data
@ToString
public class CmsConfigModel {
    private String key;//主键
    private String name;//项目名称
    private String url;//项目url
    private Map mapValue;//项目复杂值
    private String value;//项目简单值

}
