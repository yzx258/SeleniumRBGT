package com.example.selenium.bet;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.example.selenium.dto.InstructionDTO;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

/**
 * @author yucw
 * @date 2020/8/14 14:14
 * 描述：188下注平台
 */
@Slf4j
public class BetCopyUtil {

    private static String URL_ERROR = "http://47.106.143.218:8081/instruction/update/error/";
    private static String URL_SUCCESS = "http://47.106.143.218:8081/instruction/update/success/";

    /**
     * 描述：188下注
     *
     * @param
     */
    public void betSend(InstructionDTO ins) {
        // chromeDriver服务地址，需要手动下载
        // 测试环境：D:\00002YX\chromedriver.exe
        // String chromeDriverUrl = "C:\\software\\chrome\\chromedriver.exe";
        // 正式环境：C:\\software\\chrome\\chromedriver.exe
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
        betSend(driver, ins);
        System.out.println("=================== 关闭浏览器 ====================");
        driver.quit();
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
        // elementZh.sendKeys("dhxm2376");
        // elementMm.sendKeys("dhxm2376");
        elementZh.sendKeys("huangxr");
        elementMm.sendKeys("hx8325554");
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
    private void betSend(WebDriver driver, InstructionDTO ins) {

        try {
            Thread.sleep(2000);
            // 点击滚球按钮
            driver.findElement(By.xpath("//*[@id=\"sp2\"]/div[2]/span")).click();
            Thread.sleep(4000);
            List<WebElement> table = driver.findElements(By.tagName("table"));
            System.out.println("获取table数据：" + table.size());
            // 循环判断最近的篮球赛事
            for (WebElement e : table) {
                // 判断不为空的篮球赛事
                if (StrUtil.isNotBlank(e.getAttribute("id"))) {
                    System.out.println(e.findElement(By.className("dsp-iblk")).getText() + " -> " + ins.getBetHtn());
                    System.out.println(e.findElement(By.className("dsp-iblk")).getText().equals(ins.getBetHtn()));
                    // 判断需要购买的是否匹配
                    if (e.findElement(By.className("dsp-iblk")).getText().equals(ins.getBetHtn())) {
                        e.findElement(By.className("dsp-iblk")).click();
                        Thread.sleep(1000);
                        List<WebElement> allMarkets = driver.findElements(By.id("allMarkets"));
                        List<WebElement> element = allMarkets.get(0).findElements(By.className("mg-t-1"));
                        for (int i = 0; i < element.size(); i++) {
                            System.out.println(element.get(i).findElement(By.className("fts-15")).getText());
                            // 判断是否单双
                            System.out.println(element.get(i).findElement(By.className("fts-15")).getText() + " -> " + ins.getBetSessionName());
                            System.out.println(element.get(i).findElement(By.className("fts-15")).getText().equals(ins.getBetSessionName()));
                            if (element.get(i).findElement(By.className("fts-15")).getText().equals(ins.getBetSessionName())) {
                                // 判断点击单双购买操作：[1:单；2：双]
//                                List<WebElement> oddsWrapper = allMarkets.get(0).findElements(By.className("bg-c-43")).get(i).findElements(By.className("OddsWrapper"));
//                                for (WebElement w : oddsWrapper) {
//                                    System.out.println(w.getAttribute("id"));
//                                    System.out.println("请求参数："+w.findElement(By.className("odds")).getText());
//                                }
                                Thread.sleep(1000);
                                if (ins.getBetSingleOrDouble() == 1) {
                                    // 点击单
                                    System.out.println("请求参数："+allMarkets.get(0).findElements(By.className("bg-c-43")).get(i).findElements(By.className("OddsWrapper")).get(0).findElement(By.className("odds")).getText());
                                    allMarkets.get(0).findElements(By.className("bg-c-43")).get(i).findElements(By.className("OddsWrapper")).get(0).findElement(By.className("odds")).click();
                                } else {
                                    // 点击双
                                    System.out.println("请求参数："+allMarkets.get(0).findElements(By.className("bg-c-43")).get(i).findElements(By.className("OddsWrapper")).get(1).findElement(By.className("odds")).getText());
                                    allMarkets.get(0).findElements(By.className("bg-c-43")).get(i).findElements(By.className("OddsWrapper")).get(1).findElement(By.className("odds")).click();
                                }
                                Thread.sleep(1000);
                                // 下注操作
                                WebElement monery = driver.findElement(By.className("js-stake"));
                                log.info("下注金额 -> {}", ins.getBetAmount() + "");
                                monery.sendKeys(ins.getBetAmount() + "");
                                Thread.sleep(1000);
                                driver.findElement(By.className("js-placebet")).click();
                                Thread.sleep(6000);
                                try {
                                    // String text = driver.findElement(By.className("js-msg")).findElement(By.className("t-va-m")).getText();
                                    String text = driver.findElement(By.xpath("//*[@id=\"lt-left\"]/div[3]/div/div/div[3]/div[2]/div/div[2]/div[4]/span[2]")).getText();
                                    System.out.println("请求码："+text);
                                    System.out.println("下注成功："+URL_SUCCESS + ins.getId());
                                    String s = HttpUtil.get(URL_SUCCESS + ins.getId());
                                    System.out.println(s);
                                }catch (Exception ex){
                                    // String text = driver.findElement(By.className("js-message")).getText();
                                    String text = driver.findElement(By.xpath("//*[@id=\"lt-left\"]/div[3]/div/div/div[3]/div[2]/div/div[2]/div[6]")).getText();
                                    System.out.println("请求码："+text);
                                    System.out.println("下注失败："+URL_ERROR + ins.getId());
                                    String s = HttpUtil.get(URL_ERROR + ins.getId());
                                    System.out.println(s);
                                    System.out.println("=======================点击篮球赛事报错了=======================");
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("下注失败："+URL_ERROR + ins.getId());
            String s = HttpUtil.get(URL_ERROR + ins.getId());
            System.out.println(s);
            System.out.println("=======================点击篮球赛事报错了=======================");
            return;
        }
    }
}
