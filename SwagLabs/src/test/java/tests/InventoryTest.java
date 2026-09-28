package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.InventoryPage;
import pages.LoginPage;
import utils.DataDriven;

public class InventoryTest extends BaseTest {

    @Test(description = "Verify inventory page elements after login")
    public void verifyInventoryPageElements() {
        // Login with valid credentials
        new LoginPage(driver).login(DataDriven.jsonReader("validUser", "username"),
                DataDriven.jsonReader("validUser", "password"));

        InventoryPage inventoryPage = new InventoryPage(driver);
        Assert.assertTrue(inventoryPage.isUrlInventory(), "Login failed");

        Assert.assertEquals(inventoryPage.getPageTitle(), "Swag Labs", "Page title mismatch");
        Assert.assertTrue(inventoryPage.isCartIconDisplayed(), "Cart icon is not displayed");
        Assert.assertEquals(inventoryPage.getProductsCount(), 6, "Number of products mismatch");

        System.out.println("verify Inventory Page Elements");
    }
}