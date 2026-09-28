package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CartPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ===== Locators =====
    private final By cartItems        = By.className("cart_item");
    private final By itemNames        = By.cssSelector(".cart_item .inventory_item_name");
    private final By checkoutButton   = By.id("checkout");
    private final By continueShopping = By.id("continue-shopping");
    private final By errorMessage     = By.cssSelector("[data-test='error']");

    // Checkout step one (info form)
    private final By firstName  = By.id("first-name");
    private final By lastName   = By.id("last-name");
    private final By postalCode = By.id("postal-code");
    private final By continueBtn = By.id("continue");

    // Checkout step two (overview)
    private final By itemTotalLabel = By.className("summary_subtotal_label");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /** Makes sure we are really on the cart page before reading anything */
    public void waitForCartPage() {
        wait.until(ExpectedConditions.urlContains("/cart.html"));
    }

    public List<String> getCartItemNames() {
        waitForCartPage();
        List<String> names = new ArrayList<>();
        for (WebElement e : driver.findElements(itemNames)) {
            names.add(e.getText());
        }
        return names;
    }

    public boolean isCartEmpty() {
        waitForCartPage();
        return driver.findElements(cartItems).isEmpty();
    }

    public void removeProduct(String name) {
        String id = "remove-" + name.toLowerCase().replace(" ", "-");
        wait.until(ExpectedConditions.elementToBeClickable(By.id(id))).click();
    }

    public void clickContinueShopping() {
        wait.until(ExpectedConditions.elementToBeClickable(continueShopping)).click();
    }

    public void clickCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(checkoutButton)).click();
    }

    public void fillCheckoutInfo(String first, String last, String zip) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(firstName)).sendKeys(first);
        driver.findElement(lastName).sendKeys(last);
        driver.findElement(postalCode).sendKeys(zip);
        wait.until(ExpectedConditions.elementToBeClickable(continueBtn)).click();
    }

    /** Reads "Item total: $129.97" and returns 129.97 */
    public double getItemTotal() {
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(itemTotalLabel)).getText();
        return Double.parseDouble(text.substring(text.indexOf("$") + 1).trim());
    }

    /** True if an error message appears after clicking Checkout with an empty cart */
    public boolean isEmptyCartErrorDisplayed() {
        try {
            return wait.withTimeout(Duration.ofSeconds(3))
                    .until(ExpectedConditions.visibilityOfElementLocated(errorMessage))
                    .isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isOnCheckoutForm() {
        return driver.getCurrentUrl().contains("checkout-step-one");
    }
}