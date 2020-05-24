package com.cbrc.plato.util.datarule.expression.opration;

import com.cbrc.plato.util.datarule.expression.Context;
import com.cbrc.plato.util.datarule.expression.IExpression;
import com.cbrc.plato.util.datarule.expression.ListExpression;
import com.cbrc.plato.util.time.DateUtils;

public class NC extends OprationExpression{
    public NC(Context context) {
        super(context);
    }

    @Override
    public String interpret(){
        String nowTime = this.context.getTime();
        String ncTime = DateUtils.getLastYearEndMonth(nowTime);

        IExpression myExpression = new ListExpression();
        Context myContent = new Context(this.oprationContent,ncTime,this.context.getBank());
        myContent.next();
        myExpression.parse(myContent);
        return myExpression.interpret();
    }
}
