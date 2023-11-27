package com.roysumit.openaiintegration;

import org.springframework.ai.client.AiClient;
import org.springframework.ai.client.AiResponse;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.prompt.Prompt;
import org.springframework.ai.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/songs")
public class SongsController {
    
    private final AiClient aiClient;

    public SongsController(AiClient aiClient){
        this.aiClient = aiClient;
    }

    @GetMapping("/topsong/{year}")
    public TopSong topSong(@PathVariable("year") int year){
        BeanOutputParser<TopSong> parser = new BeanOutputParser<>(TopSong.class);

        String promptString = """
                What was the Billboard number one year-end top 100 single for {year}? {format}
                """;
        PromptTemplate template = new PromptTemplate(promptString);
        template.add("year", year);
        template.add("format", parser.getFormat());
        template.setOutputParser(parser);
        Prompt prompt = template.create();
        AiResponse aiResponse = aiClient.generate(prompt);
        String text = aiResponse.getGeneration().getText();
        return parser.parse(text);
    }
}
