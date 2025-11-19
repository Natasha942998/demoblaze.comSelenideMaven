import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

public class DemoBlazeTest {

    ElementsCollection cards = $$(byId("#tbodyid"));
    SelenideElement homeBtn = $x("//*[text()='Home ']");
    SelenideElement AddToCartBtn = $(byText("Add to cart"));
    SelenideElement nokiaLumiaCard = $(byText("Nokia lumia 1520"));
    SelenideElement iphoneCard = $(byText("Iphone 6 32gb"));
    SelenideElement sonyVaioCard = $(byText("Sony vaio i7"));

    @BeforeEach
    void setUp() {
        open("https://demoblaze.com"); // Открываем сайт
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
        totalPrice += priceNokia; //суммируем цену
        AddToCartBtn.click(); // Клик по кнопке "Add to Cart"
        confirm(); // Закрываем диалоговое окно
        homeBtn.click(); // Возвращаемся на главную страницу

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

        System.out.println("ОSum of products: " + totalPrice); // Выводим итоговую сумму
        $("#cartur").click(); // Открываем корзину
        int fullPrice = calculateTotalPrice();
        System.out.println("Total price in cart: " + fullPrice);//Выводим итоговую сумму

        $(byText("Place Order")).click(); // Нажимаем Place Order

        $("#name").setValue("John Doe"); // Заполняем форму заказа
        $("#country").setValue("USA");
        $("#city").setValue("New York");
        $("#card").setValue("4151178111118911");
        $("#month").setValue("10");
        $("#year").setValue("2000");

        $(byAttribute("onclick", "purchaseOrder()")).click(); // Нажимаем "Purchase"

// Ждём окна подтверждения (зелёная галочка)

        String confirmationText = $(".sweet-alert").getText(); // Получаем полный текст подтверждения
        String[] lines = confirmationText.split("\n");

        String idText = null; // Ищем строку с ID заказа
        for (String line : lines) {
            if (line.contains("Id:")) {
                idText = line.trim();
                break;
            }
        }
        if (idText == null) {
            throw new AssertionError("Не найден ID заказа в подтверждении");
        }

        System.out.println(idText); // Выводим ID заказа в консоль

        $(byText("OK")).click(); // Кликаем "OK" в окне подтверждения

        $("#sweet-alert showSweetAlert visible").shouldBe(hidden);// Проверяем, что окно подтверждения закрылось
    }

    private String extractNumbers(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Строка пуста или null: " + text);
        }

        String cleaned = text.replaceAll("[^\\d]", ""); // Удаляем все символы, кроме цифр
        if (cleaned.isEmpty()) {
            throw new IllegalArgumentException(
                    "После очистки строка не содержит цифр. Исходная: " + text);
        }
        return cleaned;
    }

    private int calculateTotalPrice() {
        int totalPrice = 0;
        for (int i = 1; i <= 3; i++) {
            String priceText = $x("//tr[" + i + "]/td[3]").getText();
            int price = Integer.parseInt(priceText.replaceAll("[^0-9]", ""));
            totalPrice += price;
        }
        return totalPrice;
    }
}