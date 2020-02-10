package utils;

import java.util.List;

/**
 * The purpose of this program is to read and parse csv files, taking into account custom separators, or double quotes.
 */
public class CSVUtil {
    private static final char SEPARATOR_CHAR = ',';
    private static final char QUOTE_CHAR = '"';
    private String csvFile;

    public CSVUtil(String csvFile) {
        this.csvFile = csvFile;
    }

    /**
     * @param cvsLine is the line to parse
     * @return String object of parsed line
     */
    public static String parseCSVLine(String cvsLine) {
        return String.valueOf(parseCSVLine(cvsLine, SEPARATOR_CHAR, QUOTE_CHAR));
    }

    /**
     * @param cvsLine is the line to parse
     * @param separators is the custom separator
     * @return StringBuilder object of parsed line
     */
    public static StringBuilder parseCSVLine(String cvsLine, char separators) {
        return parseCSVLine(cvsLine, separators, QUOTE_CHAR);
    }

    /**
     * @param cvsLine is the line to parse
     * @param separators is the custom separator
     * @param customQuote is the custom quote ("" or '')
     * @return StringBuilder object of parsed line
     */
    public static StringBuilder parseCSVLine(String cvsLine, char separators, char customQuote) {

        StringBuilder result = new StringBuilder();

        // if empty, return!
        assert cvsLine != null;

        if (customQuote == ' ') {
            customQuote = QUOTE_CHAR;
        }

        // change separators
        if (separators == ' ') {
            separators = SEPARATOR_CHAR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean isInQuotes = false;
        boolean startCollectingChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {
            if (isInQuotes) {
                startCollectingChar = true;
                if (ch == customQuote) {
                    isInQuotes = false;
                    doubleQuotesInColumn = false;
                } else {
                    // Check and allow "" in custom quote enclosed
                    if (ch == '\"') {
                        if (!doubleQuotesInColumn) {
                            curVal.append(ch);
                            doubleQuotesInColumn = true;
                        }
                    } else {
                        curVal.append(ch);
                    }
                }
            } else {
                if (ch == customQuote) {

                    isInQuotes = true;

                    // Check and llow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    // Double quotes in column will hit this!
                    if (startCollectingChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.append(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectingChar = false;

                } else if (ch != '\r') {
                    if (ch == '\n') {
                        // The end, break!
                        break;
                    } else {
                        curVal.append(ch);
                    }
                }  // Ignore LF characters
            }
        }

        result.append(curVal.toString());

        return result;
    }

}