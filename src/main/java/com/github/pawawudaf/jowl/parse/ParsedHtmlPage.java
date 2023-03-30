package com.github.pawawudaf.jowl.parse;

import org.jsoup.nodes.Element;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class ParsedHtmlPage {

    private String title;
    private Element body;
    private Set<String> links= new Set<String>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<String> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean add(String s) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends String> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }
    };

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

    public boolean isEmpty() {
        return (title == null || title.isEmpty())
            && (body == null || body.children().isEmpty())
            && (links == null || links.isEmpty());
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
