package com.cbrc.plato.util.datarule.expression;

import com.cbrc.plato.util.datarule.expression.opration.OprationFactory;

/**
 * 命令表达式
 *
 * @author
 *
 */
public class CommandExperssion implements IExpression {
    private final Context context;
    private IExpression expression;
    /**
     * 构造方法将待解析的context传入
     *
     * @param context
     */
    public CommandExperssion(Context context) {
        this.context = context;
        this.parse(this.context);
    }
    @Override
    public void parse(Context context) {
//        System.out.println("NNNN "+this.context.getCurrentToken());
        // 判断当前命令类别 在此只对For和最原始命令进行区分
        if(this.context.isNumeric()){
            expression = new NumericExpression(this.context);
        }else if(this.context.isOpration()){
            expression = OprationFactory.createOpration(this.context.getCurrentToken(),this.context);
            if(expression == null){
                System.out.println("指令无法解析："+this.context.getCurrentToken());
            }
        }else if(this.context.isRuleSymbol()){
            expression = new SymbolExpression(this.context);
        }else{
            expression = new SingleCellExpression(this.context);
        }
    }
    /**
     * 解析内容
     */
    @Override
    public String interpret() {
        // 解析内容
        if(expression == null){
            System.out.println("指令无法解析。");
            return null;
        }else{
            return this.expression.interpret();
        }
    }
}