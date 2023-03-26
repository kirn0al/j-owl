package com.github.pawawudaf.jowl.parse;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class WebsiteParser {

    private static final Pattern MEDIA_PATTERN = Pattern.compile("\\.(png|jpe?g|gif|bmp|webp|svgz?|pdf)$");
    private static final Logger LOGGER = LoggerFactory.getLogger(WebsiteParser.class);
    private static final String CSS_QUERY = "a[href]";

    private final UrlValidator urlValidator;

    public WebsiteParser(UrlValidator urlValidator) {
        this.urlValidator = urlValidator;
    }

    public Map<String, ParsedHtmlPage> parse(String seedUrl, Map<String, ParsedHtmlPage> pages, int depth) {
        ParsedHtmlPage parsedHtmlPage = fetchHtml(seedUrl);

        if (!parsedHtmlPage.isEmpty()) {
            pages.put(seedUrl, parsedHtmlPage);
        }

        if (depth > 0) {
            for (String link : parsedHtmlPage.getLinks()) {
                if (!pages.containsKey(link)) {
                    pages = parse(link, pages, depth - 1);
                }
            }
        }
        return pages;
    }

    private ParsedHtmlPage fetchHtml(String url) {
        try {
            Document html = Jsoup.connect(url).get();

            ParsedHtmlPage parsedHtmlPage = new ParsedHtmlPage();
            parsedHtmlPage.setTitle(html.title());
            parsedHtmlPage.setBody(html.body());
            parsedHtmlPage.setLinks(parseLinks(html.select(CSS_QUERY)));
            return parsedHtmlPage;
        } catch (Exception e) {
            LOGGER.error("Error fetching HTML from URL: " + url);
            return new ParsedHtmlPage();
        }
    }

    private Set<String> parseLinks(Elements links) {
        return links.stream()
            .map(linkElement -> linkElement.attr("href"))
            .filter(this::isLinkValid)
            .collect(Collectors.toSet());
    }

    private boolean isLinkValid(String url) {
        return !MEDIA_PATTERN.matcher(url.toLowerCase()).find() && urlValidator.isValid(url);
    }
}
