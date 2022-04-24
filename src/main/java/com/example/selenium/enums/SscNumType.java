package com.example.selenium.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * File Description
 *
 * @author 俞春旺
 * @company 厦门市宜车时代
 * @date 2022-04-20 16:21
 */
@Getter
@AllArgsConstructor
public enum SscNumType {

    SSC_NUM_TYPE_0(0, "万"), SSC_NUM_TYPE_1(1, "千"), SSC_NUM_TYPE_2(2, "百"), SSC_NUM_TYPE_3(3, "十"),
    SSC_NUM_TYPE_4(4, "个"),;

    private final int code;
    private final String name;

}
