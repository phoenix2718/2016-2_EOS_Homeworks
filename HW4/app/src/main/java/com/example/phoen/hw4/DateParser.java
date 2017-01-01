package com.example.phoen.hw4;

/**
 * Created by phoen on 11/4/2016.
 */
public class DateParser {
    public static String parse(String date) {
        // input example: 2016년1월26일
        // "   2016년 1월 26일  "
        // "2016년01월26일 "
        // "2016년 01월 26일 "
        date = date.replaceAll("\\s+",""); // remove all whitespace

        int indexYearChar = date.indexOf("년");
        int indexMonthChar = date.indexOf("월");
        int indexDayOfMonthChar = date.indexOf("일");

        if(indexYearChar == -1) {
            return date;
        }

        String year = date.substring(0, indexYearChar);
        String month = date.substring(indexYearChar + 1, indexMonthChar);
        String dayOfMonth = date.substring(indexMonthChar + 1, indexDayOfMonthChar);

        if(month.length() == 1) {
            // If the month is one-digit number. Example: "1월". Counterexample: "01월"
            month = "0" + month;
        }
        if(dayOfMonth.length() == 1) {
            // If the day of the month is one-digit number.
            dayOfMonth = "0" + dayOfMonth;
        }

        return (year + month + dayOfMonth);
    }
}
