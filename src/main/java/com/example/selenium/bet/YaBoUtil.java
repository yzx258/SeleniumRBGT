package com.example.selenium.bet;

import com.example.selenium.util.SleepUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-12 15:46:39
 * @description: 描述
 */
public class YaBoUtil {

    public static void main(String[] args) {
        // 初始化 - 自动化浏览器
        String chromeDriverUrl = System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", chromeDriverUrl);
        // 自己本地最新的charm版本，需要添加启动参数
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        WebDriver driver = new ChromeDriver();

        // 打开 - 浏览器
        driver.get("https://www.tbljubnh.com:9013");

        driver.manage().window().maximize();
        SleepUtil.sleepUtil(2000);
        // 账号:dhxm2376
        WebElement zh = driver.findElement(By.xpath("//*[@id=\"popoverCon\"]/div/div/div/input"));
        zh.sendKeys("yzx258");
        // 密码:ycw15659512376"
        WebElement mm = driver.findElement(By.xpath("//*[@id=\"popoverCon\"]/div/div/div/div[1]/input"));
        mm.sendKeys("yzx009560");
        SleepUtil.sleepUtil(2000);
    }



}



