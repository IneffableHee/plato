/**
 * @Auther: heyong
 * @Date: 2019/3/7 10:13
 * @Description:项目返回统一格式的响应
 */
package com.cbrc.plato.util.response;

public class PlatoResponseGenerator {
    private static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";

    //成功
    public static PlatoResponse successResponse() {
        PlatoResponse response = new PlatoResponse();
        response.setCode(PlatoResponseCodeEnum.SUCCESS);
        response.setMessage(DEFAULT_SUCCESS_MESSAGE);
        return response;
    }

    //返回成功数据
    public static <T> PlatoResponse<T> successResponse(T data) {
        PlatoResponse response = new PlatoResponse();
        response.setCode(PlatoResponseCodeEnum.SUCCESS);
        response.setMessage(DEFAULT_SUCCESS_MESSAGE);
        response.setData(data);
        System.out.println("successResponse:"+data);
        return response;
    }

    //返回失败信息
    public static PlatoResponse failResponse(String message) {
        PlatoResponse response = new PlatoResponse();
        response.setCode(PlatoResponseCodeEnum.FAIL);
        response.setMessage(message);
        return response;
    }

    //未授权
    public static PlatoResponse unauthorizedResponse() {
        PlatoResponse response = new PlatoResponse();
        response.setCode(PlatoResponseCodeEnum.UNAUTHORIZED);
        response.setMessage("权限不足！");
        return response;
    }

    //404
    public static PlatoResponse notfoundResponse(){
        PlatoResponse response = new PlatoResponse();
        response.setCode(PlatoResponseCodeEnum.NOT_FOUND);
        response.setMessage("404");
        return response;
    }

    //系统错误
    public static PlatoResponse sysexResponse(){
        PlatoResponse response = new PlatoResponse();
        response.setCode(PlatoResponseCodeEnum.SYS_EX);
        response.setMessage("服务器内部错误！");
        return response;
    }

    //返回系统错误信息
    public static PlatoResponse sysexResponse(Exception e){
        PlatoResponse response = new PlatoResponse();
        response.setCode(PlatoResponseCodeEnum.SYS_EX);
        response.setMessage(e.toString());
        return response;
    }
}
