package example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalculatorTest {
  @Test void add() { assertEquals(5, new Calculator().add(2,3)); }
  @Test void divByZero() { assertThrows(IllegalArgumentException.class, () -> new Calculator().div(1,0)); }
}

