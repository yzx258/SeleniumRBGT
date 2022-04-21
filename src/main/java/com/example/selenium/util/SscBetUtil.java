package com.example.selenium.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.selenium.bo.ssc.LotteryInfoBO;
import com.example.selenium.entity.ssc.BetSscGameInfo;
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
public class SscBetUtil {

    private final CacheMapUtil cacheMapUtil;
    private final BetSscGameInfoService betSscGameInfoService;
    private static List<Integer> bls = new ArrayList<>();
    static {
        bls.add(1);
        bls.add(10);
        bls.add(120);
        bls.add(1930);
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
        Object bet_tag = cacheMapUtil.getMap("THREAD_EXECUTION_SSC_A");
        if (null != bet_tag) {
            System.out.println("正在执行，不允许新线程操作");
            return;
        }
        // 线程开始执行
        cacheMapUtil.putMap("THREAD_EXECUTION_SSC_A", "THREAD_EXECUTION_SSC_A");

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
            driver.get("https://ybtxpc.3ndfp3ai.com/lottery/52?token=3df57ab5477d4e62aba4b4ad629f917a1650528901517");

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
            LotteryInfoBO gmInfo = null;
            int sjResult = 0;
            int bl = 0;
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
                if (gmInfo != null && kjInfo != null) {
                    do {
                        SleepUtil.sleepUtil(3000);
                        kjInfo = getLotteryInfo(driver);
                        System.out.println(kjInfo.getPeriod() + ".equals(" + gmInfo.getPeriod() + ") - "
                            + kjInfo.getPeriod().equals(gmInfo.getPeriod()));
                    } while (!kjInfo.getPeriod().equals(gmInfo.getPeriod()));
                }

                // 校验 - 操作时间
                Integer lastTime = getLastTime(driver);
                for (int op = 0; op < 1000; op++) {
                    System.out.println("当前时间：" + lastTime);
                    if (lastTime <= 40 && lastTime > 20) {
                        break;
                    }
                    SleepUtil.sleepUtil(3000);
                    lastTime = getLastTime(driver);
                }
                if (gmInfo != null) {
                    // 比赛结果
                    Integer bsResult = 0;
                    // 校验 - 第一次不校验红黑
                    System.out.println("开始校验上一场买的数据是否中将kjInfo.getTenThousand().equals(gmInfo.getTenThousand()):"
                        + kjInfo.getTenThousand() + ";" + gmInfo.getTenThousand());
                    if (kjInfo.getTenThousand().equals(gmInfo.getTenThousand())) {
                        System.out.println("==========================");
                        System.out.println("该场次黑单");
                        bl = bl + 1;
                        blResult = bls.get(bl);
                        System.out.println("倍率：" + bl);
                        System.out.println("==========================");
                        bsResult = 0;
                    } else {
                        System.out.println("==========================");
                        System.out.println("该场次红单");
                        bl = 0;
                        blResult = bls.get(bl);
                        System.out.println("倍率：" + bl);
                        System.out.println("==========================");
                        bsResult = 1;
                    }
                    // 更新 - 比赛结果
                    betSscGameInfoService.updateBetSscGameInfo(gmInfo.getPeriod(), bsResult);
                }

                // 操作 - 万位
                sjResult = op(driver);

                // 操作 - 游戏倍率 - 默认 1里
                gameMagnificationSetting(driver, "1分");

                // 下注 - 倍数
                WebElement plus = driver.findElement(By.className("lottery-content"))
                    .findElement(By.className("lottery-content-scroll"))
                    .findElement(By.className("statistics-standard")).findElement(By.className("mode-multiple"))
                    .findElement(By.className("multiple")).findElement(By.className("plus"));

                // 需要点击的次数
                Integer clickNum = bls.get(bl) - 1;
                for (int i = 0; i < clickNum; i++) {
                    SleepUtil.sleepUtil(10);
                    plus.click();
                }

                // 整合 - 下注信息
                gmInfo = new LotteryInfoBO();
                gmInfo.setPeriod(String.valueOf(Integer.parseInt(kjInfo.getPeriod()) + 1));
                gmInfo.setTenThousand(sjResult + "");
                gmInfo.setBl(bl);
                System.out.println("下注成功：" + JSONUtil.toJsonStr(gmInfo));

                BetSscGameInfo info = new BetSscGameInfo();
                info.setSscType(2);
                info.setSscNumType(1);
                info.setCreateTime(new Date());
                info.setPeriod(String.valueOf(Integer.parseInt(kjInfo.getPeriod()) + 1));
                info.setNumStr(sjResult + "");
                info.setMagnification(blResult);
                boolean saveResult = betSscGameInfoService.save(info);
                if (!saveResult) {
                    throw new RuntimeException("保存数据失败");
                }

                // 操作 - 下注
                bet(driver);

                // 操作 - 确认下注
                SleepUtil.sleepUtil(2000);
                driver.findElement(By.id("modals-container")).findElement(By.className("check-bet-modal"))
                    .findElement(By.className("modal-btns")).findElement(By.className("confirm")).click();
            } while (true);
        } catch (Exception e) {
            System.out.println("ERROR MESSAGE :" + e.getMessage());
        } finally {
            // 结束 - 此次操作
            cacheMapUtil.delMap("THREAD_EXECUTION_SSC_A");
            driver.close();
            driver.quit();
        }
    }

    /***
     * 操作 - 万位数据
     * 
     * @param driver
     * @return java.lang.Integer
     * @author yucw
     * @date 2022-04-21 10:33
     */
    public static Integer op(WebDriver driver) {
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
                SleepUtil.sleepUtil(1000);
                List<WebElement> ball =
                    ballsElements.get(0).findElement(By.className("row-balls")).findElements(By.className("ball"));
                if (null != ball) {
                    // 获取 - 随机数
                    sjResult = new Random().nextInt(10);
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
                SleepUtil.sleepUtil(2000);
                driver = driver.switchTo().window(driver.getWindowHandle());
            }
        } while (res);
        SleepUtil.sleepUtil(1000);
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
        System.out.println("开奖信息：" + info.toString());
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
            System.out.println("series:" + item.getText());
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
            System.out.println("handicap:" + item.getText());
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
    public static void getAccountInfo(WebDriver driver) {
        // 获取 - 账号信息
        WebElement account = driver.findElement(By.className("lottery-wrapper")).findElement(By.className("accountMsg"))
            .findElement(By.className("account")).findElement(By.className("balance")).findElement(By.tagName("span"));
        System.out.println("account:" + account.getText());
        SleepUtil.sleepUtil(5000);
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
