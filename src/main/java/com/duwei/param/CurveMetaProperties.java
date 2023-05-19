package com.duwei.param;

import lombok.Data;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Data
public class CurveMetaProperties implements MetaProperties, StorableProperties, Serializable {

    @Override
    public String toString() {
        return "CurveMetaProperties{" +
                "type='" + type + '\'' +
                ", q='" + q + '\'' +
                ", h='" + h + '\'' +
                ", r='" + r + '\'' +
                ", exp2='" + exp2 + '\'' +
                ", exp1='" + exp1 + '\'' +
                ", sign1='" + sign1 + '\'' +
                ", sign0='" + sign0 + '\'' +
                '}';
    }

    private static final long serialVersionUID = 1545394302608235284L;
    private String type;
    private String q;
    private String h;
    private String r;
    private String exp2;
    private String exp1;
    private String sign1;
    private String sign0;
    private Map<String, String> propertiesMap;

    private interface Constant {
        String TYPE = "type";
        String Q = "q";
        String H = "h";
        String R = "r";
        String EXP1 = "exp1";
        String EXP2 = "exp2";
        String SIGN1 = "sign1";
        String SIGN0 = "sign0";
    }


    /**
     * 默认的构造方法，使用默认配置的椭圆曲线元参数信息
     */
    public CurveMetaProperties() {
        propertiesMap = new HashMap<>();
        this.type = "a";
        propertiesMap.put(Constant.TYPE, type);

        this.q = "3930575219365354715075847558061268890629065603808596099518560241110293759667298882072364788007205810519048743034911485723616073805468353541073582239278059";
        propertiesMap.put(Constant.Q, q);

        this.r = "730750818665451459101842416367364881864821047297";
        propertiesMap.put(Constant.R, r);

        this.h = "5378817401181496627783782435620643317678636580742049708902045719111678061424728152866628683673837233791980";
        propertiesMap.put(Constant.H, h);

        this.exp1 = "63";
        propertiesMap.put(Constant.EXP1, exp1);

        this.exp2 = "159";
        propertiesMap.put(Constant.EXP2, exp2);

        this.sign0 = "1";
        propertiesMap.put(Constant.SIGN0, sign0);

        this.sign1 = "1";
        propertiesMap.put(Constant.SIGN1, sign1);
    }

    public CurveMetaProperties(String propertiesFilePath) {
        propertiesMap = new HashMap<>();
        try (InputStream inputStream = new FileInputStream(propertiesFilePath)) {
            Properties properties = new Properties();
            properties.load(inputStream);

            this.type = properties.getProperty(Constant.TYPE);
            propertiesMap.put(Constant.TYPE, this.type);

            this.q = properties.getProperty(Constant.Q);
            propertiesMap.put(Constant.Q, this.q);

            this.h = properties.getProperty(Constant.H);
            propertiesMap.put(Constant.H, this.h);

            this.r = properties.getProperty(Constant.R);
            propertiesMap.put(Constant.R, this.r);

            this.sign0 = properties.getProperty(Constant.SIGN0);
            propertiesMap.put(Constant.SIGN0, this.sign0);

            this.sign1 = properties.getProperty(Constant.SIGN1);
            propertiesMap.put(Constant.SIGN1, this.sign1);

            this.exp1 = properties.getProperty(Constant.EXP1);
            propertiesMap.put(Constant.EXP1, this.exp1);

            this.exp2 = properties.getProperty(Constant.EXP2);
            propertiesMap.put(Constant.EXP2, this.exp2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> getMetaProperties() {
        return this.propertiesMap;
    }

    @Override
    public void store(String filePath, boolean append) {
        try (OutputStream outputStream = new FileOutputStream(filePath, append)) {
            Properties properties = new Properties();
            properties.putAll(this.propertiesMap);
            properties.store(outputStream, "store meta properties");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
