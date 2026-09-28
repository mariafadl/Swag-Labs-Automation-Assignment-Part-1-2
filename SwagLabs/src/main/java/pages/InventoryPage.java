package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Set;

public class InventoryPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ===== Locators =====
    private final By cartIcon      = By.id("shopping_cart_container");
    private final By cartLink      = By.className("shopping_cart_link");
    private final By inventoryItem = By.className("inventory_item");
    private final By menuButton    = By.id("react-burger-menu-btn");
    private final By logoutLink    = By.id("logout_sidebar_link");
    private final By linkedinIcon = By.cssSelector("a[href*='linkedin.com']");
    private final By facebookIcon = By.cssSelector("a[href*='facebook.com']");
    private final By twitterIcon  = By.cssSelector("a[href*='x.com'], a[href*='twitter.com']");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ===== Part 1 =====
    public boolean isUrlInventory() {
        return wait.until(ExpectedConditions.urlContains("/inventory.html"));
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public boolean isCartIconDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(cartIcon)).isDisplayed();
    }

    public int getProductsCount() {
        wait.until(ExpectedConditions.numberOfElementsToBe(inventoryItem, 6));
        return driver.findElements(inventoryItem).size();
    }

    // ===== Helpers (dynamic locators built from product name) =====
    /** "Sauce Labs Bolt T-Shirt" -> "sauce-labs-bolt-t-shirt" */
    private String toId(String productName) {
        return productName.toLowerCase().replace(" ", "-");
    }

    private By productButton(String name) {
        return By.xpath("//div[@data-test='inventory-item-name' and text()='" + name + "']"
                + "/ancestor::div[@data-test='inventory-item']//button");
    }

    private By productPrice(String name) {
        return By.xpath("//div[@data-test='inventory-item-name' and text()='" + name + "']"
                + "/ancestor::div[@data-test='inventory-item']"
                + "//div[@data-test='inventory-item-price']");
    }

    // ===== Part 2: products =====
    public void addProductToCart(String name) {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-" + toId(name)))).click();
    }

    /** Returns the button text: "Add to cart" or "Remove" */
    public String getProductButtonText(String name) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(productButton(name))).getText();
    }

    /** "$29.99" -> 29.99 */
    public double getProductPrice(String name) {
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(productPrice(name))).getText();
        return Double.parseDouble(text.replace("$", "").trim());
    }

    public void openCart() {
        wait.until(ExpectedConditions.elementToBeClickable(cartLink)).click();
    }

    // ===== Part 2: logout =====
    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(menuButton)).click();
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
    }

    // ===== Part 2: social links =====
    public String clickLinkedin() { return clickSocialAndGetUrl(linkedinIcon, "linkedin"); }
    public String clickFacebook() { return clickSocialAndGetUrl(facebookIcon, "facebook"); }
    public String clickTwitter()  { return clickSocialAndGetUrl(twitterIcon, "x.com"); }

    /**
     * Social icons open in a NEW TAB, so we:
     * 1) click, 2) switch to the new tab, 3) wait for the URL, 4) read it,
     * 5) close the tab and return to the original one.
     */
    private String clickSocialAndGetUrl(By icon, String expectedPart) {
        String originalWindow = driver.getWindowHandle();

        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(icon));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        wait.until(ExpectedConditions.elementToBeClickable(icon)).click();

        wait.until(ExpectedConditions.numberOfWindowsToBe(2));

        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            if (!handle.equals(originalWindow)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        String url;
        try {
            // Twitter redirects twitter.com -> x.com, so we wait for the expected text
            wait.until(ExpectedConditions.urlContains(expectedPart));
        } catch (Exception ignored) {
            // if it times out we still return the current URL so the assertion shows it
        }
        url = driver.getCurrentUrl();

        driver.close();
        driver.switchTo().window(originalWindow);
        return url;
    }
}