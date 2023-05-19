package com.duwei.sample;

import java.util.List;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.sample
 * @Author: duwei
 * @Date: 2023/5/10 11:11
 * @Description: 抽样的接口类
 */
public interface Sample<T> {
    public List<T> next();
    public boolean hasNext();
}
