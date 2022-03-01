package com.example.selenium.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-20 15:29:56
 * @description: 描述
 */
@Data
public class BetLock {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String lockKey;

    private String lockValue;
}
