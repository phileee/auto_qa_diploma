import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import praktikum.Ingredient;
import praktikum.IngredientType;

@RunWith(MockitoJUnitRunner.class)
public class IngredientTest {

    Ingredient ingredient;

    @Mock
    IngredientType type;

    String name;

    float price;

    @Before
    public void generateIngredient() {
        name = "имя ингридиента";
        price = 500.5f;
        ingredient = new Ingredient(type, name, price);
    }

    @Test
    public void getPriceReturnFloat() {
        Assert.assertEquals(price, ingredient.getPrice(), 0.001);
    }

    @Test
    public void getNameReturnString() {
        Assert.assertEquals(name, ingredient.getName());
    }

    @Test
    public void getTypeReturnEnum() {
        Assert.assertEquals(type, ingredient.getType());
    }
}
