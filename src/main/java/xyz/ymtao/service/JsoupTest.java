package main.java.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @Author: ymt
 * @Description:
 * @CreateDate: 2020/12/21  10:28
 */
public class JsoupTest {
    public static void main(String[] args) {
        String html =  "<html><head><title>First parse</title></head>"
                + "<body><p>Parsed HTML into a doc.</p></body></html>";
        Document doc = Jsoup.parse(html);
        System.out.println(doc.body().text());
    }
}
