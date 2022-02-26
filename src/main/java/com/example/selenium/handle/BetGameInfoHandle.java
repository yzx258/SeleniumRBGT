package com.example.selenium.handle;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.example.selenium.bo.PostTest;
import com.example.selenium.bo.result.ResultBodyBO;
import com.example.selenium.bo.result.ResultBodyDetails;
import com.example.selenium.entity.BetGameInfo;
import com.example.selenium.mapper.BetGameInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-26 23:11:50
 * @description: 描述
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BetGameInfoHandle {

    private final BetGameInfoMapper betGameInfoMapper;

    /**
     * 获取 - 赛事结果
     *
     * @throws IOException
     */
    public void getGameInfoResult() throws IOException {
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, -1);
        c.add(Calendar.HOUR_OF_DAY, 7);
        // 这是昨天
        Date yesterday = c.getTime();
        String format = DateUtil.format(yesterday, "yyyy-MM-dd HH:mm:ss");

        PostTest test = new PostTest();
        test.setDateFrom(format);
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

        // 操作 - 数据
        BetGameInfo info;
        ResultBodyBO resultBodyBO = JSON.parseObject(body, ResultBodyBO.class);
        System.out.println(JSON.toJSONString(resultBodyBO));
        List<ResultBodyDetails> wl = resultBodyBO.getWl();
        for (ResultBodyDetails item : wl) {
            info = new BetGameInfo();
            info.setCompetitionName(item.getWil().get(0).getCn());
            info.setHomeTeamName(item.getWil().get(0).getHtn());
            info.setHomeTeamScore(item.getWil().get(0).getFths());
            info.setAwayTeamName(item.getWil().get(0).getAtn());
            info.setAwayTeamScore(item.getWil().get(0).getFtas());
            info.setCompetitionResult(item.getOc());
            info.setIsSettlement(1);
            info.setCompetitionDividendAmount(item.getWil().get(0).getO());
            info.setCompetitionType(1);
            info.setWhichSection(item.getWil().get(0).getBtn().trim().replaceAll(" 单/双", ""));
            betGameInfoMapper.insert(info);
        }
    }
}
