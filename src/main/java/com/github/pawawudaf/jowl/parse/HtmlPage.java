package com.github.pawawudaf.jowl.parse;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlPage {

    private String title;
    private Element body;
    private Elements links;

    public Elements getLinks() {
        return links;
    }

    public void setLinks(Elements links) {
        this.links = links;
    }

    public Element getBody() {
        return body;
    }

    public void setBody(Element body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
