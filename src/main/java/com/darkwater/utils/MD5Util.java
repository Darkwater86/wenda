package com.darkwater.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lenovo1 on 2017/2/4.
 */
public class MD5Util {
    private static final Logger logger= LoggerFactory.getLogger(MD5Util.class);

    public static String MD5(String key){
        char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

        try {
            byte[] btInput = key.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();

            int j = md.length;
            char str[]  = new char[ j * 2 ];
            int k = 0;
            for (int i = 0;i < j;i++){
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            logger.error("生成MD失败",e);
            return null;
        }

    }
}
