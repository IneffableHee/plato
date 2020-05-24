package com.cbrc.plato.util.datarule.checktable;

import com.cbrc.plato.util.datarule.report.DataRuleUtil;
import com.cbrc.plato.util.datarule.report.ReportCell;
import com.cbrc.plato.util.datarule.report.ReportTable;
import com.cbrc.plato.util.datarule.report.SourceMatrix;
import com.cbrc.plato.util.math.MathUtil;
import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CheckRuleTable {
    private Integer id;

    private String ruleName;

    private String ruleDiscribe;

    private Date createTime;

    private Date updateTime;

    private Integer status;

    private Integer user;

    private String userName;

    private Integer department;

    private String accuracy;        //表间精度

    private String selfAccuracy;        //表内精度

    private Integer departmentId;

    private List<CheckRuleInfo> checkRuleInfoList;

    private String bankName;

    private String time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName == null ? null : ruleName.trim();
    }

    public String getRuleDiscribe() {
        return ruleDiscribe;
    }

    public void setRuleDiscribe(String ruleDiscribe) {
        this.ruleDiscribe = ruleDiscribe == null ? null : ruleDiscribe.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public List<CheckRuleInfo> getCheckRuleInfoList() {
        return checkRuleInfoList;
    }

    public void setCheckRuleInfoList(List<CheckRuleInfo> checkRuleInfoList) {
        this.checkRuleInfoList = checkRuleInfoList;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getSelfAccuracy() {
        return selfAccuracy;
    }

    public void setSelfAccuracy(String selfAccuracy) {
        this.selfAccuracy = selfAccuracy;
    }

    public void setCellValues(SourceMatrix sourceMatrix, ReportTable reportTable){
        List<CheckRuleInfo> checkRuleInfos = this.getCheckRuleInfoList();
        FelEngine fel = new FelEngineImpl();
        for(CheckRuleInfo checkRuleInfo:checkRuleInfos){
            log.info(checkRuleInfo.getBanks()+"-"+checkRuleInfo.getTimes()+"-"+checkRuleInfo.getSource()+"-"+checkRuleInfo.getSourceName()+"-"+checkRuleInfo.getTarget()+"-"+checkRuleInfo.getTargetName());
            if(checkRuleInfo.getType()!=null&&checkRuleInfo.getType().equals("审表提示")){
                System.out.println("审表提示审表提示审表提示");
                continue;
            }

            String sourceRule = checkRuleInfo.getSource();
            String targetRule = checkRuleInfo.getTarget();
            if(sourceRule==null||sourceRule.isEmpty()||targetRule==null||targetRule.isEmpty()){
                checkRuleInfo.setResult(2);
                checkRuleInfo.setFailMessage("审表规则错误:"+sourceRule);
                continue;
            }

            System.out.println("setCellValues:"+checkRuleInfo.getType()+sourceRule);
            System.out.println("setTargValues:"+checkRuleInfo.getType()+targetRule);

            if(MathUtil.isNumber(sourceRule)){
                checkRuleInfo.setSourceValue(sourceRule);
            }else if(!DataRuleUtil.isDataRuleMix(sourceRule)){
                checkRuleInfo.setResult(2);
                checkRuleInfo.setFailMessage("审表规则错误:"+sourceRule);
                continue;
            }else{
                for(ReportCell reportCell:reportTable.getReportCells()){
                    if(sourceRule!=null&&reportCell.getRuleSource()!=null&&sourceRule.equals(reportCell.getRuleSource())){
                        log.info(reportCell.getRuleSource());
                        log.info(reportCell.getExpressionRule());
//                        log.info("---");
                        String dataRule = reportCell.getExpressionRule();
                        if(dataRule==null||dataRule.isEmpty())
                            continue;
                        String evalStr = dataRule;
                        Matcher dmatcher = Pattern.compile(DataRuleUtil.EXPRESSION_CELL).matcher(dataRule);
                        int right = 1;
                        while (dmatcher.find()) {
                            String cell = dmatcher.group(0);
//                            System.out.println("cell:");
                            String value = sourceMatrix.getCheckRuleCellValue(cell);
                            System.out.println("cell:"+cell+",value:"+value);
                            if(value!=null&&!value.isEmpty()){
                                evalStr = evalStr.replace(cell,value);
                            }else if (value.isEmpty()){
                                right = 0;
                                evalStr = null;
                                checkRuleInfo.setResult(3);
                                checkRuleInfo.setFailMessage("单元格值为空:"+sourceRule);
                                break;
                            }else{
                                right = 0;
                                evalStr = null;
                                checkRuleInfo.setResult(2);
                                checkRuleInfo.setFailMessage("审表规则错误:"+sourceRule);
                                break;
                            }
                        }

                        if(right==0)
                            break;

                        if(evalStr == null||evalStr.isEmpty()){
                            checkRuleInfo.setResult(3);
                            checkRuleInfo.setFailMessage("单元格未取到值:"+sourceRule);
                            break;
                        }else if(evalStr.contains("notUpload")){
                            checkRuleInfo.setResult(4);
                            checkRuleInfo.setFailMessage("文件未上传:"+sourceRule);
                            break;
                        }else if(evalStr.contains("ruleError")){
                            checkRuleInfo.setResult(2);
                            checkRuleInfo.setFailMessage("审表规则错误:"+sourceRule);
                            break;
                        }

                        log.info("data fel :"+evalStr);
                        try {
                            String result = String.valueOf(fel.eval(evalStr));
                            String pResult = MathUtil.xiaoshuweishu(result,5);
                            log.info("data result:"+result+","+pResult);
                            checkRuleInfo.setSourceValue(pResult);
                        }catch (Exception e){
                            log.info("data fel fault:"+evalStr);
                            checkRuleInfo.setResult(2);
                            checkRuleInfo.setFailMessage("计算错误:"+sourceRule+":"+evalStr+",请核对数据或规则！");
                            break;
                        }
                        break;
                    }
                }
//                this.setSourceValue(fel,sourceMatrix,checkRuleInfo);
            }

            if(checkRuleInfo.getResult()!=null){
                continue;
            }


            if(checkRuleInfo.getType().equals("科目校验")&&checkRuleInfo.getRule().equals("=")&&StringUtils.isBlank(targetRule))
                continue;

            if(MathUtil.isNumber(targetRule)){
                checkRuleInfo.setTargetValue(targetRule);
            }else if(!DataRuleUtil.isDataRuleMix(targetRule)){
                checkRuleInfo.setResult(2);
                checkRuleInfo.setFailMessage("审表规则错误:"+targetRule);
                continue;
            }else{
                for(ReportCell reportCell:reportTable.getReportCells()) {
                    if(targetRule!=null&&reportCell.getRuleSource()!=null&&targetRule.equals(reportCell.getRuleSource())) {
                        log.info("setCellValues:"+reportCell.getRuleSource());
                        log.info(reportCell.getExpressionRule());

                        String dataRule = reportCell.getExpressionRule();
                        if(dataRule==null||dataRule.isEmpty())
                            continue;
                        String evalStr = dataRule;
                        Matcher dmatcher = Pattern.compile(DataRuleUtil.EXPRESSION_CELL).matcher(dataRule);
                        int right = 1;
                        while (dmatcher.find()) {
                            String cell = dmatcher.group(0);
//                            System.out.println("cell:");
                            String value = sourceMatrix.getCheckRuleCellValue(cell);
                            System.out.println("cell:"+cell+",value:"+value);
                            if(value!=null&&!value.isEmpty()){
                                evalStr = evalStr.replace(cell,value);
                            }else if (value.isEmpty()){
                                right = 0;
                                evalStr = null;
                                checkRuleInfo.setResult(3);
                                checkRuleInfo.setFailMessage("单元格值为空:"+sourceRule);
                                break;
                            }else{
                                right = 0;
                                evalStr = null;
                                checkRuleInfo.setResult(2);
                                checkRuleInfo.setFailMessage("审表规则错误:"+targetRule);
                                break;
                            }
                        }

                        if(right==0)
                            continue;
                        if(evalStr == null||evalStr.isEmpty()){
                            checkRuleInfo.setResult(3);
                            checkRuleInfo.setFailMessage("单元格未取到值:"+targetRule);
                            break;
                        }else if(evalStr.contains("notUpload")){
                            checkRuleInfo.setResult(4);
                            checkRuleInfo.setFailMessage("文件名错误或文件未上传:"+targetRule);
                            break;
                        }else if(evalStr.contains("ruleError")){
                            checkRuleInfo.setResult(2);
                            checkRuleInfo.setFailMessage("审表规则错误:"+targetRule);
                            break;
                        }

                        log.info("data fel :"+evalStr);
                        try {
                            String result = String.valueOf(fel.eval(evalStr));
                            String pResult = MathUtil.xiaoshuweishu(result,5);
                            log.info("data result:"+result+","+pResult);
                            checkRuleInfo.setTargetValue(pResult);
                        }catch (Exception e){
                            log.info("data fel fault:"+evalStr);
                            checkRuleInfo.setResult(2);
                            checkRuleInfo.setFailMessage("计算错误:"+sourceRule+":"+evalStr+",请核对数据或规则！");
                            break;
                        }
                        break;
                    }
                }
//                this.setTargetValue(fel,sourceMatrix,checkRuleInfo);
            }
        }
    }

    public void  setSourceValue(FelEngine fel,SourceMatrix sourceMatrix,CheckRuleInfo checkRuleInfo){
        String sourceRule = checkRuleInfo.getSource();
        String sourceStr = sourceRule;
        Matcher matcher = Pattern.compile(DataRuleUtil.SINGLE_CELL).matcher(sourceRule);
        while (matcher.find()) {
            String cell = matcher.group(0);
            String value = sourceMatrix.getCheckRuleCellValue(cell);
            log.info(matcher.group(0) +" value:"+value);
            if(value == null){
                sourceStr = null;
                checkRuleInfo.setResult(4);
                break;
            }else if(value.contains("ruleError")){
                sourceStr = null;
                checkRuleInfo.setResult(2);
                checkRuleInfo.setFailMessage(matcher.group(0)+"规则错误！");
                break;
            } else if(MathUtil.isNumber(value)){
                sourceStr = sourceStr.replace(cell,value);
            }else if(value.equals("notUpload")){
                sourceStr = null;
                checkRuleInfo.setResult(4);
                checkRuleInfo.setFailMessage(matcher.group(0)+"未找到文件！");
                break;
            }else if(value.equals("out")){
                sourceStr = null;
                checkRuleInfo.setResult(5);
                checkRuleInfo.setFailMessage(matcher.group(0)+"单元格不在表格有效区域内！");
                break;
            }else if(value.isEmpty()){
                sourceStr = null;
                checkRuleInfo.setResult(3);
                checkRuleInfo.setFailMessage(matcher.group(0)+"单元格为空！");
                break;
            }
        }
        if(sourceStr == null||sourceStr.isEmpty()){
            return;
        }
        if(MathUtil.isNumber(sourceStr)){
            checkRuleInfo.setSourceValue(MathUtil.xiaoshuweishu(sourceStr,5));
            return;
        }
        log.info("source data fel :"+sourceStr);
        try {
            String result = String.valueOf(fel.eval(sourceStr));
            log.info("source data result:"+result);
            checkRuleInfo.setSourceValue(MathUtil.xiaoshuweishu(result,5));
        }catch (Exception e){
            log.info("source data fel fault:"+sourceStr);
            checkRuleInfo.setResult(2);
            checkRuleInfo.setFailMessage(sourceRule+"计算出错："+sourceStr);
            return;
        }
    }

    public void  setTargetValue(FelEngine fel,SourceMatrix sourceMatrix,CheckRuleInfo checkRuleInfo){
        String targetRule = checkRuleInfo.getTarget();
        String targetStr = targetRule;
        Matcher matcher = Pattern.compile(DataRuleUtil.SINGLE_CELL).matcher(targetRule);
        while (matcher.find()) {
            String cell = matcher.group(0);
            String value = sourceMatrix.getCheckRuleCellValue(cell);
            System.out.println(matcher.group(0) +" value:"+value);
            if(value == null){
                targetStr = null;
                checkRuleInfo.setResult(4);
                break;
            }else if(MathUtil.isNumber(value)){
                targetStr = targetStr.replace(cell,value);
            }else if(value.equals("notUpload")){
                targetStr = null;
                checkRuleInfo.setResult(4);
                checkRuleInfo.setFailMessage(matcher.group(0)+"未找到文件！");
                break;
            }else if(value.equals("out")){
                targetStr = null;
                checkRuleInfo.setResult(5);
                checkRuleInfo.setFailMessage(matcher.group(0)+"单元格不在表格有效区域内！");
                break;
            }else if(value.isEmpty()){
                targetStr = null;
                checkRuleInfo.setResult(3);
                checkRuleInfo.setFailMessage(matcher.group(0)+"单元格为空！");
                break;
            }
        }
        if(targetStr == null||targetStr.isEmpty()){
            return;
        }
        if(MathUtil.isNumber(targetStr)){
            checkRuleInfo.setTargetValue(MathUtil.xiaoshuweishu(targetStr,5));
            return;
        }
        log.info("target data fel :"+targetStr);
        try {
            String result = String.valueOf(fel.eval(targetStr));
            log.info("target data result:"+result);
            checkRuleInfo.setTargetValue(MathUtil.xiaoshuweishu(result,5));
        }catch (Exception e){
            log.info("target data fel fault:"+targetStr);
            checkRuleInfo.setResult(2);
            checkRuleInfo.setFailMessage(targetStr+"计算出错："+targetStr);
            return;
        }
    }

    public void getRuleResult(){
        List<CheckRuleInfo> checkRuleInfos = this.getCheckRuleInfoList();
        List<CheckRuleInfo> subjects = new ArrayList<>();
        String subjectName = null;
        boolean isProject = false;
        for(CheckRuleInfo checkRuleInfo:checkRuleInfos){
            if(checkRuleInfo.getResult()!=null)
                continue;

            if(checkRuleInfo.getType()!=null&&checkRuleInfo.getType().equals("科目校验")){
                if(!StringUtils.isBlank(subjectName)&&!subjectName.equals(checkRuleInfo.getSubjectName())){
                    subjects.clear();
                    subjects = new ArrayList<>();
                }
                subjectName = checkRuleInfo.getSubjectName();
                subjects.add(checkRuleInfo);
                continue;
            }

            if(!StringUtils.isBlank(subjectName)){
                List<String> subjectValues = new ArrayList<>();
                String subjectRule = null,subjectTarget = null;
                double subjectAcuracy = 0;
                for(CheckRuleInfo subject:subjects){
                    subjectRule = StringUtils.isBlank(subject.getRule())?subjectRule:subject.getRule();
                    subjectTarget = StringUtils.isBlank(subject.getTarget())?subjectTarget:subject.getTarget();
                    System.out.println("aas "+subjectRule+subject.getRule()+subjectTarget+subject.getTarget());
                    if(!StringUtils.isBlank(checkRuleInfo.getAccuracy())&&Double.valueOf(checkRuleInfo.getAccuracy())!=0){
                        subjectAcuracy = Double.valueOf(checkRuleInfo.getAccuracy());
                    }else if(!StringUtils.isBlank(accuracy)&&Double.valueOf(accuracy)!=0){
                        Double.valueOf(this.accuracy);
                    }
                    System.out.println(subject.getSourceName()+subject.getSource()+subject.getSourceValue());
                    subjectValues.add(subject.getSourceValue());
                }
                System.out.println(subjectRule+subjectTarget+subjectAcuracy);
                boolean tof = MathUtil.SubjectTOF(subjectValues,subjectRule,subjectTarget,subjectAcuracy);
                for(CheckRuleInfo subject:subjects){
                    if(tof){
                        subject.setResult(0);
                    }else{
                        subject.setResult(1);
                    }
                }
            }


            if(checkRuleInfo.getType()!=null&&checkRuleInfo.getType().equals("审表提示")){
                checkRuleInfo.setResult(10);
                continue;
            }

            boolean tof;
            if(!StringUtils.isBlank(checkRuleInfo.getAccuracy())&&Double.valueOf(checkRuleInfo.getAccuracy())!=0){
                tof = MathUtil.TOF(checkRuleInfo.getSourceValue(),checkRuleInfo.getTargetValue(),checkRuleInfo.getRule(),Double.valueOf(checkRuleInfo.getAccuracy()));
            }else if(!StringUtils.isBlank(accuracy)&&Double.valueOf(accuracy)!=0){
                tof = MathUtil.TOF(checkRuleInfo.getSourceValue(),checkRuleInfo.getTargetValue(),checkRuleInfo.getRule(),Double.valueOf(accuracy));
            }else{
                tof = MathUtil.TOF(checkRuleInfo.getSourceValue(),checkRuleInfo.getTargetValue(),checkRuleInfo.getRule());
            }

            if(tof){
                checkRuleInfo.setResult(0);
            }else{
                checkRuleInfo.setResult(1);
            }
        }
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getDepartment() {
        return department;
    }

    public void setDepartment(Integer department) {
        this.department = department;
    }
}