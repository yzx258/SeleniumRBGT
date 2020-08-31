package com.example.selenium.bet;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yucw
 * @date 2020/8/14 14:14
 * 描述：188下注平台
 */
public class BetUtil {

    private static final String DS = "总得分:滚球 单 / 双";

    /**
     * 描述：188下注
     *
     * @param
     */
    public void betSend() {
        // chromeDriver服务地址，需要手动下载
        String chromeDriverUrl = System.getProperty("user.dir")+"\\src\\main\\resources\\chromedriver.exe";
        System.out.println(chromeDriverUrl);
        System.setProperty("webdriver.chrome.driver", chromeDriverUrl);
        // 自己本地最新的charm版本，需要添加启动参数
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        WebDriver driver = new ChromeDriver();
        // 登录信息
        login(driver);
        // 点击188赛事信息
        btnSend(driver);
        // 下注
        betSend(driver);
        try {
            Thread.sleep(3000);
            System.out.println("=================== 关闭浏览器 ====================");
            driver.quit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 描述：登录网站
     *
     * @param driver
     */
    private void login(WebDriver driver) {
        // 目标URL
        driver.get("https://www.52365v.com");
        driver.manage().window().maximize();
        // 获取用户输入框
        WebElement elementZh = driver.findElement(By.xpath("//*[@id=\"login\"]/form/div[1]/div[1]/input"));
        // 清空输入框
        elementZh.clear();
        // 获取用户输入框
        WebElement elementMm = driver.findElement(By.xpath("//*[@id=\"login\"]/form/div[1]/div[2]/input"));
        // 清空输入框
        elementMm.clear();
        elementZh.sendKeys("dhxm2376");
        elementMm.sendKeys("dhxm2376");
        //点击确定按钮
        driver.findElement(By.xpath("//*[@id=\"login\"]/form/button")).click();
    }

    /**
     * 描述：按钮点击篮球赛事
     *
     * @param driver
     */
    private void btnSend(WebDriver driver) {
        // 点击体育赛事
        driver.findElement(By.xpath("//*[@id=\"head\"]/div[2]/div[1]/div/a/div/div")).click();
        // 点击188赛事
        driver.findElement(By.xpath("/html/body/div[6]/div/div[2]/div/a[3]/img")).click();
        //存在iframe,首先需要进到iframe
        ((JavascriptExecutor) driver).executeScript("document.getElementsByTagName(\"iframe\").item(0).setAttribute(\"sportframe\",\"sportframe\")");
        driver.switchTo().frame("sportframe");
    }

    /**
     * 描述：发送下注
     *
     * @param driver
     */
    private void betSend(WebDriver driver) {

        try {
            Thread.sleep(3000);
            // 点击滚球按钮
            driver.findElement(By.xpath("//*[@id=\"sp2\"]/div[2]/span")).click();
            Thread.sleep(3000);
            List<WebElement> table = driver.findElements(By.tagName("table"));
            for (WebElement e : table) {
                System.out.println(e.getAttribute("id"));
            }
            table.get(2).findElement(By.className("dsp-iblk")).click();
            System.out.println("休眠开始");
            Thread.sleep(3000);
            System.out.println("休眠结束");
            List<WebElement> allMarkets = driver.findElements(By.id("allMarkets"));
            List<WebElement> element = allMarkets.get(0).findElements(By.className("mg-t-1"));
            for (int i = 0; i < element.size(); i++) {
                System.out.println(element.get(i).findElement(By.className("fts-15")).getText());
                // 判断是否单双
                if (DS.equals(element.get(i).findElement(By.className("fts-15")).getText())) {
                    // 点击单双购买操作
                    // 60秒
                    allMarkets.get(0).findElements(By.className("bg-c-43")).get(i).findElements(By.className("OddsWrapper")).get(0).findElement(By.className("odds")).click();
                    Thread.sleep(3000);
                    // 下注操作
                    // 60秒
                    WebElement monery = driver.findElement(By.className("js-stake"));
                    monery.sendKeys("10");
                    Thread.sleep(1000);
                    driver.findElement(By.className("js-placebet")).click();
                    Thread.sleep(2000);
                    System.out.println(driver.findElement(By.className("js-message")).getText());
                    if (driver.findElement(By.className("js-message")).getText().contains("投注失败")) {
                        // 60秒
                        Thread.sleep(2000);
                        driver.findElement(By.className("js-placebet")).click();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("=======================点击篮球赛事报错了=======================");
            return;
        }
    }
}
