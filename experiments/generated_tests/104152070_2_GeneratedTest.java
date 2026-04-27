java
package react4j;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class ContextsTest
{
  @Test
  public void get()
  {
    final Context<String> context1 = Contexts.get( String.class );
    assertNotNull( context1 );
    final Context<String> context2 = Contexts.get( String.class );
    assertEquals( context1, context2 );
    final Context<Integer> context3 = Contexts.get( Integer.class );
    assertNotNull( context3 );
    assertNotEquals( context1, context3 );
  }

  @Test
  public void getWithQualifier()
  {
    final Context<String> context1 = Contexts.get( String.class, "A" );
    assertNotNull( context1 );
    final Context<String> context2 = Contexts.get( String.class, "A" );
    assertEquals( context1, context2 );
    final Context<String> context3 = Contexts.get( String.class, "B" );
    assertNotNull( context3 );
    assertNotEquals( context1, context3 );
  }

  @Test
  public void nextContextId()
  {
    assertTrue( Contexts.nextContextId() > 0 );
    assertTrue( Contexts.nextContextId() > 1 );
  }
}