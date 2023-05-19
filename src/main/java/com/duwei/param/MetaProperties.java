package com.duwei.param;

import java.util.Map;

/**
 * @BelongsProject:
 * @BelongsPackage:
 * @Author: duwei
 * @Date: 2022/11/24 14:55
 * @Description: 元参数接口
 */
public interface MetaProperties {
    /**
     * 获取元参数集合
     */
    Map<String,String> getMetaProperties();
}
