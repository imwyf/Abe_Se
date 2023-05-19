package com.duwei.entity;

import com.duwei.key.UserPrivateKey;
import com.duwei.param.PublicParams;
import com.duwei.param.TransportablePublicParams;
import lombok.Data;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.entity
 * @Author: duwei
 * @Date: 2023/5/9 17:34
 * @Description: 数据用户
 */
@Data
public abstract class AbstractEntity {
    protected PublicParams publicParams;


    /**
     * 数据使用者构建公共参数
     *
     * @param transportablePublicParams TA传输过来的公共参数
     */
    public void buildPublicParams(TransportablePublicParams transportablePublicParams) {
        this.publicParams = PublicParams.reBuild(transportablePublicParams);
    }

}
