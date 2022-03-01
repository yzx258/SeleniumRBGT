package com.example.selenium.util;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.example.selenium.bo.CompleteSettlementBO;
import com.example.selenium.bo.WaitSettlementBO;
import com.example.selenium.bo.result.ResultBodyBO;
import com.example.selenium.handle.BetGameAccountInfoHandle;
import com.example.selenium.util.img.HttpUtil;

import cn.hutool.core.date.DateUtil;
import lombok.RequiredArgsConstructor;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-26 14:02:25
 * @description: 描述
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GetYaBoResultUtil {

    private final BetGameAccountInfoHandle betGameAccountInfoHandle;

    /***
     * waitSettlement
     *
     * @return java.lang.String
     * @author yucw
     * @date 2022-02-28 11:05
     */
    public ResultBodyBO waitSettlement() throws IOException {
        // 获取 - 参数
        String token = betGameAccountInfoHandle.getToken();
        String waitSettlementUrl = betGameAccountInfoHandle.getUrl("waitSettlementUrl");

        WaitSettlementBO waitSettlement = new WaitSettlementBO();
        int[] arr = new int[] {2};
        waitSettlement.setBetConfirmationStatusList(arr);
        String param = JSON.toJSONString(waitSettlement);
        System.out.println("param:" + param);
        String result = HttpUtil.YaBoPost(waitSettlementUrl, token, param);
        if (null != result) {
            ResultBodyBO res = JSON.parseObject(result, ResultBodyBO.class);
            if (null != res && res.getStatusCode() == 100) {
                return res;
            }
        }
        return null;
    }

    /***
     * completeSettlement
     *
     * @return
     * @author yucw
     * @date 2022-02-28 10:42
     */
    public ResultBodyBO completeSettlement() throws IOException {
        // 获取 - 参数
        String token = betGameAccountInfoHandle.getToken();
        String completeSettlementUrl = betGameAccountInfoHandle.getUrl("completeSettlementUrl");

        // 获取 - 当前时间
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DAY_OF_MONTH, -1);
        c.add(Calendar.HOUR_OF_DAY, 6);
        // 这是昨天
        Date yesterday = c.getTime();
        String format = DateUtil.format(yesterday, "yyyy-MM-dd HH:mm:ss");

        CompleteSettlementBO param = new CompleteSettlementBO();
        param.setDateFrom(format);
        param.setDateTo("2023-08-25 11:59:59");

        String result = HttpUtil.YaBoPost(completeSettlementUrl, token, JSON.toJSONString(param));
        if (null != result) {
            ResultBodyBO res = JSON.parseObject(result, ResultBodyBO.class);
            if (null != res && res.getStatusCode() == 100) {
                return res;
            }
        }
        return null;
    }

    /**
     * 转换节数
     *
     * @param whichSection
     * @return
     */
    public static String changeBetWhichSection(String whichSection) {
        if (whichSection.contains("第一节")) {
            return "第一节";
        } else if (whichSection.contains("第二节")) {
            return "第二节";
        } else if (whichSection.contains("第三节")) {
            return "第三节";
        } else if (whichSection.contains("第四节")) {
            return "第四节";
        } else {
            return "";
        }
    }
}
