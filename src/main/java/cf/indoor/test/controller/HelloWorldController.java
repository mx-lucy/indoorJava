package cf.indoor.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloWorldController {

    @RequestMapping("Hellow")
    public String HellowWord()
    {
        return "HelloWorld";
    }
}


