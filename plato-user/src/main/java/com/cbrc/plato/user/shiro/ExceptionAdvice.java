package com.cbrc.plato.user.shiro;

import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

@Slf4j
@ControllerAdvice
@ResponseBody
public class ExceptionAdvice {
    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class, BindException.class,
            ServletRequestBindingException.class, MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public PlatoBasicResult handleHttpMessageNotReadableException(Exception e) {
        log.info("参数解析失败");
        log.error("参数解析失败", e);
        if (e instanceof BindException){
            return  PlatoResult.failResult(((BindException)e).getAllErrors().get(0).getDefaultMessage()) ;
        }
        return PlatoResult.failResult(e.getMessage());
    }

    /**
     * 405 - Method Not Allowed
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public PlatoBasicResult handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.info("不支持当前请求方法");
        log.error("不支持当前请求方法", e);
        return  PlatoResult.failResult("不支持当前请求方法");
    }

    /**
     * shiro权限异常处理
     * @return
     */
    @ExceptionHandler(UnauthorizedException.class)
    public PlatoBasicResult unauthorizedException(UnauthorizedException e){
        log.info("UnauthorizedException");
        log.error(e.getMessage(), e);

        return PlatoResult.unauthorizedResult();
    }

    /**
     * shiro未登录
     * @return
     */
    @ExceptionHandler(UnauthenticatedException.class)
    public PlatoBasicResult unauthorizedException(UnauthenticatedException e){
        log.info("UnauthenticatedException");
        log.error(e.getMessage(), e);
        return PlatoResult.unauthenticatedResult();
    }

    /**
     * 500
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public PlatoBasicResult handleException(Exception e) {
        log.info("服务运行异常");
        e.printStackTrace();
        log.error("服务运行异常", e);
        return PlatoResult.sysexResult(e);
    }
}

