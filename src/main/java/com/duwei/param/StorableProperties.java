package com.duwei.param;

/**
 * @Author: duwei
 * @Date: 2022/11/24 14:52
 * @Description: 可存储的参数接口
 */
public interface StorableProperties {
    /**
     * 将参数存储到文件
     *
     * @param filePath 文件路径
     * @param append   是否以追加方式写入文件
     */
    void store(String filePath, boolean append);
}
