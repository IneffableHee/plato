package com.cbrc.plato.util.datarule.expression;

public class RuleExpression implements IExpression{
    // 上下文环境
    private final Context context;
    // 存储下一个表达式引用
    private IExpression expression;

    public RuleExpression(String rule,String time,String bank) {
        this.context = new Context(rule,time);
        this.context.setBank(bank);
        this.parse(this.context);
    }

    public RuleExpression(String rule,String time) {
        this.context = new Context(rule,time);
        this.parse(this.context);
    }

    @Override
    public void parse(Context context) {
        // 获取第一个命令节点
        this.context.next();
    }

    @Override
    public String interpret() {
        this.expression = new ListExpression();
        this.expression.parse(this.context);
        // ListExpression表达式开始解析
        return this.expression.interpret();
    }
}
