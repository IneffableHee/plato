package com.cbrc.plato.core.basic.service.impl;

import com.cbrc.plato.core.basic.model.CellOperation;
import com.cbrc.plato.core.basic.model.CheckTable;
import com.cbrc.plato.core.basic.model.ReportCell;
import com.cbrc.plato.core.basic.service.IReportCellService;
import com.cbrc.plato.util.math.MathUtil;
import com.cbrc.plato.util.response.PlatoResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReportCellService implements IReportCellService {
    @Override
    public List<ReportCell> parseReportCells(Map<String, String> cells) {
        if(cells != null){
            List<ReportCell> reportCells = new ArrayList<>();
            for(Map.Entry<String, String> entry : cells.entrySet()){
                ReportCell reportCell = new ReportCell();

                String mapKey = entry.getKey();
                String mapValue = entry.getValue();

                reportCell.setCell(mapKey);
                if(mapValue.indexOf("~")!=-1){
                    String[] strArray = mapValue.split("~");
                    if(strArray.length!=2){
                        log.info(mapKey+"取数规则错误："+mapValue);
                        return null;
                    }
                    reportCell.setBankName(strArray[1]);
                    mapValue = strArray[0];
                }
                if(mapValue.indexOf("#")!=-1){
                    String[] strArray = mapValue.split("#");
                    if(strArray.length!=2){
                        log.info(mapKey+"取数规则错误："+mapValue);
                        return null;
                    }
                    reportCell.setExcludeRule(strArray[1]);
                    mapValue = strArray[0];
                }
                if(mapValue.indexOf("$")!=-1){
//                    log.info(mapValue);
                    String[] strArray = mapValue.split("\\$");
                    if(strArray.length!=2){
                        log.info(mapKey+"取数规则错误："+mapValue+","+strArray[0]);
                        return null;
                    }
                    reportCell.setWarningRule(strArray[1]);
                    mapValue = strArray[0];
                }
                if(mapValue.indexOf(",")!=-1){
                    String[] strArray = mapValue.split(",");
                    if(strArray.length!=2){
                        log.info(mapKey+"取数规则错误："+mapValue);
                        return null;
                    }
                    reportCell.setDataRule(strArray[1]);
                    if(strArray[0].equals("@js-j")){
                        reportCell.setMonth(0);
                    }else{
                        reportCell.setMonth(1);
                    }
                }
                reportCells.add(reportCell);
            }
            return reportCells;
        }
        return null;
    }

    /**
     *
     * @param cells
     * @return
     */
    @Override
    public List<CheckTable> parseReportCells1(Map<String, String> cells) {
        if(cells != null){
            List<CheckTable> checkTables = new ArrayList<>();
            for(Map.Entry<String, String> entry : cells.entrySet()){
                CheckTable checkTable = new CheckTable();
                ReportCell reportCell = new ReportCell();
                String mapKey = entry.getKey();
                String mapValue = entry.getValue();
//                reportCell.setCell(mapKey);
//                checkTable.setReportCell (reportCell);
                checkTable.setTableForSourceCell (mapKey);
                if( null != mapValue){
                    String[] strArray = mapValue.split(",");
                    if(strArray.length!=6){
                        log.info ("---------length"+strArray.length);
                        log.info(mapKey+"-取数规则错误："+mapValue);
                        return null;
                    }
                    if(strArray[0].equals ("季报")){
                        checkTable.setMonth (0);
                    }else if(strArray[0].equals ("月报")){
                        checkTable.setMonth (1);
                    }
                    checkTable.setToCheckTarget (strArray[1]);
                    checkTable.setCheckRule (strArray[2]);
                    checkTable.setCheckTarget (strArray[3]);
                    checkTable.setProjectName (strArray[4]);
                    checkTable.setProjectTargetName (strArray[5]);
                }
                checkTables.add(checkTable);
            }
            return checkTables;
        }
        return null;
    }





    @Override
    public ReportCell cellMathematical(ReportCell reportCell,Map<String,String> tableCellValues,List<CellOperation> cellOperations) throws  Exception{
        String datarule = reportCell.getDataRule();
        for(CellOperation cellOperation : cellOperations){
            String operation = cellOperation.getOperation();
            String exclude = cellOperation.getExcludeRule();
            String value = cellOperation.getValue();
            if(StringUtils.isEmpty(value)){
                datarule = datarule.replace(operation,"9999999");
//                        return PlatoResult.failResult("单元格无数据:"+mapKey);
            }else{
                datarule = datarule.replace(operation,value);
                log.info("operation:"+operation+",value:"+value);
            }
        }

        for(Map.Entry<String, String> entry : tableCellValues.entrySet()){
            String mapKey = entry.getKey();
            String mapValue = entry.getValue();
            if(StringUtils.isEmpty(mapValue)){
                datarule = datarule.replace(mapKey,"9999999");
                log.info("---- 单元格无数据1:"+mapKey);
//                        return PlatoResult.failResult("单元格无数据:"+mapKey);
            }else{
                datarule = datarule.replace(mapKey,mapValue);
            }
        }
        log.info("----jisuan shuzi:"+datarule);
        double cValue = 0;
        try {
            cValue = MathUtil.jisuanStr(datarule);
            log.info("---- jisuan value:"+cValue);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("机构："+reportCell.getBankName()+"计算出错！规则："+reportCell.getDataRule()+",算式："+datarule);
        }
        reportCell.setValue(String.valueOf(cValue));
        return reportCell;
    }

    @Override
    public ReportCell setWarning(ReportCell reportCell) {
        double cValue = Double.valueOf(reportCell.getValue());
        String wRule = reportCell.getWarningRule();
        if(wRule!=null){
            NumberFormat nf= NumberFormat.getPercentInstance();
            String rule = null;
            if(wRule.startsWith(">")){
                rule = wRule.replace(">","");
                if(rule.indexOf("/")!=-1){
                    try {
                        rule = String.valueOf(MathUtil.jisuanStr(rule));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(rule!=null){
                    if(rule.indexOf("%")!=-1){
                        try {
                            Number m = nf.parse(rule);
                            if(cValue>m.doubleValue()){
                                reportCell.setWarning(true);
                                log.info("wRule:"+wRule+",rule:"+rule+",value"+m);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }else{
                        double m = Double.valueOf(rule);
                        if(cValue>m){
                            reportCell.setWarning(true);
                            log.info("wRule:"+wRule+",rule:"+rule+",value"+m);
                        }
                    }

                }
            }else if(wRule.startsWith("<")){
                rule = wRule.replace("<","");
                if(rule.indexOf("/")!=-1){
                    try {
                        rule = String.valueOf(MathUtil.jisuanStr(rule));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(rule!=null){
                    if(rule.indexOf("%")!=-1){
                        try {
                            Number m = nf.parse(rule);
                            if(cValue<m.doubleValue()){
                                reportCell.setWarning(true);
                                log.info("wRule:"+wRule+",rule:"+rule+",value"+m);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }else{
                        double m = Double.valueOf(rule);
                        if(cValue<m){
                            reportCell.setWarning(true);
                            log.info("wRule:"+wRule+",rule:"+rule+",value"+m);
                        }
                    }

                }
            }else if(wRule.startsWith("≥")){
                rule = wRule.replace("≥","");
                if(rule.indexOf("/")!=-1){
                    try {
                        rule = String.valueOf(MathUtil.jisuanStr(rule));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(rule!=null){
                    if(rule.indexOf("%")!=-1){
                        try {
                            Number m = nf.parse(rule);
                            if(cValue>=m.doubleValue()){
                                reportCell.setWarning(true);
                                log.info("wRule:"+wRule+",rule:"+rule+",value"+m);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }else{
                        double m = Double.valueOf(rule);
                        if(cValue>=m){
                            reportCell.setWarning(true);
                            log.info("wRule:"+wRule+",rule:"+rule+",value"+m);
                        }
                    }
                }
            }else if(wRule.startsWith("≤")){
                rule = wRule.replace("≤","");
                if(rule.indexOf("/")!=-1){
                    try {
                        rule = String.valueOf(MathUtil.jisuanStr(rule));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(rule!=null){
                    if(rule.indexOf("%")!=-1){
                        try {
                            Number m = nf.parse(rule);
                            if(cValue<=m.doubleValue()){
                                reportCell.setWarning(true);
                                log.info("wRule:"+wRule+",rule:"+rule+",value"+m);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }else{
                        double m = Double.valueOf(rule);
                        if(cValue<=m){
                            reportCell.setWarning(true);
                            log.info("wRule:"+wRule+",rule:"+rule+",value"+m);
                        }
                    }

                }
            }else if(wRule.startsWith("!=")){
                rule = wRule.replace("!=","");
                if(rule.indexOf("/")!=-1){
                    try {
                        rule = String.valueOf(MathUtil.jisuanStr(rule));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(rule!=null){
                    if(rule.indexOf("%")!=-1){
                        try {
                            Number m = nf.parse(rule);
                            if(cValue!=m.doubleValue()){
                                reportCell.setWarning(true);
                                log.info("wRule:"+wRule+",rule:"+rule+",value"+m);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }else{
                        double m = Double.valueOf(rule);
                        if(cValue!=m){
                            reportCell.setWarning(true);
                            log.info("wRule:"+wRule+",rule:"+rule+",value"+m);
                        }
                    }
                }
            }else if(wRule.startsWith("=")){
                rule = wRule.replace("=","");
                if(rule.indexOf("/")!=-1){
                    try {
                        rule = String.valueOf(MathUtil.jisuanStr(rule));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(rule!=null){
                    if(rule.indexOf("%")!=-1){
                        try {
                            Number m = nf.parse(rule);
                            if(cValue==m.doubleValue()){
                                reportCell.setWarning(true);
                                log.info("wRule:"+wRule+",rule:"+rule+",value"+m);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }else {
                        double m = Double.valueOf(rule);
                        if(cValue==m){
                            reportCell.setWarning(true);
                            log.info("wRule:"+wRule+",rule:"+rule+",value"+m);
                        }
                    }
                }
            }
        }
        return reportCell;
    }
}
