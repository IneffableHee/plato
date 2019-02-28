package com.cbrc.plato.api.www.oozf;

import com.cbrc.plato.core.oozf.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/1104/test")
public class OozfTestController {

    @Autowired
    private ITestService testService;

    @RequestMapping("/hello")
    public String hello(){
        return testService.test();
    }


}
