package com.cbrc.plato.util.datarule.expression.opration;

import com.cbrc.plato.util.datarule.expression.Context;
import com.cbrc.plato.util.time.DateUtils;

public class QNTQZL extends OprationExpression{
    public QNTQZL(Context context) {
        super(context);
    }

    @Override
    public String interpret(){
//        System.out.println("nowTime:"+this.context.getTime());
        this.nowTime = DateUtils.getYearOnYear(this.context.getTime());
        this.lastTime = DateUtils.getLastYearEndMonth(this.nowTime);

        String parseStr = super.interpret();
        String[] parseArr = parseStr.split("&");

        String parse = "";

        if(parseArr[1].length()>28){
            parse = parseArr[0]+"-"+parseArr[1];
        }else{
            parse = parseArr[0]+"-("+parseArr[1]+")";
        }
//        System.out.println("opration:TBZZL parse "+this.oprationContent+":"+parse);

        return parse;
    }
}
