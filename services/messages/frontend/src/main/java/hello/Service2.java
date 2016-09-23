package hello;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@RestController
public class Service2 {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/")
    @ResponseBody
    String home() {

        return "Hello from frontend server!";
    }

    @RequestMapping("/list")
    @ResponseBody
    String standardResponse() {
        logger.info("List method called!!");
        String json = "";

        List<Item> items = new ArrayList<Item>();
        items.add(new Item("1","Angular", "https://angularjs.org/", "Angular description"));
        items.add(new Item("2","Spring", "https://spring.io/", "Spring description"));
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            json = objectMapper.writeValueAsString(items);
            logger.info(json);
        }
        catch (JsonProcessingException ex){
              logger.error(ex.getMessage());
        }

        return json;
    }

    public static void main(String[] args) {
        SpringApplication.run(Service2.class, args);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}