package com.example.selenium.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @project_name: 7657
 * @package_name: com.example.demo.test
 * @name: BeytTest
 * @author: 俞春旺
 * @date: 2020/11/8 10:32
 * @day_name_full: 星期日
 * @remark: 无
 **/
import com.alibaba.fastjson.JSONObject;

/**
 * © 2019 TUJIAN, Inc.TermsPrivacy
 *
 * @author: 俞春旺
 * @date: 2019/8/20 9:39
 */

public class ImageRecognitionUtil {

    /**
     * 用户名
     */
    public final static String USER_NAME = "RBGT_0922";
    /**
     * 密码
     */
    public final static String USER_POW = "15659512376";

    /**
     * 请求图鉴第三方接口
     *
     * @param url
     * @param obj
     * @return
     * @throws IOException
     */
    private static String httpRequestData(String url, JSONObject obj) throws IOException {
        // TODO Auto-generated method stub
        URL u;
        HttpURLConnection con;
        DataOutputStream osw;
        StringBuffer buffer = new StringBuffer();
        u = new URL(url);
        con = (HttpURLConnection) u.openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setUseCaches(false);
        con.setInstanceFollowRedirects(true);
        con.setRequestProperty("Content-Type", "application/json");
        osw = new DataOutputStream(con.getOutputStream());
        osw.writeBytes(obj.toString());
        osw.flush();
        osw.close();
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        String temp;
        while ((temp = br.readLine()) != null) {
            buffer.append(temp);
            buffer.append('\n');
        }
        return buffer.toString();
    }

    /**
     * 描述：调用验证码识别接口
     *
     * @param image
     * @return
     */
    public static String imageRecognition(String image) {
        //图片转换过的base64编码
        JSONObject obj = new JSONObject();
        obj.put("username", USER_NAME);
        obj.put("password", USER_POW);
        // typeId为可选参数 根据文档填写说明填写 1:纯数字 2:纯英文
        obj.put("typeid", "3");
        // 响应信息
        String result = "";
        obj.put("image", image);
        try {
            String url = "http://api.ttshitu.com/base64";
            String ret = httpRequestData(url, obj);
            JSONObject jsonObject = JSONObject.parseObject(ret);
            if (jsonObject.getBoolean("success")) {
                result = jsonObject.getJSONObject("data").getString("result");
                System.out.println("识别成功结果为:" + result);
            } else {
                System.out.println("识别失败原因为:" + jsonObject.getString("message"));
                result = "ERROR";
            }
        } catch (Exception e) {
            System.out.println("识别失败异常:" + e.getMessage());
            result = "ERROR";
        }
        return result;
    }

    public static void main(String[] args) {
        //你的用户名
        String username = "RBGT_0922";
        //你的密码
        String password = "15659512376";
        //图片转换过的base64编码
        String image = "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";
        JSONObject obj = new JSONObject();
        obj.put("username", username);
        obj.put("password", password);
        //typeid为可选参数 根据文档填写说明填写 1:纯数字 2:纯英文
        obj.put("typeid", "3");
        //备注字段: 可以不写
        String remark = "输出计算结果";
        obj.put("image", image);
        try {
            String url = "http://api.ttshitu.com/base64";
            String ret = httpRequestData(url, obj);
            JSONObject jsonObject = JSONObject.parseObject(ret);
            if (jsonObject.getBoolean("success")) {
                String result = jsonObject.getJSONObject("data").getString("result");
                System.out.println("识别成功结果为:" + result);
            } else {
                System.out.println("识别失败原因为:" + jsonObject.getString("message"));
            }
        } catch (Exception e) {
            System.out.println("识别失败异常:");
        }
    }
}
