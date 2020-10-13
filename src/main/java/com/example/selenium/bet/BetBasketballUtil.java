package com.example.selenium.bet;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

/**
 * @company： 厦门宜车时代信息技术有限公司
 * @copyright： Copyright (C), 2020
 * @author： yucw
 * @date： 2020/10/13 13:59
 * @version： 1.0
 * @description:
 */
@Slf4j
public class BetBasketballUtil {

    public static void main(String[] args) throws InterruptedException {
        // chromeDriver服务地址，需要手动下载
        // 测试环境：[D:\00002YX\chromedriver.exe]地址需要自己给
        // String chromeDriverUrl = "C:\\software\\chrome\\chromedriver.exe";
        // 正式环境：System.getProperty("user.dir")+"\\src\\main\\resources\\chromedriver.exe";
        String chromeDriverUrl = System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe";
        System.out.println(chromeDriverUrl);
        System.setProperty("webdriver.chrome.driver", "D:\\00002YX\\chromedriver.exe");
        // 自己本地最新的charm版本，需要添加启动参数
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        WebDriver driver = new ChromeDriver();
        BetCopyUtil betCopyUtil = new BetCopyUtil();
        // 登录信息
        betCopyUtil.login(driver);
        // 点击新BBIT赛事信息
        betCopyUtil.btnSend(driver);
        // 点击滚球按钮
        driver.findElement(By.xpath("/html/body/div[2]/div/a[1]")).click();
        Thread.sleep(4000);

        // 获取新的ifram
        driver.switchTo().frame("bcsportsbookiframe");
        Thread.sleep(4000);
        // 点击篮球
        driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[1]/div/div/div[4]/div[2]/ul/div[4]/div/li/div")).click();
        Thread.sleep(4000);
        // 点击刷新按钮，确保正常连接
        driver.findElement(By.xpath("//*[@id=\"asianView\"]/div/div[3]/div[1]/div[2]/button")).click();

        List<WebElement> table = driver.findElements(By.tagName("table"));
        log.info("table头部数据 -> {}", table.size());
        // 循环判断最近的篮球赛事
        for (WebElement e : table) {
            Thread.sleep(4000);
            // 判断不为空的篮球赛事
            List<WebElement> trs = e.findElements(By.tagName("tr"));
            System.out.println("trs 大小 -> " + trs.size());
            if (trs.size() >= 2) {
                List<WebElement> td1 = trs.get(1).findElements(By.tagName("td"));
                List<WebElement> td2 = trs.get(2).findElements(By.tagName("td"));
                if (td1.size() > 2 && td2.size() > 2) {
                    // 比赛第几节
                    String djj = td1.get(0).getText().replaceAll("\r|\n", "/");
                    // 比赛队伍名称
                    String zd = td1.get(1).getText();
                    zd = zd.replaceAll("\r|\n", "");
                    String kd = td2.get(0).getText();
                    kd = kd.replaceAll("\r|\n", "");
                    System.out.println(djj + ".contains(\"即将开赛\"): " + !djj.contains("即将开赛"));
                    if (StrUtil.isNotBlank(kd) && StrUtil.isNotBlank(zd) && !djj.contains("即将开赛")) {
                        System.out.println("====================================");
                        System.out.println("td1 大小 ：" + td1.size());
                        System.out.println("td2 大小 ：" + td2.size());
                        System.out.println("比赛进行中:" + djj);
                        System.out.println(zd + " VS " + kd);
                        // 点击下注
                        //*[@id="asianView"]/div/div[3]/div[6]/div/ng-include/div[2]/div[4]/div[1]/table/tbody/tr[3]/td[6]/p
                        String text = td2.get(5).getText();
                        System.out.println("我是获取的数据：" + text);
                        td2.get(5).findElement(By.tagName("p")).click();

                        try {
                            // 查看比赛分数
                            List<WebElement> elements = driver.findElement(By.className("driver")).findElements(By.tagName("ul"));
                            // 主队分数
                            List<WebElement> li1 = elements.get(1).findElements(By.tagName("li"));
                            // 客队分数
                            List<WebElement> li2 = elements.get(2).findElements(By.tagName("li"));
                            System.out.println(li2.get(1).getText() + " : " + li2.get(1).getText());
                            Thread.sleep(2000);
                            System.out.println("正常返回数据");
                            driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[1]/ul/li[2]/div/p")).click();
                        } catch (Exception e1) {
                            System.out.println("点击不到数据，点击返回");
                            Thread.sleep(2000);
                            driver.findElement(By.xpath("/html/body/div/div[2]/div/div/div/div/div/div[3]/div[1]/ul/li[2]/div/p")).click();
                        }
                    }
                }
            }

        }
    }
}
