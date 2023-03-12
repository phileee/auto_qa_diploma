import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import praktikum.Bun;
import praktikum.Burger;
import praktikum.Ingredient;

@RunWith(MockitoJUnitRunner.class)
public class BurgerTest {

    Burger burger;

    @Before
    public void generateBurger() {
        burger = new Burger();
    }

    @Mock
    private Bun bun;

    @Mock
    Ingredient ingredient;

    @Mock
    Ingredient ingredientSecond;

    @Test
    public void setBunsIsSetterOfBun() {
        burger.setBuns(bun);
        Assert.assertEquals(bun, burger.bun);
    }

    @Test
    public void addIngredientDoesAdd() {
        burger.addIngredient(ingredient);
        Assert.assertTrue(burger.ingredients.contains(ingredient));
    }

    @Test
    public void removeIngredientDoesRemove() {
        burger.ingredients.add(ingredient);
        int indexIngredients = burger.ingredients.indexOf(ingredient);
        burger.removeIngredient(indexIngredients);
        Assert.assertFalse(burger.ingredients.contains(ingredient));
    }

    @Test
    public void moveIngredientRemoveAndAddIngredientToNewIndex() {
        burger.ingredients.add(ingredient);
        burger.ingredients.add(ingredientSecond);

        int index = 0;
        int newIndex = 1;

        burger.moveIngredient(index, newIndex);
        Assert.assertEquals(ingredientSecond, burger.ingredients.get(index));
    }
}
