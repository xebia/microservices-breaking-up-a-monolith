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
          "<html>\n" +
          "\n" +
          "<head>\n" +
          "<title>Vertical microservices app</title>\n" +
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
    return "<p>" +
      "I am the shop manager, my includes:" +
      "</p>"+
      "<div style=\"color:#0000FF\">" +
        "<esi:include src=\"/user\"/>" +
      "</div style=\"color:#FAEBD7\">" +
        "<esi:include src=\"/shop/index.html\"/>" +
      "</div>";
  }
}
