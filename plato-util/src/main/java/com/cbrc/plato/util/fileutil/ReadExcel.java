package com.cbrc.plato.util.fileutil;

import com.cbrc.plato.util.utilpo.Common;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fangtao
 * @date 2019-04-12
 */
@Slf4j
public class ReadExcel {

    /**
     * read the Excel file
     * @param filePath 文件储存路径
     */
    public static String readExcel(String filePath, Map<String,String> cellMap) throws IOException {
        String vaule = "";
        if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            vaule =  readXls(filePath ,cellMap);
        } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            vaule = readXlsx(filePath, cellMap);
        }
        return vaule;
    }

    /**
     * Read the Excel 2010
     * @param filePath the path of the excel file
     */
    public static String readXlsx(String filePath ,Map<String,String> cellMap) throws IOException {
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
        String tt = "";
        // Read the Sheet
        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
        if (xssfSheet != null) {
                XSSFRow xssfRow = xssfSheet.getRow(Integer.parseInt(cellMap.get("fileY"))-1);
                XSSFCell value =  xssfRow.getCell(Integer.parseInt( cellMap.get("fileX"))-1);
                tt = getValue(value);
                xssfWorkbook.close();
                inputStream.close();
                return tt;
            }else{
                xssfWorkbook.close();
                inputStream.close();
                return "单元格值为空";
            }
    }

    /**
     * Read the Excel 2003-2007
     * @param filePath the path of the Excel
     */
    public static String readXls(String filePath , Map<String,String> cellMap) throws IOException {
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
        String tt = "";
            // Read the Sheet
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
            if (hssfSheet != null) {
                HSSFRow hssfRow = hssfSheet.getRow(Integer.parseInt(cellMap.get("fileY"))-1);
                HSSFCell value =  hssfRow.getCell(Integer.parseInt( cellMap.get("fileX"))-1);
                tt = getValue(value);
                hssfWorkbook.close();
                inputStream.close();
                return tt;
            }else{
                hssfWorkbook.close();
                inputStream.close();
                return "单元格值为空";
            }
    }

    @SuppressWarnings("static-access")
    public  static String getValue(XSSFCell xssfRow) {
        String strCell = "";
        if ("BOOLEAN".equals(xssfRow.getCellTypeEnum().toString())) {
            return String.valueOf(xssfRow.getBooleanCellValue());
        } else if ("NUMERIC".equals(xssfRow.getCellTypeEnum().toString())){
            if (XSSFDateUtil.isCellDateFormatted(xssfRow)) {
                //  如果是date类型则 ，获取该cell的date值
                strCell = new SimpleDateFormat("yyyy-MM-dd").format(XSSFDateUtil.getJavaDate(xssfRow.getNumericCellValue()));
            } else { // 纯数字
                strCell = String.valueOf(xssfRow.getNumericCellValue());
            }
            return strCell;
        } else {
            return String.valueOf(xssfRow.getStringCellValue());
        }
    }

    @SuppressWarnings("static-access")
    public  static String getValue(HSSFCell hssfCell) {
        String strCell = "";
        if ("BOOLEAN".equals(hssfCell.getCellTypeEnum().toString())) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if ("NUMERIC".equals(hssfCell.getCellTypeEnum().toString())){
            if (XSSFDateUtil.isCellDateFormatted(hssfCell)) {
                //  如果是date类型则 ，获取该cell的date值
                strCell = new SimpleDateFormat("yyyy-MM-dd").format(XSSFDateUtil.getJavaDate(hssfCell.getNumericCellValue()));
            } else { // 纯数字
                strCell = String.valueOf(hssfCell.getNumericCellValue());
            }
            return strCell;
        } else {
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }

    public static List<Map<String,Object>> excelToJson(String filePath) throws IOException {
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            list =  readXls(filePath);
        } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            list = readXlsx(filePath);
        }
        return list;
    }
    public static List<Map<String,Object>>  readXlsx(String filePath) throws IOException {
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        // Read the Sheet
        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
        int rows = xssfSheet.getPhysicalNumberOfRows();
        Map<String, Object> map;
        for (int i = 1; i < rows; i++) {
            XSSFRow row = xssfSheet.getRow(i);
            if (row != null) {
                map = new HashMap<String, Object>();
                //获取到Excel文件中的所有的列
                String groupId = getValue(row.getCell(0));
                String[] tt1 = groupId.split("\\.");
                groupId = tt1[0];
                String excelCode = getValue(row.getCell(1));
                String dataName = getValue(row.getCell(2));
                String dataSource = getValue(row.getCell(3));
                String dataType = getValue(row.getCell(4));
                String onlyCode = getValue(row.getCell(5));
                String parent;
                if(row.getCell(6) == null ){
                    parent = "";
                }else{
                    parent = getValue(row.getCell(6));
                }
                map.put("groupId",groupId);
                map.put("excelCode",excelCode);
                map.put("dataName",dataName);
                map.put("dataSource",dataSource);
                map.put("dataType",dataType);
                map.put("onlyCode",onlyCode);
                map.put("parent",parent);
                list.add(map);
            }
        }
        inputStream.close();
        xssfWorkbook.close();
        return list;
    }

    public static List<Map<String,Object>> readXls(String filePath) throws IOException {
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        // Read the Sheet
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        int rows = hssfSheet.getPhysicalNumberOfRows();
        Map<String, Object> map;
        for (int i = 1; i < rows; i++) {
            // 读取左上端单元格
            HSSFRow row = hssfSheet.getRow(i);
            if (row != null) {
                    map = new HashMap<String, Object>();
                    //获取到Excel文件中的所有的列
                    int cells = row.getPhysicalNumberOfCells();
                    String groupId = getValue(row.getCell(0));
                    String[] tt1 = groupId.split("\\.");
                    groupId = tt1[0];
                    String excelCode = getValue(row.getCell(1));
                    String dataName = getValue(row.getCell(2));
                    String dataSource = getValue(row.getCell(3));
                    String dataType = getValue(row.getCell(4));
                    String onlyCode = getValue(row.getCell(5));
                    String parent;
                    if(row.getCell(6) == null ){
                        parent = "";
                    }else{
                        parent = getValue(row.getCell(6));
                    }
                    map.put("groupId",groupId);
                    map.put("excelCode",excelCode);
                    map.put("dataName",dataName);
                    map.put("dataSource",dataSource);
                    map.put("dataType",dataType);
                    map.put("onlyCode",onlyCode);
                    map.put("parent",parent);
                    list.add(map);
            }
        }
        inputStream.close();
        hssfWorkbook.close();
        return list;
    }
}