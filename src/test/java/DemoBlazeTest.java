import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;


public class DemoBlazeTest {
    //ElementsCollection SelenideElements;
    ElementsCollection cards = $$(byId("#tbodyid"));
    // ElementsCollection headerSite = SelenideElements(Selectors.byCssSelector("#nav-item active"));
    // ElementsCollection homeBtn = SelenideElements(Selectors.byXpath("//*[text()='Home ']"));
    SelenideElement homeBtn = $x("//*[text()='Home ']");
    SelenideElement AddToCartBtn = $(byText("Add to cart"));
    SelenideElement nokiaLumiaCard = $(byText("Nokia lumia 1520"));
    SelenideElement iphoneCard = $(byText("Iphone 6 32gb"));
    SelenideElement sonyVaioCard = $(byText("Sony vaio i7"));

    @BeforeEach
    void setUp() {
        // Открываем сайт
        open("https://demoblaze.com");
    }

    @AfterEach
    void tearDown() {
        closeWebDriver();
    }

    @Test
    void testAddProductsToCartAndPlaceOrder() {

        int totalPrice = 0;

        nokiaLumiaCard.click();
        String priceNokiaRaw = $("h3").getText(); // Получаем текст цены как строку
        String cleanedNokiaPrice = extractNumbers(priceNokiaRaw);
        int priceNokia = Integer.parseInt(cleanedNokiaPrice);
        totalPrice += priceNokia;
        AddToCartBtn.click();                                         // Клик по кнопке "Add to Cart"
        confirm();                                           // Закрываем диалоговое окно
        homeBtn.click();                                                    // Возвращаемся на главную

        iphoneCard.click();
        String priceIphoneRaw = $("h3").getText();
        String cleanedIphonePrice = extractNumbers(priceIphoneRaw);
        int priceIphone = Integer.parseInt(cleanedIphonePrice);
        totalPrice += priceIphone;
        AddToCartBtn.click();
        confirm();
        homeBtn.click();

        sonyVaioCard.click();
        String priceSonyRaw = $("h3").getText();
        String cleanedSonyPrice = extractNumbers(priceSonyRaw);
        int priceSony = Integer.parseInt(cleanedSonyPrice);
        totalPrice += priceSony;
        AddToCartBtn.click();
        confirm();
        homeBtn.click();
        // Выводим итоговую сумму
        System.out.println("Общая сумма всех товаров: " + totalPrice);

        $("#cartur").click();                       // Открываем корзину
        $(byText("Place Order")).click();

        ElementsCollection priceCells = $$("table tbody tr td:nth-child(3)");
        List<String> cartPrices = priceCells.texts();

        int sumInCart = cartPrices.stream()
                .map(String::trim)                  // убираем пробелы
                .mapToInt(Integer::parseInt)       // преобразуем в int
                .sum();

        System.out.println("Сумма в корзине: " + sumInCart);

//ElementsCollection priceCells = $$("table tbody tr td:nth-child(3)").filter(Condition.visible);
        // Заполняем форму заказа
        $("#name").setValue("John Doe");
        $("#country").setValue("USA");
        $("#city").setValue("New York");
        $("#card").setValue("4151178111118911");
        $("#month").setValue("10");
        $("#year").setValue("2000");

        // Нажимаем "Purchase"
        $(byAttribute("onclick", "purchaseOrder()")).click();

        // Ждём окна подтверждения (зелёная галочка)
        // $(".sweet-alert").shouldBe(visible);
        // $("sa-confirm-button-container").click(); //нажимаем ок style="display: inline-block;"
        // $(byAttribute("style", "display: inline-block;")).click();

        // Получаем полный текст подтверждения
        String confirmationText = $(".sweet-alert").getText();
        String[] lines = confirmationText.split("\n");

        // Ищем строку с ID заказа
        String idText = null;
        for (String line : lines) {
            if (line.contains("Id:")) {
                idText = line.trim();
                break;
            }
        }
        if (idText == null) {
            throw new AssertionError("Не найден ID заказа в подтверждении");
        }


        // Выводим ID заказа в консоль
        System.out.println(idText);

        // Кликаем "OK" в окне подтверждения
        //$(".sa-button-container button").click();
        $(byText("OK")).click();

        // Проверяем, что окно подтверждения закрылось
        $("#sweet-alert  showSweetAlert visible").shouldBe(hidden);
    }

    private String extractNumbers(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Строка пуста или null: " + text);
        }

        // Удаляем все символы, кроме цифр
        String cleaned = text.replaceAll("[^\\d]", "");

        if (cleaned.isEmpty()) {
            throw new IllegalArgumentException(
                    "После очистки строка не содержит цифр. Исходная: " + text
            );
        }

        return cleaned;

    }

    private int parsePrice(String priceText) {
        return Integer.parseInt(priceText.trim());
    }
}