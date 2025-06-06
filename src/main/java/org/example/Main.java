package org.example;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {

    private static final Random rnd = new Random();

    public static List<String> browsers = List.of(
            //实验阶段 前十个号用来跑脚本
            "kxyw4p3",
            "kxyw4p0",
            "kxyw4oy",
            "kxyw4ox",
            "kxyw4ow",
            "kxyw4ov",
            "kxyw4ot",
            "kxyw4os",
            "kxyw4or",
            "kxyw4oq",
            "kxyw4op",
            "kxyw4oo",
            "kxyw4on",
            "kxyw4om",
            "kxyw4ok",
            "kxyw4oj",
            "kxyw4oi",
            "kxyw4oh",
            "kxyw4og",
            "kxyw4of",
            "kxyw4oe",
            "kxyw4od",
            "kxyw4oc",
            "kxyw4ob",
            "kxyw4oa",
            "kxyw4o9",
            "kxyw4o8",
            "kxyw4o7",
            "kxyw4o6",
            "kxyw4o5",
            "kxyw4o4",
            "kxyw4o3",
            "kxyw4o2",
            "kxyw4o1",
            "kxyw4ny",
            "kxyw4nx",
            "kxyw4nw",
            "kxyw4nv",
            "kxyw4nu",
            "kxyw4nt",
            "kxyw4ns",
            "kxyw4nr",
            "kxyw4no",
            "kxyw4nn",
            "kxyw4nl",
            "kxyw4nk",
            "kxyw4nj",
            "kxyw4ng",
            "kxyw4nf",
            "kxyw4ne"
    );


    public static List<String> users = List.of(
            "@mia_okx",
            "@okx",
            "@binancezh",
            "@cz_binance",
            "@_TAOLIMAO",
            "@binancezh",
            "@wallet",
            "@aeyakovenko",
            "@soon_svm",
            "@elonmusk",
            "@teslaownersSV",
            "@BTCdayu",
            "@Fencun_nft",
            "@ashiikesnow",
            "@yuyue_chris",
            "@_FORAB",
            "@traderxiaoxia",
            "@ai_9684xtpa",
            "@cryptocishanjia",
            "@0xAA_Science",
            "@maid_crypto",
            "@jason_chen998",
            "@Paris13Jeanne",
            "@hebi555",
            "@Ice_Frog666666",
            "@hualunjiejie",
            "@pepecoineth",
            "@youngerbest",
            "@youngerbest",
            "@Mimoo1201",
            "@cookiedotfun",
            "@chuishaoren1",
            "@hongshen6666btc",
            "@Crypto_Cat888",
            "@AKAKAY04",
            "@wenxue600",
            "@r2yield",
            "@skate_chain",
            "@sunyuchentron",
            "@Galxe",
            "@RootDataCrypto",
            "@hualunjiejie",
            "@EvaCmore",
            "@0xAllen888",
            "@0xcryptowizard"
    );

    public static void main(String[] args) throws InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        long start = System.currentTimeMillis();
        for (String browser : browsers) {
            System.out.println("开始执行:" + browser);
            String debugPort = "";
            String webDriver = "";
            try {
                HttpResponse<String> response;
                while (true){
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI("http://127.0.0.1:50325/api/v1/browser/start?user_id=" + browser))
                            .GET()
                            .build();
                    response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println(response.body());
                    if ((int)JSON.parseObject(response.body()).get("code") == 0){
                        break;
                    } else {
                        Thread.sleep(10 * 1000);
                    }
                }
                JSONObject jsonObject = JSON.parseObject(response.body());
                Map<String, String> data = (Map<String, String>) jsonObject.get("data");
                debugPort = data.get("debug_port");
                webDriver = data.get("webdriver");
            } catch (Exception e) {
                System.out.println("error:" + e.getMessage());
            }

            // 设置 chromedriver 路径
            System.setProperty("webdriver.chrome.driver", webDriver);

            // 配置 ChromeOptions
            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("debuggerAddress", "127.0.0.1:" + debugPort);
            options.addArguments("--disable-blink-features=AutomationControlled");

            WebDriver driver = new ChromeDriver(options);

            try {
                // 隐藏 navigator.webdriver 特征
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined});");

                // 打开 Twitter 主页
                driver.get("https://twitter.com/home");
                humanDelay(5000, 10000);

                // 执行养号操作
                simulateNaturalBehavior(driver);

                // 随机选择一个要关注的账号
                List<String> accountsToFollow = Arrays.asList(users.get(rnd.nextInt(users.size())));
                for (String account : accountsToFollow) {
                    followSpecificAccount(driver, account);
                }

                //关闭浏览器
                HttpResponse<String> response;
                while (true){
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI("http://127.0.0.1:50325/api/v1/browser/stop?user_id=" + browser))
                            .GET()
                            .build();
                    response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println(response.body());
                    if ((int)JSON.parseObject(response.body()).get("code") == 0){
                        break;
                    } else {
                        Thread.sleep(10 * 1000);
                    }
                }

                System.out.println("养号及关注操作执行完毕");
            } catch (Exception e) {
                System.out.println("脚本执行出错: " + e.getMessage());
            } finally {
                driver.quit();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("succeed, total time: " + (end - start) + "ms");
    }

    // 模拟自然用户行为
    private static void simulateNaturalBehavior(WebDriver driver) throws InterruptedException {
        simulateHomePageBrowsing(driver);
        searchAndBrowseAccount(driver, users.get(rnd.nextInt(users.size())));
        simulateExplorePage(driver);
        simulateTweetInteraction(driver);
    }

    // 优化主页浏览：时间再减半
    private static void simulateHomePageBrowsing(WebDriver driver) throws InterruptedException {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("article")));
            int scrollTimes = 13 + rnd.nextInt(8); // 13-20 次
            Long lastScrollY = 0L;
            for (int i = 0; i < scrollTimes; i++) {
                if (rnd.nextBoolean()) { // 50% 快速滑动
                    int fastScrollTimes = 2 + rnd.nextInt(4);
                    for (int j = 0; j < fastScrollTimes; j++) {
                        humanSmoothScroll(driver, 300 + rnd.nextInt(500));
                        humanDelay(200, 500);
                    }
                } else { // 50% 阅读阶段
                    if (rnd.nextBoolean()) {
                        humanDelay(3000, 10000);
                    } else {
                        int slowScrollTimes = 3 + rnd.nextInt(3);
                        for (int j = 0; j < slowScrollTimes; j++) {
                            humanSmoothScroll(driver, 20 + rnd.nextInt(30));
                            humanDelay(100, 300);
                        }
                    }
                }
                Long currentScrollY = getScrollY(driver);
                System.out.println("主页滚动位置: " + currentScrollY);
                if (currentScrollY < lastScrollY) {
                    System.out.println("检测到页面回滚到顶部，恢复到: " + lastScrollY);
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("window.scrollTo(0, arguments[0]);", lastScrollY);
                }
                lastScrollY = currentScrollY;
                if (rnd.nextInt(10) < 2) {
                    humanRandomMouseMove(driver);
                }
                if (rnd.nextInt(10) < 3) {
                    List<WebElement> images = driver.findElements(By.cssSelector("img[alt][src*='twimg.com']"));
                    if (!images.isEmpty()) {
                        WebElement image = images.get(rnd.nextInt(images.size()));
                        humanMoveAndHover(driver, image);
                        humanDelay(1000, 3000);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("主页浏览失败: " + e.getMessage());
        }
    }

    // 抽取搜索账号逻辑
    private static WebElement searchAccount(WebDriver driver, String account) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement searchBox = null;
        for (int attempt = 1; attempt <= 2; attempt++) {
            try {
                searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[aria-label='Search query']")));
                break;
            } catch (TimeoutException e) {
                System.out.println("尝试 " + attempt + " 次定位搜索框失败，重试...");
                humanDelay(2000, 5000);
            }
        }
        if (searchBox == null) {
            throw new NoSuchElementException("无法定位搜索框");
        }

        for (char c : account.toCharArray()) {
            searchBox.sendKeys(String.valueOf(c));
            Thread.sleep(100 + rnd.nextInt(200));
        }
        searchBox.sendKeys(Keys.ENTER);
        humanDelay(3000, 6000);

        WebElement accountLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='/" + account.substring(1) + "']")));
        return accountLink;
    }

    // 搜索并浏览特定账号
    private static void searchAndBrowseAccount(WebDriver driver, String account) throws InterruptedException {
        try {
            WebElement accountLink = searchAccount(driver, account);
            humanMoveAndHover(driver, accountLink);
            accountLink.click();
            humanDelay(3000, 6000);

            int scrollTimes = 5 + rnd.nextInt(6); // 5-10 次
            Long lastScrollY = 0L;
            for (int i = 0; i < scrollTimes; i++) {
                if (rnd.nextBoolean()) {
                    int fastScrollTimes = 2 + rnd.nextInt(4);
                    for (int j = 0; j < fastScrollTimes; j++) {
                        humanSmoothScroll(driver, 300 + rnd.nextInt(500));
                        humanDelay(200, 500);
                    }
                } else {
                    if (rnd.nextBoolean()) {
                        humanDelay(3000, 10000);
                    } else {
                        int slowScrollTimes = 3 + rnd.nextInt(3);
                        for (int j = 0; j < slowScrollTimes; j++) {
                            humanSmoothScroll(driver, 20 + rnd.nextInt(30));
                            humanDelay(100, 300);
                        }
                    }
                }
                Long currentScrollY = getScrollY(driver);
                System.out.println("账号页滚动位置: " + currentScrollY);
                if (currentScrollY < lastScrollY) {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("window.scrollTo(0, arguments[0]);", lastScrollY);
                }
                lastScrollY = currentScrollY;
                if (rnd.nextInt(10) < 2) {
                    humanRandomMouseMove(driver);
                }
            }
        } catch (Exception e) {
            System.out.println("搜索或浏览账号 " + account + " 失败: " + e.getMessage());
        }
    }

    // 关注特定账号：先返回顶部再关注，随机点赞一条帖子
    private static void followSpecificAccount(WebDriver driver, String account) throws InterruptedException {
        try {
            WebElement accountLink = searchAccount(driver, account);
            humanMoveAndHover(driver, accountLink);
            accountLink.click();
            humanDelay(3000, 6000);

            int scrollTimes = 5 + rnd.nextInt(6); // 5-10 次
            Long lastScrollY = 0L;
            for (int i = 0; i < scrollTimes; i++) {
                if (rnd.nextBoolean()) {
                    int fastScrollTimes = 2 + rnd.nextInt(4);
                    for (int j = 0; j < fastScrollTimes; j++) {
                        humanSmoothScroll(driver, 300 + rnd.nextInt(500));
                        humanDelay(200, 500);
                    }
                } else {
                    if (rnd.nextBoolean()) {
                        humanDelay(3000, 10000);
                    } else {
                        int slowScrollTimes = 3 + rnd.nextInt(3);
                        for (int j = 0; j < slowScrollTimes; j++) {
                            humanSmoothScroll(driver, 20 + rnd.nextInt(30));
                            humanDelay(100, 300);
                        }
                    }
                }
                Long currentScrollY = getScrollY(driver);
                System.out.println("关注页滚动位置: " + currentScrollY);
                if (currentScrollY < lastScrollY) {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("window.scrollTo(0, arguments[0]);", lastScrollY);
                }
                lastScrollY = currentScrollY;
                if (rnd.nextInt(10) < 2) {
                    humanRandomMouseMove(driver);
                }
            }

            // 返回页面顶部
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollTo(0, 0);");
            System.out.println("返回页面顶部，滚动位置: " + getScrollY(driver));
            humanDelay(1000, 3000);

            // 关注账号
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            try {
                WebElement followButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[aria-label*='Follow @']")));
                humanMoveAndHover(driver, followButton);
                followButton.click();
                System.out.println("成功关注了 " + account);
                humanDelay(3000, 7000);
            } catch (TimeoutException e) {
                try {
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[aria-label*='Following']")));
                    System.out.println("已经关注了 " + account);
                } catch (TimeoutException e2) {
                    System.out.println("无法找到关注按钮，可能页面未加载或选择器失效");
                }
            }

            // 随机点赞一条帖子
//            try {
//                List<WebElement> tweets = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("article")));
//                if (!tweets.isEmpty()) {
//                    WebElement tweet = tweets.get(rnd.nextInt(tweets.size()));
//                    // 确保推文在视口中
//                    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", tweet);
//                    humanDelay(1000, 2000);
//
//                    // 定位点赞按钮
//                    WebElement likeButton = tweet.findElement(By.cssSelector("button[aria-label*='Like']"));
//                    humanMoveAndHover(driver, likeButton);
//                    likeButton.click();
//                    System.out.println("成功点赞了 " + account + " 的一条推文");
//                    humanDelay(1000, 3000);
//                } else {
//                    System.out.println("未找到 " + account + " 的推文，跳过点赞");
//                }
//            } catch (Exception e) {
//                System.out.println("点赞 " + account + " 的推文失败: " + e.getMessage());
//            }
        } catch (Exception e) {
            System.out.println("关注 " + account + " 失败: " + e.getMessage());
        }
    }

    // 模拟探索页面
    private static void simulateExplorePage(WebDriver driver) throws InterruptedException {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            WebElement explore = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='/explore']")));
            humanMoveAndHover(driver, explore);
            explore.click();
            humanDelay(4000, 8000);
            int scrollTimes = 3 + rnd.nextInt(3); // 3-5 次
            Long lastScrollY = 0L;
            for (int i = 0; i < scrollTimes; i++) {
                if (rnd.nextBoolean()) {
                    int fastScrollTimes = 2 + rnd.nextInt(4);
                    for (int j = 0; j < fastScrollTimes; j++) {
                        humanSmoothScroll(driver, 300 + rnd.nextInt(500));
                        humanDelay(200, 500);
                    }
                } else {
                    if (rnd.nextBoolean()) {
                        humanDelay(3000, 10000);
                    } else {
                        int slowScrollTimes = 3 + rnd.nextInt(3);
                        for (int j = 0; j < slowScrollTimes; j++) {
                            humanSmoothScroll(driver, 20 + rnd.nextInt(30));
                            humanDelay(100, 300);
                        }
                    }
                }
                Long currentScrollY = getScrollY(driver);
                System.out.println("探索页滚动位置: " + currentScrollY);
                if (currentScrollY < lastScrollY) {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("window.scrollTo(0, arguments[0]);", lastScrollY);
                }
                lastScrollY = currentScrollY;
                if (rnd.nextInt(10) < 2) {
                    humanRandomMouseMove(driver);
                }
            }
        } catch (Exception e) {
            System.out.println("探索页操作失败: " + e.getMessage());
        }
    }

    // 模拟推文互动
    private static void simulateTweetInteraction(WebDriver driver) throws InterruptedException {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            List<WebElement> tweets = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("article")));
            if (!tweets.isEmpty()) {
                WebElement tweet = tweets.get(rnd.nextInt(tweets.size()));
                humanMoveAndHover(driver, tweet);
                tweet.click();
                humanDelay(3000, 6000);
                int scrollTimes = 2 + rnd.nextInt(2); // 2-3 次
                Long lastScrollY = 0L;
                for (int i = 0; i < scrollTimes; i++) {
                    humanSmoothScroll(driver, 20 + rnd.nextInt(30));
                    humanDelay(100, 300);
                    Long currentScrollY = getScrollY(driver);
                    System.out.println("推文页滚动位置: " + currentScrollY);
                    if (currentScrollY < lastScrollY) {
                        JavascriptExecutor js = (JavascriptExecutor) driver;
                        js.executeScript("window.scrollTo(0, arguments[0]);", lastScrollY);
                    }
                    lastScrollY = currentScrollY;
                }
                driver.navigate().back();
            }
        } catch (Exception e) {
            System.out.println("推文互动失败: " + e.getMessage());
        }
    }

    // 随机延迟
    private static void humanDelay(int minMs, int maxMs) throws InterruptedException {
        Thread.sleep(minMs + rnd.nextInt(maxMs - minMs));
    }

    // 平缓滚动
    private static void humanSmoothScroll(WebDriver driver, int scrollAmount) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        int steps = 3 + rnd.nextInt(3);
        int stepAmount = scrollAmount / steps;
        for (int i = 0; i < steps; i++) {
            js.executeScript("window.scrollBy(0, arguments[0]);", stepAmount);
            try {
                Thread.sleep(100 + rnd.nextInt(200));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 获取滚动位置，处理 Double 类型
    private static Long getScrollY(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            Object scrollY = js.executeScript("return window.scrollY;");
            if (scrollY instanceof Double) {
                return Math.round((Double) scrollY);
            } else if (scrollY instanceof Long) {
                return (Long) scrollY;
            } else {
                return 0L;
            }
        } catch (Exception e) {
            System.out.println("获取 scrollY 失败: " + e.getMessage());
            return 0L;
        }
    }

    // 优化鼠标移动：JavaScript 模拟
    private static void humanRandomMouseMove(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // 获取视口尺寸
        Long innerWidth = (Long) js.executeScript("return window.innerWidth;");
        Long innerHeight = (Long) js.executeScript("return window.innerHeight;");
        if (innerWidth == null || innerHeight == null || innerWidth <= 0 || innerHeight <= 0) {
            System.out.println("无法获取视口尺寸，跳过鼠标移动");
            return;
        }

        // 限制在安全范围
        int padX = (int) (innerWidth * 0.3);
        int padY = (int) (innerHeight * 0.7);
        int maxX = (int) (innerWidth * 0.7);
        int maxY = (int) (innerHeight * 0.95);

        int x = padX + rnd.nextInt(maxX - padX);
        int y = padY + rnd.nextInt(maxY - padY);

        // 保存滚动位置
        Long currentScrollY = getScrollY(driver);
        System.out.println("鼠标移动前滚动位置: " + currentScrollY + ", 目标坐标: (" + x + ", " + y + "), 视口尺寸: (" + innerWidth + ", " + innerHeight + ")");

        // 禁用滚动事件
        js.executeScript("window.onscroll = null;");

        // 使用 JavaScript 模拟鼠标移动
        try {
            String script = "var evt = new MouseEvent('mousemove', {" +
                    "bubbles: true," +
                    "cancelable: true," +
                    "clientX: arguments[0]," +
                    "clientY: arguments[1]" +
                    "});" +
                    "document.dispatchEvent(evt);";
            js.executeScript(script, x, y);
            Thread.sleep(500 + rnd.nextInt(1000));
        } catch (Exception e) {
            System.out.println("JavaScript 鼠标移动失败: " + e.getMessage() + "，跳过");
        }

        // 验证滚动位置
        Long newScrollY = getScrollY(driver);
        if (newScrollY < currentScrollY) {
            System.out.println("鼠标移动导致回滚，从 " + newScrollY + " 恢复到 " + currentScrollY);
            js.executeScript("window.scrollTo(0, arguments[0]);", currentScrollY);
        }
    }

    // 模拟鼠标移动并悬停
    private static void humanMoveAndHover(WebDriver driver, WebElement elem) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            Point location = elem.getLocation();
            int x = location.getX() + rnd.nextInt(5);
            int y = location.getY() + rnd.nextInt(5);

            Long currentScrollY = getScrollY(driver);

            js.executeScript("window.onscroll = null;");

            String script = "var evt = new MouseEvent('mouseover', {" +
                    "bubbles: true," +
                    "cancelable: true," +
                    "clientX: arguments[0]," +
                    "clientY: arguments[1]" +
                    "});" +
                    "arguments[2].dispatchEvent(evt);";
            js.executeScript(script, x, y, elem);
            Thread.sleep(500 + rnd.nextInt(1500));

            Long newScrollY = getScrollY(driver);
            if (newScrollY < currentScrollY) {
                js.executeScript("window.scrollTo(0, arguments[0]);", currentScrollY);
            }
        } catch (Exception e) {
            System.out.println("鼠标悬停失败: " + e.getMessage());
        }
    }
}