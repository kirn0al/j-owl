package com.github.pawawudaf.jowl.index;

import com.github.pawawudaf.jowl.parse.HtmlPage;
import com.github.pawawudaf.jowl.parse.WebsiteParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    private final WebsiteParser websiteParser;
    private final IndexService indexService;

    @Autowired
    public IndexController(WebsiteParser websiteParser, IndexService indexService) {
        this.websiteParser = websiteParser;
        this.indexService = indexService;
    }

    @GetMapping("/index")
    @ResponseStatus(HttpStatus.CREATED)
    public void index(@RequestBody IndexCommand indexCommand) {
        logger.info("Indexing process started...");
        try {
            indexService.indexDocuments(websiteParser.parse(indexCommand.getLink(), new HashMap<String, HtmlPage>()));
        } catch (Exception e) {
            logger.error("An error occurred during the indexing process: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to index documents", e);
        }
        logger.info("The indexing process successfully ended");
    }

    @GetMapping("/show")
    @ResponseStatus(HttpStatus.OK)
    public String showIndex() {
        return indexService.getStringOfIndexedDocuments();
    }
}
