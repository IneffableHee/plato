/**
 * @Auther: heyong
 * @Date: 2019/3/7 09:59
 * @Description:响应代码
 */
package com.cbrc.plato.util.response;

public enum PlatoResultCodeEnum {
    SUCCESS("SUCCESS"),//成功
    FAIL("FAIL"),//失败
    UNAUTHORIZED("UNAUTHORIZED"),//未授权（无权限）
    UNAUTHENTICATED("UNAUTHENTICATED"),//未登录
    NOT_FOUND("NOT_FOUND"),//接口不存在
    SYS_EX("SYS_EX"),//服务器内部错误
    KICKOUT("KICKOUT");//被踢出

    private String code;

    PlatoResultCodeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

}
