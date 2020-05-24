package com.cbrc.plato.util.datarule.checktable;

import com.cbrc.plato.util.excel.ExcelRead;

public class CheckTableUtil {
    public static CheckRuleTable createCheckRuleTableWithExcel(String path){
        CheckRuleTable checkRuleTable;
        try {
            checkRuleTable = ExcelRead.readExcelToCheckRuleTable(path);
            return checkRuleTable;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
