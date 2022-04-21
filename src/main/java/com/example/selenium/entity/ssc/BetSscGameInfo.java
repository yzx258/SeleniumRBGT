package com.example.selenium.entity.ssc;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

/**
 * @company 5倍率：1 2 4 8 16 32 64 128 259 522 1052 2126 4241 8395 16918 @company 9倍率： 1 10 12 193 1960
 *
 *
 * @author 俞春旺
 * @company 厦门市宜车时代
 * @date 2022-04-20 15:28
 */
@Data
public class BetSscGameInfo {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 期数
     */
    private String period;

    /**
     * 操作类型：1：单双；2：单球
     */
    private Integer sscType;
    /**
     * 个
     */
    private String singleDigit;
    /**
     * 十
     */
    private String ten;
    /**
     * 百
     */
    private String hundreds;
    /**
     * 千
     */
    private String thousands;
    /**
     * 万
     */
    private String tenThousand;

    private Integer result;
}
