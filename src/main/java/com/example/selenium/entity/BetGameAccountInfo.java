package com.example.selenium.entity;

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
public class BetGameAccountInfo {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String accKey;

    private String accVal;
}
