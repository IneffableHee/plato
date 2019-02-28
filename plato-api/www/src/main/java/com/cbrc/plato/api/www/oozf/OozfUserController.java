package com.cbrc.plato.api.www.oozf;

import com.cbrc.plato.core.oozf.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/1104/user")
public class OozfUserController {

    @Autowired
    IUserService oozfUserService;

    @RequestMapping("/hello/{id}")
    public String sayHello(@PathVariable("id") Integer id){
        return oozfUserService.sayHello(id);
    }
}
