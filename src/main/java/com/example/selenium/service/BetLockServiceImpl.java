package com.example.selenium.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.selenium.entity.BetLock;
import com.example.selenium.mapper.BetLockMapper;

/**
 * File Description
 *
 * @author 俞春旺
 * @company 厦门市宜车时代
 * @date 2022-03-15 16:03
 */
@Service
public class BetLockServiceImpl extends ServiceImpl<BetLockMapper, BetLock> implements BetLockService {}
