/*
 Author: Samuel Agyakwa
 Date: 02/20/2020
 */

package utils;

/**
 * The purpose of this program is to read and parse csv files, taking into account custom separators, or double quotes.
 */
public class CustomCSVReader {
    private static final char SEPARATOR_CHAR = ',';
    private static final char QUOTE_CHAR = '"';

    public CustomCSVReader() {
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
     * @param separator is the custom separator
     * @param customQuote is the custom quote ("" or '')
     * @return StringBuilder object of parsed line
     */
    public static StringBuilder parseCSVLine(String cvsLine, char separator, char customQuote) {

        StringBuilder result = new StringBuilder();

        // if empty, return!
        assert cvsLine != null;

        if (customQuote == ' ') {
            customQuote = QUOTE_CHAR;
        }

        // change separators
        if (separator == ' ') {
            separator = SEPARATOR_CHAR;
        }

        StringBuffer stringValue = new StringBuffer();
        boolean isInQuotes = false;
        boolean collectChar = false;
        boolean isDoubleQuotesInColumn = false;

        char[] csvCharArray = cvsLine.toCharArray();

        for (char currentCharacter : csvCharArray) {
            // Check if in quotes
            if (isInQuotes) {
                collectChar = true;
                // Check for custom quotes now
                if (currentCharacter == customQuote) {
                    isInQuotes = false;
                    isDoubleQuotesInColumn = false;
                }
                else {
                    // Check and allow "" in custom quote enclosed
                    if (currentCharacter == '\"') {
                        if (!isDoubleQuotesInColumn) {
                            stringValue.append(currentCharacter);
                            isDoubleQuotesInColumn = true;
                        }
                    }
                    else {
                        stringValue.append(currentCharacter);
                    }
                }
            }
            else {
                if (currentCharacter == customQuote) {

                    isInQuotes = true;

                    // Check and llow "" in empty quote enclosed
                    if (csvCharArray[0] != '"' && customQuote == '\"') {
                        stringValue.append('"');
                    }

                    // Double quotes in column will hit this!
                    if (collectChar) {
                        stringValue.append('"');
                    }

                }
                else if (currentCharacter == separator) {

                    result.append(stringValue.toString());

                    stringValue = new StringBuffer();
                    collectChar = false;

                }
                else if (currentCharacter != '\r') {
                    if (currentCharacter == '\n') {
                        // The end, break!
                        break;
                    }
                    else {
                        stringValue.append(currentCharacter);
                    }
                }  // Ignore LF characters
            }
        }

        result.append(stringValue.toString());

        return result;
    }

}