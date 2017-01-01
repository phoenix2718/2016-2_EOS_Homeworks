package com.example.phoen.hw4;

/**
 * Created by phoen on 11/3/2016.
 */
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class DataParser {
    private String url; // Request URL
    private String date;
    private String place;
    private String no2;
    private String o3;
    private String co;
    private String so2;
    private String pm10;
    private String pm25;

    public String getUrl() { return url; }
    public String getDate() { return date; }
    public String getPlace() { return place; }
    public String getNo2() { return no2; }
    public String getO3() { return o3; }
    public String getCo() { return co; }
    public String getSo2() { return so2; }
    public String getPm10() { return pm10; }
    public String getPm25() { return pm25; }
    public void printInfo() {
        System.out.println("Date 날짜: " + date);
        System.out.println("Place 장소: " + place);
        System.out.println("NO2 이산화질소: " + no2 + " (ppm)");
        System.out.println("O3 오존: " + o3 + " (ppm)");
        System.out.println("CO 일산화탄소: " + co + " (ppm)");
        System.out.println("SO2 아황산가스: " + so2 + " (ppm)");
        System.out.println("PM10 미세먼지: " + pm10 + " (㎍/㎥)");
        System.out.println("PM25 초미세먼지: " + pm25 + " (㎍/㎥)");
    }

    public String getTodayString() {
        // reference: http://docs.oracle.com/javase/6/docs/api/java/util/GregorianCalendar.html

        Calendar todayCalendar = GregorianCalendar.getInstance();
        Date todayDate = new Date();
        todayCalendar.setTime(todayDate);

        int year = todayCalendar.get(Calendar.YEAR);
        int month = todayCalendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = todayCalendar.get(Calendar.DAY_OF_MONTH);

        String todayString = Integer.toString(year);

        if(month < 10) {
            todayString = todayString + "0" + Integer.toString(month);
        } else {
            todayString = todayString + Integer.toString(month);
        }
        if(dayOfMonth < 10) {
            todayString = todayString + "0" + Integer.toString(dayOfMonth);
        } else {
            todayString = todayString + Integer.toString(dayOfMonth);
        }

        return todayString;
    }

    public void loadData(String date, String place) {
        if(date.toLowerCase() == "today") {
            date = getTodayString();
        }
        if(date.length() != 8) {
            // date should be the format like "20161102", meaning "Nov. 2rd, 2016"
            return;
        }
        if(this.date == date) {
            // The data has already been loaded.
            // Skip the request procedure.
            return;
        }

        try {
            String key = "6b4641647870686f36304549425661";
            int startIndex = 1;
            int endIndex = 1; // because we only use one data.

            String placeEncoded = URLEncoder.encode(place, "UTF-8"); // "노원구"로 입력하면, xml값을 제대로 받아오지 못한다. 한글이기 때문. (reference: http://slowlywalk1993.tistory.com/entry/Java-URL-%ED%95%9C%EA%B8%80%EC%9D%B8%EC%8B%9D-%ED%95%9C%EA%B8%80-%EC%9D%B8%EC%BD%94%EB%94%A9)
            String urlString = String.format(
                    "http://openapi.seoul.go.kr:8088/%s/xml/DailyAverageAirQuality/%d/%d/%s/%s", key, startIndex,
                    endIndex, date, placeEncoded);
            this.url = urlString;

            URL url = new URL(urlString);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(url.openStream()));

            String rootElementName = doc.getDocumentElement().getNodeName();

            if (rootElementName.equals("DailyAverageAirQuality") == false) {
                return;
            }
            int listTotalCount = Integer.parseInt(
                    doc.getDocumentElement().getElementsByTagName("list_total_count").item(0).getTextContent());
            if (listTotalCount != 1) {
                return;
            }

            // Stored required data elements
            Element row = (Element) doc.getDocumentElement().getElementsByTagName("row").item(0);
            // 1. 이산화질소 농도
            no2 = row.getElementsByTagName("NO2").item(0).getTextContent();
            // 2. 오존 농도
            o3 = row.getElementsByTagName("O3").item(0).getTextContent();
            // 3. 일산화탄소 농도
            co = row.getElementsByTagName("CO").item(0).getTextContent();
            // 4. 아황산가스 농도
            so2 = row.getElementsByTagName("SO2").item(0).getTextContent();
            // 5. 미세먼지
            pm10 = row.getElementsByTagName("PM10").item(0).getTextContent();
            // 6. 초미세먼지
            pm25 = row.getElementsByTagName("PM25").item(0).getTextContent();

            this.place = place;
            this.date = date;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}