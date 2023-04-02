package com.github.pawawudaf.jowl.parse;

import org.jsoup.nodes.Element;

import java.util.Objects;
import java.util.Set;

public class ParsedHtmlPage {

    private String title;
    private Element body;
    private Set<String> links;

    public Set<String> getLinks() {
        return links;
    }

    public void setLinks(Set<String> links) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParsedHtmlPage that = (ParsedHtmlPage) o;
        return Objects.equals(title, that.title) && Objects.equals(body, that.body) && Objects.equals(links, that.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, body, links);
    }

    @Override
    public String toString() {
        return "ParsedHtmlPage{" +
            "title='" + title + '\'' +
            ", body=" + body +
            ", links=" + links +
            '}';
    }
}
