package com.xebia.shopmanager;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
@RequestMapping("/index.html")
public class IndexResource {

    @RequestMapping(method = RequestMethod.GET, produces = "text/html")
    public String getIndex() {
        return "<!DOCTYPE html\n" +
          "PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" +
          "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
          "<!--\n" +
          "This is a skeleton html file that you can use to get you started on each new\n" +
          "HTML project\n" +
          "\n" +
          "Name: Your Name Here\n" +
          "Class: CIS 3303\n" +
          "Section: x\n" +
          "-->\n" +
          "<html>\n" +
          "\n" +
          "<head>\n" +
          "<title>My Title</title>\n" +
          "</head>\n" +
          "\n" +
          "<body>\n" +
          "\n" +
          getBody() +
          "\n" +
          "</body>\n" +
          "</html>";
    }

  private String getBody() {
    return "I am the shop manager";
  }
}
