package com.rideshare.app.custom;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateFormatValidator {
    private Pattern pattern;
    private Matcher matcher;

    private static final String DATE_PATTERN =
            "(0?[1-9]|1[012]) [/.-] (0?[1-9]|[12][0-9]|3[01]) [/.-] ((19|20)\\d\\d)";


    /**
     * Validate date format with regular expression
     * @param date date address for validation
     * @return true valid date format, false invalid date format
     */
    public boolean validate(final String date) {

        matcher = pattern.matcher(date);

        return matcher.matches();

    }
}
