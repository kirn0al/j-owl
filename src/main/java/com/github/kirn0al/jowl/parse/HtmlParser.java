package com.github.kirn0al.jowl.parse;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class HtmlParser {

    // TODO: add Concurrency
    private static final Logger logger = LoggerFactory.getLogger(HtmlParser.class);
    private static final Pattern MEDIA_PATTERN = Pattern.compile("\\.(png|jpe?g|gif|bmp|webp|svgz?|pdf)$");
    private static final String CSS_QUERY = "a[href]";
    private static final int STOP_DEPTH = 1;

    private final UrlValidator urlValidator;

    public HtmlParser(UrlValidator urlValidator) {
        this.urlValidator = urlValidator;
    }

    public Map<String, ParsedHtmlPage> parse(Set<String> urls, Map<String, ParsedHtmlPage> pages, int depth, Set<String> visited) {
        if (depth < STOP_DEPTH || urls.isEmpty()) {
            return pages;
        }

        Set<String> newUrls = new HashSet<>();
        for (String url : urls) {
            if (!visited.contains(url)) {
                logger.info("Current URL: {}", url);
                visited.add(url);
                ParsedHtmlPage parsedHtmlPage = fetchHtml(url);

                if (parsedHtmlPage.getLinks().isEmpty()) {
                    pages.put(url, parsedHtmlPage);
                    continue;
                }

                newUrls.addAll(parsedHtmlPage.getLinks());
                pages.put(url, parsedHtmlPage);
            }
        }

        return parse(newUrls, pages, depth - 1, visited);
    }


    private ParsedHtmlPage fetchHtml(String url) {
        try {
            Document html = Jsoup.connect(url).get();

            ParsedHtmlPage parsedHtmlPage = new ParsedHtmlPage();
            parsedHtmlPage.setTitle(html.title());
            parsedHtmlPage.setBody(html.body());
            parsedHtmlPage.setLinks(parseLinks(html.select(CSS_QUERY)));
            return parsedHtmlPage;
        } catch (SocketTimeoutException e) {
            logger.error("A connection timed out while processing the following URL: {}", url);
        } catch (HttpStatusException e) {
            logger.error("A resource accessed by URL {} gave response with status code {}", url, e.getStatusCode());
        } catch (IOException e) {
            logger.error("IOException caused by error during fetching HTML from the following URL: {}", url);
        }
        return new ParsedHtmlPage();
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
