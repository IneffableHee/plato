package com.cbrc.plato.api.admin.controller;

import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/common")
@RestController
@Slf4j
public class CommonController {

    /**
     * 未授权跳转方法
     * @return
     */
    @RequestMapping("/unauth")
    public PlatoBasicResult unauth(){
        SecurityUtils.getSubject().logout();
        log.info("unauth");
        return PlatoResult.unauthenticatedResult();
    }

    /**
     * 被踢出后跳转方法
     * @return
     */
    @RequestMapping("/kickout")
    public PlatoBasicResult kickout(){
        log.info("kickout");
        return PlatoResult.kickoutResult();
    }

}
