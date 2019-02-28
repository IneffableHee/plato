package com.cbrc.plato.api.www.east;

import com.cbrc.plato.core.east.service.IEastTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/east/test")
public class EastTestController {

    @Autowired
    private IEastTestService eastTestService;

    @RequestMapping("/hello")
    public String hello(){
        return eastTestService.test();
    }


}
