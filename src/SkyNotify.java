import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SkyNotify {
    public static void main(String[] args) throws IOException{
        double latitude = 0;
        double longitude = 0;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String now = dtf.format(LocalDateTime.now());

        String yearString = now.split("/")[0];
        String monthString = now.split("/")[1];
        String dayString = now.split("/")[2];
        monthString = (monthString.length() < 2) ? "0" + monthString : monthString;
        dayString = (dayString.length() < 2) ? "0" + dayString : dayString;

        List<String> strings = null;
        for(String link : getLinks(latitude, longitude)) {
            if(link.contains("id=" + yearString + monthString + dayString)) {
                strings = (getNewsInfo(link));
                for(String str : strings) {
                    System.out.println(str);
                }
            }
        }

        String sender = "skynotify.mail@gmail.com";
        final String username = "skynotify.mail@gmail.com";
        final String password = "HelloWorld!2018";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        BufferedReader reader = new BufferedReader(new FileReader("emails.txt"));
        ArrayList<String> lines = new ArrayList<String>();
        for(Object line : reader.lines().toArray()) {
            lines.add((String) line);
        }
        for(String line : lines) {
            if(!line.isEmpty()) {
                String recipient = line;
                try
                {
                    if(strings != null && strings.size() > 2) {
                        MimeMessage message = new MimeMessage(session);
                        message.setFrom(new InternetAddress(sender));
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

                        message.setSubject(strings.get(0));

                        message.setText(strings.get(0) + "\n" + strings.get(1) + "\n" + strings.get(2));
                        Transport.send(message);
                        System.out.println("Mail successfully sent to " + recipient);
                    }
                }
                catch (MessagingException mex)
                {
                    mex.printStackTrace();
                }
            }
        }
    }

    public static ArrayList<String> getLinks(double lat, double lng) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String now = dtf.format(LocalDateTime.now());
        int currentYear = Integer.parseInt(now.split("/")[0]);
        int currentMonth = Integer.parseInt(now.split("/")[1]);
        int currentDay = Integer.parseInt(now.split("/")[2]);

        String end = "" + (currentYear + 1) + "/" + currentMonth + "/" + currentDay;
        return getLinks(lat, lng, now , end);
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

    public static ArrayList<String> getNewsInfo(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        ArrayList<String> out = new ArrayList<String>();  // title, date, time
        Element widetitle = doc.getElementsByClass("widetitle").first();
        out.add(widetitle.text());

        Element dateAndTime = doc.getElementsByClass("link_green").first();
        out.add(dateAndTime.text());

        Element newsbody = doc.getElementsByClass("newsbody").first();
        out.add(newsbody.text());

        return out;
    }
}
