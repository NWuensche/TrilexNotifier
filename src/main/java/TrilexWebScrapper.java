/**
 * @author NWuensche
 */

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TrilexWebScrapper {

    public static void main(String[] args) {
        String nextTuesday = findNext(DayOfWeek.TUESDAY);
        Document doc = null;
        try {
            doc = Jsoup.connect(Secrets.url + nextTuesday).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!doc.outerHtml().contains(Secrets.timeStart) || !doc.outerHtml().contains(Secrets.timeEnd)) {
            sendInstapushMessage("Tuesday");
        }
    }

    /**
     * @return Could also be today, format dd.MM.yy
     */
    private static String findNext(DayOfWeek dayOfWeek) {
        LocalDate d = LocalDate.now();
        while(d.getDayOfWeek() != dayOfWeek) {
            d = d.plusDays(1);
        }
        return d.format(DateTimeFormatter.ofPattern("dd.MM.yy")).replace("-",".");
    }

    private static void sendInstapushMessage(String whichDay) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.instapush.im/v1/post");

        httpPost.addHeader("x-instapush-appid", Secrets.appID);
        httpPost.addHeader("x-instapush-appsecret", Secrets.appSecret);
        httpPost.addHeader("Content-Type", "application/json");

        String content = "{\"event\":\"Trilex\",\"trackers\":{\"dayOfWeek\":\"" + whichDay + " \"}}";
        BasicHttpEntity b = new BasicHttpEntity();
        b.setContent(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
        httpPost.setEntity(b);

        try {
            CloseableHttpResponse res1 = httpclient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
