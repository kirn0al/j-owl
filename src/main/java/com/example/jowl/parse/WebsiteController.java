package com.example.jowl.parse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebsiteController {

    private final WebsiteParser websiteParser;

    public WebsiteController(WebsiteParser websiteParser) {
        this.websiteParser = websiteParser;
    }

    @GetMapping(path = "/parse")
    public void parse(@RequestBody LinkCommand linkCommand) {
        websiteParser.parse(linkCommand.getLink());
    }
}
