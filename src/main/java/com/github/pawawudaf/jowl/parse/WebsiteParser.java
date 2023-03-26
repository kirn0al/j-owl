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
import java.util.regex.Pattern;

@Component
public class WebsiteParser {

    private static final UrlValidator urlValidator = new UrlValidator();
    private static final Pattern MEDIA_PATTERN = Pattern.compile("\\.(png|jpe?g|gif|bmp|webp|svgz?|pdf)$");
    private static final Logger logger = LoggerFactory.getLogger(WebsiteParser.class);

    public Map<String, HtmlPage> parse(String seedUrl, Map<String, HtmlPage> dataMap, int maxDepth) {
        HtmlPage htmlPage = fetchHtml(seedUrl);
        if (!htmlPage.isEmpty()) {
            dataMap.put(seedUrl, htmlPage);
        }
        if (maxDepth > 0) {
            int linksProcessed = 0;
            for (String link : parseLinks(htmlPage.getLinks())) {
                if (!dataMap.containsKey(link)) {
                    dataMap = parse(link, dataMap, maxDepth - 1);
                    linksProcessed++;
                }
            }
            logger.info("Processed " + linksProcessed + " links for URL: " + seedUrl);
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
            logger.error("Error fetching HTML from URL: " + url);
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
        return !MEDIA_PATTERN.matcher(url.toLowerCase()).find() && urlValidator.isValid(url);
    }
}
