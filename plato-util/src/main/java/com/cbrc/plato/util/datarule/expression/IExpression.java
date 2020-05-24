package com.cbrc.plato.util.datarule.expression;

public interface IExpression {
    /**
     * 解析
     *
     * @param context
     */
    public void parse(Context context);
    /**
     * 执行方法
     *
     * @param
     */
    public String interpret();
}
