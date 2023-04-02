package com.github.pawawudaf.jowl.index;

import org.apache.lucene.search.TopDocs;

public class IndexDto {

    private String title;
    private String link;
    private TopDocs searchResults; // TODO: set body instead topDocs

    public IndexDto(String title, String link, TopDocs searchResults) {
        this.title = title;
        this.link = link;
        this.searchResults = searchResults;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public TopDocs getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(TopDocs searchResults) {
        this.searchResults = searchResults;
    }

}
