package com.cbrc.plato.util.datarule.expression;

import com.cbrc.plato.util.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

public class NumericExpression implements IExpression{
    private Context context;
    private String command;

    public NumericExpression(Context context){
        this.context = context;
        this.parse(this.context);
    }

    @Override
    public void parse(Context context) {
        this.command = this.context.getCurrentToken();
    }

    @Override
    public String interpret() {
        if(this.command.equals("znxs")){
            String nowTime = this.context.getTime();
            Date date = DateUtils.StrToDate(nowTime,"yyyyMM");
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            double month = c.get(Calendar.MONTH)+1;
            double value = month/12;
            String svalue = String.valueOf(value);
            return svalue;
        }else{
            return  this.command;
        }
    }
}
