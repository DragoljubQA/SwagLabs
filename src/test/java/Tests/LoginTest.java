package Tests;

import Base.BaseTest;
import Helpers.WebDriverFactory;
import Pages.HomePage;
import Pages.LoginPage;
import com.github.javafaker.Faker;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static Helpers.Data.*;
import Helpers.Data;

public class LoginTest extends BaseTest {


    @BeforeMethod
    public void pageSetUp() {
        /*ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);*/
        driver = WebDriverFactory.createWebDriver();
        wdwait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get(LandingURL);
        loginPage = new LoginPage(driver);
        homePage = new HomePage(driver);
        data = new Data();
    }

    public void logIn(String username, String password) {
        loginPage.insertUsername(username);
        loginPage.insertPassword(password);
        loginPage.clickOnLoginButton();
    }

    @Test (priority = 10)
    public void userCanLogIn() {
        logIn(validUsername, validPassword);
        Assert.assertEquals(driver.getCurrentUrl(), HomeURL);
        Assert.assertTrue(homePage.CartButton.isDisplayed());
        Assert.assertTrue(homePage.SortButton.isDisplayed());
    }

    @Test (priority = 20)
    public void userCannotLogInWithInvalidUsername() {
        logIn("username", validPassword);
        Assert.assertNotEquals(driver.getCurrentUrl(), HomeURL);
        Assert.assertFalse(elementIsDisplayed(homePage.CartButton));
        Assert.assertFalse(elementIsDisplayed(homePage.SortButton));
    }

    @Test (priority = 30)
    public void userCannotLogInWithInvalidPassword() {
        logIn(validUsername, "password");
        Assert.assertNotEquals(driver.getCurrentUrl(), HomeURL);
        Assert.assertFalse(elementIsDisplayed(homePage.CartButton));
        Assert.assertFalse(elementIsDisplayed(homePage.SortButton));
    }

    @Test (priority = 40, dataProvider = "usernameAndPassword")
    public void userCannotLogInWithInvalidUsernameAndPassword(String username, String password) {
        logIn(username, password);
        Assert.assertNotEquals(driver.getCurrentUrl(), HomeURL);
        Assert.assertFalse(elementIsDisplayed(homePage.CartButton));
        Assert.assertFalse(elementIsDisplayed(homePage.SortButton));
    }

    @DataProvider(name = "usernameAndPassword")
    public Object[][] Data() {
        Object[][] data = {{"username", "password"}, {"standard", "pass_word"}, {"user", "pass"}};
        return data;
    }

    @Test (priority = 50)
    public void userCannotLogInWithRandomData() {
        Faker faker = new Faker();
        for (int i = 0; i < 10; i++) {
            logIn(faker.name().username(), faker.internet().password(6, 10, true));
            Assert.assertNotEquals(driver.getCurrentUrl(), HomeURL);
            Assert.assertFalse(elementIsDisplayed(homePage.CartButton));
            Assert.assertFalse(elementIsDisplayed(homePage.SortButton));
        }
        //System.out.println(faker.regexify("[A-Za-z0-9]{10}"));
        //System.out.println("projekatxyz+"+faker.name().firstName()+"@gmail.com");

    }

    @AfterMethod
    public void tearDown(ITestResult result) throws IOException {
        if (result.getStatus() == ITestResult.FAILURE || result.getStatus() == ITestResult.SKIP){
            File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            //File savedScreenshot = new File("target/screenshots/"+result.getTestClass().getRealClass().getSimpleName()+"/"+result.getMethod().getMethodName()+".jpg");
            File savedScreenshot = new File("target/screenshots/"+System.currentTimeMillis()+".jpg");

            FileUtils.copyFile(screenshot, savedScreenshot);
        }

        driver.quit();
    }

}
