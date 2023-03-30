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

    private static final Logger logger = LoggerFactory.getLogger(WebsiteParser.class);
    private static final Pattern MEDIA_PATTERN = Pattern.compile("\\.(png|jpe?g|gif|bmp|webp|svgz?|pdf)$");
    private static final Logger LOGGER = LoggerFactory.getLogger(WebsiteParser.class);
    private static final String CSS_QUERY = "a[href]";
    private static final int MIN_DEPTH = 1;

    private final UrlValidator urlValidator;

    public WebsiteParser(UrlValidator urlValidator) {
        this.urlValidator = urlValidator;
    }

    public Map<String, ParsedHtmlPage> parse(Set<String> urls, Map<String, ParsedHtmlPage> pages, int depth) {
        if (depth < MIN_DEPTH) {
            return pages;
        }
        for (String url : urls) {
            ParsedHtmlPage parsedHtmlPage = fetchHtml(url);
            if (parsedHtmlPage.getTitle() == null) {
                continue;
            }
            pages.putIfAbsent(url, parsedHtmlPage);
            pages.putAll(parse(parsedHtmlPage.getLinks(), pages, depth - 1));
        }
        return pages;   // TODO: handle case when urls = 0
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
