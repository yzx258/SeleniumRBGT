package com.example.selenium.util;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-20 12:35:33
 * @description: 描述
 */
@Component
public class CacheMapUtil {

    private static Map<String, String> map = new ConcurrentHashMap<>();

    /**
     * 获取值
     *
     * @param key
     * @return
     */
    public static String getMap(String key) {
        // 获取 - 缓存信息
        return map.get(key);
    }

    /**
     * 设定值
     *
     * @param key
     * @param var
     */
    public static void putMap(String key, String var) {
        map.put(key, var);
    }

    public static void delMap(String key) {
        map.remove(key);
    }

}
