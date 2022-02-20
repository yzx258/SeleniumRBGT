package com.example.selenium.bo;

import lombok.Data;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-20 14:04:17
 * @description: 描述
 */
@Data
public class YaBoAccountInfoBO {

    /**
     * 账号金额
     */
    private Integer totalAmount;

    /**
     * 待结算金额
     */
    private Integer pendingSettlementAmount;

}
