package cf.indoor.test;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

public class hello {

    @RequestMapping("Hellow")
    public String HellowWord()
    {
        return "HelloWorld";
    }
}
