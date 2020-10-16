package com.example.selenium.util;

/**
 * @company： 厦门宜车时代信息技术有限公司
 * @copyright： Copyright (C), 2020
 * @author： yucw
 * @date： 2020/10/16 11:16
 * @version： 1.0
 * @description:
 */
public class SleepUtil {
    /**
     * 描述：休眠工具类
     * @param millis
     */
    public static void sleepUtil(long millis){
        try {
            Thread.sleep(millis);
        }catch (Exception e){
            System.out.println("休眠失败");
        }
    }
}
