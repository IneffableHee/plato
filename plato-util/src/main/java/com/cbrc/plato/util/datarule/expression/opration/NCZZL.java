package com.cbrc.plato.util.datarule.expression.opration;

import com.cbrc.plato.util.datarule.expression.Context;
import com.cbrc.plato.util.time.DateUtils;

public class NCZZL extends OprationExpression{
    public NCZZL(Context context) {
        super(context);
    }

    @Override
    public String interpret(){
        this.nowTime = this.context.getTime();
        this.lastTime = DateUtils.getLastYearEndMonth(nowTime);

        String parseStr = super.interpret();
        String[] parseArr = parseStr.split("&");

        String parse = "";

        if(parseArr[0].length()>28){
            parse = "(("+parseArr[0]+")-("+parseArr[1]+"))/("+parseArr[1]+")";
        }else{
            parse = "("+parseArr[0]+"-"+parseArr[1]+")/"+parseArr[1];
        }
//        System.out.println("opration:TBZZL parse "+this.oprationContent+":"+parse);

        return parse;
    }
}
