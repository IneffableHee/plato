package com.cbrc.plato.util.excel;

import com.cbrc.plato.util.datarule.checktable.CheckRuleTable;
import com.cbrc.plato.util.datarule.datainfo.DataJZD;
import com.cbrc.plato.util.datarule.model.QnyjYuqi90East;
import com.cbrc.plato.util.fileutil.FileUtil;
import com.cbrc.plato.util.utilpo.Common;

import java.util.*;

public class ExcelRead {
    public static Map<String,String> readExcelCells(String filePath, Set<String> cellSet, int sheet)throws Exception {
        Map<String,String> value = new HashMap<String,String>();
        if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            try {
                System.out.println("POIExcelRead.readExcelCells begin");
                value = POIExcelRead.readExcelCells(filePath ,cellSet,sheet);
            }catch (Exception e){
                try {
                    System.out.println("JXLExcelRead.readExcelCells begin");
                    value = JXLExcelRead.readExcelCells(filePath ,cellSet,sheet);
                }catch (Exception es){
                    if(es.getMessage().contains("取数规则错误")){
                        throw new Exception(es.getMessage());
                    }
                    throw new Exception("fail:"+filePath+"读取失败，文件excel版本过低导致无法读取，请另存后重新上传！!");
                }
            }
            return  value;
        } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            return POIExcelRead.readXlsxCells(filePath, cellSet,sheet);
        }
        return null;
    }

    /*读取报表模板文件，返回需要计算的单元格以及计算规则*/
    public static Map<String,String> readReportMouldCells(String filePath,int sheet) throws  Exception{
        Map<String,String> value = new HashMap<String,String>();
        if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            try {
                value = POIExcelRead.readReportMouldCells(filePath ,sheet);
            }catch (Exception e){
                value = JXLExcelRead.readReportMouldCells(filePath,sheet);
            }
            return  value;
        } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            return POIExcelRead.readReportMouldCells(filePath,sheet);
        }
        return null;
    }
    /*读取报表模板文件，返回需要计算的单元格以及计算规则*/

    /**
     * kai_w_x
     * @param filePath
     * @param sheet
     * @return
     * @throws Exception
     */
    public static Map<String,String> readReportMouldCells1(String filePath , int sheet) throws  Exception{
        Map<String,String> value = new HashMap<String,String>();

        if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            try {
                value = POIExcelRead.readReportMouldCells1(filePath ,sheet);
            }catch (Exception e){
                value = JXLExcelRead.readReportMouldCells(filePath,sheet);
            }
            return  value;
        } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            return POIExcelRead.readReportMouldCells1(filePath,sheet);
        }
        return null;
    }
    /*读取文件，返回需要某一列的最大值*/
    public static String readColMax(String filePath , int sheet,String col) throws  Exception{
        String value = null;
        if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            try {
                value = POIExcelRead.getColMax(filePath ,sheet,col);
            }catch (Exception e){
                value = JXLExcelRead.getColMax(filePath,sheet,col);
            }
            return  value;
        } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            return POIExcelRead.getColMax(filePath ,sheet,col);
        }
        return null;
    }

    /*读取文件，返回需要某一列的最大值，去除不取值部分*/
    public static String readColMaxWhithExclude(String filePath , int sheet,String col,Set<String> exclude) throws  Exception{
        String value = null;
        if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            try {
                value = POIExcelRead.getColMaxExclude(filePath ,sheet,col,exclude);
            }catch (Exception e){
//                value = JXLExcelRead.getColMaxExclude(filePath,sheet,col,exclude);
            }
            return  value;
        } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            return POIExcelRead.getColMaxExclude(filePath ,sheet,col,exclude);
        }
        return null;
    }

    public static List<DataJZD> readJZD(int bankId,String time,String filePath) throws Exception{
        List<DataJZD> jzdValue = new ArrayList<>();
        if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            try {
                jzdValue = POIExcelRead.readJZD(bankId,time,filePath);
            }catch (Exception e){
                try {
                    jzdValue = JXLExcelRead.readJZD(bankId,time,filePath);
                }catch (Exception es){
                    throw new Exception("fail:"+filePath+"读取失败，文件excel版本过低导致无法读取，请另存后重新上传！!");
                }
            }
            return  jzdValue;
        } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            return POIExcelRead.readJZD(bankId,time,filePath);
        }
        return null;
    }

    public static List<QnyjYuqi90East> readYuqi90(String filePath) throws Exception{
        List<QnyjYuqi90East> yuqi90Value = new ArrayList<QnyjYuqi90East>();
        if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            try {
                    yuqi90Value = POIExcelRead.readYuqi90(filePath);
            }catch (Exception e){
                try {
                    yuqi90Value = JXLExcelRead.readYuqi90(filePath);
                }catch (Exception es){
                    throw new Exception("fail:"+filePath+"读取失败，文件excel版本过低导致无法读取，请另存后重新上传！!");
                }
            }
            return  yuqi90Value;
        } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            return POIExcelRead.readYuqi90(filePath);
        }
        return null;
    }

    public static CheckRuleTable readExcelToCheckRuleTable(String filePath) throws Exception{
        CheckRuleTable checkRuleTable;
        if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            try {
                checkRuleTable = POIExcelRead.readExcelToCheckRuleTable(filePath);
            }catch (Exception e){
                try {
                    checkRuleTable = JXLExcelRead.readExcelToCheckRuleTable(filePath);
                }catch (Exception es){
                    throw new Exception("fail:"+filePath+"读取失败，文件excel版本过低导致无法读取，请另存后重新上传！!");
                }
            }
            return  checkRuleTable;
        } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            return POIExcelRead.readExcelToCheckRuleTable(filePath);
        }
        return null;
    }
}
