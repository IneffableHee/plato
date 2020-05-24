package com.cbrc.plato.util.datarule.expression.opration;

import com.cbrc.plato.util.datarule.expression.Context;
import com.cbrc.plato.util.datarule.expression.IExpression;
import com.cbrc.plato.util.datarule.expression.ListExpression;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OprationExpression implements IExpression {
    public final Context context;
    public String oprationContent = "";
    public String nowTime;
    public String lastTime;

    public OprationExpression(Context context){
        this.context = context;
        this.parse(this.context);
    }

    @Override
    public void parse(Context context) {
        this.context.next();
        int tip = 0;
        while (true) {
            if(this.context.getCurrentToken() == null){
                //缺少函数终结符
                log.info("\033[31;4m" + "ERROR：缺少函数终结符！"+ "\033[0m");
                this.oprationContent = null;
                break;
            }
            // 判断函数开始
            if (this.context.equalsWithCommand("<")) {
                if(tip == 0){
                    tip++;
                }else{
                    //前一个函数没有结束，又开始下一个函数
                    log.info("\033[31;4m" + "ERROR：函数缺少终结符！"+ "\033[0m");
                    this.oprationContent = null;
                    break;
                }
                // 开始索引内容
                this.context.next();
            } else if (this.context.equalsWithCommand(">")) {
                tip = 0;
                // 结束索引内容
                break;
            } else {
                // 设置当前索引变量内容
                this.oprationContent+=this.context.getCurrentToken();
                // 获取下一个节点
                this.context.next();
            }
        }
    }

    @Override
    public String interpret() {
        IExpression myExpression = new ListExpression();
//        System.out.println("this.oprationContent:"+this.oprationContent);
        Context myContent = new Context(this.oprationContent,nowTime,this.context.getBank());
        myContent.next();
        myExpression.parse(myContent);
        String parse1 = myExpression.interpret();
//        System.out.println("parse1:"+parse1);

        IExpression my2Expression = new ListExpression();
        Context my2Content = new Context(this.oprationContent,lastTime,this.context.getBank());
        my2Content.next();
        my2Expression.parse(my2Content);
        String parse2 = my2Expression.interpret();
        return  parse1+"&"+parse2;
    }
}
