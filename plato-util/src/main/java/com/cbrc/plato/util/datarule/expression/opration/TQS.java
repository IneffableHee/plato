package com.cbrc.plato.util.datarule.expression.opration;

import com.cbrc.plato.util.datarule.expression.Context;
import com.cbrc.plato.util.datarule.expression.IExpression;
import com.cbrc.plato.util.datarule.expression.ListExpression;
import com.cbrc.plato.util.time.DateUtils;

public class TQS extends OprationExpression{
    public TQS(Context context) {
        super(context);
    }

    @Override
    public String interpret(){
        this.nowTime = this.context.getTime();
        String lastTime = DateUtils.getYearOnYear(nowTime);

        IExpression myExpression = new ListExpression();
        Context myContent = new Context(this.oprationContent,lastTime,this.context.getBank());
        myContent.next();
        myExpression.parse(myContent);
        return myExpression.interpret();
    }
}
