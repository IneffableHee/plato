package com.cbrc.plato.core.oozf.service.impl;

import com.cbrc.plato.core.oozf.service.ITestService;
import com.cbrc.plato.util.time.DateUtils;
import org.springframework.stereotype.Service;

@Service
public class TestService implements ITestService {
    public String test(){
        return "Hello ! This is plato 1104 core!"+ DateUtils.getDateTime("");
//        return "Hello Plato Core!";
    }
}
