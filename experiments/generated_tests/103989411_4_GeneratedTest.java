java
package com.uber.athenax.vm.compiler.planner;

import com.uber.athenax.vm.compiler.parser.impl.ParseException;
import org.apache.calcite.sql.SqlNodeList;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PlannerTest {

  @Test
  public void testParseSimpleSelect() throws ParseException {
    String sql = "SELECT * FROM MyTable";
    SqlNodeList sqlNodeList = Planner.parse(sql);
    assertNotNull(sqlNodeList);
    assertTrue(sqlNodeList.size() > 0);
  }

  @Test
  public void testParseSelectWithWhere() throws ParseException {
    String sql = "SELECT * FROM MyTable WHERE id = 1";
    SqlNodeList sqlNodeList = Planner.parse(sql);
    assertNotNull(sqlNodeList);
    assertTrue(sqlNodeList.size() > 0);
  }

  @Test
  public void testParseCreateTable() throws ParseException {
    String sql = "CREATE TABLE MyTable (id INT, name VARCHAR)";
    SqlNodeList sqlNodeList = Planner.parse(sql);
    assertNotNull(sqlNodeList);
    assertTrue(sqlNodeList.size() > 0);
  }

  @Test(expected = ParseException.class)
  public void testParseInvalidSQL() throws ParseException {
    String sql = "SELEC * FROM MyTable";
    Planner.parse(sql);
  }

  @Test
  public void testParseMultipleStatements() throws ParseException {
    String sql = "SELECT * FROM Table1; SELECT * FROM Table2;";
    SqlNodeList sqlNodeList = Planner.parse(sql);
    assertNotNull(sqlNodeList);
    assertTrue(sqlNodeList.size() > 0);
  }

  @Test
  public void testParseSelectWithGroupBy() throws ParseException {
    String sql = "SELECT id, COUNT(*) FROM MyTable GROUP BY id";
    SqlNodeList sqlNodeList = Planner.parse(sql);
    assertNotNull(sqlNodeList);
    assertTrue(sqlNodeList.size() > 0);
  }

  @Test
  public void testParseSelectWithOrderBy() throws ParseException {
    String sql = "SELECT * FROM MyTable ORDER BY id";
    SqlNodeList sqlNodeList = Planner.parse(sql);
    assertNotNull(sqlNodeList);
    assertTrue(sqlNodeList.size() > 0);
  }

  @Test
  public void testParseSelectWithLimit() throws ParseException {
    String sql = "SELECT * FROM MyTable LIMIT 10";
    SqlNodeList sqlNodeList = Planner.parse(sql);
    assertNotNull(sqlNodeList);
    assertTrue(sqlNodeList.size() > 0);
  }

  @Test
  public void testParseSelectWithJoin() throws ParseException {
    String sql = "SELECT * FROM Table1 JOIN Table2 ON Table1.id = Table2.id";
    SqlNodeList sqlNodeList = Planner.parse(sql);
    assertNotNull(sqlNodeList);
    assertTrue(sqlNodeList.size() > 0);
  }

  @Test
  public void testParseSelectWithAlias() throws ParseException {
      String sql = "SELECT t1.id, t2.name FROM Table1 AS t1 JOIN Table2 AS t2 ON t1.id = t2.id";
      SqlNodeList sqlNodeList = Planner.parse(sql);
      assertNotNull(sqlNodeList);
      assertTrue(sqlNodeList.size() > 0);
  }

}