package com.example.selenium.test;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;


public class SeleniumFirst01 {

    public static void main(String[] args) {
        //chromedriver服务地址，需要手动下载
        System.setProperty("webdriver.chrome.driver","D:\\001RJ\\015Chormedriver\\chromedriver.exe");
        //自己本地最新的charm版本，需要添加启动参数
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--no-sandbox");

        WebDriver driver = new ChromeDriver();
        //目标URL
        driver.get("https://www.52365v.com");
        driver.manage().window().maximize();
        //获取用户输入框
        WebElement elementZh = driver.findElement(By.xpath("//*[@id=\"login\"]/form/div[1]/div[1]/input"));
        System.out.println("1=="+elementZh.getTagName());
        //清空输入框
        elementZh.clear();

        //获取用户输入框
        WebElement elementMm = driver.findElement(By.xpath("//*[@id=\"login\"]/form/div[1]/div[2]/input"));
        System.out.println("1=="+elementMm.getTagName());
        //清空输入框
        elementMm.clear();

        elementZh.sendKeys("dhxm2376");
        elementMm.sendKeys("dhxm2376");

        //点击确定按钮
        driver.findElement(By.xpath("//*[@id=\"login\"]/form/button")).click();

        // 点击体育赛事
        driver.findElement(By.xpath("//*[@id=\"head\"]/div[2]/div[1]/div/a/div/div")).click();

        // 点击188赛事
        driver.findElement(By.xpath("/html/body/div[6]/div/div[2]/div/a[3]/img")).click();

        //存在iframe,首先需要进到iframe
        ((JavascriptExecutor)driver).executeScript("document.getElementsByTagName(\"iframe\").item(0).setAttribute(\"sportframe\",\"sportframe\")");
        driver.switchTo().frame("sportframe");
        try {
            // 60秒
            System.out.println("休眠开始");
            Thread.sleep(5000);
            System.out.println("休眠结束");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.findElement(By.xpath("//*[@id=\"sp2\"]/div[2]/span")).click();
        try {
            // 60秒
            System.out.println("休眠开始");
            Thread.sleep(5000);
            System.out.println("休眠结束");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 点击篮球滚球
        List<WebElement> table = driver.findElements(By.tagName("table"));
        System.out.println("我是获取table数量：" +table.size());
        for(WebElement e : table){
            System.out.println(e.getAttribute("id"));
        }
        System.out.println("需要点击的table:" + table.get(2).getAttribute("id"));
        System.out.println("需要点击的tabl的中文:" + table.get(2).findElement(By.className("dsp-iblk")).getText());
        table.get(2).findElement(By.className("dsp-iblk")).click();

        try {
            // 60秒
            System.out.println("休眠开始");
            Thread.sleep(5000);
            System.out.println("休眠结束");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.findElements(By.id("allMarkets"));
////
////        //存在iframe,首先需要进到iframe
////        ((JavascriptExecutor)driver).executeScript("document.getElementsByTagName(\"iframe\").item(0).setAttribute(\"id\",\"asn\")");
////        driver.switchTo().frame("asn");

        //获取标题
//        String title = driver.getTitle();
//        System.out.printf(title);
//
//        //获取输入框
//        WebElement element = driver.findElement(By.xpath("//*[@id="login"]/form/div[1]/div[1]/input"));
//        System.out.println("1=="+element.getTagName());
//        //清空输入框
//        element.clear();
//
////      WebElement element1 = driver.findElement(By.id("kw"));
////      System.out.println("2=="+element1.getTagName());
////      element1.clear();
//
//        //隐式等待2秒，TimeUnit.SECONDS表示单位秒
//        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
//
//        //在百度的输入框中输入lao4j
//        element.sendKeys("lao4j");
//
//        //点击确定按钮
//        driver.findElement(By.xpath("//input[@id='su']")).click();
//        //隐式等待2秒，TimeUnit.SECONDS表示单位秒
//        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
//
//        //获取搜索出来的第一条信息的文本值
//        String text = driver.findElement(By.xpath("//*[@id=\"1\"]/h3/a")).getText();
//
//        //判断获取的信息是否包含lao4j的博客 - CSDN博客
//        Assert.assertThat( text, containsString( "lao4j的博客" ));
//
//        //关闭浏览器
//        driver.quit();
//
//        // 浏览器最大化
//        //driver.manage().window().maximize();
//
//        // 设置浏览器的高度为800像素，宽度为480像素
//        //driver.manage().window().setSize(new Dimension(800, 600))
//
//        // 隐式等待
//        //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//
//        // 浏览器后退
//        //driver.navigate().back();
//
//        // 浏览器前进
//        //driver.navigate().forward()

    }

}