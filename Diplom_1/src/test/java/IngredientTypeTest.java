import org.junit.Assert;
import org.junit.Test;
import praktikum.IngredientType;

import java.util.ArrayList;
import java.util.List;

public class IngredientTypeTest {

    List<String> ingredientStrings = List.of("SAUCE", "FILLING");

    @Test
    public void ingredientTypeTest() {
        ArrayList<String> ingredientTypeStrings = new ArrayList<>();
        for (IngredientType ingredientType : IngredientType.values()) {
            ingredientTypeStrings.add(ingredientType.toString());
        }
        Assert.assertEquals(ingredientStrings, ingredientTypeStrings);
    }
}
