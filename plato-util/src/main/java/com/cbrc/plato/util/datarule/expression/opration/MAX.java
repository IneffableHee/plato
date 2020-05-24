package com.cbrc.plato.util.datarule.expression.opration;

import com.cbrc.plato.util.datarule.expression.Context;
import com.cbrc.plato.util.datarule.expression.IExpression;
import com.cbrc.plato.util.datarule.expression.ListExpression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MAX extends OprationExpression{
    public MAX(Context context) {
        super(context);
    }

    @Override
    public String interpret(){
        IExpression myExpression = new ListExpression();
        Context myContent = new Context(this.oprationContent,this.context.getTime(),this.context.getBank());
        myContent.next();
        myExpression.parse(myContent);
        String parse = myExpression.interpret();
        String parseArr[] = parse.split("]");

        if(this.context.getExcludeRule()!=null){
            String exclude = this.context.getExcludeRule();
            Matcher matcher = Pattern.compile("[a-zA-Z]+\\d+?.*?\\[.*?\\]").matcher(exclude);
            while (matcher.find()) {
                String totalTable = parseArr[0].substring(parseArr[0].lastIndexOf(",")+1,parseArr[0].indexOf("["));
                String excludeTable = matcher.group(0).substring(0,matcher.group(0).indexOf("["));
                if(!totalTable.equals(excludeTable)){
                    System.out.println("不取数规则的table与max规则的表不一致，max规则:"+totalTable+",不取数规则:"+excludeTable);
                    return null;
                }
//                System.out.println("totalTable:"+totalTable+",excludeTable:"+excludeTable+">> "+exclude);
                exclude = exclude.replace(matcher.group(0),matcher.group(0).replace(excludeTable+"[","["));
//                System.out.println("lelele:"+parseArr[0]+","+matcher.group(0)+"--"+exclude+">> "+exclude);
            }
            return parseArr[0]+"max#"+exclude+"]}";
//            Pattern p= Pattern.compile(DataRuleUtil.SINGLE_CELL);
//            Matcher m=p.matcher(exclude);
//            if(m.matches()){
//                System.out.println("lelele:"+exclude+","+m.group(0));
//            }
        }
        return parseArr[0]+"max]}";
    }
}
