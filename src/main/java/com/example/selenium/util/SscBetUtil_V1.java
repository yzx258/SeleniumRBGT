package com.example.selenium.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.selenium.bo.ssc.LotteryInfoBO;
import com.example.selenium.entity.ssc.BetSscGameInfo;
import com.example.selenium.enums.SscNumType;
import com.example.selenium.handle.BetGameAccountInfoHandle;
import com.example.selenium.service.BetSscGameInfoService;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * File Description
 *
 * @author 俞春旺
 * @company 厦门市宜车时代
 * @date 2022-04-19 20:08
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SscBetUtil_V1 {

    private final CacheMapUtil cacheMapUtil;
    private final BetSscGameInfoService betSscGameInfoService;
    private final BetGameAccountInfoHandle betGameAccountInfoHandle;

    private final DingUtil dingUtil;
    private static List<Integer> bls = new ArrayList<>();
    static {
        bls.add(1);
        bls.add(3);
        bls.add(80);
        bls.add(900);
    }

    /***
     *
     * 发送
     * 
     * @return void
     * @author yucw
     * @date 2022-04-21 09:18
     */
    public void send() {

        // 定义 - 参数
        Object bet_tag = cacheMapUtil.getMap("THREAD_EXECUTION_SSC_B");
        if (null != bet_tag) {
            System.out.println("正在执行，不允许新线程操作");
            return;
        }

        // 查询 - 地址是否存在
        String bet_url = betGameAccountInfoHandle.getUrl("SSC_URL");
        if (bet_url == null) {
            throw new RuntimeException("获取不到地址，请检查！");
        }

        // 线程开始执行
        cacheMapUtil.putMap("THREAD_EXECUTION_SSC_B", "THREAD_EXECUTION_SSC_B");

        // 初始化 - 自动化浏览器
        String chromeDriverUrl = System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", chromeDriverUrl);
        // 自己本地最新的charm版本，需要添加启动参数
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        WebDriver driver = new ChromeDriver();
        Integer blResult = bls.get(0);
        try {

            // 操作 - 打开浏览器
            driver.get(bet_url);

            // 操作 - 屏幕最大化
            SleepUtil.sleepUtil(2000);
            driver.manage().window().maximize();

            // 获取 - 当前页面
            driver = driver.switchTo().window(driver.getWindowHandle());
            SleepUtil.sleepUtil(2000);

            // 获取 - 去掉弹窗
            driver.findElement(By.className("content-footer")).findElement(By.className("btn")).click();
            SleepUtil.sleepUtil(2000);

            // 获取 - 游戏类型 - 默认 "河内1分彩"
            getGamesType(driver, "河内1分彩");

            // 定义 - 参数
            LotteryInfoBO kjInfo = null;
            String period = null;
            // 循环 - 操作
            do {
                // 刷新 - 当前
                driver.navigate().refresh();
                SleepUtil.sleepUtil(2000);
                // 获取 - 当前
                driver = driver.switchTo().window(driver.getWindowHandle());
                SleepUtil.sleepUtil(2000);

                // 获取 - 历史开奖
                kjInfo = getLotteryInfo(driver);
                if (period != null && kjInfo != null) {
                    do {
                        SleepUtil.sleepUtil(2000);
                        kjInfo = getLotteryInfo(driver);
                        if (Integer.parseInt(kjInfo.getPeriod()) > Integer.parseInt(period)) {
                            dingUtil
                                .sendMassage("目前期数：" + kjInfo.getPeriod() + ";购买期数：" + period + "不符合，可能存在数据丢失，请注意！！");
                        }

                    } while (!kjInfo.getPeriod().equals(period)
                        || Integer.parseInt(kjInfo.getPeriod()) < Integer.parseInt(period));
                }
                log.info("==================================");
                log.info("开奖信息：" + JSONUtil.toJsonStr(kjInfo));
                log.info("==================================");
                System.out.println();
                // 校验 - 操作时间
                Integer lastTime = getLastTime(driver);
                for (int op = 0; op < 1000; op++) {
                    if (lastTime <= 40 && lastTime > 35) {
                        break;
                    }
                    SleepUtil.sleepUtil(1000);
                    lastTime = getLastTime(driver);
                }

                // 操作 - 游戏倍率 - 默认
                gameMagnificationSetting(driver, "1分");

                // 期数
                period = String.valueOf(Integer.parseInt(kjInfo.getPeriod()) + 1);

                // 操作 - 万位
                log.info("==================================");
                Integer ww = op(driver, SscNumType.SSC_NUM_TYPE_0.getCode());
                // 万位下注
                if (null != ww) {
                    Integer wwBl = 0;
                    // 校验 - 第一次不校验红黑
                    BetSscGameInfo wwInfo = betSscGameInfoService.getBetSscGameInfo(kjInfo.getPeriod(),
                        SscNumType.SSC_NUM_TYPE_0.getCode());
                    if (wwInfo != null) {
                        // 比赛结果
                        Integer bsResult = 0;
                        if (kjInfo.getTenThousand().equals(wwInfo.getNumStr())) {
                            sendError(SscNumType.SSC_NUM_TYPE_0.getCode(), kjInfo.getPeriod(), ww, bls.get(wwBl),
                                driver, kjInfo);
                            wwBl = wwInfo.getBl() + 1;
                            blResult = bls.get(wwBl);
                            bsResult = 0;
                        } else {
                            wwBl = 0;
                            blResult = bls.get(wwBl);
                            bsResult = 1;
                        }
                        // 更新 - 比赛结果
                        betSscGameInfoService.updateBetSscGameInfo(kjInfo.getPeriod(), bsResult,
                            SscNumType.SSC_NUM_TYPE_0.getCode(), kjInfo.getTenThousand());
                    }
                    // 下注
                    sendOk(wwBl, period, ww, SscNumType.SSC_NUM_TYPE_0.getCode(), blResult, driver);
                    if(wwBl > 0){
                        sendSuccess(SscNumType.SSC_NUM_TYPE_0.getCode(), kjInfo.getPeriod(), ww, bls.get(wwBl),
                                driver, kjInfo);
                    }
                }
                log.info("==================================");
                System.out.println();

                log.info("==================================");
                Integer qw = op(driver, SscNumType.SSC_NUM_TYPE_1.getCode());
                // 千位下注
                if (null != qw) {
                    Integer qwBl = 0;
                    BetSscGameInfo qwInfo = betSscGameInfoService.getBetSscGameInfo(kjInfo.getPeriod(),
                        SscNumType.SSC_NUM_TYPE_1.getCode());
                    if (qwInfo != null) {
                        // 比赛结果
                        Integer bsResult = 0;
                        // 校验 - 第一次不校验红黑
                        if (kjInfo.getThousands().equals(qwInfo.getNumStr())) {
                            sendError(SscNumType.SSC_NUM_TYPE_1.getCode(), kjInfo.getPeriod(), qw, bls.get(qwBl),
                                driver, kjInfo);
                            qwBl = qwInfo.getBl() + 1;
                            blResult = bls.get(qwBl);
                            bsResult = 0;
                        } else {
                            qwBl = 0;
                            blResult = bls.get(qwBl);
                            bsResult = 1;
                        }
                        // 更新 - 比赛结果
                        betSscGameInfoService.updateBetSscGameInfo(kjInfo.getPeriod(), bsResult,
                            SscNumType.SSC_NUM_TYPE_1.getCode(), kjInfo.getThousands());
                    }
                    // 下注
                    sendOk(qwBl, period, qw, SscNumType.SSC_NUM_TYPE_1.getCode(), blResult, driver);
                    if(qwBl > 0){
                        sendSuccess(SscNumType.SSC_NUM_TYPE_1.getCode(), kjInfo.getPeriod(), ww, bls.get(qwBl),
                                driver, kjInfo);
                    }
                }
                log.info("==================================");
                System.out.println();

                log.info("==================================");
                Integer bw = op(driver, SscNumType.SSC_NUM_TYPE_2.getCode());
                // 百位下注
                if (null != bw) {
                    Integer bwBl = 0;
                    BetSscGameInfo bwInfo = betSscGameInfoService.getBetSscGameInfo(kjInfo.getPeriod(),
                        SscNumType.SSC_NUM_TYPE_2.getCode());
                    if (bwInfo != null) {
                        // 比赛结果
                        Integer bsResult = 0;
                        // 校验 - 第一次不校验红黑
                        if (kjInfo.getHundreds().equals(bwInfo.getNumStr())) {
                            sendError(SscNumType.SSC_NUM_TYPE_2.getCode(), kjInfo.getPeriod(), qw, bls.get(bwBl),
                                driver, kjInfo);
                            bwBl = bwInfo.getBl() + 1;
                            blResult = bls.get(bwBl);
                            bsResult = 0;
                            bw = Integer.parseInt(bwInfo.getNumStr());
                        } else {
                            bwBl = 0;
                            blResult = bls.get(bwBl);
                            bsResult = 1;
                        }
                        // 更新 - 比赛结果
                        betSscGameInfoService.updateBetSscGameInfo(kjInfo.getPeriod(), bsResult,
                            SscNumType.SSC_NUM_TYPE_2.getCode(), kjInfo.getHundreds());
                    }
                    // 下注
                    sendOk(bwBl, period, bw, SscNumType.SSC_NUM_TYPE_2.getCode(), blResult, driver);
                    if(bwBl > 0){
                        sendSuccess(SscNumType.SSC_NUM_TYPE_2.getCode(), kjInfo.getPeriod(), ww, bls.get(bwBl),
                                driver, kjInfo);
                    }
                }
                log.info("==================================");
                System.out.println();

                log.info("==================================");
                Integer sw = op(driver, SscNumType.SSC_NUM_TYPE_3.getCode());
                // 十位下注
                if (null != sw) {
                    Integer swBl = 0;
                    BetSscGameInfo swInfo = betSscGameInfoService.getBetSscGameInfo(kjInfo.getPeriod(),
                        SscNumType.SSC_NUM_TYPE_3.getCode());
                    if (swInfo != null) {
                        // 比赛结果
                        Integer bsResult = 0;
                        // 校验 - 第一次不校验红黑
                        if (kjInfo.getTen().equals(swInfo.getNumStr())) {
                            sendError(SscNumType.SSC_NUM_TYPE_3.getCode(), kjInfo.getPeriod(), sw, bls.get(swBl),
                                driver, kjInfo);
                            swBl = swInfo.getBl() + 1;
                            blResult = bls.get(swBl);
                            bsResult = 0;
                        } else {
                            swBl = 0;
                            blResult = bls.get(swBl);
                            bsResult = 1;
                        }
                        // 更新 - 比赛结果
                        betSscGameInfoService.updateBetSscGameInfo(kjInfo.getPeriod(), bsResult,
                            SscNumType.SSC_NUM_TYPE_3.getCode(), kjInfo.getTen());
                    }
                    // 下注
                    sendOk(swBl, period, sw, SscNumType.SSC_NUM_TYPE_3.getCode(), blResult, driver);
                    if(swBl > 0){
                        sendSuccess(SscNumType.SSC_NUM_TYPE_3.getCode(), kjInfo.getPeriod(), ww, bls.get(swBl),
                                driver, kjInfo);
                    }
                }
                log.info("==================================");
                System.out.println();

                log.info("==================================");
                Integer gw = op(driver, SscNumType.SSC_NUM_TYPE_4.getCode());
                // 个位下注
                if (null != gw) {
                    Integer gwBl = 0;
                    BetSscGameInfo gwInfo = betSscGameInfoService.getBetSscGameInfo(kjInfo.getPeriod(),
                        SscNumType.SSC_NUM_TYPE_4.getCode());
                    if (gwInfo != null) {
                        // 比赛结果
                        Integer bsResult = 0;
                        // 校验 - 第一次不校验红黑
                        if (kjInfo.getSingleDigit().equals(gwInfo.getNumStr())) {
                            sendError(SscNumType.SSC_NUM_TYPE_4.getCode(), kjInfo.getPeriod(), gw, bls.get(gwBl),
                                driver, kjInfo);
                            gwBl = gwInfo.getBl() + 1;
                            blResult = bls.get(gwBl);
                            bsResult = 0;
                        } else {
                            gwBl = 0;
                            blResult = bls.get(gwBl);
                            bsResult = 1;
                        }
                        // 更新 - 比赛结果
                        betSscGameInfoService.updateBetSscGameInfo(kjInfo.getPeriod(), bsResult,
                            SscNumType.SSC_NUM_TYPE_4.getCode(), kjInfo.getSingleDigit());
                    }
                    // 下注
                    sendOk(gwBl, period, gw, SscNumType.SSC_NUM_TYPE_4.getCode(), blResult, driver);
                    if(gwBl > 0){
                        sendSuccess(SscNumType.SSC_NUM_TYPE_4.getCode(), kjInfo.getPeriod(), ww, bls.get(gwBl),
                                driver, kjInfo);
                    }
                }
                log.info("==================================");
                System.out.println();
                log.info(period + "期，下注完成，请等待开奖......");
            } while (true);
        } catch (Exception e) {
            System.out.println("ERROR MESSAGE :" + e.getMessage());
            dingUtil.sendMassage("操作失败，请留意系统！");
        } finally {
            // 结束 - 此次操作
            cacheMapUtil.delMap("THREAD_EXECUTION_SSC_B");
            driver.close();
            driver.quit();
        }
    }

    /***
     * 发送黑单信息
     * 
     * @param sscNumType
     * @param period
     * @param numStr
     * @param bsResult
     * @param driver
     * @return void
     * @author yucw
     * @date 2022-04-22 10:53
     */
    public void sendError(Integer sscNumType, String period, Integer numStr, Integer bsResult, WebDriver driver,
        LotteryInfoBO kjInfo) {
        // 聚合 - 钉钉消息
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\n---------------------");
        stringBuffer.append("\n账户余额：" + getAccountInfo(driver));
        stringBuffer.append("\n比赛期数：" + period);
        stringBuffer.append("\n---------------------");

        if (0 == sscNumType) {
            stringBuffer.append("\n万位：" + numStr);
        } else if (1 == sscNumType) {
            stringBuffer.append("\n千位：" + numStr);
        } else if (2 == sscNumType) {
            stringBuffer.append("\n百位：" + numStr);
        } else if (3 == sscNumType) {
            stringBuffer.append("\n十位：" + numStr);
        } else if (4 == sscNumType) {
            stringBuffer.append("\n个位：" + numStr);
        }

        stringBuffer.append("\n---------------------");
        if (bsResult == 1) {
            stringBuffer.append("\n比赛倍率：1");
        } else {
            stringBuffer.append("\n比赛倍率：1" + bsResult);
        }
        stringBuffer.append("\n比赛结果：黑单");
        stringBuffer
            .append("\n比赛信息：" + kjInfo.getPeriod() + "(期数);" + kjInfo.getTenThousand() + "(万);" + kjInfo.getThousands()
                + "(千);" + kjInfo.getHundreds() + "(百);" + kjInfo.getTen() + "(十);" + kjInfo.getSingleDigit() + "(个)");
        // 发送 - 钉钉消息
        dingUtil.sendMassage(stringBuffer.toString());

    }

    /***
     * 发送黑单信息
     *
     * @param sscNumType
     * @param period
     * @param numStr
     * @param bsResult
     * @param driver
     * @return void
     * @author yucw
     * @date 2022-04-22 10:53
     */
    public void sendSuccess(Integer sscNumType, String period, Integer numStr, Integer bsResult, WebDriver driver,
                          LotteryInfoBO kjInfo) {
        // 聚合 - 钉钉消息
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\n---------------------");
        stringBuffer.append("\n账户余额：" + getAccountInfo(driver));
        stringBuffer.append("\n比赛期数：" + period);
        stringBuffer.append("\n---------------------");

        if (0 == sscNumType) {
            stringBuffer.append("\n万位：" + numStr);
        } else if (1 == sscNumType) {
            stringBuffer.append("\n千位：" + numStr);
        } else if (2 == sscNumType) {
            stringBuffer.append("\n百位：" + numStr);
        } else if (3 == sscNumType) {
            stringBuffer.append("\n十位：" + numStr);
        } else if (4 == sscNumType) {
            stringBuffer.append("\n个位：" + numStr);
        }

        stringBuffer.append("\n---------------------");
        if (bsResult == 1) {
            stringBuffer.append("\n比赛倍率：1");
        } else {
            stringBuffer.append("\n比赛倍率：1" + bsResult);
        }
        // 发送 - 钉钉消息
        dingUtil.sendMassage(stringBuffer.toString());

    }

    /***
     * 下注
     * 
     * @param bl
     * @param period
     * @param numStr
     * @param sscNumType
     * @param magnification
     * @param driver
     * @return void
     * @author yucw
     * @date 2022-04-21 17:04
     */
    private void sendOk(Integer bl, String period, Integer numStr, Integer sscNumType, Integer magnification,
        WebDriver driver) {
        log.info("我是倍率：bl:{},bls.get(bl):{}",bl,bls.get(bl));
        if (bl > 0) {
            // 下注 - 倍数
            WebElement multiple =
                driver.findElement(By.className("lottery-content")).findElement(By.className("lottery-content-scroll"))
                    .findElement(By.className("statistics-standard")).findElement(By.className("mode-multiple"))
                    .findElement(By.className("multiple")).findElement(By.className("muliple-input"));

            multiple.click();
            multiple.clear();
            multiple.sendKeys(bls.get(bl) + "");
            SleepUtil.sleepUtil(500);
        }

        // 插入 - 记录
        BetSscGameInfo info = new BetSscGameInfo();
        info.setSscType(2);
        info.setSscNumType(sscNumType);
        info.setCreateTime(new Date());
        info.setPeriod(period);
        info.setNumStr(numStr + "");
        info.setBl(bl);
        if (bl == 0) {
            info.setMagnification(1);
        } else {
            info.setMagnification(Integer.parseInt("1" + magnification));
        }
        boolean saveResult = betSscGameInfoService.save(info);
        if (!saveResult) {
            throw new RuntimeException("保存数据失败");
        }

        // 操作 - 下注
        bet(driver);

        // 操作 - 确认下注
        SleepUtil.sleepUtil(500);
        driver.findElement(By.id("modals-container")).findElement(By.className("check-bet-modal"))
            .findElement(By.className("modal-btns")).findElement(By.className("confirm")).click();
        SleepUtil.sleepUtil(500);
    }

    /***
     * 操作 - 万位数据 0-WW 1-QW 2-BW 3-SW 4-GW
     * 
     * @param driver
     * @return java.lang.Integer
     * @author yucw
     * @date 2022-04-21 10:33
     */
    public static Integer op(WebDriver driver, Integer sscNumType) {
        int sjResult = 0;
        boolean res = true;
        List<WebElement> ballsElements = null;
        do {
            try {
                // 获取 - 操作信息
                ballsElements = driver.findElement(By.className("lottery-content"))
                    .findElement(By.className("lottery-content-scroll")).findElement(By.className("bet-wrapper"))
                    .findElement(By.className("balls-ul")).findElements(By.className("balls-row"));

                // 获取 - 万位操作信息
                SleepUtil.sleepUtil(50);
                List<WebElement> ball = ballsElements.get(sscNumType).findElement(By.className("row-balls"))
                    .findElements(By.className("ball"));
                if (null != ball) {
                    // 获取 - 随机数
                    sjResult = 9;
                    for (int i = 0; i < ball.size(); i++) {
                        if (i == sjResult) {
                            continue;
                        }
                        ball.get(i).findElement(By.className("ball-item")).click();
                    }
                }

                res = false;
            } catch (Exception e) {
                System.out.println("OP - 获取操作失败......");
                SleepUtil.sleepUtil(1000);
                driver = driver.switchTo().window(driver.getWindowHandle());
            }
        } while (res);
        SleepUtil.sleepUtil(500);
        return sjResult;
    }

    /**
     * 获取 - 开奖信息
     *
     * @param driver
     * @return
     */
    public static LotteryInfoBO getLotteryInfo(WebDriver driver) {
        boolean res = true;
        List<WebElement> span = null;
        List<WebElement> kjElementList = null;
        do {
            try {
                // 获取 - 期数
                kjElementList = driver.findElement(By.className("sidebar-menu"))
                    .findElement(By.className("menu-content")).findElement(By.className("standard-menu"))
                    .findElement(By.className("recent-lottery")).findElement(By.className("lottery-recently"))
                    .findElement(By.className("recently-list-body")).findElements(By.tagName("li"));
                // 获取 - 历史开奖
                span = kjElementList.get(0).findElement(By.className("main-code")).findElements(By.className("curr"));
                // 停止获取
                res = false;
            } catch (Exception e) {
                System.out.println("查询不到历史数据，可能弹出开奖信息中.....");
                SleepUtil.sleepUtil(1500);
            }
        } while (res);

        // 聚合 - 开奖信息
        LotteryInfoBO info = new LotteryInfoBO();
        info.setPeriod(kjElementList.get(0).findElement(By.className("main-issue")).getText());
        info.setSingleDigit(span.get(4).findElement(By.tagName("i")).findElement(By.tagName("span")).getText());
        info.setTen(span.get(3).findElement(By.tagName("i")).findElement(By.tagName("span")).getText());
        info.setHundreds(span.get(2).findElement(By.tagName("i")).findElement(By.tagName("span")).getText());
        info.setThousands(span.get(1).findElement(By.tagName("i")).findElement(By.tagName("span")).getText());
        info.setTenThousand(span.get(0).findElement(By.tagName("i")).findElement(By.tagName("span")).getText());
        return info;
    }

    /**
     * 获取 - 游戏类型 - 默认 "河内1分彩"
     *
     * @param driver
     */
    public static void getGamesType(WebDriver driver, String GamesTypeName) {
        // 获取 - 游戏类型
        List<WebElement> series = driver.findElement(By.className("lottery-list")).findElements(By.className("series"))
            .get(2).findElements(By.tagName("li"));
        for (WebElement item : series) {
            if (GamesTypeName.equals(item.getText())) {
                item.click();
                break;
            }
        }
        SleepUtil.sleepUtil(2000);

        // 操作 - 标准盘
        List<WebElement> handicap = driver.findElement(By.className("lottery-content-scroll"))
            .findElement(By.className("handicapList")).findElements(By.className("handicap"));
        for (WebElement item : handicap) {
            if ("标准盘".equals(item.getText())) {
                item.click();
                break;
            }
        }
        SleepUtil.sleepUtil(2000);
    }

    /**
     * 操作 - 游戏倍率 - 默认 1里
     *
     * @param driver
     * @param gameMagnificationSetting
     */
    public static void gameMagnificationSetting(WebDriver driver, String gameMagnificationSetting) {
        // 操作 - 倍率 2厘
        List<WebElement> moneyMode = driver.findElement(By.className("lottery-content-scroll"))
            .findElement(By.className("statistics-standard")).findElement(By.className("mode-multiple"))
            .findElement(By.className("moneyMode")).findElements(By.tagName("span"));
        for (WebElement item : moneyMode) {
            if (gameMagnificationSetting.equals(item.getText())) {
                item.click();
                break;
            }
        }
        SleepUtil.sleepUtil(2000);
    }

    /***
     * 下注
     *
     * @param driver
     * @return void
     * @author yucw
     * @date 2022-04-20 17:06
     */
    public static void bet(WebDriver driver) {
        // 操作 - 下注按钮
        WebElement bet = driver.findElement(By.className("lottery-content-scroll"))
            .findElement(By.className("statistics-standard")).findElement(By.className("bet-statistics"))
            .findElement(By.className("statistics-btns")).findElement(By.className("btn-bet"));
        bet.click();
    }

    /**
     * 获取 - 账号信息
     *
     * @param driver
     */
    public static String getAccountInfo(WebDriver driver) {
        // 获取 - 账号信息
        SleepUtil.sleepUtil(500);
        WebElement account = driver.findElement(By.className("lottery-wrapper")).findElement(By.className("accountMsg"))
            .findElement(By.className("account")).findElement(By.className("balance")).findElement(By.tagName("span"));
        return account.getText();
    }

    /***
     * 获取 - 当前时间
     * 
     * @param driver
     * @return java.lang.Integer
     * @author yucw
     * @date 2022-04-20 17:25
     */
    public static Integer getLastTime(WebDriver driver) {
        // 校验 - 当前时间
        List<WebElement> elements =
            driver.findElement(By.className("lottery-wrapper")).findElement(By.className("lottery-w"))
                .findElement(By.className("lottery-content")).findElement(By.className("lottery-info-wrap"))
                .findElement(By.className("lottery-info")).findElements(By.className("lottery-info-layout"));
        List<WebElement> time = elements.get(1).findElement(By.className("head-countdown"))
            .findElement(By.className("currentIssue")).findElement(By.className("currentIssue-right"))
            .findElement(By.className("lottery-counter")).findElements(By.className("time-box"));
        String num_left = time.get(2).findElement(By.className("num_left")).getText();
        String num_right = time.get(2).findElement(By.className("num_right")).getText();
        // 获取 - 剩余时间
        Integer lastTime = Integer.parseInt(num_left + num_right);
        return lastTime;
    }
}
