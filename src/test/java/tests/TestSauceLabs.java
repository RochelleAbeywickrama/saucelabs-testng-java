package tests;

import pageObjects.HomePage;
import pageObjects.LoginPage;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;

public class TestSauceLabs {

    private WebDriver driver;

    public static final String USERNAME = "rochelle_abeywickrama";
    public static final String ACCESS_KEY = "ebe60356-1df4-47b8-93ec-29431d1de934";

    public static final DesiredCapabilities CAPABILITIES = new DesiredCapabilities();

    String sauceURL = "https://ondemand.saucelabs.com/wd/hub";
    String launchURL = "https://the-internet.herokuapp.com/";

    /* All the valid combinations should be added to the data provider */
    @DataProvider
    public static Object[][] hardCodedBrowsers() {
        return new Object[][]{
                {"Windows 10", "81.0", "chrome"},
                {"Windows 10", "76.0", "firefox"},
                {"Windows 10", "11.0", "internet explorer"},
                {"Windows 10", "81.0", "MicrosoftEdge"},
                {"macOS 10.15", "13.0", "safari"}
        };
    }

    /* Set desired capabilities*/
    public void setCapabilities(String platform, String version, String browser) {

        Date date = new Date();

        CAPABILITIES.setCapability("username", USERNAME);
        CAPABILITIES.setCapability("accessKey", ACCESS_KEY);
        CAPABILITIES.setCapability("browserName", browser);
        CAPABILITIES.setCapability("platform", platform);
        CAPABILITIES.setCapability("version", version);
        CAPABILITIES.setCapability("build", "SauceLabs - Selenium - Java");
        CAPABILITIES.setCapability("name", getClass().getSimpleName() + " - " +platform+"_"+browser+"_"+version);
        System.out.println(" Execution Started in - OS - " + platform + " | Browser - " + browser + " | Version - " + version);
    }


    /* Invoke the Remote Web */
    public void invokeWebDriver(String platform, String version, String browser) throws MalformedURLException {

        setCapabilities(platform, version, browser);
        driver = new RemoteWebDriver(new URL(sauceURL), CAPABILITIES);
        driver.get(launchURL);
        driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
        System.out.println(driver.getTitle());
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void testHomePage(String platform, String version, String browser) throws MalformedURLException {
        this.invokeWebDriver(platform, version, browser);

        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = homePage.clickFormAuthentication();
        loginPage.setUsername("tomsmith");
        loginPage.setPassword("SuperSecretPassword!");
        loginPage.clickLoginButton();
        assertTrue(loginPage.getAlertText().contains("You logged into a secure area!"), "Alert text is incorrect");
    }


    @AfterMethod
    public void cleanUpAfterTestMethod(ITestResult result) {
        ((JavascriptExecutor) driver).executeScript("sauce:job-result=" + (result.isSuccess() ? "passed" : "failed"));
        driver.quit();
    }
}
