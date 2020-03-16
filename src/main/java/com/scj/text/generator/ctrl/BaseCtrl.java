package com.scj.text.generator.ctrl;

import com.scj.text.generator.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description
 * @Author LaoQin
 * @Date 2020/03/15 23:03
 **/
@RestController
public class BaseCtrl {
    @Autowired
    BaseService baseService;

    @PostMapping(value = "text")
    public String test(String text,String color,HttpServletResponse response) throws Exception {
        baseService.test(text,color);
        return null;
    }

}
