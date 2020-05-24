/**
 * @Auther: heyong
 * @Date: 2019/3/7 09:59
 * @Description:响应代码
 */
package com.cbrc.plato.util.response;

public enum PlatoResponseCodeEnum {
    SUCCESS("SUCCESS"),//成功
    FAIL("FAIL"),//失败
    UNAUTHORIZED("U_AUTH"),//未认证（签名错误）
    NOT_FOUND("NOT_FOUND"),//接口不存在
    SYS_EX("SYS_EX");//服务器内部错误

    private String code;

    PlatoResponseCodeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

}
