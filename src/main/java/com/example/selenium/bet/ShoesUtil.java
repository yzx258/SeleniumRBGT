package com.example.selenium.bet;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @company： 厦门宜车时代信息技术有限公司
 * @copyright： Copyright (C), 2020
 * @author： yucw
 * @date： 2020/9/17 10:47
 * @version： 1.0
 * @description:
 */
@Slf4j
public class ShoesUtil {

    public static void main(String[] args) throws InterruptedException {
        // 需要手动扫描登录
        String chromeDriverUrl = System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe";
        System.out.println(chromeDriverUrl);
        System.setProperty("webdriver.chrome.driver", "D:\\00002YX\\chromedriver.exe");
        // 自己本地最新的charm版本，需要添加启动参数
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        WebDriver driver = new ChromeDriver();
        driver.get("https://detail.tmall.com/item.htm?spm=a220m.1000858.1000725.1.360f21baWHu70D&id=603088309420&skuId=4574017883069&user_id=890482188&cat_id=2&is_b=1&rn=ccd495fad165845f06099547d8169b63");
        driver.manage().window().maximize();
        Thread.sleep(15000);
        driver.findElement(By.xpath("//*[@id=\"J_LinkBuy\"]")).click();
        Thread.sleep(3000);
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,document.body.scrollHeight)");
        Thread.sleep(3000);
        driver.findElement(By.xpath("//*[@id=\"submitOrderPC_1\"]/div/a")).click();
    }

}
