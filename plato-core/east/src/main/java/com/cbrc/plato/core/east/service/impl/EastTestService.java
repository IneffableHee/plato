package com.cbrc.plato.core.east.service.impl;

import com.cbrc.plato.core.east.service.IEastTestService;
import com.cbrc.plato.util.time.DateUtils;
import org.springframework.stereotype.Service;

@Service
public class EastTestService implements IEastTestService {
    public String test(){
        return "Hello ! This is plato east core!"+ DateUtils.getDateTime("");
//        return "Hello Plato Core!";
    }
}
