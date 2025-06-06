package org.r2;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.beust.ah.A;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.util.PageUtil;
import org.util.chromeExtentions.OKXExtensionUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

public class ExecuteTasks {

    private static final Random rnd = new Random();

    // 浏览器 ID 列表
    public static List<String> browsers = List.of(
            "kxyw4p3",
            "kxyw4p0",
            "kxyw4oy",
            "kxyw4ox",
            "kxyw4ow",
            "kxyw4ov", "kxyw4ot", "kxyw4os",
            "kxyw4or", "kxyw4oq", "kxyw4op", "kxyw4oo",
            "kxyw4on", "kxyw4om",
            "kxyw4ok"
//            , "kxyw4oj",
//            "kxyw4oi", "kxyw4oh", "kxyw4og", "kxyw4of", "kxyw4oe", "kxyw4od", "kxyw4oc", "kxyw4ob",
//            "kxyw4oa", "kxyw4o9", "kxyw4o8", "kxyw4o7", "kxyw4o6", "kxyw4o5", "kxyw4o4", "kxyw4o3",
//            "kxyw4o2", "kxyw4o1", "kxyw4ny", "kxyw4nx", "kxyw4nw", "kxyw4nv", "kxyw4nu", "kxyw4nt",
//            "kxyw4ns", "kxyw4nr", "kxyw4no", "kxyw4nn", "kxyw4nl", "kxyw4nk", "kxyw4nj", "kxyw4ng",
//            "kxyw4nf", "kxyw4ne"
    );

    // 钱包地址列表/faucet address:0xf02D957658D8C836c9240545122BE6D168713Db6
    public static List<String> addresses = List.of(
            "0xf02D957658D8C836c9240545122BE6D168713Db6",
            "0xA16C56e87ff0cf44B1D843DbcA11eb7F348839A7",
            "0x117A4D82908F5d495943A588DA671d90909f8107",
            "0xEBAA0B86355C830790a49D149CcB80ad81a52266",
            "0x329ADD75074Aa6f2189D4BD32b2eF663C854D88A",
            "0x832f0AA31cebfd43904EEa8Bc0451fa72A3b8Ad9",
            "0xfff7Df895dA2AcB212235fA940eDbcaFd0a3C56f", "0x0100505Cb84444022317C3E663DFA3F7b80Dee86",
            "0xAF2139fd81623172D6e8Dd7496D07a766f4a1143", "0x0B665EC35117cFa88293F40Ba2f46218955b7a7C",
            "0x5A8b7fFAFb90cf20e14C862F7E44FB8A182A5B2F", "0xA977E4A7B803FAEEa8DD0557457c142D91B7FB64",
            "0x34b8147a6B3e8b8a7C29A71008Fc9d623a90490A", "0xdc1a9090B2DBC9d99E381f41B877c36c3BC9d2d0",
            "0xa4214a6D6386aF0F319255F60Eed83dEdb58B1b4"
//            , "0x7Ce6d1A0176d0787B9083c415ef660db1DbD75a3",
//            "0x8Cc441Aa7D97e5e6C17D7c751Aa35EF390Ed2763", "0x8621723c5aeD26d18Ad7f39191f17C92FB68e629",
//            "0xAD4ba69f08Fa104d502cEc1A05a3F0fe6E3B0bc7", "0x24FE2681dc8298F81F1b46454b0aB62f744379f4",
//            "0x4E83b95Bf73873EFBF6B7bCCBf8378C79c67eCE3", "0x5a37887917Ec80A71bE5ab7e6e8f20F3AF94CAe3",
//            "0x0a8BC2Dfb81dDF2287C85508Ef3d2Ea5046E5444", "0x09DAE57594F9215a6E966485fB03Aa6C9B9eF80b",
//            "0x38955Be75EeaEb300fF77fd79969390b611d1bE3", "0x34A9A76D0D1CE318D3fB9BA325B75E76fadea7dB",
//            "0x394E51840047a2E5d32907F430b8502D65BCd733", "0x183Eb619451Ab68eBA99fB3Bda4Fb760B93C4471",
//            "0x699Ad0980E652f6Fa638852DC2DEe10cAd6B8C21", "0x75204a8c70772d3D81d0cE7063B5f6E25A29D4D7",
//            "0x81d5ec9A21072B84a7a26C593FDf1A0F72fd40D8", "0x6170227300b4b568F5Bbb4E14368B9aDdCE7194b",
//            "0x28554F907107362d02E6E5130A7bC7c32897973F", "0xaC3bBAC867b7C79ABa32D2354a8886834368FDCd",
//            "0x0d6029346a449598f55F2543BC8433D061684968", "0x48cA4C1968dA6aCe40e94a44eFCBEb2e2FC1431d",
//            "0x21947Be6Ea5977A6D52163C1B8B57bb7cc2933Be", "0x309797e4115640F34acc4164408Fac6029092547",
//            "0x9bC3F717C5b14A5f159927C17000c7840C1e85f2", "0xB7738206cA94B9F8462740D6e89FEDdFbB696b34",
//            "0x4e5A40D06218103D74a9F196d08ac680b9AFd0C5", "0x80e8B937bd212b08b59c74898147FECB9F8d0bc5",
//            "0x8c3341C8D25B947a5b397Ef13eC605D0A021cb07", "0x710e0B4181Ebbe6D6EbF17ec25F04f4a11D98Eb3",
//            "0xf1e8CBFb06e22D7f310d269249e0cD28C514c663", "0x7122ecfb3B48917D7e3F30b7F7723EE5720A28c0",
//            "0xac0fc533ECd20008DE91C121Edd6287e122270Ef", "0xe16289ba567c3551021c6bd3b2898f0aAE4D94BE",
//            "0x2d468AD460975ACA59952873950120fcC4e9BE12", "0xbd84d8d9df8aC4B85f99C44A96869406135Dc1f5"
    );


    public static final Map<String, String> NETWORK_MAP = new HashMap<>();

    static {
        NETWORK_MAP.put("ETHEREUM", " ETH(Sepolia)");
        NETWORK_MAP.put("PLUME", " Plume Testnet");
        NETWORK_MAP.put("ARB", " Arbitrum Sepolia");
//        NETWORK_MAP.put("MONAD", " Monad Testnet");
//        NETWORK_MAP.put("BNB", " BNB Testnet");
        NETWORK_MAP.put("BASE", " Base Sepolia");
    }

    public static ArrayList<String> failList = new ArrayList<>();


    public static void main(String[] args) {
        HttpClient httpClient = HttpClient.newHttpClient();
        long start = System.currentTimeMillis();

        for (int i = 0; i < browsers.size(); i++) {
            String browser = browsers.get(i);
            String address = addresses.get(i);
            String debugPort = "";
            String webDriver = "";

            // 启动浏览器
            try {
                HttpResponse<String> response;
                while (true) {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI("http://127.0.0.1:50325/api/v1/browser/start?user_id=" + browser))
                            .GET()
                            .build();
                    response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println("启动浏览器响应: " + response.body());
                    if ((int) JSON.parseObject(response.body()).get("code") == 0) {
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
                System.out.println("启动浏览器 " + browser + " 失败: " + e.getMessage());
                continue;
            }

            // 设置 ChromeDriver
            System.setProperty("webdriver.chrome.driver", webDriver);
            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("debuggerAddress", "127.0.0.1:" + debugPort);
            options.addArguments("--disable-blink-features=AutomationControlled");

            WebDriver driver = new ChromeDriver(options);

            try {
                // 隐藏 webdriver 特征
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined});");

                Thread.sleep(5 * 1000);

                PageUtil.closeExtraTabsSafe(driver, 2);

                //login
                String okxLoginWindowHandle = OKXExtensionUtil.loginOKXWallet(driver, "User@123");
                String r2WindowHandle = PageUtil.openNewWindow(driver, "https://www.r2.money/swap");
                login(driver, okxLoginWindowHandle, r2WindowHandle);
                driver.get("https://www.r2.money/swap");
                r2WindowHandle = driver.getWindowHandle();
                // doSwap
                doSwapAllChain(driver, okxLoginWindowHandle, r2WindowHandle);
                // doStake
                driver.get("https://www.r2.money/sr2usd");
                r2WindowHandle = driver.getWindowHandle();
                doStakeAllChain(driver, okxLoginWindowHandle, r2WindowHandle);
                // doLP
                driver.get("https://www.r2.money/liquidity");
                r2WindowHandle = driver.getWindowHandle();
                doLPAllChain(driver, okxLoginWindowHandle, r2WindowHandle);

                // 关闭浏览器
                HttpResponse<String> response;
                while (true) {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(new URI("http://127.0.0.1:50325/api/v1/browser/stop?user_id=" + browser))
                            .GET()
                            .build();
                    response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println("关闭浏览器响应: " + response.body());
                    if ((int) JSON.parseObject(response.body()).get("code") == 0) {
                        break;
                    } else {
                        Thread.sleep(10 * 1000);
                    }
                }

                System.out.println("浏览器 " + browser + " 执行脚本完成");
            } catch (Exception e) {
                System.out.println("浏览器 " + browser + " 执行脚本出错: " + e.getMessage());
                failList.add("浏览器 " + browser + " 执行脚本出错: " + e.getMessage());
            } finally {
                driver.quit();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("脚本执行完成，总耗时: " + (end - start) + "毫秒");
        System.out.println(failList);
    }

    private static void doLPAllChain(WebDriver driver, String okxLoginWindowHandle, String r2WindowHandle) throws InterruptedException {
        switchChain(driver, NETWORK_MAP.get("ETHEREUM"));
        doLP(driver, okxLoginWindowHandle, r2WindowHandle, 490, 20 * 1000);
        Thread.sleep(30 * 1000);
        switchChain(driver, NETWORK_MAP.get("PLUME"));
        doLP(driver, okxLoginWindowHandle, r2WindowHandle, 490, 3 * 1000);
        Thread.sleep(5 * 1000);
        switchChain(driver, NETWORK_MAP.get("ARB"));
        doLP(driver, okxLoginWindowHandle, r2WindowHandle, 490, 3 * 1000);
        Thread.sleep(5 * 1000);
    }

    private static void doLP(WebDriver driver, String okxLoginWindowHandle, String r2WindowHandle, int amount, int sleepTime) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement webElement = null;
        try {
            //click Add LP
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[.//span[text()='Add Liquidity']]")
            ));
            webElement = driver.findElements(By.xpath("//button[.//span[text()='Add Liquidity']]")).get(1);
            webElement.click();
            System.out.println("click Add LP done!");
            humanDelay(3000, 6000);


            //click input chain
            webElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input[type='number'][min='0']") // 修正后的 CSS 选择器
            ));
            webElement.click();
            webElement.clear();
            PageUtil.sendKeysHumanLike(webElement, String.valueOf(amount));
            System.out.println("sendKeys:" + amount + " done!");
            humanDelay(1000, 2000);

            //click Add
            webElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[.//span[text()='Add']]")
            ));
            webElement.click();
            System.out.println("click Add done!");
            humanDelay(3000, 6000);


            //okx wallet approve
            driver.switchTo().window(okxLoginWindowHandle);
            if (OKXExtensionUtil.needApprove(driver)) {
                //approve
                OKXExtensionUtil.approve(driver);
                humanDelay(3000, 6000);
                Thread.sleep(sleepTime);
                if (OKXExtensionUtil.needApprove(driver)) {
                    OKXExtensionUtil.approve(driver);
                    humanDelay(3000, 6000);
                    Thread.sleep(sleepTime);
                }
//                //switch to r2
//                driver.switchTo().window(r2WindowHandle);
//                //click Add
//                WebElement addElement = null;
//                Thread.sleep(10 * 1000);
//                try {
//                    addElement = wait.until(ExpectedConditions.elementToBeClickable(
//                            By.xpath("//button[.//span[text()='Add']]")
//                    ));
//                } catch (Exception e) {
//                    System.out.println("no Add Element");
//                }
//                if (addElement != null) {
//                    addElement.click();
//                    System.out.println("click Add done!");
//                    humanDelay(3000, 6000);
//                }
            }

            //okx wallet confirm
            driver.switchTo().window(okxLoginWindowHandle);
            OKXExtensionUtil.confirm(driver);
            driver.switchTo().window(r2WindowHandle);
            humanDelay(3000, 6000);
        } catch (Exception e) {
            System.out.println("login error:" + e.getMessage());
            throw e;
        }

    }

    private static void doStakeAllChain(WebDriver driver, String okxLoginWindowHandle, String r2WindowHandle) throws InterruptedException {
        switchChain(driver, NETWORK_MAP.get("ETHEREUM"));
        doStake(driver, okxLoginWindowHandle, r2WindowHandle, 500, 20 * 1000);
        Thread.sleep(20 * 1000);
        switchChain(driver, NETWORK_MAP.get("PLUME"));
        doStake(driver, okxLoginWindowHandle, r2WindowHandle, 500, 3 * 1000);
        switchChain(driver, NETWORK_MAP.get("ARB"));
        doStake(driver, okxLoginWindowHandle, r2WindowHandle, 500, 3 * 1000);
        switchChain(driver, NETWORK_MAP.get("BASE"));
        doStake(driver, okxLoginWindowHandle, r2WindowHandle, 1000, 5 * 1000);


        //        switchChain(driver, NETWORK_MAP.get("MONAD"));
//        doStake(driver, okxLoginWindowHandle, r2WindowHandle, 500);


//        switchChain(driver, NETWORK_MAP.get("BNB"));

    }

    private static void doSwapAllChain(WebDriver driver, String okxLoginWindowHandle, String r2WindowHandle) throws InterruptedException {
        //switch chain
        for (Map.Entry<String, String> entry : NETWORK_MAP.entrySet()) {
            String value = entry.getValue();
            switchChain(driver, value);
            doSwap(driver, okxLoginWindowHandle, r2WindowHandle, entry.getKey());
        }
    }

    private static void doStake(WebDriver driver, String okxLoginWindowHandle, String r2WindowHandle, int amount, int sleepTime) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement webElement = null;
        try {
            //input
            webElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input[type='number'][min='0']") // 修正后的 CSS 选择器
            ));
            webElement.click();
            webElement.clear();
            PageUtil.sendKeysHumanLike(webElement, String.valueOf(amount));
            System.out.println("sendKeys:" + amount + " done!");
            humanDelay(1000, 2000);

            //click Stake
            webElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[.//span[text()='Stake']]")
            ));
            webElement.click();
            System.out.println("click Stake done!");
            humanDelay(3000, 6000);


            //okx wallet approve
            driver.switchTo().window(okxLoginWindowHandle);
            if (OKXExtensionUtil.needApprove(driver)) {
                //approve
                OKXExtensionUtil.approve(driver);
                humanDelay(3000, 6000);
                Thread.sleep(sleepTime);
//                //switch to r2
//                driver.switchTo().window(r2WindowHandle);
//                //click Stake
//                WebElement stakeWebElement = null;
//                try {
//                    stakeWebElement = wait.until(ExpectedConditions.elementToBeClickable(
//                            By.xpath("//button[.//span[text()='Stake']]")
//                    ));
//                } catch (Exception e) {
//                    System.out.println("no stakeWebElement");
//                }
//
//                if (stakeWebElement != null) {
//                    stakeWebElement.click();
//                    System.out.println("click Stake done!");
//                    humanDelay(3000, 6000);
//                }
            }

            //okx wallet confirm
            driver.switchTo().window(okxLoginWindowHandle);
            OKXExtensionUtil.confirm(driver);
            driver.switchTo().window(r2WindowHandle);
            humanDelay(3000, 6000);
        } catch (Exception e) {
            System.out.println("login error:" + e.getMessage());
            throw e;
        }

    }

    private static void doSwap(WebDriver driver, String okxLoginWindowHandle, String r2WindowHandle, String key) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement webElement = null;
        try {
            //input number
            webElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("input[type='number'][min='0']") // 修正后的 CSS 选择器
            ));
            webElement.click();
            webElement.clear();
            PageUtil.sendKeysHumanLike(webElement, "1000");
            System.out.println("sendKeys:" + 1000 + " done!");
            humanDelay(1000, 2000);

            //click buy
            webElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[.//span[text()='Buy']]")
            ));
            webElement.click();
            System.out.println("click buy done!");
            humanDelay(3000, 6000);

            //okx wallet approve
            driver.switchTo().window(okxLoginWindowHandle);
            if (OKXExtensionUtil.needApprove(driver)) {
                //approve
                OKXExtensionUtil.approve(driver);
                humanDelay(3000, 6000);
                if (key.equals("ETHEREUM")) {
                    Thread.sleep(20 * 1000);
                } else if (key.equals("BASE")){
                    Thread.sleep(5 * 1000);
                } else {
                    Thread.sleep(3 * 1000);
                }
//                //switch to r2
//                driver.switchTo().window(r2WindowHandle);
//                //click buy
//                WebElement buyWebElement = null;
//                try {
//                    buyWebElement = wait.until(ExpectedConditions.elementToBeClickable(
//                            By.xpath("//button[.//span[text()='Buy']]")
//                    ));
//                } catch (Exception e) {
//                    System.out.println("no buyWebElement");
//                }
//                if (buyWebElement != null) {
//                    buyWebElement.click();
//                    System.out.println("click buy done!");
//                    humanDelay(3000, 6000);
//                }
            }

            //okx wallet confirm
            driver.switchTo().window(okxLoginWindowHandle);
            OKXExtensionUtil.confirm(driver);
            humanDelay(3000, 6000);
            driver.switchTo().window(r2WindowHandle);
            if (key.equals("ETHEREUM")) {
                Thread.sleep(20 * 1000);
            } else if (key.equals("BASE")){
                Thread.sleep(5 * 1000);
            } else {
                Thread.sleep(3 * 1000);
            }
        } catch (Exception e) {
            System.out.println("login error:" + e.getMessage());
            throw e;
        }
    }

    private static void switchChain(WebDriver driver, String chain) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));
        WebElement webElement = null;
        try {
            //click switch chain
            webElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("img[data-v-f6171d07][alt='']") // 修正后的 CSS 选择器
            ));
            webElement.click();
            System.out.println("click switch chain done!");
            humanDelay(3000, 6000);

            //choose chain
            webElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(text(), '" + chain + "')]")
            ));
            webElement = webElement.findElement(By.tagName("img"));
            webElement.click();
            System.out.println("click choose chain " + chain + "done!");
            humanDelay(3000, 6000);
        } catch (Exception e) {
            System.out.println("login error:" + e.getMessage());
            throw e;
        }
    }

    private static void login(WebDriver driver, String okxLoginWindowHandle, String r2WindowHandle) throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement webElement = null;
        try {
            //click connect wallet
            webElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.w-48.border.border-white.py-2") // 修正后的 CSS 选择器
            ));
            if (webElement.getText().contains("Connect")) {
                webElement.click();
                System.out.println("click connect wallet done");
                //choose okx wallet
                humanDelay(3000, 6000);
                SearchContext shadowContext = PageUtil.expandShadowRoots(driver,
                        "w3m-modal",
                        "wui-flex > wui-card > w3m-router",
                        "div > w3m-connect-view",
                        "wui-flex > w3m-wallet-login-list",
                        "wui-flex > w3m-connect-announced-widget",
                        "wui-flex > wui-list-wallet[name='OKX Wallet']"
                );
                webElement = shadowContext.findElement(
                        By.cssSelector("button")
                );
                webElement.click();
                System.out.println("click okx wallet done!");
                humanDelay(3000, 6000);
            }

            //confirm sign
            try {
                driver.switchTo().window(okxLoginWindowHandle);
                OKXExtensionUtil.connect(driver);
//                webElement = wait.until(ExpectedConditions.elementToBeClickable(
//                        By.xpath("//button[.//div[text()='Confirm']]") // 修正后的 CSS 选择器
//                ));
//                webElement.click();
                System.out.println("okx wallet confirm sign done");
                humanDelay(3000, 6000);
            } catch (Exception e) {
                System.out.println("okx wallet 无需签名");
            }
            driver.switchTo().window(r2WindowHandle);

            //输入邀请码
            WebElement startElement = null;
            try {
                startElement = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[text()='Enter Your Invite Code']") // 修正后的 CSS 选择器
                ));
            } catch (Exception e) {
                System.out.println("no startElement");
            }
            if (startElement == null) {
                return;
            }
            List<WebElement> inputs = driver.findElements(By.cssSelector("input.code-input"));
            // 遍历输入字符
            for (int i = 0; i < "R2YLD".length() && i < inputs.size(); i++) {
                WebElement input = inputs.get(i);
                input.clear(); // 清除已有值（可选）
                input.sendKeys(String.valueOf("R2YLD".charAt(i)));

                // 模拟人类输入延迟（100~300ms）
                try {
                    Thread.sleep(new Random().nextInt(200) + 100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            webElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[.//span[text()='Start']]") // 修正后的 CSS 选择器
            ));
            webElement.click();
            System.out.println("click start done!");
            humanDelay(3000, 6000);

            webElement = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[.//span[text()='I Understand']]") // 修正后的 CSS 选择器
            ));
            webElement.click();
            System.out.println("click I Understand done!");
            humanDelay(3000, 6000);
        } catch (Exception e) {
            System.out.println("login error:" + e.getMessage());
            throw e;
        }
    }

    private static void humanDelay(int minMs, int maxMs) throws InterruptedException {
        Thread.sleep(minMs + rnd.nextInt(maxMs - minMs));
    }
}