package com.example.selenium.entity.ssc;

import java.util.Date;

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 操作类型：1：单双；2：单球
     */
    private Integer sscType;

    /**
     * 操作类型：0：W;1：Q;2：B；3：S;4：5
     */
    private Integer sscNumType;

    /**
     * 数字
     */
    private String numStr;

    private Integer magnification;

    private Integer bl;

    private String kjHm;

    /**
     * 结果
     */
    private Integer result;
}
