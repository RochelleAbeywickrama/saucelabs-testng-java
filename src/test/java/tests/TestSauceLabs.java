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
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;

public class TestSauceLabs {

    private WebDriver driver;

    public static final String USERNAME = "udaramanupriya";
    public static final String ACCESS_KEY = "78a746db-b5d8-40d6-90d0-5623837af2e8";

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
                {"Windows 10", "81.0", "edge"},
                {"macOS 10.13", "13.0", "safari"}
        };
    }

    /* Set desired capabilities*/
    public void setCapabilities(String platform, String version, String browser) {

        CAPABILITIES.setCapability("username", USERNAME);
        CAPABILITIES.setCapability("accessKey", ACCESS_KEY);
        CAPABILITIES.setCapability("browserName", browser);
        CAPABILITIES.setCapability("platform", platform);
        CAPABILITIES.setCapability("version", version);
//      capabilities.setCapability("build", "Onboarding Sample App - Java-TestNG");
        CAPABILITIES.setCapability("name", getClass().getSimpleName());
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
