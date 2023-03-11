package com.github.pawawudaf.jowl.index;

import com.github.pawawudaf.jowl.parse.ParsedData;
import com.github.pawawudaf.jowl.parse.WebsiteParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class IndexController {

    private static final int INITIAL_DEPTH = 1;

    private final WebsiteParser websiteParser;
    private final IndexService indexService;

    public IndexController(WebsiteParser websiteParser, IndexService indexService) {
        this.websiteParser = websiteParser;
        this.indexService = indexService;
    }

    // TODO: change http method & path
    // TODO: add @RequestParam() with depth, add @ResponseStatus()
    @GetMapping("/index/")
    public void index(@RequestBody IndexCommand indexCommand) {
        // TODO: add logging with try-catch

        ParsedData data = websiteParser.parse(indexCommand.getLink(), new ParsedData(), INITIAL_DEPTH);
//        ParsedData data = websiteParser.parse(indexCommand.getLink(), new HashMap<>(), initialDepth);

        indexService.indexDocuments(data);

        // TODO: add logging with try-catch
    }

    // TODO: change path, rename method name
    @GetMapping("/indexed")
    public String getIndexed() {
        return indexService.getStringOfIndexedDocuments();
    }
}
