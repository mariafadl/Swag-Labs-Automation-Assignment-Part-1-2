package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.InventoryPage;
import pages.LoginPage;
import utils.DataDriven;

public class LoginTest extends BaseTest {

    @Test(priority = 1, description = "Verify successful login")
    public void verifySuccessfulLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(DataDriven.jsonReader("validUser", "username"),
                DataDriven.jsonReader("validUser", "password"));
        InventoryPage inventoryPage = new InventoryPage(driver);
        Assert.assertTrue(inventoryPage.isUrlInventory(), "User was not redirected to inventory page");
        Assert.assertTrue(inventoryPage.isUrlInventory(), "User was not redirected to inventory page");

        System.out.println("Login successful");

    }

    @Test(priority = 2, description = "Verify invalid login")
    public void verifyInvalidLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(DataDriven.jsonReader("invalidUser", "username"),
                DataDriven.jsonReader("invalidUser", "password"));

        Assert.assertTrue(loginPage.getErrorMessage().contains("Username and password do not match"),
                "Unexpected error message for invalid login");

        System.out.println("Invalid username and password");
    }

    @Test(priority = 3, description = "Verify login without password")
    public void verifyLoginWithoutPassword() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterUsername(DataDriven.jsonReader("noPassword", "username"));
        // password intentionally left empty
        loginPage.clickLogin();

        Assert.assertTrue(loginPage.getErrorMessage().contains("Password is required"),
                "Unexpected error message when password is empty");

        System.out.println("empty password");
    }
}