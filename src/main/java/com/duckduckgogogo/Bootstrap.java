package com.duckduckgogogo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@SpringBootApplication
public class Bootstrap {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);

        logger.info("HELLOWORLD Started.");
    }

    @RequestMapping(value = "/")
    public ModelAndView index() {
        Map<String, Object> model = new HashMap<String, Object>();

        Map<String, String> page = new HashMap<String, String>();
        page.put("username", "灵魂之王");

        model.put("page", page);

        return new ModelAndView("index", model);
    }

    @GetMapping(value = "/home")
    @ResponseBody
    public String home() {
        return "你好，世界。";
    }
}
