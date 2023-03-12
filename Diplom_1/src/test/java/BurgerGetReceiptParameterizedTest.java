import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import praktikum.Bun;
import praktikum.Burger;
import praktikum.Ingredient;
import praktikum.IngredientType;

@RunWith(Parameterized.class)
public class BurgerGetReceiptParameterizedTest {

    Burger burger;
    @Mock
    Bun bun;
    @Mock
    Ingredient ingredient;

    @Mock
    IngredientType ingredientType;

    private final String bunName;
    private final String ingredientTypeString;
    private final String ingredientName;
    private final float finalPrice;

    public BurgerGetReceiptParameterizedTest(String bunName, String ingredientTypeString, String ingredientName, float finalPrice) {
        this.bunName = bunName;
        this.ingredientTypeString = ingredientTypeString;
        this.ingredientName = ingredientName;
        this.finalPrice = finalPrice;
    }

    @Parameterized.Parameters(name = "Сборка бургера. Тестовые данные: {0} {1} {2} {3}")
    public static Object[][] getDataForReceipt() {
        return new Object[][] {
                {"black bun", "sauce", "hot sauce", 100},
                {"white_bun", "filling", "dinosaur", 100.5f},
        };
    }

    @Before
    public void generateMocksAndObject() {
        MockitoAnnotations.openMocks(this);
        burger = new Burger();
        burger.bun = bun;
        burger.ingredients.add(ingredient);
    }

    @Test
    public void getReceiptReturnString() {
        Mockito.when(bun.getName()).thenReturn(bunName);
        Mockito.when(ingredient.getType()).thenReturn(ingredientType);
        Mockito.when(ingredientType.toString()).thenReturn(ingredientTypeString);
        Mockito.when(ingredient.getName()).thenReturn(ingredientName);
        Mockito.when(burger.getPrice()).thenReturn(finalPrice);

        String resultBurger = String.format("(==== %s ====)%n= %s %s =%n(==== %s ====)%n%nPrice: %f%n", bunName, ingredientTypeString, ingredientName, bunName, finalPrice);

        Assert.assertEquals(resultBurger, burger.getReceipt());
    }
}
