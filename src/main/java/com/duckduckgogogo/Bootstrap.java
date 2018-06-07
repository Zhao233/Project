package com.duckduckgogogo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Bootstrap {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
    

    public static void main(String[] args) {
    	
    	
    	
        SpringApplication.run(Bootstrap.class, args);

        logger.info("HELLOWORLD Started.");
        
         
        /*
        RestTemplate restTemplate = new RestTemplate();
        
        Project project =new Project();
        
        project.setSourceFileUrl("111");
        
        URI uri = restTemplate.postForLocation("http://127.0.0.1:8080/api/project", project, Project.class);  
        logger.info("666666666");
//        System.out.println("Location : "+uri.toASCIIString());
        logger.info("666666666");
        */
       
    }
}
