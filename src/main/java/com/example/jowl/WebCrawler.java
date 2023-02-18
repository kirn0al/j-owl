package com.example.jowl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class WebCrawler {

    private static final String LINK_VALIDATION_REGEX = "^(http://|https://)[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,}(:(\\d)+)?(/($|[a-zA-Z0-9\\-\\.\\?\\,\\'\\\\/+=&amp;%\\$#_\\*!]+))*$";

    private final Set<String> visitedUrls;
    private final Queue<String> urlsToVisit;

    public WebCrawler() {
        this.visitedUrls = new HashSet<>();
        this.urlsToVisit = new LinkedList<>();
    }
    public void crawl(String seedUrl) {
        urlsToVisit.add(seedUrl);
        while (!urlsToVisit.isEmpty()) {
            String url = urlsToVisit.remove();
            visitedUrls.add(url);
            String html = fetchHtml(url);
            List<String> links = parseLinks(html);
            for (String link : links) {
                if (!visitedUrls.contains(link)) {
                    urlsToVisit.add(link);
                }
            }
            storeResults(html);
        }
    }

    private String fetchHtml(String url) {
        try {
            Document html = Jsoup.connect(url).get();
            return html.toString();
        } catch (IOException e) {
            return "";
        }
    }

    private List<String> parseLinks(String html) {
        List<String> links = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements linkElements = doc.select("a[href]");

        for (Element linkElement : linkElements) {
            String link = linkElement.attr("href");
            if(isLinkValid(link)) {
                links.add(link);
            }
        }
        return links;
    }

    private boolean isLinkValid(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        return url.matches(LINK_VALIDATION_REGEX);
    }

    private void storeResults(String html) {
        try (PrintWriter out = new PrintWriter(new FileWriter("results.txt", true))) {
            out.println(html);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
