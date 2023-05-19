package com.duwei.param;

import it.unisa.dia.gas.plaf.jpbc.pairing.parameters.PropertiesParameters;

/**
 * @Author: duwei
 * @Date: 2022/11/24 15:07
 * @Description: 各种参数的适配器，继承自JPBC的PropertiesParameters，用来作为PairingFactory的传入参数
 */
public class PropertiesParametersAdapter extends PropertiesParameters {
    public PropertiesParametersAdapter(MetaProperties metaProperties) {
        this.parameters.putAll(metaProperties.getMetaProperties());
    }

    public PropertiesParametersAdapter() {
    }
}
