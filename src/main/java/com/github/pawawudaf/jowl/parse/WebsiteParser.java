package com.github.pawawudaf.jowl.parse;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class WebsiteParser {

    private static final UrlValidator urlValidator = new UrlValidator();
    private static final Logger logger = LoggerFactory.getLogger(WebsiteParser.class);

    public Map<String, HtmlPage> parse(String seedUrl, Map<String, HtmlPage> dataMap, int maxDepth) {
        HtmlPage htmlPage = fetchHtml(seedUrl);
        dataMap.put(seedUrl, htmlPage);
        if (maxDepth > 0) {
            for (String link : parseLinks(htmlPage.getLinks())) {
                if (!dataMap.containsKey(link)) {
                    parse(link, dataMap, maxDepth - 1);
                }
            }
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
            logger.error("Error fetching HTML from URL: " + url, e);
            return new HtmlPage();
        }
    }

    private List<String> parseLinks(Elements links) {
        return links.stream()
            .map(linkElement -> linkElement.attr("href"))
            .filter(this::isLinkValid)
            .toList();
    }

    private boolean isLinkValid(String url) {
        return urlValidator.isValid(url);
    }
}
