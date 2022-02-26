package com.example.selenium.util;

import org.springframework.stereotype.Component;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @company：厦门宜车时代信息技术有限公司
 * @copyright：Copyright (C), 2020
 * @author：yucw @date：2020/9/4 17:48 @version：1.0
 * @description: 钉钉发送消息
 */
@Component
@Slf4j
public class DingUtil {

    /**
     * 钉钉机器人地址
     */
    private final static String URL =
        "https://oapi.dingtalk.com/robot/send?access_token=55c6eb198bd005a332d0a9349631c65b6be99fbe6e8b16f6d09e4f11114c49ba";

    /**
     * 发送钉钉机器人消息
     * 
     * @param msg
     */
    public void sendMassage(String msg) {
        String text = "{\"msgtype\": \"text\",\"text\": {\"content\": \"信息推送" + msg + "\"}}";
        HttpUtil.post(URL, text);
    }
}
