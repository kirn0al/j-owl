package com.github.pawawudaf.jowl.parse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class WebsiteParser {

    private static final String LINK_VALIDATION_REGEX = "^(http://|https://)";
    private static final int MAX_CRAWLING_DEPTH = 2; // TODO: get rid of constant

    public ParsedData parse(String seedUrl, ParsedData parsedData, int currentDepth) {
        // TODO: change condition
        if (currentDepth > MAX_CRAWLING_DEPTH) {
            return parsedData;
        }

        // TODO: fetchHtml
        String html = fetchHtml(seedUrl);

        // TODO: work with HtmlPage

        // TODO: -----
        parsedData.putObject(seedUrl, html);
        List<String> parsedLinks = parseLinks(html);

        parsedLinks.stream()
            .filter(link -> !parsedData.isUrlContained(link))
            .forEach(link -> {
                parse(link, parsedData, currentDepth + 1);
            });
        // TODO: -----

        // map.put(link, htmlPage);

        return parsedData;
    }

    private String fetchHtml(String url) {
        try {
            Document html = Jsoup.connect(url).get();

            // new HtmlPage();
//            html.body();
//            html.title();
//            list of links

            return html.toString();
        } catch (IOException e) {
            throw new FetchHtmlException("Error fetching HTML from URL: " + url, e);
        }
    }

    private List<String> parseLinks(String html) {
        Document doc = Jsoup.parse(html);
        Elements linkElements = doc.select("a[href]");

        return linkElements.stream()
            .map(linkElement -> linkElement.attr("href"))
            .filter(this::isLinkValid)
            .toList();
    }

    private boolean isLinkValid(String url) {
        return url.contains(LINK_VALIDATION_REGEX);
    }

    private static final class FetchHtmlException extends RuntimeException {

        public FetchHtmlException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
