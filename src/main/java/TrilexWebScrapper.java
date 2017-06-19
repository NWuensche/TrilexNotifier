/**
 * @author NWuensche
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
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


    public static void sendInstapushMessage(String whichDay) {
        ProcessBuilder pb = new ProcessBuilder("bash", createTempScript(whichDay).toString());
        try {
            Process p1 = pb.start();
            p1.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * @return Script which can be executed
     */
    public static File createTempScript(String whichDay) {
        String cmd = "curl -X POST  -H \"x-instapush-appid: " + Secrets.appID + "\" -H \"x-instapush-appsecret: " + Secrets.appSecret + "\" -H \"Content-Type: application/json\" -d '{\"event\":\"Trilex\",\"trackers\":{\"dayOfWeek\":\"" + whichDay +  "\"}}' https://api.instapush.im/v1/post";

        File tempScript = null;
        try {
            tempScript = File.createTempFile("script", null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Writer streamWriter = null;
        try {
            streamWriter = new OutputStreamWriter(new FileOutputStream(
                    tempScript));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        PrintWriter printWriter = new PrintWriter(streamWriter);

        printWriter.println("#!/bin/bash");
        printWriter.println(cmd);

        printWriter.close();

        return tempScript;
    }
}
