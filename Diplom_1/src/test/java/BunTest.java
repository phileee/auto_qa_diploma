import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import praktikum.Bun;

import java.util.Random;

public class BunTest {
    private Bun bun;
    private String name;
    private float price;
    private final Random random = new Random();

    @Before
    public void generateBun() {
        name = "random bun" + random.nextInt();
        price = random.nextFloat();
        bun = new Bun(name, price);
    }

    @Test
    public void getNameReturnString() {
        Assert.assertEquals(name, bun.getName());
    }

    @Test
    public void getPriceReturnFloat() {
        Assert.assertEquals(price, bun.getPrice(), 0.00001);
    }
}
