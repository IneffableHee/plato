package com.cbrc.plato.util.datarule.expression;

public class SymbolExpression implements IExpression{
    private Context context;
    private String command;

    public SymbolExpression(Context context){
        this.context = context;
        this.parse(this.context);
    }

    @Override
    public void parse(Context context) {
        this.command = this.context.getCurrentToken();
    }

    @Override
    public String interpret() {
        return  this.command;
    }
}
