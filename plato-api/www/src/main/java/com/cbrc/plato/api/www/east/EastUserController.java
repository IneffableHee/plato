package com.cbrc.plato.api.www.east;

import com.cbrc.plato.core.east.service.IEastUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/east/user")
public class EastUserController {

    @Autowired
    IEastUserService eastUserService;

    @RequestMapping("/hello/{id}")
    public String sayHello(@PathVariable("id") Integer id){
        return eastUserService.sayHello(id);
    }
}
