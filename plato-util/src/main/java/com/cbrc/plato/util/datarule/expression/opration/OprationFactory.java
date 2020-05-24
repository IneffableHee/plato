package com.cbrc.plato.util.datarule.expression.opration;

import com.cbrc.plato.util.datarule.expression.Context;

public class OprationFactory {
    public static OprationExpression createOpration(String name, Context context){
        switch (name){
            case "avgy":
                return new AVGY(context);
            case "hbzzl":
                return new HBZZL(context);
            case "tbzzl":
                return new TBZZL(context);
            case "nc":
            case "ncs":
                return new NC(context);
            case "sqs":
                return new SQS(context);
            case "sy":
                return new SY(context);
            case "tqs":
                return new TQS(context);
            case "max":
                return new MAX(context);
            case "nczzl":
                return new NCZZL(context);
            case "qntqzl":
                return new QNTQZL(context);
            case "cklsl":
                return new CKLSL(context);
                default:
                    return null;
        }
    }
}
