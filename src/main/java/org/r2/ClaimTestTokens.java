package org.r2;


import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.util.PageUtil;


import java.time.Duration;
import java.util.*;

public class ClaimTestTokens {

    private static final Random rnd = new Random();

    // 浏览器 ID 列表
    public static List<String> browsers = List.of(
            "kxyw4p3",
            "kxyw4p0",
            "kxyw4oy", "kxyw4ox",
            "kxyw4ow", "kxyw4ov", "kxyw4ot", "kxyw4os",
            "kxyw4or", "kxyw4oq", "kxyw4op", "kxyw4oo", "kxyw4on", "kxyw4om", "kxyw4ok",
            "kxyw4oj", "kxyw4oi",
            "kxyw4oh", "kxyw4og", "kxyw4of", "kxyw4oe", "kxyw4od", "kxyw4oc", "kxyw4ob",
            "kxyw4oa", "kxyw4o9", "kxyw4o8", "kxyw4o7", "kxyw4o6", "kxyw4o5", "kxyw4o4", "kxyw4o3",
            "kxyw4o2", "kxyw4o1", "kxyw4ny", "kxyw4nx", "kxyw4nw", "kxyw4nv", "kxyw4nu", "kxyw4nt",
            "kxyw4ns", "kxyw4nr", "kxyw4no", "kxyw4nn", "kxyw4nl", "kxyw4nk", "kxyw4nj", "kxyw4ng",
            "kxyw4nf", "kxyw4ne"
    );

    // 钱包地址列表/faucet address:0xf02D957658D8C836c9240545122BE6D168713Db6
    public static List<String> addresses = List.of(
            "0xf02D957658D8C836c9240545122BE6D168713Db6",
            "0xA16C56e87ff0cf44B1D843DbcA11eb7F348839A7",
            "0x117A4D82908F5d495943A588DA671d90909f8107", "0xEBAA0B86355C830790a49D149CcB80ad81a52266",
            "0x329ADD75074Aa6f2189D4BD32b2eF663C854D88A", "0x832f0AA31cebfd43904EEa8Bc0451fa72A3b8Ad9",
            "0xfff7Df895dA2AcB212235fA940eDbcaFd0a3C56f", "0x0100505Cb84444022317C3E663DFA3F7b80Dee86",
            "0xAF2139fd81623172D6e8Dd7496D07a766f4a1143", "0x0B665EC35117cFa88293F40Ba2f46218955b7a7C",
            "0x5A8b7fFAFb90cf20e14C862F7E44FB8A182A5B2F", "0xA977E4A7B803FAEEa8DD0557457c142D91B7FB64",
            "0x34b8147a6B3e8b8a7C29A71008Fc9d623a90490A", "0xdc1a9090B2DBC9d99E381f41B877c36c3BC9d2d0",
            "0xa4214a6D6386aF0F319255F60Eed83dEdb58B1b4", "0x7Ce6d1A0176d0787B9083c415ef660db1DbD75a3",
            "0x8Cc441Aa7D97e5e6C17D7c751Aa35EF390Ed2763", "0x8621723c5aeD26d18Ad7f39191f17C92FB68e629",
            "0xAD4ba69f08Fa104d502cEc1A05a3F0fe6E3B0bc7", "0x24FE2681dc8298F81F1b46454b0aB62f744379f4",
            "0x4E83b95Bf73873EFBF6B7bCCBf8378C79c67eCE3", "0x5a37887917Ec80A71bE5ab7e6e8f20F3AF94CAe3",
            "0x0a8BC2Dfb81dDF2287C85508Ef3d2Ea5046E5444", "0x09DAE57594F9215a6E966485fB03Aa6C9B9eF80b",
            "0x38955Be75EeaEb300fF77fd79969390b611d1bE3", "0x34A9A76D0D1CE318D3fB9BA325B75E76fadea7dB",
            "0x394E51840047a2E5d32907F430b8502D65BCd733", "0x183Eb619451Ab68eBA99fB3Bda4Fb760B93C4471",
            "0x699Ad0980E652f6Fa638852DC2DEe10cAd6B8C21", "0x75204a8c70772d3D81d0cE7063B5f6E25A29D4D7",
            "0x81d5ec9A21072B84a7a26C593FDf1A0F72fd40D8", "0x6170227300b4b568F5Bbb4E14368B9aDdCE7194b",
            "0x28554F907107362d02E6E5130A7bC7c32897973F", "0xaC3bBAC867b7C79ABa32D2354a8886834368FDCd",
            "0x0d6029346a449598f55F2543BC8433D061684968", "0x48cA4C1968dA6aCe40e94a44eFCBEb2e2FC1431d",
            "0x21947Be6Ea5977A6D52163C1B8B57bb7cc2933Be", "0x309797e4115640F34acc4164408Fac6029092547",
            "0x9bC3F717C5b14A5f159927C17000c7840C1e85f2", "0xB7738206cA94B9F8462740D6e89FEDdFbB696b34",
            "0x4e5A40D06218103D74a9F196d08ac680b9AFd0C5", "0x80e8B937bd212b08b59c74898147FECB9F8d0bc5",
            "0x8c3341C8D25B947a5b397Ef13eC605D0A021cb07", "0x710e0B4181Ebbe6D6EbF17ec25F04f4a11D98Eb3",
            "0xf1e8CBFb06e22D7f310d269249e0cD28C514c663", "0x7122ecfb3B48917D7e3F30b7F7723EE5720A28c0",
            "0xac0fc533ECd20008DE91C121Edd6287e122270Ef", "0xe16289ba567c3551021c6bd3b2898f0aAE4D94BE",
            "0x2d468AD460975ACA59952873950120fcC4e9BE12", "0xbd84d8d9df8aC4B85f99C44A96869406135Dc1f5"
    );

    // Discord 频道链接列表
    public static List<String> discordChannels = List.of(
            "https://discord.com/channels/1308368864505106442/1339883019556749395"
//            "https://discord.com/channels/1308368864505106442/1364457608962117774",
//            "https://discord.com/channels/1308368864505106442/1364457925632065620",
//            "https://discord.com/channels/1308368864505106442/1367156681154236467",
//            "https://discord.com/channels/1308368864505106442/1372399850339045488",
//            "https://discord.com/channels/1308368864505106442/1374560325059350538"
    );

    public static ArrayList<String> failList = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        act();
    }

    public static void act(){
        long start = System.currentTimeMillis();

        for (int i = 0; i < browsers.size(); i++) {
            String browser = browsers.get(i);
            String address = addresses.get(i);
            System.out.println("------Claim browser: " + browser + " address:" + address + "-------");
            String debugPort = "";
            String webDriver = "";

            // 启动浏览器
            Map<String, String> data = PageUtil.retry(() -> PageUtil.startBrowser(browser), 3);
            if (data == null) {
                continue;
            }
            debugPort = data.get("debug_port");
            webDriver = data.get("webdriver");

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


                // 使用第一个标签页
                String workingWindow = driver.getWindowHandle();

                // 遍历每个 Discord 频道
                for (String channel : discordChannels) {
                    claimTestTokens(driver, channel, address, workingWindow);
                }
                System.out.println("浏览器 " + browser + " 的测试币领取任务完成");
            } catch (Exception e) {
                failList.add(browser);
                System.out.println("浏览器 " + browser + " 执行脚本出错: " + e.getMessage());
            } finally {
                // 关闭浏览器
                PageUtil.retry(() -> {
                    PageUtil.stopBrowser(browser);
                    return null;
                }, 3);
                driver.quit();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("脚本执行完成，总耗时: " + (end - start) + "毫秒");
        System.out.println(failList);
    }

    private static void claimTestTokens(WebDriver driver, String channelUrl, String address, String workingWindow) throws InterruptedException {
        try {
            // 确保在工作标签页
            driver.switchTo().window(workingWindow);
            System.out.println("访问频道: " + channelUrl);

            // 访问 Discord 频道
            driver.navigate().refresh();
            driver.get(channelUrl);
            driver.navigate().refresh();
            Thread.sleep(4 * 1000);
            humanDelay(3000, 6000);

            // 等待并定位消息输入框
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement messageBox = null;
            for (int attempt = 1; attempt <= 3; attempt++) {
                try {
                    messageBox = wait.until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector("div[role='textbox'][data-slate-editor='true'][aria-multiline='true']")
                    ));
                    break;
                } catch (Exception e) {
                    System.out.println("尝试 " + attempt + " 次定位输入框失败: " + e.getMessage());
                    if (attempt < 3) {
                        humanDelay(1000, 3000);
                        driver.navigate().refresh();
                    }
                }
            }
            if (messageBox == null) {
                System.out.println("无法定位消息输入框，跳过频道: " + channelUrl);
                return;
            }

            // 输入 faucet 命令
            String command = "/faucet " + address;
            messageBox.click();
            for (char c : command.toCharArray()) {
                messageBox.sendKeys(String.valueOf(c));
                Thread.sleep(50 + rnd.nextInt(150));
            }
            messageBox.sendKeys(Keys.ENTER);
            System.out.println("在频道 " + channelUrl + " 中发送命令: " + command);
            humanDelay(2000, 3000);
        } catch (Exception e) {
            System.out.println("在频道 " + channelUrl + " 领取测试币失败: " + e.getMessage());
        }
    }

    private static void humanDelay(int minMs, int maxMs) throws InterruptedException {
        Thread.sleep(minMs + rnd.nextInt(maxMs - minMs));
    }
}