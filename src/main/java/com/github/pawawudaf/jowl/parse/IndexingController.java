package com.github.pawawudaf.jowl.parse;

import com.github.pawawudaf.jowl.index.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexingController {

    private static final int INITIAL_DEPTH = 1;

    @Autowired
    private WebsiteParser websiteParser;
    @Autowired
    private IndexService indexService;

    @GetMapping(path = "/index")
    public void index(@RequestBody LinkCommand linkCommand) {
        ParsedData data = websiteParser.parse(linkCommand.getLink(), new ParsedData(), INITIAL_DEPTH);
        indexService.indexDocuments(data);
    }

    @GetMapping(path = "/indexed")
    public String getIndexed() {
        return indexService.getStringOfIndexedDocuments();
    }
}
