package com.example.selenium.spec;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.example.selenium.dto.InstructionDTO;
import com.example.selenium.util.DingUtil;
import lombok.Data;

import java.util.List;

/**
 * @company： laoyu
 * @copyright： Copyright (C), 2020
 * @author： yucw
 * @date： 2020/10/14 16:07
 * @version： 1.0
 * @description: 下注缓存
 */
@Data
public class BetCacheSpec {

    /**
     * 主队名称
     */
    private String homeTeam;

    /**
     * 客队名称
     */
    private String awayTeam;

    /**
     * 比赛节点
     */
    private String node;

    /**
     * 第几场比赛
     */
    private int number = 0;

    /**
     * 比赛购买单双，默认买单
     * 0【单】;1【双】
     */
    private int oddAndEven = 0;

    /**
     * 比赛分数
     */
    private String score;

    /**
     * 是否红单
     * 0【已购买,等待出结果】;1【已红单】;2【已黑单】
     */
    private int isRed = 0;

    /**
     * 下注倍率
     */
    private int magnification = 0;

    public static void main(String[] args) {
        BetCacheSpec betCacheSpec = new BetCacheSpec();
        betCacheSpec.setHomeTeam("湖人总冠军1");
        betCacheSpec.setAwayTeam("快船总冠军2");
        betCacheSpec.setNode("第一节");
        betCacheSpec.setIsRed(0);
        betCacheSpec.setOddAndEven(0);
        betCacheSpec.setNumber(0);
        betCacheSpec.setMagnification(0);
        BuyRecordSpec b = new BuyRecordSpec();
        b.setJson(JSON.toJSONString(betCacheSpec));
        b.setType(1);
        try {
            HttpUtil.post("http://47.106.143.218:8081/buy/add", JSON.toJSONString(b));
            String result = HttpUtil.get("http://47.106.143.218:8081/buy/get/1");
            System.out.println("result:" + JSON.parseObject(result).get("data"));
            List<BuyRecordJson> bls = JSON.parseArray(JSON.parseObject(result).get("data").toString(), BuyRecordJson.class);
            System.out.println(bls.size());
            BetCacheSpec betCacheSpec1 = JSON.parseObject(bls.get(0).getJson(), BetCacheSpec.class);
            System.out.println("==============================");
            System.out.println(betCacheSpec1);
            System.out.println("==============================");
        } catch (Exception e) {
            DingUtil d = new DingUtil();
            d.sendMassage("保存下注记录有问题，请关注该比赛：" + JSON.toJSONString(betCacheSpec));
        }

    }
}
