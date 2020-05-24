/**
 * @Auther: heyong
 * @Date: 2019/3/7 10:13
 * @Description:项目返回统一格式的响应
 */
package com.cbrc.plato.util.response;

public class PlatoResult {
    //成功
    public static PlatoBasicResult successResult() {
        PlatoBasicResult response = new PlatoBasicResult();
        response.setCode(PlatoResultCodeEnum.SUCCESS);
        response.setMessage(PlatoResultCodeEnum.SUCCESS.toString());
        return response;
    }

    //返回成功数据
    public static <T> PlatoBasicResult<T> successResult(T data) {
        PlatoBasicResult response = new PlatoBasicResult();
        response.setCode(PlatoResultCodeEnum.SUCCESS);
        response.setMessage(PlatoResultCodeEnum.SUCCESS.toString());
        response.setData(data);
        return response;
    }

    //返回失败信息
    public static PlatoBasicResult failResult(String message) {
        PlatoBasicResult response = new PlatoBasicResult();
        response.setCode(PlatoResultCodeEnum.FAIL);
        response.setMessage(message);
        return response;
    }

    //未授权
    public static PlatoBasicResult unauthorizedResult() {
        PlatoBasicResult response = new PlatoBasicResult();
        response.setCode(PlatoResultCodeEnum.UNAUTHORIZED);
        response.setMessage("您的权限不足！");
        return response;
    }

    //未登录
    public static PlatoBasicResult unauthenticatedResult(){
        PlatoBasicResult result = new PlatoBasicResult();
        result.setCode(PlatoResultCodeEnum.UNAUTHENTICATED);
        result.setMessage("未登录！");
        return result;
    }

    //被踢出
    public static PlatoBasicResult kickoutResult(){
        PlatoBasicResult result = new PlatoBasicResult();
        result.setCode(PlatoResultCodeEnum.KICKOUT);
        result.setMessage("您已经在其他地方登录，请重新登录！");
        return result;
    }

    //404
    public static PlatoBasicResult notfoundResult(){
        PlatoBasicResult response = new PlatoBasicResult();
        response.setCode(PlatoResultCodeEnum.NOT_FOUND);
        response.setMessage("404");
        return response;
    }

    //系统错误
    public static PlatoBasicResult sysexResult(){
        PlatoBasicResult response = new PlatoBasicResult();
        response.setCode(PlatoResultCodeEnum.SYS_EX);
        response.setMessage("服务器内部错误！");
        return response;
    }

    //返回系统错误信息
    public static PlatoBasicResult sysexResult(Exception e){
        PlatoBasicResult response = new PlatoBasicResult();
        response.setCode(PlatoResultCodeEnum.SYS_EX);
        response.setMessage(e.toString());
        return response;
    }
}
