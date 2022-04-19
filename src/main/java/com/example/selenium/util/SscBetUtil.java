package com.example.selenium.util;

import com.example.selenium.bo.ssc.LotteryInfoBO;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

/**
 * File Description
 *
 * @author 俞春旺
 * @company 厦门市宜车时代
 * @date 2022-04-19 20:08
 */
@Slf4j
public class SscBetUtil {

    public static void main(String[] args) {
        // 初始化 - 自动化浏览器
        String chromeDriverUrl = System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", chromeDriverUrl);
        // 自己本地最新的charm版本，需要添加启动参数
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        WebDriver driver = new ChromeDriver();

        // 操作 - 打开浏览器
        driver.get("https://ybtxpc.3ndfp3ai.com/lottery/52?token=ef04a7f57acb4292adf5f81504e921ae1650368445510");

        // 操作 - 屏幕最大化
        SleepUtil.sleepUtil(2000);
        driver.manage().window().maximize();

        // 获取 - 当前页面
        driver = driver.switchTo().window(driver.getWindowHandle());
        SleepUtil.sleepUtil(2000);

        // 获取 - 去掉弹窗
        driver.findElement(By.className("content-footer")).findElement(By.className("btn")).click();
        SleepUtil.sleepUtil(5000);

        // 获取 - 历史开奖
        LotteryInfoBO lotteryInfo = getLotteryInfo(driver);
        log.info("开奖信息：{}",lotteryInfo.toString());


        // 结束 - 此次操作
        driver.close();
        driver.quit();
    }

    /**
     * 获取 - 开奖信息
     * @param driver
     * @return
     */
    public static LotteryInfoBO getLotteryInfo(WebDriver driver){

        // 获取 - 期数
        List<WebElement> kjElementList = driver.findElement(By.className("sidebar-menu")).findElement(By.className("menu-content")).findElement(By.className("standard-menu"))
                .findElement(By.className("recent-lottery"))
                .findElement(By.className("lottery-recently"))
                .findElement(By.className("recently-list-body")).findElements(By.tagName("li"));
        // 获取 - 历史开奖
        List<WebElement> span = kjElementList.get(0).findElement(By.className("main-code")).findElements(By.className("curr"));

        // 聚合 - 开奖信息
        LotteryInfoBO info = new LotteryInfoBO();
        info.setPeriod(kjElementList.get(0).findElement(By.className("main-issue")).getText());
        info.setSingleDigit(span.get(0).findElement(By.tagName("i")).findElement(By.tagName("span")).getText());
        info.setTen(span.get(1).findElement(By.tagName("i")).findElement(By.tagName("span")).getText());
        info.setHundreds(span.get(2).findElement(By.tagName("i")).findElement(By.tagName("span")).getText());
        info.setThousands(span.get(3).findElement(By.tagName("i")).findElement(By.tagName("span")).getText());
        info.setTenThousand(span.get(4).findElement(By.tagName("i")).findElement(By.tagName("span")).getText());
        return info;
    }

    /**
     * 获取 - 游戏类型 - 默认 "河内1分彩"
     * @param driver
     */
    public static void getGamesType(WebDriver driver,String GamesTypeName){
        // 获取 - 游戏类型
        List<WebElement> series =
                driver.findElement(By.className("lottery-list")).findElements(By.className("series"))
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
     * @param driver
     * @param gameMagnificationSetting
     */
    public static void gameMagnificationSetting(WebDriver driver,String gameMagnificationSetting){
        // 操作 - 倍率 2厘
        List<WebElement> moneyMode = driver.findElement(By.className("lottery-content-scroll"))
                .findElement(By.className("statistics-standard")).findElement(By.className("mode-multiple"))
                .findElement(By.className("moneyMode")).findElements(By.tagName("span"));
        for (WebElement item : moneyMode) {
            System.out.println("moneyMode:" + item.getText());
            if (gameMagnificationSetting.equals(item.getText())) {
                item.click();
                break;
            }
        }
        SleepUtil.sleepUtil(5000);
    }

    /**
     * 获取 - 账号信息
     * @param driver
     */
    public static void getAccountInfo(WebDriver driver){
        // 获取 - 账号信息
        WebElement account = driver.findElement(By.className("lottery-wrapper")).findElement(By.className("accountMsg"))
                .findElement(By.className("account")).findElement(By.className("balance")).findElement(By.tagName("span"));
        System.out.println("account:" + account.getText());
        SleepUtil.sleepUtil(5000);
    }

}
