/**
 * @Auther: heyong
 * @Date: 2019/3/7 09:58
 * @Description:项目返回统一格式的响应
 */
package com.cbrc.plato.util.response;

public class PlatoBasicResult<T> {
    private PlatoResultCodeEnum code;
    private String message;
    private T data;

    public PlatoBasicResult(){

    }

    public PlatoResultCodeEnum getCode(){
        return code;
    }

    public void setCode(PlatoResultCodeEnum code){
        this.code = code;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
