package com.github.pawawudaf.jowl.index;

import com.github.pawawudaf.jowl.parse.ParsedHtmlPage;
import com.github.pawawudaf.jowl.parse.WebsiteParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
    private static final StopWatch stopWatch = new StopWatch();

    private final WebsiteParser websiteParser;
    private final IndexService indexService;

    @Autowired
    public IndexController(WebsiteParser websiteParser, IndexService indexService) {
        this.websiteParser = websiteParser;
        this.indexService = indexService;
    }

    @GetMapping("/index/{depth}")
    @ResponseStatus(HttpStatus.CREATED)
    public void index(@PathVariable int depth, @RequestBody IndexWriteCommand indexWriteCommand) {
        if (!stopWatch.isRunning()) stopWatch.start("Indexing");
        logger.info("Indexing process started... Seed URL: {}. Depth parameter: {}", indexWriteCommand.getLink(), depth);
        Set<String> seedUrl = Collections.singleton(indexWriteCommand.getLink());
        Map<String, ParsedHtmlPage> parsedPages = websiteParser.parse(seedUrl, new HashMap<>(), depth, new HashSet<>());
        indexService.indexDocuments(parsedPages);
        stopWatch.stop();
        logger.info("The indexing process is done. Elapsed time: {} sec.", stopWatch.getLastTaskInfo().getTimeSeconds());
    }

    @GetMapping("/show")
    @ResponseStatus(HttpStatus.OK)
    public List<IndexDto> showIndex() {
        return indexService.getAllIndexedDocuments();
    }

    public static final class IndexingErrorException extends RuntimeException {

        public IndexingErrorException(String message, Exception exception) {
            super(message, exception);
        }
    }
}
