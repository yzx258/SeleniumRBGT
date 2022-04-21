package com.example.selenium.handle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.selenium.service.BetSscGameInfoService;

import lombok.RequiredArgsConstructor;

/**
 * File Description
 *
 * @author 俞春旺
 * @company 厦门市宜车时代
 * @date 2022-04-20 17:52
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BetSscGameInfoHandler {

    private final BetSscGameInfoService betSscGameInfoService;
}
