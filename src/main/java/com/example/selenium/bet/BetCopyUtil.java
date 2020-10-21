package com.example.selenium.bet;

import cn.hutool.cache.Cache;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.example.selenium.dto.InstructionDTO;
import com.example.selenium.util.DingUtil;
import com.example.selenium.util.SleepUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
    public void betSend(InstructionDTO ins, Cache<String, String> fifoCache) {
        // chromeDriver服务地址，需要手动下载
        // 测试环境：[D:\00002YX\chromedriver.exe]地址需要自己给
        // String chromeDriverUrl = "C:\\software\\chrome\\chromedriver.exe";
        // 正式环境：System.getProperty("user.dir")+"\\src\\main\\resources\\chromedriver.exe";
        String chromeDriverUrl = System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe";
        System.out.println(chromeDriverUrl);
        System.setProperty("webdriver.chrome.driver", chromeDriverUrl);
        // 自己本地最新的charm版本，需要添加启动参数
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        WebDriver driver = new ChromeDriver();
        try {
            // 登录信息
            login(driver);
            // 点击188赛事信息
            btnSend(driver);
        } catch (Exception e) {
            // 出现异常直接关闭浏览器
            DingUtil dingUtil = new DingUtil();
            dingUtil.sendMassage("\\rBSMC : " + ins.getBetHtn() + "VS" + ins.getBetAtn() + "[第" + ins.getBetSession() + "节 / " + (ins.getBetSingleOrDouble() == 1 ? "搭" : "洒") + "] \\rXZJR : " + ins.getBetAmount() + " \\rXZBS : 失败了 \\rXZCS : " + ins.getBetNumber() + " \\rZJEM : 浏览器网络延迟");
            // 清除KEY
            fifoCache.remove(ins.getId());
            driver.quit();
        }
        // 下注
        betTargetSend(driver, ins);
        System.out.println("=================== 关闭浏览器 ====================");
        driver.quit();
    }

    /**
     * 描述：登录网站
     *
     * @param driver
     */
    public void login(WebDriver driver) {
        // 目标URL
        driver.get("https://www.52365l.com");
        driver.manage().window().maximize();
        SleepUtil.sleepUtil(2000);
        // 账号:dhxm2376
        WebElement zh = driver.findElement(By.xpath("/html/body/div[2]/div/div[2]/div/div/form/div[1]/div/div[1]/div/input"));
        zh.sendKeys("dhxm2376");
        // 密码:ycw15659512376"
        WebElement mm = driver.findElement(By.xpath("/html/body/div[2]/div/div[2]/div/div/form/div[1]/div/div[2]/div[1]/input"));
        mm.sendKeys("ycw8324479");
        SleepUtil.sleepUtil(2000);
        //点击确定按钮
        driver.findElement(By.xpath("/html/body/div[2]/div/div[2]/div/div/form/div[2]/button")).click();
    }

    /**
     * 描述：按钮点击篮球赛事
     *
     * @param driver
     */
    public void btnSend(WebDriver driver) {
        SleepUtil.sleepUtil(4000);
        try{
            // //*[@id="indexann"]/h2/div
            driver.findElement(By.xpath("//*[@id=\"indexann\"]/h2/div")).click();
        }catch (Exception e){
            System.out.println("点击不到数据");
        }
        SleepUtil.sleepUtil(2000);
        // 点击体育赛事
        driver.findElement(By.xpath("//*[@id=\"index\"]/div[2]/div/div/ul/li/a/div")).click();
        SleepUtil.sleepUtil(4000);

        // 点击BBIT赛事
        List<WebElement> elements = driver.findElements(By.className("sport-list"));
        for (WebElement e : elements) {
            WebElement img = e.findElement(By.tagName("img"));
            String src = img.getAttribute("src");
            if (src.contains("3149")) {
                img.click();
                break;
            }
        }
        SleepUtil.sleepUtil(8000);
    }

    /**
     * 描述：发送下注
     *
     * @param driver
     */
    private void betTargetSend(WebDriver driver, InstructionDTO ins) {
        DingUtil dingUtil = new DingUtil();
        try {
            Thread.sleep(4000);
            // 点击滚球按钮
            driver.findElement(By.xpath("//*[@id=\"sp2\"]/div[2]/span")).click();
            Thread.sleep(4000);
            List<WebElement> table = driver.findElements(By.tagName("table"));
            log.info("table头部数据 -> {}", table.size());
            // 循环判断最近的篮球赛事
            for (WebElement e : table) {
                // 判断不为空的篮球赛事
                if (StrUtil.isNotBlank(e.getAttribute("id"))) {
                    log.info("对比数据 -> {}", e.findElement(By.className("dsp-iblk")).getText() + " -> " + ins.getBetHtn());
                    log.info("对比数据结果 -> {}", e.findElement(By.className("dsp-iblk")).getText().equals(ins.getBetHtn()));
                    // 判断需要购买的是否匹配
                    if (e.findElement(By.className("dsp-iblk")).getText().equals(ins.getBetHtn())) {
                        e.findElement(By.className("dsp-iblk")).click();
                        Thread.sleep(1000);
                        List<WebElement> allMarkets = driver.findElements(By.id("allMarkets"));
                        List<WebElement> element = allMarkets.get(0).findElements(By.className("mg-t-1"));
                        for (int i = 0; i < element.size(); i++) {
                            // 判断是否单双
                            log.info("对比数据 -> {}", element.get(i).findElement(By.className("fts-15")).getText() + " -> " + ins.getBetSessionName());
                            log.info("对比数据结果 -> {}", element.get(i).findElement(By.className("fts-15")).getText().equals(ins.getBetSessionName()));
                            if (element.get(i).findElement(By.className("fts-15")).getText().equals(ins.getBetSessionName())) {
                                // 判断点击单双购买操作：[1:单；2：双]
                                Thread.sleep(1000);
                                if (ins.getBetSingleOrDouble() == 1) {
                                    // 点击单
                                    System.out.println("请求参数：" + allMarkets.get(0).findElements(By.className("bg-c-43")).get(i).findElements(By.className("OddsWrapper")).get(0).findElement(By.className("odds")).getText());
                                    allMarkets.get(0).findElements(By.className("bg-c-43")).get(i).findElements(By.className("OddsWrapper")).get(0).findElement(By.className("odds")).click();
                                } else {
                                    // 点击双
                                    System.out.println("请求参数：" + allMarkets.get(0).findElements(By.className("bg-c-43")).get(i).findElements(By.className("OddsWrapper")).get(1).findElement(By.className("odds")).getText());
                                    allMarkets.get(0).findElements(By.className("bg-c-43")).get(i).findElements(By.className("OddsWrapper")).get(1).findElement(By.className("odds")).click();
                                }
                                Thread.sleep(1000);
                                // 下注操作
                                WebElement em = driver.findElement(By.className("js-stake"));
                                log.info("下注金额 -> {}", ins.getBetAmount() + "");
                                em.sendKeys(ins.getBetAmount() + "");
                                Thread.sleep(1000);
                                driver.findElement(By.className("js-placebet")).click();
                                Thread.sleep(6000);
                                try {
                                    String text = driver.findElement(By.xpath("//*[@id=\"lt-left\"]/div[3]/div/div/div[3]/div[2]/div/div[2]/div[4]/span[2]")).getText();
                                    System.out.println("请求码：" + text);
                                    System.out.println("下注成功：" + URL_SUCCESS + ins.getId());
                                    String s = HttpUtil.get(URL_SUCCESS + ins.getId());
                                    System.out.println(s);
                                    String ZJE = driver.findElement(By.xpath("//*[@id=\"lt-left\"]/div[1]/span[2]")).getText();
                                    dingUtil.sendMassage("\\rBSMC : " + ins.getBetHtn() + "VS" + ins.getBetAtn() + "[第" + ins.getBetSession() + "节 / " + (ins.getBetSingleOrDouble() == 1 ? "搭" : "洒") + "] \\rXZJR : " + ins.getBetAmount() + " \\rXZBS : 成功了 \\rXZCS : " + ins.getBetNumber() + " \\rZJE : " + ZJE);
                                } catch (Exception ex) {
                                    String text = driver.findElement(By.xpath("//*[@id=\"lt-left\"]/div[3]/div/div/div[3]/div[2]/div/div[2]/div[6]")).getText();
                                    System.out.println("请求码：" + text);
                                    String s = HttpUtil.get(URL_ERROR + ins.getId());
                                    System.out.println(s);
                                    String ZJE = driver.findElement(By.xpath("//*[@id=\"lt-left\"]/div[1]/span[2]")).getText();
                                    dingUtil.sendMassage("\\rBSMC : " + ins.getBetHtn() + "VS" + ins.getBetAtn() + "[第" + ins.getBetSession() + "节 / " + (ins.getBetSingleOrDouble() == 1 ? "搭" : "洒") + "] \\rXZJR : " + ins.getBetAmount() + " \\rXZBS : 失败了 \\rXZCS : " + ins.getBetNumber() + " \\rZJE : " + ZJE);
                                    System.out.println("=======================点击篮球赛事报错了=======================");
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.info("下注失败：" + URL_ERROR + ins.getId());
            String s = HttpUtil.get(URL_ERROR + ins.getId());
            System.out.println(s);
            String ZJE = driver.findElement(By.xpath("//*[@id=\"lt-left\"]/div[1]/span[2]")).getText();
            dingUtil.sendMassage("\\rBSMC : " + ins.getBetHtn() + "VS" + ins.getBetAtn() + "[第" + ins.getBetSession() + "节 / " + (ins.getBetSingleOrDouble() == 1 ? "搭" : "洒") + "] \\rXZJR : " + ins.getBetAmount() + " \\rXZBS : 失败了 \\rXZCS : " + ins.getBetNumber() + " \\rZJEM : " + ZJE);
            System.out.println("=======================点击篮球赛事报错了=======================");
            return;
        }
    }
}
