package com.cbrc.plato.core.basic.service.impl;

import com.cbrc.plato.core.basic.service.IBasicTestServicce;
import com.cbrc.plato.util.time.DateUtils;
import org.springframework.stereotype.Service;

@Service
public class BasicTestService implements IBasicTestServicce {
    public String test(){
        return "Hello ! This is plato basic core!"+ DateUtils.getDateTime("");
//        return "Hello Plato Core!";
    }
}
