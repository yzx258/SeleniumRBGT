package com.example.selenium.task;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.example.selenium.bet.BetCopyUtil;
import com.example.selenium.dto.InstructionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: rbgt
 * @author: 俞春旺
 * @create: 2020/06/14 09:03
 * @description： 描述：
 */
@Service
@Slf4j
public class TaskUtil {

    /**
     * 响应码
     */
    private static String R_CODE = "200";
    private static String URL_S = "http://47.106.143.218:8081/instruction/get";

    /**
     * 描述：发起下注
     */
    @Async("myTaskAsyncPool")
    public void getBasketballTournament()
    {
        // 最简单的HTTP请求，可以自动通过header等信息判断编码，不区分HTTP和HTTPS
        String result1= HttpUtil.get(URL_S);
        System.out.println("响应码 ："+ JSON.parseObject(result1).get("code"));
        String code = JSON.parseObject(result1).get("code").toString();
        if(!R_CODE.equals(code)){
            log.info("请求失败，请查询！");
            return;
        }
        // 获取转换参数
        JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(JSON.parseObject(result1).get("data")));
        List<InstructionDTO> instructionDTOS = JSON.parseArray(jsonArray.toJSONString(), InstructionDTO.class);
        BetCopyUtil bet = new BetCopyUtil();
        for(InstructionDTO ins : instructionDTOS){
            // 执行操作
            bet.betSend(ins);
        }
    }
}