package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.InventoryPage;
import pages.LoginPage;
import utils.DataDriven;

import java.util.List;

public class CartTest extends BaseTest {

    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private List<String> products;

    /** Runs AFTER BaseTest.setUp() -> browser is already open */
    @BeforeMethod
    public void loginBeforeTest() {
        new LoginPage(driver).login(DataDriven.jsonReader("validUser", "username"),
                DataDriven.jsonReader("validUser", "password"));
        inventoryPage = new InventoryPage(driver);
        cartPage = new CartPage(driver);
        products = DataDriven.jsonReaderList("cartProducts");   // from JSON, not hardcoded
        Assert.assertTrue(inventoryPage.isUrlInventory(), "Login failed");
    }

    /** Helper: add all JSON products in order */
    private void addAllProducts() {
        for (String product : products) {
            inventoryPage.addProductToCart(product);
        }
    }

    // ===== 1) Social links =====
    @Test(priority = 1, description = "Verify social links")
    public void verifySocialLinks() {
        Assert.assertTrue(inventoryPage.clickLinkedin().contains("linkedin"), "LinkedIn URL is wrong");
        Assert.assertTrue(inventoryPage.clickFacebook().contains("facebook"), "Facebook URL is wrong");
        Assert.assertTrue(inventoryPage.clickTwitter().contains("x.com"), "X URL is wrong");
        System.out.println("Social links verified");
    }

    // ===== 2) Empty cart =====
    @Test(priority = 2, description = "Verify cart is empty")
    public void verifyCartIsEmpty() {
        inventoryPage.openCart();
        Assert.assertTrue(cartPage.isCartEmpty(), "Cart should be empty");
        System.out.println("Cart is empty");
    }

    // ===== 3) Add 3 products (data-driven) =====
    @Test(priority = 3, description = "Add 3 products and verify order")
    public void verifyAddThreeProducts() {
        addAllProducts();
        inventoryPage.openCart();
        Assert.assertEquals(cartPage.getCartItemNames(), products,
                "Cart items or their order don't match the added products");
        System.out.println("Cart items and their order don't match the added products");
    }

    // ===== 4) Remove one product =====
    @Test(priority = 4, description = "Remove Bolt T-Shirt and verify buttons")
    public void verifyRemoveOneProduct() {
        String toRemove = products.get(1);   // Sauce Labs Bolt T-Shirt

        addAllProducts();
        inventoryPage.openCart();
        cartPage.removeProduct(toRemove);
        cartPage.clickContinueShopping();

        Assert.assertTrue(inventoryPage.isUrlInventory());
        Assert.assertEquals(inventoryPage.getProductButtonText(toRemove), "Add to cart",
                "Removed product button should be 'Add to cart'");
        Assert.assertEquals(inventoryPage.getProductButtonText(products.get(0)), "Remove");
        Assert.assertEquals(inventoryPage.getProductButtonText(products.get(2)), "Remove");
        System.out.println("Removed product button should be 'Add to cart'");
    }

    // ===== 5) Cart total price =====
    @Test(priority = 5, description = "Verify item total at checkout")
    public void verifyCartTotalPrice() {
        // 1) read prices BEFORE adding
        double expectedSum = 0;
        for (String product : products) {
            expectedSum += inventoryPage.getProductPrice(product);
            System.out.println("Products price " + expectedSum);
        }

        // 2) add products, 3) go to checkout
        addAllProducts();
        inventoryPage.openCart();
        cartPage.clickCheckout();
        cartPage.fillCheckoutInfo(DataDriven.jsonReader("checkoutInfo", "firstName"),
                DataDriven.jsonReader("checkoutInfo", "lastName"),
                DataDriven.jsonReader("checkoutInfo", "postalCode"));

        // 4) compare (delta 0.01 because of double precision)
        Assert.assertEquals(cartPage.getItemTotal(), expectedSum, 0.01,
                "Item total at checkout doesn't match the sum of inventory prices");
    }

    // ===== 6) Checkout with empty cart =====
    @Test(priority = 6, description = "Checkout with empty cart should show an error")
    public void verifyCheckoutWithEmptyCart() {
        inventoryPage.openCart();
        Assert.assertTrue(cartPage.isCartEmpty(), "Precondition failed: cart is not empty");

        cartPage.clickCheckout();

        Assert.assertTrue(cartPage.isEmptyCartErrorDisplayed() && !cartPage.isOnCheckoutForm(),
                "BUG: site allowed checkout with an empty cart (no error message shown)");

    }

    // ===== 7) Cart state after logout/login =====
    @Test(priority = 7, description = "Verify cart items after logout and login")
    public void verifyCartStateAfterLogout() {
        inventoryPage.addProductToCart(products.get(0));
        inventoryPage.addProductToCart(products.get(1));

        inventoryPage.logout();

        new LoginPage(driver).login(DataDriven.jsonReader("validUser", "username"),
                DataDriven.jsonReader("validUser", "password"));
        Assert.assertTrue(inventoryPage.isUrlInventory(), "Second login failed");

        inventoryPage.openCart();
        // Actual behavior of Saucedemo: the cart is kept (stored in the browser)
        Assert.assertEquals(cartPage.getCartItemNames(), products.subList(0, 2),
                "Cart items were expected to persist after logout/login");

        System.out.println("Cart items and products were expected to persist");
    }
}