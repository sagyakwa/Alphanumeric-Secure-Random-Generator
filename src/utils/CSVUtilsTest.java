package utils;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static com.sun.org.apache.xerces.internal.util.PropertyState.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CSVUtilsTest {

    public static void main (String[] args) throws Exception {
        CSVUtilsTest test = new CSVUtilsTest();
        test.test_double_quotes();
        test.test_double_quotes_but_comma_in_column();
        test.test_double_quotes_but_double_quotes_in_column();
        test.test_no_quote();
        test.test_no_quote_but_double_quotes_in_column();

    }

    @Test
    public void test_no_quote() {

        String line = "10,AU,Australia";
        List<String> result = Collections.singletonList(CSVUtils.parseCSVLine(line));

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Australia"));

    }

    @Test
    public void test_no_quote_but_double_quotes_in_column() throws Exception {

        String line = "10,AU,Aus\"\"tralia";

        List<String> result = Collections.singletonList(CSVUtils.parseCSVLine(line));
        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Aus\"tralia"));

    }

    @Test
    public void test_double_quotes() {

        String line = "\"10\",\"AU\",\"Australia\"";
        List<String> result = CSVUtils.parseLine(line);

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Australia"));

    }

    @Test
    public void test_double_quotes_but_double_quotes_in_column() {

        String line = "\"10\",\"AU\",\"Aus\"\"tralia\"";
        List<String> result = CSVUtils.parseLine(line);

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Aus\"tralia"));

    }

    @Test
    public void test_double_quotes_but_comma_in_column() {

        String line = "\"10\",\"AU\",\"Aus,tralia\"";
        List<String> result = CSVUtils.parseLine(line);

        assertThat(result, IsNull.notNullValue());
        assertThat(result.size(), is(3));
        assertThat(result.get(0), is("10"));
        assertThat(result.get(1), is("AU"));
        assertThat(result.get(2), is("Aus,tralia"));

    }

}
}
