package com.duwei.util;

import com.duwei.entity.TA;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Objects;

/**
 * @BelongsProject: Abe_Se
 * @BelongsPackage: com.duwei.util
 * @Author: duwei
 * @Date: 2023/5/10 9:04
 * @Description: 将元素映射到椭圆曲线上的工具类
 */
public class ElementUtils {
    static MessageDigest messageDigest;

    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    public static byte[] sha512(String data) {
        return messageDigest.digest(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 将明文消息映射到指定的域上
     *
     * @param data  原始消息数据
     * @param field 域(Zp、Gt、G0)
     * @return 映射后的元素
     */
    public static Element hashMapToField(String data, Field field) {
        byte[] hash = sha512(data);
        return field.newElementFromHash(hash, 0, hash.length).getImmutable();
    }

    public static Element messageToField(String message, Field field) {
        BigInteger bigInteger = new BigInteger(1, message.getBytes(StandardCharsets.UTF_8));
        return field.newElement(bigInteger).getImmutable();
    }

    public static String fieldToMessage(Element element) {
        byte[] data = element.toBytes();
        int firstNonZeroIndex = findFirstNonZeroIndex(data);
        return new String(data, firstNonZeroIndex, data.length - firstNonZeroIndex, StandardCharsets.UTF_8);
    }

    private static int findFirstNonZeroIndex(byte[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != 0) {
                return i;
            }
        }
        return 0;
    }

    public static Element vectorMul(int[] element1,Element[] element2){
        Objects.requireNonNull(element1);
        Objects.requireNonNull(element2);
        int len = element1.length;
        Field field = element2[0].getField();
        Element result = field.newZeroElement();
        for (int i = 0; i < len; i++) {
          result.add(
            field.newElement(element1[i]).mul(element2[i])
          );
        }
        return result.getImmutable();
    }

    public static Element vectorMul(Element[] element1, Element[] element2) {
        Objects.requireNonNull(element1);
        Objects.requireNonNull(element2);
        int len = element1.length;
        if (len != element2.length) {
            throw new IllegalArgumentException();
        }
        Field field = element1[0].getField();
        Element result = field.newZeroElement();
        for (int i = 0; i < len; i++) {
            result.add(element1[i].mul(element2[i]));
        }
        return result.getImmutable();
    }

    public static void main(String[] args) {
        TA ta = new TA();
        ta.setUp(new HashSet<>());
        Field Zp = ta.getPublicParams().getCurveElementParams().getZ();
        Field GT = ta.getPublicParams().getCurveElementParams().getGT();
        String message = "数据加密 ：关键字加密在收到数据使用";
        System.out.println(message);
        System.out.println(fieldToMessage(messageToField(message,GT)));


    }

}
