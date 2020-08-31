package com.example.selenium.hutool;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;

import java.util.HashMap;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2020-08-31 22:00:29
 * @description: 工具类测试
 */
public class HuToolTest {

    public static void main(String[] args) {
        // 最简单的HTTP请求，可以自动通过header等信息判断编码，不区分HTTP和HTTPS
        String result1= HttpUtil.get("https://www.baidu.com");
        System.out.println(result1);

        // 当无法识别页面编码的时候，可以自定义请求页面的编码
        String result2= HttpUtil.get("https://www.baidu.com", CharsetUtil.CHARSET_UTF_8);
        System.out.println(result1);
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("city", "北京");
        String result3= HttpUtil.get("https://www.baidu.com", paramMap);
        System.out.println(result1);
    }
}
