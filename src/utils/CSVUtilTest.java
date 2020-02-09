package utils;

import org.hamcrest.core.IsNull;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CSVUtilTest {
    @Test
    public void test_no_quote() {

        String line = "10,AU,Australia";
        List<String> result = Collections.singletonList(CSVUtil.parseCSVLine(line));

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(1));
        assertThat(result.get(0), is("[10, AU, Australia]"));

    }

    @Test
    public void test_no_quote_but_double_quotes_in_column() throws Exception {

        String line = "10,AU,Aus\"\"tralia";

        List<String> result = Collections.singletonList(CSVUtil.parseCSVLine(line));
        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(1));
        assertThat(result.get(0), is("[10, AU, Aus\"tralia]"));

    }

    @Test
    public void test_double_quotes() {

        String line = "\"10\",\"AU\",\"Australia\"";
        List<String> result = Collections.singletonList(CSVUtil.parseCSVLine(line));

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(1));
        assertThat(result.get(0), is("[10, AU, Australia]"));

    }

    @Test
    public void test_double_quotes_but_double_quotes_in_column() {

        String line = "\"10\",\"AU\",\"Aus\"\"tralia\"";
        List<String> result = Collections.singletonList(CSVUtil.parseCSVLine(line));

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(1));
        assertThat(result.get(0), is("[10, AU, Aus\"tralia]"));

    }

    @Test
    public void test_double_quotes_but_comma_in_column() {

        String line = "\"10\",\"AU\",\"Aus,tralia\"";
        List<String> result = Collections.singletonList(CSVUtil.parseCSVLine(line));
        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(1));
        assertThat(result.get(0), is("[10, AU, Aus,tralia]"));

    }

}

