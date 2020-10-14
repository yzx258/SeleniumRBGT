package com.example.selenium.spec;

import lombok.Data;

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
     * 是否红单
     * 0【已购买,等待出结果】;1【已红单】;2【已黑单】
     */
    private int isRed = 0;

    /**
     * 下注倍率
     */
    private int magnification = 0;
}
