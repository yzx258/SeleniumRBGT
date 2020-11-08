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
        obj.put("typeid", "1");
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
        obj.put("typeid", "1");
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
