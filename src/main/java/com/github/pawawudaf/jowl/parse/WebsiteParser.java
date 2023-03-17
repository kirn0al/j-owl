package com.github.pawawudaf.jowl.parse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class WebsiteParser {

    private static final String LINK_VALIDATION_REGEX = "^(http://|https://)";

    public Map<String, HtmlPage> parse(String seedUrl, Map<String, HtmlPage> dataMap) {
        HtmlPage htmlPage = fetchHtml(seedUrl);
        for (String link : parseLinks(htmlPage.getLinks())) {
            if (dataMap.containsKey(link)) {
                continue;
            }
            dataMap.put(link, htmlPage);
            parse(link, dataMap);
        }
        return dataMap;
    }

    private HtmlPage fetchHtml(String url) {
        try {
            Document html = Jsoup.connect(url).get();
            HtmlPage htmlPage = new HtmlPage();
            htmlPage.setTitle(html.title());
            htmlPage.setBody(html.body());
            htmlPage.setLinks(html.select("a[href]"));

            return htmlPage;
        } catch (IOException e) {
            throw new FetchHtmlException("Error fetching HTML from URL: " + url, e);
        }
    }

    private List<String> parseLinks(Elements links) {
        return links.stream()
            .map(linkElement -> linkElement.attr("href"))
            .filter(this::isLinkValid)
            .toList();
    }

    private boolean isLinkValid(String url) {
        return url.contains(LINK_VALIDATION_REGEX);
    }

    private static final class FetchHtmlException extends RuntimeException {

        public FetchHtmlException(String message, Exception exception) {
            super(message, exception);
        }
    }
}
