package com.example.selenium.bet;

import com.alibaba.fastjson.JSON;
import com.example.selenium.bo.YaBoInfoBO;
import com.example.selenium.util.SleepUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 俞春旺
 * @program: SeleniumRBGT
 * @date 2022-02-12 15:46:39
 * @description: 描述
 */
public class YaBoUtil {

    /**
     * 定义本地缓存
     */
    private static Map<String, List<YaBoInfoBO>> map = new ConcurrentHashMap<>();

    public static void main(String[] args) {

        // 定义 - 参数
        // competitionName-联赛名称;homeTeamName-主队;awayTeamName-客队;whichSection-第几节;screenings-场次;betAmount-下注金额
        String competitionName = null;
        String homeTeamName = null;
        String awayTeamName = null;
        String whichSection = null;
        Integer screenings = null;
        String betAmount = null;
        YaBoInfoBO yb = null;
        List<YaBoInfoBO> ybList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            // 初始化 - 自动化浏览器
            String chromeDriverUrl = System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe";
            System.setProperty("webdriver.chrome.driver", chromeDriverUrl);
            // 自己本地最新的charm版本，需要添加启动参数
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--no-sandbox");
            WebDriver driver = new ChromeDriver();

            try {
                // 操作 - 打开浏览器
                driver.get("https://im.1f873fef.com/?timestamp=XuTaM99bUJlPtOx5ld7h1oHmOfxqHgVo/YoJSdAIfW0=&token=d25b95674bc9ab9be21cff10356f5714&LanguageCode=CHS");

                // 操作 - 屏幕最大化
                driver.manage().window().maximize();
                SleepUtil.sleepUtil(2000);

                // 操作 - 赛选篮球联赛
                driver.findElement(By.xpath("//*[@id=\"left_panel\"]/div[5]/div/div[2]/div/div[3]/div/label/span")).click();
                SleepUtil.sleepUtil(2000);


                // 获取 - 联赛名称
                List<WebElement> competition_header = driver.findElements(By.className("competition_header"));
                WebElement competition_header_team = competition_header.get(0).findElement(By.className("competition_header_team"));
                // 设定 - 联赛名称
                competitionName = competition_header_team.getText();
                System.out.println("联赛名称：" + competitionName);

                if (map.get(chromeDriverUrl) != null) {
                    System.out.println(JSON.toJSONString(map));
                    System.out.println("已存在，不允许下注");
                    return;
                }

                // 操作 - 获取正在比赛的场次
                List<WebElement> event_listing = driver.findElements(By.className("event_listing"));
                SleepUtil.sleepUtil(2000);

                for (WebElement el : event_listing) {

                    // 获取 - 每个场次比赛
                    List<WebElement> row_live = el.findElements(By.className("row_live"));
                    if (row_live != null && row_live.size() > 0) {
                        // 获取 - 该模块下的所有比赛
                        for (WebElement rl : row_live) {

                            // 获取 - 场次赛事信息
                            List<WebElement> elements = rl.findElement(By.className("team")).findElements(By.className("teamname_inner"));
                            // 设定 - 主队、客队
                            homeTeamName = elements.get(0).getText();
                            awayTeamName = elements.get(1).getText();
                            System.out.println(elements.get(0).getText() + "VS" + elements.get(1).getText());
                            System.out.println("---------------------------");


                            // 操作 - 点击该场比赛进入投注界面
                            SleepUtil.sleepUtil(2000);
                            System.out.println(rl.findElement(By.className("team")).findElements(By.className("teamname_inner")).get(0).getText());
                            System.out.println(rl.findElement(By.className("team")).findElements(By.className("teamname_inner")).get(1).getText());
                            System.out.println("---------------------------");

                            // 获取 - 比分
                            WebElement score = rl.findElement(By.className("team")).findElement(By.className("datetime")).findElement(By.tagName("div"));
                            WebElement node = rl.findElement(By.className("team")).findElement(By.className("datetime")).findElement(By.tagName("span"));
                            System.out.println("score:" + score.isEnabled());
                            System.out.println("node:" + node.isEnabled());
                            // 操作 - 同时存在及进入
                            if (score.isEnabled() && node.isEnabled()) {
                                // 设定 - 节数
                                whichSection = changeWhichSection(node.getText());

                                // 校验 - 第四节不购买
                                if (whichSection.equals("第4节")) {
                                    System.out.println("第四捷比赛，不购买，只对比前面赛果");
                                    continue;
                                }

                                // 获取 - 比分
                                List<WebElement> bfList = row_live.get(0).findElement(By.className("score_row")).findElement(By.className("ended_game_score")).findElements(By.className("ended_score_wrp"));
                                for (WebElement bf : bfList) {
                                    System.out.println("比分：" + bf.getText());
                                }

                                System.out.println("---------------------------");

                                // 获取 - 进入下注页面
                                WebElement aClick = rl.findElement(By.className("additional")).findElement(By.tagName("a"));
                                String ccNumber = aClick.getText();
                                System.out.println("场次：" + ccNumber.substring(0, ccNumber.length() - 1));
                                System.out.println("场次：" + Integer.parseInt(ccNumber.substring(0, ccNumber.length() - 1)));

                                SleepUtil.sleepUtil(3000);
                                aClick.click();

                                System.out.println("===============================投注界面======================================");
                                // 操作 - 重新进入新投注页面

                                // 获取 - 当前页面
                                driver = driver.switchTo().window(driver.getWindowHandle());
                                SleepUtil.sleepUtil(2000);

                                // 操作 - 点击已节为操作
                                WebElement sevmenu_tab_content = driver.findElement(By.className("sevmenu_tab_content"));
                                List<WebElement> sevmenu_tab_inner = sevmenu_tab_content.findElements(By.className("sevmenu_tab_inner"));
                                for (WebElement sti : sevmenu_tab_inner) {
                                    if (sti.getText().contains("节")) {
                                        sti.findElement(By.className("tab_label")).click();
                                        SleepUtil.sleepUtil(2000);
                                    }
                                }

                                // 操作 - 收起全部倍率
                                WebElement sxClick = driver.findElement(By.xpath("//*[@id=\"sev_bet_filter\"]/div[2]/div[2]"));
                                sxClick.click();
                                SleepUtil.sleepUtil(2000);

                                // 获取 - 所有下注头节点
                                List<WebElement> betList = driver.findElements(By.className("bet_type_wrap"));
                                System.out.println("bet size:" + betList.size());
                                for (WebElement bet : betList) {
                                    WebElement betElement = bet.findElement(By.className("bet_type_row")).findElement(By.className("left"));
                                    if (betElement.getText().contains("单/双")) {
                                        System.out.println("下注模块类型：" + betElement.getText());
                                        System.out.println("---------------------------");

                                        // 操作 - 将单双点开
                                        betElement.click();
                                        SleepUtil.sleepUtil(2000);

                                        // 获取 - 最新一节单双选项
                                        List<WebElement> dsList = bet.findElement(By.className("ReactCollapse--collapse"))
                                                .findElement(By.className("ReactCollapse--content"))
                                                .findElement(By.className("bet_type_content"))
                                                .findElement(By.className("content_row"))
                                                .findElement(By.className("odds_col"))
                                                .findElements(By.className("col"));


                                        System.out.println("第一格子选项/倍率：" + dsList.get(0).findElement(By.className("handi")).getText() + "/" + dsList.get(0).findElement(By.className("odds_wrap")).getText());
                                        System.out.println("第二格子选项/倍率：" + dsList.get(1).findElement(By.className("handi")).getText() + "/" + dsList.get(1).findElement(By.className("odds_wrap")).getText());


                                        // 操作 - 下注单
                                        WebElement odds_wrap = dsList.get(0).findElement(By.className("odds_wrap")).findElement(By.className("odds"));
                                        odds_wrap.click();
                                        SleepUtil.sleepUtil(5000);

                                        // 下注 - 单
                                        WebElement bet_slip_wrap = driver.findElement(By.className("bet_slip_wrap"));
                                        List<WebElement> bet_slip = bet_slip_wrap.findElements(By.className("bet_slip"));
                                        for (WebElement bs : bet_slip) {
                                            // 获取 - 下注input
                                            WebElement input_wrap = bs.findElement(By.className("input_amount_wrap")).findElement(By.className("bet_amt_input_wrap"))
                                                    .findElement(By.className("input_wrap"))
                                                    .findElement(By.className("placebet_input"));

                                            // 下注 - 5元
                                            SleepUtil.sleepUtil(2000);
                                            input_wrap.sendKeys("5");

                                            // 下注 - 按钮
                                            SleepUtil.sleepUtil(1000);
                                            WebElement btn_placebet_action = bet_slip_wrap.findElement(By.className("btn_placebet_action"));

                                            // 操作 - 按钮存在且允许点击
                                            if (btn_placebet_action.isEnabled() && btn_placebet_action.isDisplayed()) {
                                                // 点击 - 确认下注
                                                btn_placebet_action.click();

                                                // 下注 - 确认按钮
                                                SleepUtil.sleepUtil(10000);
                                                WebElement btn_placebet_inaction = bet_slip_wrap.findElement(By.className("btn_placebet_inaction"));
                                                if (btn_placebet_inaction.isEnabled() && btn_placebet_inaction.isDisplayed()) {
                                                    btn_placebet_inaction.click();

                                                    yb = new YaBoInfoBO();
                                                    yb.setCompetitionName(competitionName);
                                                    yb.setHomeTeamName(homeTeamName);
                                                    yb.setAwayTeamName(awayTeamName);
                                                    yb.setWhichSection(whichSection);
                                                    yb.setScreenings(1);
                                                    yb.setBetAmount("5");
                                                    ybList.add(yb);

                                                    map.put(yb.getCompetitionName(), ybList);
                                                    System.out.println(JSON.toJSONString(map));
                                                }
                                            } else {
                                                // 操作 - 关闭失败下注
                                                WebElement close = bet_slip_wrap.findElement(By.className("icon-fi-status-close"));
                                                close.click();
                                            }
                                        }
                                        break;
                                    }
                                }
                                System.out.println("===============================投注界面======================================");

                                WebElement main_nav_top_back = driver.findElement(By.xpath("//*[@id=\"sev_event_header\"]/div[1]/div[1]"));
                                main_nav_top_back.click();
                                SleepUtil.sleepUtil(5000);
                                System.out.println("回退至首页");

                                // 结束 - 此次操作
                                driver.close();
                                driver.quit();
                                break;
                            }
                            continue;
                        }
                    }
                    continue;
                }

                // 结束 - 此次操作
                driver.close();
                driver.quit();
            } catch (Exception e) {
                System.out.println(e);
                driver.close();
                driver.quit();
            }
        }
        System.out.println(JSON.toJSONString(map));
    }

    /**
     * 转换节数
     *
     * @param whichSection
     * @return
     */
    public static String changeWhichSection(String whichSection) throws Exception {
        if (whichSection.contains("第1节")) {
            return "第1节";
        } else if (whichSection.contains("第2节")) {
            return "第2节";
        } else if (whichSection.contains("第3节")) {
            return "第3节";
        } else if (whichSection.contains("第4节")) {
            return "第4节";
        } else {
            throw new Exception("查找不到节数");
        }
    }
}



