package com.cbrc.plato.core.business.service.impl;

import com.cbrc.plato.core.business.service.IBusinessTestService;
import com.cbrc.plato.util.time.DateUtils;
import org.springframework.stereotype.Service;

@Service
public class BusinessTestService implements IBusinessTestService {
    public String test(){
        return "Hello ! This is plato business core!"+ DateUtils.getDateTime("");
//        return "Hello Plato Core!";
    }
}
