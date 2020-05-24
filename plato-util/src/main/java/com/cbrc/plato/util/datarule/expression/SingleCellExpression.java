package com.cbrc.plato.util.datarule.expression;

public class SingleCellExpression implements IExpression{
    private Context context;
    private String command;

    public SingleCellExpression(Context context){
        this.context = context;
        this.parse(this.context);
    }

    @Override
    public void parse(Context context) {
        this.command = "{"+this.context.getTime()+","+this.context.getBank()+","+this.context.getCurrentToken()+"}";
    }

    @Override
    public String interpret() {
        return  this.command;
    }
}
