package example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CalculatorPitestTest {

  @Test
  void testDivNormalCase() {
    Calculator calculator = new Calculator();
    assertEquals(2, calculator.div(6, 3));
  }

  @Test
  void testDivByZeroThrowsException() {
    Calculator calculator = new Calculator();
    assertThrows(IllegalArgumentException.class, () -> calculator.div(10, 0));
  }

  @Test
  void testMulPositiveNumbers() {
    Calculator calculator = new Calculator();
    assertEquals(15, calculator.mul(3, 5));
  }

  @Test
  void testSubPositiveNumbers() {
    Calculator calculator = new Calculator();
    assertEquals(5, calculator.sub(10, 5));
  }
}
