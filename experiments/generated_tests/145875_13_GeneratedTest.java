java
package com.dbdeploy.database;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class QueryStatementSplitterTest {
    @Test
    public void shouldReturnEmptyListWhenNoStatements() {
        QueryStatementSplitter splitter = new QueryStatementSplitter();
        List<String> statements = splitter.split("");
        assertThat(statements, is(empty()));
    }

    @Test
    public void shouldReturnASingleStatementWhenNoDelimiterIsFound() {
        QueryStatementSplitter splitter = new QueryStatementSplitter();
        List<String> statements = splitter.split("select * from my_table");
        assertThat(statements, contains("select * from my_table"));
    }

    @Test
    public void shouldReturnASingleStatementWhenADelimiterIsFound() {
        QueryStatementSplitter splitter = new QueryStatementSplitter();
        List<String> statements = splitter.split("select * from my_table;");
        assertThat(statements, contains("select * from my_table"));
    }

    @Test
    public void shouldReturnTwoStatementsWhenTwoStatementsAreFound() {
        QueryStatementSplitter splitter = new QueryStatementSplitter();
        List<String> statements = splitter.split("select * from my_table; select * from your_table;");
        assertThat(statements, contains("select * from my_table", "select * from your_table"));
    }

    @Test
    public void shouldReturnTwoStatementsWhenTwoStatementsAreFoundOnSeparateLines() {
        QueryStatementSplitter splitter = new QueryStatementSplitter();
        List<String> statements = splitter.split("select * from my_table;\nselect * from your_table;");
        assertThat(statements, contains("select * from my_table", "select * from your_table"));
    }

    @Test
    public void shouldReturnTwoStatementsWhenTwoStatementsAreFoundOnSeparateLinesWithDifferentLineEndings() {
        QueryStatementSplitter splitter = new QueryStatementSplitter();
        List<String> statements = splitter.split("select * from my_table;\r\nselect * from your_table;");
        assertThat(statements, contains("select * from my_table", "select * from your_table"));
    }

    @Test
    public void shouldHandleDifferentDelimiters() {
        QueryStatementSplitter splitter = new QueryStatementSplitter();
        splitter.setDelimiter("GO");
        List<String> statements = splitter.split("select * from my_tableGO\nselect * from your_tableGO");
        assertThat(statements, contains("select * from my_table", "select * from your_table"));
    }
}