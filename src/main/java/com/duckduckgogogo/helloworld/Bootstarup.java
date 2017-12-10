package com.duckduckgogogo.helloworld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@SpringBootApplication
public class Bootstarup {
    private static final Logger logger = LoggerFactory.getLogger(Bootstarup.class);

    public static void main(String[] args) {
        SpringApplication.run(Bootstarup.class, args);

        logger.info("HELLOWORLD Started.");
    }

    // @RequestMapping(value = "/", method = RequestMethod.GET)
    @GetMapping(value = "/")
    public void homePage(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("DEBUG logger test.");

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter o = response.getWriter()) {
            o.write("<p>I am the king of the world.</p>");
            o.write("<p>我是世界之王。</p>");

            o.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
