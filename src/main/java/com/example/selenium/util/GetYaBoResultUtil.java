package com.example.selenium.util;

import com.alibaba.fastjson.JSON;
import com.example.selenium.bo.PostTest;
import com.example.selenium.bo.result.ResultBodyBO;
import com.example.selenium.bo.result.ResultBodyDetails;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-26 14:02:25
 * @description: 描述
 */
public class GetYaBoResultUtil {

    public static void main(String[] args) throws IOException {
        PostTest test = new PostTest();
        test.setDateFrom("2022-02-25 14:00:00");
        test.setDateTo("2023-08-25 11:59:59");

        String url = "https://im.1f873fef.com/api/MyBet/GetBetStatement";

        String encoding = "utf-8";

        String body = "";

        // 创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        // 装填参数
        StringEntity s = new StringEntity(JSON.toJSONString(test), "utf-8");
        s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        // 设置参数到请求对象中
        httpPost.setEntity(s);

        // 设置header信息
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("x-token", "9fa6b866d528d71a870bb4105a0efa33");

        // 执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpPost);
        // 获取结果实体
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            // 按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, encoding);
        }
        EntityUtils.consume(entity);
        // 释放链接
        response.close();

        ResultBodyBO resultBodyBO = JSON.parseObject(body, ResultBodyBO.class);
        List<ResultBodyDetails> wl = resultBodyBO.getWl();
        for (ResultBodyDetails item : wl) {
            System.out.println(JSON.toJSONString(item));
        }
    }

    /**
     * 获取 - 结算结果
     *
     * @throws Exception
     */
    public void getMatchResults() throws Exception {

    }
}
