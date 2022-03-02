package com.example.selenium.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-26 22:47:16
 * @description: 描述
 */
@Data
public class BetGameInfo {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 单 - 0；双 - 1
     */
    private Integer singleOrDouble;

    /**
     * 操作线程：0-A线程;1-B线程；2-C线程
     */
    private Integer operateThreadType;

    /***
     *
     * 线程ID
     * 
     * @return
     * @author yucw
     * @date 2022-02-28 14:20
     */
    private String lockId;

    /**
     * 联赛名称
     */
    private String competitionName;

    /**
     * 主队名称
     */
    private String homeTeamName;

    /**
     * 主队名称比分
     */
    private String homeTeamScore;

    /**
     * 客队名称
     */
    private String awayTeamName;

    /**
     * 客队名称比分
     */
    private String awayTeamScore;

    /**
     * 第几节
     */
    private String whichSection;

    /**
     * 场次
     */
    private Integer screenings;

    /**
     * 原始金额
     */
    private BigDecimal originalAmount;

    /**
     * 剩余金额
     */
    private BigDecimal balanceAmount;

    /**
     * 投注金额
     */
    private BigDecimal betAmount;

    /**
     * 是否结算：0-待结算；1-已结算
     */
    private Integer isSettlement;

    /**
     * 比赛结果：2-黑；1-红
     */
    private Integer competitionResult;

    /**
     * 比赛倍率
     */
    private BigDecimal competitionMagnification;

    /**
     * 比赛红利
     */
    private BigDecimal competitionDividendAmount;

    /**
     * 比赛类型：0-单双；1-待定
     */
    private Integer competitionType;
}