package utils;

import java.util.ArrayList;
import java.util.List;

public class CSVUtils {
    private static final char SEPARATOR_CHAR = ',';
    private static final char QUOTE_CHAR = '"';
    String csvFile;
    private StringBuilder stringBuilder = new StringBuilder();

    public CSVUtils(String csvFile) {
        this.csvFile = csvFile;
    }

    public static String parseCSVLine(String cvsLine) {
        return String.valueOf(parseCSVLine(cvsLine, SEPARATOR_CHAR, QUOTE_CHAR));
    }

    public static List<String> parseCSVLine(String cvsLine, char separators) {
        return parseCSVLine(cvsLine, separators, QUOTE_CHAR);
    }

    public static List<String> parseCSVLine(String cvsLine, char separators, char customQuote) {

        List<String> result = new ArrayList<>();

        //if empty, return!
        assert cvsLine != null;

        if (customQuote == ' ') {
            customQuote = QUOTE_CHAR;
        }

        if (separators == ' ') {
            separators = SEPARATOR_CHAR;
        }

        StringBuffer curVal = new StringBuffer();
        boolean inQuotes = false;
        boolean startCollectChar = false;
        boolean doubleQuotesInColumn = false;

        char[] chars = cvsLine.toCharArray();

        for (char ch : chars) {
            if (inQuotes) {
                startCollectChar = true;
                if (ch == customQuote) {
                    inQuotes = false;
                    doubleQuotesInColumn = false;
                } else {
                    //Fixed : allow "" in custom quote enclosed
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

                    inQuotes = true;

                    //Fixed : allow "" in empty quote enclosed
                    if (chars[0] != '"' && customQuote == '\"') {
                        curVal.append('"');
                    }

                    //double quotes in column will hit this!
                    if (startCollectChar) {
                        curVal.append('"');
                    }

                } else if (ch == separators) {

                    result.add(curVal.toString());

                    curVal = new StringBuffer();
                    startCollectChar = false;

                } else if (ch != '\r') {
                    if (ch == '\n') {
                        //the end, break!
                        break;
                    } else {
                        curVal.append(ch);
                    }
                }  //ignore LF characters
            }
        }

        result.add(curVal.toString());

        return result;
    }

}