package com.cbrc.plato.util.datarule.expression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 列表表达式
 *
 * @author
 *
 */
public class ListExpression implements IExpression {
    private Context context;
    private final ArrayList<IExpression> list = new ArrayList<IExpression>();
    /**
     * 构造方法将待解析的context传入
     *
     * @param context
     */
    public void parse(Context context) {
        this.context = context;
//        System.out.println("ac"+this.context.getCurrentToken());
        // 在ListExpression解析表达式中,循环解释语句中的每一个单词,直到终结符表达式或者异常情况退出
        while (true) {
            if (this.context.getCurrentToken() == null) {
                // 获取当前节点如果为 null 则表示end
                break;
            }else {
                // 建立Command 表达式
                IExpression expression = new CommandExperssion(this.context);
                // 添加到列表中
                list.add(expression);
                this.context.next();
            }
        }
    }
    /**
     * 实现解释方法
     */
    @Override
    public String interpret() {
        // 循环list列表中每一个表达式 解释执行
        List<String> parsedRule = new ArrayList<>();
        Iterator<IExpression> iterator = list.iterator();
        while (iterator.hasNext()) {
            parsedRule.add(iterator.next().interpret());
        }
        return String.join("",parsedRule);
    }
}
