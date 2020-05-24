package com.cbrc.plato.util.datarule.expression;

import com.cbrc.plato.util.datarule.report.DataRuleUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 上下文环境
 *
 * @author
 *
 */
public class Context {
    // 待解析的文本内容
//    private final LinkedList<String> sourceRule;
    private final StringTokenizer sourceRule;
    //解析出来的文本内容
    private List<String> parsedRule;
    //不取数规则(max函数使用)
    private String excludeRule;
    // 当前命令
    private String currentToken;
    //期次
    private final String time;
    //银行
    private String bank;
    // 用来存储动态变化信息内容
    private final Map<String, Object> map = new HashMap<String, Object>();
    /**
     * 构造方法设置解析内容
     *
     * @param rule
     */
    public Context(String rule,String time) {
        // 解析文本内容
        rule = DataRuleUtil.symbolNormalization(rule);
        if(rule.indexOf("#")!=-1){
            String[] strArray = rule.split("#");
            if(strArray.length!=2){
                System.out.println("取数规则错误："+rule);
            }
            this.excludeRule = strArray[1];
            rule = strArray[0];
        }
        this.sourceRule = DataRuleUtil.splitSourceRule(rule);
        this.parsedRule = new LinkedList<String>();
        this.time = time;
    }

    public Context(String rule,String time,String bank) {
        // 解析文本内容
        rule = DataRuleUtil.symbolNormalization(rule);
        this.sourceRule = DataRuleUtil.splitSourceRule(rule);
//        for(String str:this.sourceRule){
//            System.out.println("splitSourceRule:"+str);
//        }
        this.parsedRule = new LinkedList<String>();
        this.time = time;
        this.bank = bank;
    }
    /**
     * 解析文本
     */
    public String next() {
//        if (this.sourceRule.size()>0) {
//            currentToken = this.sourceRule.getFirst();
//            this.sourceRule.removeFirst();
////            System.out.println(currentToken);
//        } else {
//            currentToken = null;
//        }
        if (this.sourceRule.hasMoreTokens()) {
            currentToken = this.sourceRule.nextToken();
//            System.out.println(currentToken);
        } else {
            currentToken = null;
        }
        return currentToken;
    }
    /**
     * 判断命令是否正确
     *
     * @param command
     * @return
     */
    public boolean equalsWithCommand(String command) {
        if (command == null || !command.equals(this.currentToken)) {
            return false;
        }
        return true;
    }
    /**
     * 判断命令是否为函数运算，需要解析
     *
     * @param
     * @return
     */
    public boolean isOpration(){
        Pattern p= Pattern.compile(DataRuleUtil.OPRATION);
        Matcher m=p.matcher(this.currentToken);
        if(m.matches()){
            return true;
        }else{
            return false;
        }
    }
    /**
     * 判断是否为数学运算符号，无需解析
     *
     * @param
     * @return
     */
    public boolean isMathSymbol(){
        Pattern p= Pattern.compile(DataRuleUtil.MATH_SYMBOL);
        Matcher m=p.matcher(this.currentToken);
        if(m.matches()){
            return true;
        }else{
            return false;
        }
    }
    /**
     * 判断是否为规则符号，无需解析
     *
     * @param
     * @return
     */
    public boolean isRuleSymbol(){
        Pattern p= Pattern.compile(DataRuleUtil.RULE_SYMBOL);
        Matcher m=p.matcher(this.currentToken);
        if(m.matches()){
            return true;
        }else{
            return false;
        }
    }
    /**
     * 判断是否为数字，无需解析
     *
     * @param
     * @return
     */
    public boolean isNumeric(){
        Pattern pattern = Pattern.compile("^\\d+(\\.\\d+)?$|^\\d+\\.?\\d*\\%?$|znxs");
        return pattern.matcher(this.currentToken).matches();
    }
    /**
     * 获得当前命令内容
     *
     * @return
     */
    public String getCurrentToken() {
        return this.currentToken;
    }
    /**
     * 获得节点的内容
     *
     * @return
     */
    public String getTokenContent(String text) {
        String str = text;
        if (str != null) { // 替换map中的动态变化内容后返回 Iterator<String>
            // 替换map中的动态变化内容后返回
            Iterator<String> iterator = this.map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object obj = map.get(key);
                str = str.replaceAll(key, obj.toString());
            }
        }
        return str;
    }
    public void put(String key, Object value) {
        this.map.put(key, value);
    }
    public void clear(String key) {
        this.map.remove(key);
    }

    public String getTime() {
        return time;
    }

    public List<String> getParsedRule() {
        return parsedRule;
    }

    public void setParsedRule(List<String> parsedRule) {
        this.parsedRule = parsedRule;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getExcludeRule() {
        return excludeRule;
    }

    public void setExcludeRule(String excludeRule) {
        this.excludeRule = excludeRule;
    }
}