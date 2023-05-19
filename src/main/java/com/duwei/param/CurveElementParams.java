package com.duwei.param;

import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import lombok.Data;

/**
 * 可直接使用的椭圆曲线参数，但是无法传输
 */
@Data
public class CurveElementParams {
    private Pairing pairing;
    private Field G0;
    private Field Z;
    private Field GT;
    private CurveMetaProperties curveMetaProperties;


    private CurveElementParams() {
    }

    public static CurveElementParams getInstance(CurveMetaProperties curveMetaProperties) {
        CurveElementParams curveElementParams = new CurveElementParams();
        curveElementParams.setCurveMetaProperties(curveMetaProperties);
        PropertiesParametersAdapter propertiesParametersAdapter = new PropertiesParametersAdapter(curveMetaProperties);
        Pairing pairing = PairingFactory.getPairing(propertiesParametersAdapter);
        curveElementParams.setPairing(pairing);
        curveElementParams.setG0(pairing.getG1());
        curveElementParams.setZ(pairing.getZr());
        curveElementParams.setGT(pairing.getGT());
        return curveElementParams;
    }
}
