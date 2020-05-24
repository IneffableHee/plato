package com.cbrc.plato.util.datarule.datainfo;

import com.cbrc.plato.util.datarule.report.DataRuleUtil;

import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataInfoUtil {

    public static String parseRule(String rule){
        rule = DataRuleUtil.symbolNormalization(rule);

        StringTokenizer st = new StringTokenizer(rule, DataRuleUtil.RULE_SYMBOL, true);
        LinkedList<String> sourceList = new LinkedList<String>();
        while(st.hasMoreTokens()){
            String str = st.nextToken();
            sourceList.add(str);
//            System.out.println(str);
        }
        return String.join("",sourceList);
    }

    public static String parseOldRule(String excel,String rule){
        rule = DataRuleUtil.symbolNormalization(rule);

        Pattern op =Pattern.compile(DataRuleUtil.OPRATION.replace("|",",|").replace(")",",)"));
        Matcher om = op.matcher(rule);
        String opration = "";
        if(om.find()){
            if(rule.startsWith(om.group())){
                opration = om.group().replace(",","");
                rule = rule.substring(opration.length()+1);
                Pattern p =Pattern.compile(DataRuleUtil.RULE_SYMBOL);
                Matcher m = p.matcher(rule);

                /*按照句子结束符分割句子*/
                String[] words = p.split(rule);
                LinkedList<String> sourceList = new LinkedList<String>();
                /*将句子结束符连接到相应的句子后*/
                if(words.length > 0)
                {
                    int count = 0;
                    while(count < words.length)
                    {
                        if(!words[count].isEmpty())
                        {
                            String str = words[count];
                            if(str.matches(DataRuleUtil.OPRATION)){
                                sourceList.add(str);
                            }else{
                                sourceList.add(words[count]);
                            }
                        }

                        if(m.find())
                        {
                            if(!m.group().isEmpty()){
                                sourceList.add(m.group());
                            }
                        }
                        count++;
                    }
                }
                if(opration.isEmpty()){
                    return String.join("",sourceList);
                }else{
                    return opration+"<"+String.join("",sourceList)+">";
                }
            }else{
                System.out.println(om.group());
                return null;
            }
        }
        return null;
    }
}
