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
public enum SscType {

    SSC_TYPE_1(1, "单双"), SSC_TYPE_2(2, "单球"),;

    private final int code;
    private final String name;

}
