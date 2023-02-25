package com.github.pawawudaf.jowl.parse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class WebsiteController {

    private static final int INITIAL_DEPTH = 1;
    private final WebsiteParser websiteParser;

    public WebsiteController(WebsiteParser websiteParser) {
        this.websiteParser = websiteParser;
    }

    @GetMapping(path = "/parse")
    public void parse(@RequestBody LinkCommand linkCommand) {
        websiteParser.parse(linkCommand.getLink(), new HashMap<String, String>(), INITIAL_DEPTH);
    }
}
