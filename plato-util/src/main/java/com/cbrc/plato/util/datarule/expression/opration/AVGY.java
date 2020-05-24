package com.cbrc.plato.util.datarule.expression.opration;

import com.cbrc.plato.util.datarule.expression.Context;
import com.cbrc.plato.util.time.DateUtils;

public class AVGY extends OprationExpression{
    public AVGY(Context context) {
        super(context);
    }

    @Override
    public String interpret(){
        this.nowTime = this.context.getTime();
        this.lastTime = DateUtils.getLastYearEndMonth(nowTime);

        String parseStr = super.interpret();
//        System.out.println("parseStr:"+parseStr);
        String[] parseArr = parseStr.split("&");

        String parse = "";

        parse = "(("+parseArr[0]+"+"+parseArr[1]+")/2)";

//        System.out.println("opration AVGY parse :"+this.oprationContent+":"+parse);

        return parse;
    }
}
