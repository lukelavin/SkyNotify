package com.example.hashimj.lol;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventLoader extends AsyncTaskLoader<List<String>> {

    public EventLoader(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public List<String> loadInBackground() {
    }

    public static ArrayList<String> getLinks(double lat, double lng) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String now = dtf.format(LocalDateTime.now());
        int currentYear = Integer.parseInt(now.split("/")[0]);
        int currentMonth = Integer.parseInt(now.split("/")[1]);
        int currentDay = Integer.parseInt(now.split("/")[2]);

        return getLinks(lat, lng, now , "" + (currentYear + 1) + "/" + currentMonth + "/" + currentDay);
    }

    public static ArrayList<String> getLinks(double lat, double lng, String startYYYYMMDD, String endYYYYMMDD) throws IOException {
        int timezone = 0;
        String searchUrl = "https://in-the-sky.org/search.php?latitude=" + lat +
                "&longitude=" + lng +
                "&timezone=-0" + timezone + "%3A00" +
                "&s=&searchtype=News&objorder=1&magmin=&magmax=&distmin=&distmax=&lyearmin=1957&lyearmax=2018&satorder=0&satgroup=0&satdest=0&satsite=0&satowner=0&feed=thesky&ordernews=asc&maxdiff=1" +"" +
                "&startday=" + startYYYYMMDD.split("/")[2] + "&startmonth=" + startYYYYMMDD.split("/")[1] + "&startyear=" + startYYYYMMDD.split("/")[0] +
                "&endday=" + endYYYYMMDD.split("/")[2] + "&endmonth=" + endYYYYMMDD.split("/")[1] + "&endyear=" + endYYYYMMDD.split("/")[0];
        Document doc = Jsoup.connect(searchUrl).get();
        int i = 0;
        Elements tables = doc.getElementsByTag("table");
        Element table = tables.get(1);

        Elements rows = table.getElementsByTag("a");
        List<String> links = new ArrayList<String>();
        for (Element e: rows) {
            String link = e.attr("href");
            if(link.startsWith("https://in-the-sky.org/news.php?") && !links.contains(link)) {
                links.add(link);
                i++;
            }
        }
        System.out.println(links);
        System.out.println("count=" + i);
        return (ArrayList) links;
    }
}
