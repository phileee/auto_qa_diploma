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

@RunWith(Parameterized.class)
public class BurgerGetPriceParameterizedTest {

    Burger burger;
    @Mock
    Bun bun;
    @Mock
    Ingredient ingredientFirst;

    @Mock
    Ingredient ingredientSecond;
    private final float priceOfBun;
    private final float priceOfIngredient;
    private final float priceOfOtherIngredient;
    private final float result;

    public BurgerGetPriceParameterizedTest(float priceOfBun, float priceOfIngredient, float priceOfOtherIngredient, float result) {
        this.priceOfBun = priceOfBun;
        this.priceOfIngredient = priceOfIngredient;
        this.priceOfOtherIngredient = priceOfOtherIngredient;
        this.result = result;
    }

    @Parameterized.Parameters
    public static Object[][] getPricesForTest() {
        return new Object[][] {
                {100.07f, 100, 0.4f, 300.54f},
                {100, 0.55f, 100.07f, 300.62f},
                {200.57f, 100.57f, 100, 601.71f},
        };
    }


    @Before
    public void generateBurger() {
        MockitoAnnotations.openMocks(this);
        burger = new Burger();
        burger.bun = bun;
        burger.ingredients.add(ingredientFirst);
        burger.ingredients.add(ingredientSecond);
    }

    @Test
    public void getPriceReturnFloatNumber() {
        Mockito.when(bun.getPrice()).thenReturn(priceOfBun);
        Mockito.when(ingredientFirst.getPrice()).thenReturn(priceOfIngredient);
        Mockito.when(ingredientSecond.getPrice()).thenReturn(priceOfOtherIngredient);

        float finalPrice = burger.getPrice();
        Assert.assertEquals(result, finalPrice, 0.001);
    }

}
