package com.face.facetest.utils;

import sun.misc.BASE64Decoder;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @Auther: zs
 * @Date: 2020/4/7 16:54
 * @Description:
 */
public class ImageUtils {

    public static byte[] base64ToByte(String baseStr){
        if (baseStr == null){
            return null;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // 解密
            byte[] b = decoder.decodeBuffer(baseStr);
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            return b;
        } catch (Exception e) {
            return null;
        }
    }
}
