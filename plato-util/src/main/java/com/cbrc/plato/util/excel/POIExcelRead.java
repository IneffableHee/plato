package com.cbrc.plato.util.excel;

import com.cbrc.plato.util.datarule.checktable.CheckRuleInfo;
import com.cbrc.plato.util.datarule.checktable.CheckRuleTable;
import com.cbrc.plato.util.datarule.datainfo.DataJZD;
import com.cbrc.plato.util.datarule.model.QnyjYuqi90East;
import com.cbrc.plato.util.datarule.report.DataRuleUtil;
import com.cbrc.plato.util.fileutil.FileUtil;
import com.cbrc.plato.util.fileutil.XSSFDateUtil;
import com.cbrc.plato.util.time.DateUtils;
import com.cbrc.plato.util.utilpo.Common;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class POIExcelRead {
    /**
     * 提取列名称的正则表达式
     */
    private static final String DISTILL_COLUMN_REG = "^([A-Z]{1,})";


    /**
     * read the Excel file
     * @param filePath 文件储存路径
     */
    public static Map<String,String> readExcelCells(String filePath, Set<String> cellSet, int sheet) throws Exception {
        if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            return  readXlsCells(filePath ,cellSet,0);
        } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            return readXlsxCells(filePath, cellSet,0);
        }
        return null;
    }


    public static Map<String,String> readReportMouldCells(String filePath , int sheet) throws Exception {
        log.info("readReportMouldCells");
        Map<String,String> value = new LinkedHashMap<String,String>();
        log.info("readReportMouldCells qq");
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);
        log.info("readReportMouldCells ww");
        Workbook wb = null;
        try {
            if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
                wb = new HSSFWorkbook(inputStream);
            } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
                wb = new XSSFWorkbook(inputStream);
            }
        }catch (Exception e){
            log.info(e.getMessage());
            inputStream.close();
            e.printStackTrace();
        }
        log.info("readReportMouldCells ee");
        // Read the Sheet
        Sheet poiSheet = wb.getSheetAt(sheet);
        log.info("readReportMouldCells aa");
        if (poiSheet != null) {
//            log.info("readReportMouldCells ss");
//            int rrows=poiSheet.getRow(0).getPhysicalNumberOfCells();
            log.info("readReportMouldCells dd");
            int cols=poiSheet.getLastRowNum();//获得总行数
            System.out.println("cols:"+cols);
//            System.out.println("rows:"+rrows+",cols:"+cols);
            // 列循环
            for (int j = 0; j < cols+3; j++) {
                //行循环
                Row row = poiSheet.getRow(j);
                if(row!=null){
                    int rows=row.getPhysicalNumberOfCells();
                    if(j>19){
                        System.out.println("ooooo "+j+","+rows);
                    }
                    for (int k = 0; k < rows; k++) {
                        Cell cell =  row.getCell(k);
                        if(cell==null||cell.getCellTypeEnum()!=CellType.STRING)
                            continue;
                        if(cell!=null){
                            String cvalue = getValue(cell);
//                            System.out.println("----- "+ExcelCommon.parseCellInfo(j,k)+":"+ExcelCommon.SymbolsCNtoEN(cvalue));
                            if(!StringUtils.isEmpty(cvalue)){
                                if(cvalue.startsWith("@js-")){
//                                    System.out.println(ExcelCommon.parseCellInfo(j,k)+":"+ExcelCommon.SymbolsCNtoEN(cvalue));
                                    value.put(ExcelCommon.parseCellInfo(j,k),ExcelCommon.SymbolsCNtoEN(cvalue));
                                }else if(cvalue.contains("{{name}}")){
                                    System.out.println(111);
                                    value.put(ExcelCommon.parseCellInfo(j,k),cvalue);
                                }else if(cvalue.contains("{{time}}")){
                                    System.out.println(222);
                                    value.put(ExcelCommon.parseCellInfo(j,k),cvalue);
                                }
                            }
                        }
                    }
                }
            }

            wb.close();
            inputStream.close();
            return value;
        }else{
            log.info("readReportMouldCells rr");
            wb.close();
            inputStream.close();
            return null;
        }
    }



    /**
     * kai_w_x
     * @param filePath
     * @param sheet
     * @return
     * @throws Exception
     */
    public static Map<String,String> readReportMouldCells1(String filePath , int sheet) throws Exception {
        Map<String,String> value = new LinkedHashMap<String,String>();
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);

        Workbook wb = null;
        try {
            if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
                wb = new HSSFWorkbook(inputStream);
            } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
                wb = new XSSFWorkbook(inputStream);
            }
        }catch (Exception e){
            inputStream.close();
            e.printStackTrace();
        }

        // Read the Sheet
        Sheet poiSheet = wb.getSheetAt(sheet);

        if (poiSheet != null) {
            int rows=poiSheet.getRow(0).getPhysicalNumberOfCells();
            int cols=poiSheet.getLastRowNum();//获得总行数
            // 列循环
            for (int j = 1; j < cols; j++) {
                //行循环
                int k =3;
//                for( int k = 3; k<rows;k++){
                    Row row = poiSheet.getRow(j);
                    Cell cell =  row.getCell(k);
                    if(cell!=null){
                        String cvalue = getValue(cell);
                        if(!StringUtils.isEmpty(cvalue)){
                            String isMonth = getValue (row.getCell(k-2));
                            String checkRule=  getValue (row.getCell(k+1));
                            String checkTarget=  getValue (row.getCell(k+2));
                            String projectName=getValue (row.getCell(k-1));
                            String projectTargetName=getValue (row.getCell (k+3));
                            value.put(ExcelCommon.parseCellInfo(j,k),ExcelCommon.SymbolsCNtoEN(isMonth+","+cvalue+","+checkRule+",")+checkTarget+","+projectName+","+projectTargetName);
                        }
//                    }
                }
            }

            wb.close();
            inputStream.close();
            return value;
        }else{
            wb.close();
            inputStream.close();
            return null;
        }
    }


    public static String getColMax(String filePath , int sheet,String col) throws Exception{
        String value = null;
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);

        Workbook wb = null;
        try {
            if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
                wb = new HSSFWorkbook(inputStream);
            } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
                wb = new XSSFWorkbook(inputStream);
            }
        }catch (Exception e){
            inputStream.close();
            e.printStackTrace();
        }

        // Read the Sheet
        Sheet poiSheet = wb.getSheetAt(sheet);

        Integer icol = 0;
        for(int i=0;i<col.length();i++){
            if(col.length()==1){
                icol = col.charAt(0)-'A'+1;
            }else if(col.length()==2){
                icol = (col.charAt(0)-'A'+1)*26 + col.charAt(1) -'A' +1;
            }else if(col.length()==3){
                icol = (col.charAt(0)-'A'+1)*26*26 + (col.charAt(1)-'A'+1)*26 + col.charAt(2) -'A' +1;
            }
        }

        List<Double> values = new ArrayList<>();

        if (poiSheet != null) {
            int rows=poiSheet.getRow(0).getPhysicalNumberOfCells();//获得总行数
            int cols=poiSheet.getLastRowNum();
//            log.info("row:"+rows+",col:"+cols);
            //行循环
            for (int k = 0; k <= cols; k++) {
                Row row = poiSheet.getRow(k);
                Cell cell =  row.getCell(icol-1);
//                log.info(k+","+icol+":"+getValue(cell));
                if(cell!=null){

                    if (cell.getCellTypeEnum() == CellType.NUMERIC||cell.getCellTypeEnum() == CellType.FORMULA){
                        if (!XSSFDateUtil.isCellDateFormatted(cell)) { // 纯数字
                            String cvalue = getValue(cell);
                            if(!StringUtils.isEmpty(cvalue)){
                                values.add(Double.valueOf(cvalue));
                            }
                        }
                    }
                }
            }

            wb.close();
            inputStream.close();
            return Collections.max(values).toString();
        }else{
            wb.close();
            inputStream.close();
            return null;
        }
    }

    public static String getColMaxExclude(String filePath , int sheet,String col,Set<String> exclude) throws Exception{

        String value = null;
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);

        Workbook wb = null;
        try {
            if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
                wb = new HSSFWorkbook(inputStream);
            } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
                wb = new XSSFWorkbook(inputStream);
            }
        }catch (Exception e){
            inputStream.close();
            e.printStackTrace();
        }

        // Read the Sheet
        Sheet poiSheet = wb.getSheetAt(sheet);

        int icol = ExcelCommon.charToCol(col);
        List<Double> values = new ArrayList<>();

        if (poiSheet != null) {
            int rows=poiSheet.getRow(0).getPhysicalNumberOfCells();//获得总行数
            int cols=poiSheet.getLastRowNum();
//            log.info("row:"+rows+",col:"+cols);
            //行循环
            for (int k = 0; k <= cols; k++) {
                Row row = poiSheet.getRow(k);
                Cell cell =  row.getCell(icol-1);
//                log.info(k+","+icol+":"+getValue(cell));
                int add = 1;
                for(String s:exclude){
//                    log.info("WWWWWWWWWW  "+s);
                    if(s.indexOf("≥")!=-1){
                        if(!parseExclude(s,"≥",poiSheet,row)){
                            add = 0;
                        }
                        continue;
                    }
                    else if(s.indexOf("≤")!=-1){
                        if(!parseExclude(s,"≤",poiSheet,row)){
                            add = 0;
                        }
                        continue;
                    }
                    else if(s.indexOf(">")!=-1){
                        if(!parseExclude(s,">",poiSheet,row)){
                            add = 0;
                        }
                        continue;
                    }
                    else if(s.indexOf("<")!=-1){
                        if(!parseExclude(s,"<",poiSheet,row)){
                            add = 0;
                        }
                        continue;
                    }
                    else if(s.indexOf("=")!=-1&&s.indexOf("!=")==-1){
                        if(!parseExclude(s,"=",poiSheet,row)){
                            add = 0;
                        }
                        continue;
                    }
                    else if(s.indexOf("!=")!=-1){
                        if(!parseExclude(s,"!=",poiSheet,row)){
                            add = 0;
                        }
                        continue;
                    }
                    else {
                        throw  new Exception("不取数规则错误："+s);
                    }

                }

                if(add == 1){
                    if(cell!=null){
                        if (cell.getCellTypeEnum() == CellType.NUMERIC||cell.getCellTypeEnum() == CellType.FORMULA){
                            if (!XSSFDateUtil.isCellDateFormatted(cell)) { // 纯数字
                                String cvalue = getValue(cell);
                                if(!StringUtils.isEmpty(cvalue)){
                                    values.add(Double.valueOf(cvalue));
//                                    log.info("=PPPPPPPPPP  col  not equals add  "+cvalue);
                                }
                            }
                        }
                    }
                }

            }

            wb.close();
            inputStream.close();
            return Collections.max(values).toString();
        }else{
            wb.close();
            inputStream.close();
            return null;
        }
    }

    public static ArrayList<Double> getColValues(Sheet sheet,String cell,String type)throws Exception{
        Pattern number = Pattern.compile(DataRuleUtil.NUMBER);

        int cols=sheet.getRow(0).getPhysicalNumberOfCells();
        int rows=sheet.getLastRowNum();//获得总行数

        String mcell = cell.replace(type,"");
        System.out.println("mcell:"+mcell);
        if (mcell.contains("#")){
            String maxCell = mcell.substring(0,mcell.indexOf("#"));
            String excludeRule = mcell.substring(mcell.indexOf("#")+1,mcell.length());
            List<String> excludeArr = new ArrayList<>();
            if(excludeRule.contains(",")){
                String[] arr = excludeRule.split(",");
                excludeArr = Arrays.asList(arr);
            }else{
                excludeArr.add(excludeRule);
            }

            List<List<String>> excludes = new ArrayList<>();
            for(String exclude:excludeArr){
                List<String> excludeInfo = new ArrayList<>();
                String scell = exclude.substring(exclude.indexOf("[")+1,exclude.indexOf("]"));
                Pattern rowPattern = Pattern.compile("[a-zA-Z]+");
                Pattern singlePattern = Pattern.compile("[a-zA-Z]+\\d+");
                if(rowPattern.matcher(scell).matches()||singlePattern.matcher(scell).matches()){
//                    System.out.println("scell Row :"+scell);
                    excludeInfo.add(scell);
                    excludeInfo.add(exclude.substring(exclude.indexOf("]")+1));
                }else {
                    throw new Exception(type+"<>规则错误！错误代码："+cell);
                }
                excludes.add(excludeInfo);
            }

            Pattern pattern = Pattern.compile("[a-zA-Z]+");
            Pattern rowPattern = Pattern.compile("[a-zA-Z]+");
            Pattern singlePattern = Pattern.compile("[a-zA-Z]+\\d+");
//            System.out.println("maxCell:"+maxCell);
            if(pattern.matcher(maxCell).matches()){
                List<Integer> cellInfo = ExcelCommon.parseCellInfo(maxCell+"1");
//                System.out.println("cellInfo:"+cellInfo.toString());
                if(cols<cellInfo.get(0)-1||rows<cellInfo.get(1)-1){
                    throw new Exception("单元格不存在值或超出限制："+cell+",第"+(cellInfo.get(0))+"列，第"+(cellInfo.get(1))+"行。共"+cols+"列，"+cols+"行.");
                }
                ArrayList<Double> values = new ArrayList<>();
                for(int i=0;i<rows+1;i++){
                    Row hssfRow = sheet.getRow(i);
                    Cell hssfCell =  hssfRow.getCell(cellInfo.get(0)-1);
                    if(hssfCell==null||getValue(hssfCell)==null||StringUtils.isBlank(getValue(hssfCell))){
                        continue;
                    }
                    int add = 0;
                    for(List<String>  entry:excludes){
//                        System.out.println(entry.get(0)+","+entry.get(1));
                        Cell excludeCell = null;
                        if(rowPattern.matcher(entry.get(0)).matches()){
                            List<Integer> ecellInfo = ExcelCommon.parseCellInfo(entry.get(0)+"1");
                            excludeCell = hssfRow.getCell(ecellInfo.get(0)-1);
//                            System.out.println("rowPattern:"+(ecellInfo.get(0)-1));
                        }else if(singlePattern.matcher(entry.get(0)).matches()){
//                            System.out.println("singlePattern:"+entry.get(0));
                            List<Integer> ecellInfo = ExcelCommon.parseCellInfo(entry.get(0));
                            Row eeRow = sheet.getRow(ecellInfo.get(1)-1);
                            excludeCell =  eeRow.getCell(ecellInfo.get(0)-1);
                        }

                        String evalue = null;
                        try {
                            evalue = getValue(excludeCell);
//                            System.out.println("getValue:"+excludeCell+evalue);
                        }catch (Exception e){
                            break;
                        }

                        String excludeStr = entry.get(1);
                        if(excludeStr.startsWith("!=")){
                            if(excludeStr.substring(2).equals("null")){
                                if(evalue!=null||!StringUtils.isBlank(evalue)){
                                    add = 1;
                                    continue;
                                }
                            }else if(number.matcher(evalue).matches()&&number.matcher(excludeStr.substring(1)).matches()){
                                if(Double.valueOf(evalue)!=Double.valueOf(excludeStr.substring(1))){
                                    add = 1;
                                    continue;
                                }
                            }else if(!evalue.equals(excludeStr.substring(2))){
                                add = 1;
                                continue;
                            }
                        }else if (excludeStr.startsWith("=")){
//                            System.out.println("excludeStr:"+excludeStr+evalue);
                            if(excludeStr.substring(1).equals("null")){
                                if(evalue==null||StringUtils.isBlank(evalue)){
                                    add = 1;
                                    continue;
                                }
                            }else if(evalue == null||StringUtils.isBlank(evalue)){
                                add = 1;
                                continue;
                            }else if(number.matcher(evalue).matches()&&number.matcher(excludeStr.substring(1)).matches()){
                                if(Double.valueOf(evalue)==Double.valueOf(excludeStr.substring(1))){
                                    add = 1;
                                    continue;
                                }
                            }else if(evalue.equals(excludeStr.substring(1))){
                                add = 1;
                                continue;
                            }
                        }else if(excludeStr.startsWith(">")){
                            if(number.matcher(evalue).matches()&&number.matcher(excludeStr.substring(1)).matches()){
                                if(Double.valueOf(evalue)>Double.valueOf(excludeStr.substring(1))){
                                    add = 1;
                                    continue;
                                }
                            }
                        }else if(excludeStr.startsWith("<")){
                            if(number.matcher(evalue).matches()&&number.matcher(excludeStr.substring(1)).matches()){
                                if(Double.valueOf(evalue)<Double.valueOf(excludeStr.substring(1))){
                                    add = 1;
                                    continue;
                                }
                            }
                        }else if(excludeStr.startsWith("≥")){
                            if(number.matcher(evalue).matches()&&number.matcher(excludeStr.substring(1)).matches()){
                                if(Double.valueOf(evalue)>=Double.valueOf(excludeStr.substring(1))){
                                    add = 1;
                                    continue;
                                }
                            }
                        }else if(excludeStr.startsWith("≤")){
                            if(number.matcher(evalue).matches()&&number.matcher(excludeStr.substring(1)).matches()){
                                if(Double.valueOf(evalue)<=Double.valueOf(excludeStr.substring(1))){
                                    add = 1;
                                    continue;
                                }
                            }
                        }
                    }
                    if(add==1){
                        continue;
                    }

                    String cellValue = getValue(hssfCell);
                    if(cellValue!=null&&number.matcher(cellValue).matches()){
//                        System.out.println("cellValue:"+cellValue);
                        values.add(Double.valueOf(cellValue));
                    }
                }
                return values;
            }else {
                throw new Exception(type+"<>规则错误，只能取一列的值！错误代码："+cell);
            }
        }else{
            Pattern pattern = Pattern.compile("[a-zA-Z]+");
            if(pattern.matcher(mcell).matches()){
//                            value.put(cell,getColMax(filePath,0,mcell));
                List<Integer> cellInfo = ExcelCommon.parseCellInfo(mcell+"1");
                if(cols<cellInfo.get(0)-1||rows<cellInfo.get(1)-1){
                    throw new Exception("单元格不存在值或超出限制："+cell+",第"+(cellInfo.get(0))+"列，第"+(cellInfo.get(1))+"行。共"+cols+"列，"+cols+"行.");
                }
                ArrayList<Double> values = new ArrayList<>();
                for(int i=0;i<rows+1;i++){
                    Row hssfRow = sheet.getRow(i);
                    Cell hssfCell =  hssfRow.getCell(cellInfo.get(0)-1);
                    String cellValue = getValue(hssfCell);
                    if(number.matcher(cellValue).matches()){
                        values.add(Double.valueOf(cellValue));
                    }
                }
                return values;
            }else {
                throw new Exception("max<>规则错误，只能取一列的值！错误代码："+cell);
            }
        }
    }

    public static String getColMin(Sheet sheet,String cell) throws Exception{
        try {
            return (String.valueOf(Collections.min(getColValues(sheet,cell,"min"))));
        }catch (Exception e){
            throw new Exception(e);
        }

    }


    public static String getColMax(Sheet sheet,String cell) throws Exception{
        try {
            System.out.println("getColMax:"+cell);
            return (String.valueOf(Collections.max(getColValues(sheet,cell,"max"))));
        }catch (Exception e){
            throw new Exception(e);
        }
    }

    public static Map<String,String> readXlsCells(String filePath , Set<String> cellSet,int sheet) throws Exception {
        log.info("filePath:"+filePath);

        Map<String,String> value = new HashMap<String,String>();
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);
        Workbook wb = null;
        try {
            wb = new HSSFWorkbook(inputStream);
        }catch (Exception e){
            wb.close();
            inputStream.close();
            log.info("poi excel read fail:"+e);
//            e.printStackTrace();
            throw new Exception(filePath+"文件excel版本过低导致无法读取，请另存后重新上传！");
        }
        // Read the Sheet
        Sheet hssfSheet = wb.getSheetAt(sheet);
//        log.info("1111");
        if (hssfSheet != null) {
            for(String cell:cellSet){
                if(cell.contains("max")){       //处理取最大值的情况
                    try {
                        value.put(cell,getColMax(hssfSheet,cell));
                    }catch (Exception e){
                        wb.close();
                        inputStream.close();
                        System.out.println("max"+cell);
                        System.out.println(e);
                        return null;
                    }
                }else if(cell.contains("min")){
                    try {
                        value.put(cell,getColMin(hssfSheet,cell));
                    }catch (Exception e){
                        wb.close();
                        inputStream.close();
                        System.out.println("min");
                        System.out.println(e);
                        return null;
                    }
                } else{
                    List<Integer> cellInfo = null;
//                    log.info("222");
                    cellInfo = ExcelCommon.parseCellInfo(cell);
                    if(cellInfo==null){
                        log.info(cell+"错误，不符合格式！");
                        value.put(cell,"ruleError");
                        continue;
//                        wb.close();
//                        inputStream.close();
//                        throw new Exception(cell+"取数规则错误，不符合格式！");
                    }
                    if(hssfSheet.getRow(cellInfo.get(1)-1)==null||hssfSheet.getRow(cellInfo.get(1)-1).getCell(cellInfo.get(0)-1)==null){
                        value.put(cell,"out");
                        System.out.println("poi read xls 单元格不存在值或超出限制："+cell+",第"+(cellInfo.get(0))+"列，第"+(cellInfo.get(1))+"行。");
                        continue;
                    }
//                    log.info("333");
                    Row hssfRow = hssfSheet.getRow(cellInfo.get(1)-1);
                    Cell hssfCell =  hssfRow.getCell(cellInfo.get(0)-1);
//                    log.info("444");
                    value.put(cell,getValue(hssfCell));
                }
            }
            wb.close();
            inputStream.close();
            return value;
        }else{
            wb.close();
            inputStream.close();
            return null;
        }
    }

    /**
     * Read the Excel 2010
     * @param filePath the path of the excel file
     */
    public static Map<String,String> readXlsxCells(String filePath ,Set<String> cellSet,int sheet) throws Exception {
        Map<String,String> value = new HashMap<String,String>();

        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
        // Read the Sheet
        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(sheet);

        if (xssfSheet != null) {
            for(String cell:cellSet){
                if(cell.contains("max")){       //处理取最大值的情况
                    try {
                        value.put(cell,getColMax(xssfSheet,cell));
                    }catch (Exception e){
                        xssfWorkbook.close();
                        inputStream.close();
                        System.out.println(e);
                        return null;
                    }
                }else if(cell.contains("min")){
                    try {
                        value.put(cell,getColMin(xssfSheet,cell));
                    }catch (Exception e){
                        xssfWorkbook.close();
                        inputStream.close();
                        System.out.println(e);
                        return null;
                    }
                } else {
                    List<Integer> cellInfo = null;
                    cellInfo = ExcelCommon.parseCellInfo(cell);
                    if(cellInfo==null){
                        log.info(cell+"错误，不符合格式！");
                        value.put(cell,"ruleError");
                        continue;
//                        xssfWorkbook.close();
//                        inputStream.close();
//                        throw new Exception(cell+"取数规则错误，不符合格式！");
                    }
                    if(xssfSheet.getRow(cellInfo.get(1)-1)==null||xssfSheet.getRow(cellInfo.get(1)-1).getCell(cellInfo.get(0)-1)==null){
                        value.put(cell,"out");
                        System.out.println("poi read xlsx 单元格不存在值或超出限制："+cell+",第"+(cellInfo.get(0))+"列，第"+(cellInfo.get(1))+"行。");
                        continue;
                    }
                    XSSFRow xssfRow = xssfSheet.getRow(cellInfo.get(1)-1);
                    XSSFCell xssfCell =  xssfRow.getCell(cellInfo.get(0)-1);
                    value.put(cell,getValue(xssfCell));
                }
            }
            xssfWorkbook.close();
            inputStream.close();
            return value;
        }else{
            xssfWorkbook.close();
            inputStream.close();
            return null;
        }
    }


    public static String getValue(XSSFCell xssfCell) {
        String strCell = "";
        if (xssfCell.getCellTypeEnum() == CellType.BOOLEAN) {
            return String.valueOf(xssfCell.getBooleanCellValue());
        } else if (xssfCell.getCellTypeEnum() == CellType.NUMERIC||xssfCell.getCellTypeEnum() == CellType.FORMULA){
            if (XSSFDateUtil.isCellDateFormatted(xssfCell)) {
                //  如果是date类型则 ，获取该cell的date值
                strCell = new SimpleDateFormat("yyyy-MM-dd").format(XSSFDateUtil.getJavaDate(xssfCell.getNumericCellValue()));
            } else { // 纯数字
                DecimalFormat df = new DecimalFormat("#.#######");
                strCell = df.format(xssfCell.getNumericCellValue());
//                strCell = String.valueOf(xssfRow.getNumericCellValue());
            }
            return strCell;
        } else if(xssfCell.getCellTypeEnum() == CellType.STRING){
            return String.valueOf(xssfCell.getStringCellValue());
        }else{
            return "";
        }
    }

    public static String getValue(Cell cell) {
        String strCell = "";
//        log.info(cell.getCellTypeEnum().toString());
        if(cell == null){
            return null;
        }
        if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellTypeEnum() == CellType.NUMERIC||cell.getCellTypeEnum() == CellType.FORMULA){
            if (XSSFDateUtil.isCellDateFormatted(cell)) {
                //  如果是date类型则 ，获取该cell的date值
                strCell = new SimpleDateFormat("yyyy-MM-dd").format(XSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
            } else { // 纯数字
                DecimalFormat df = new DecimalFormat("#.#######");
                strCell = df.format(cell.getNumericCellValue());
                /*BigDecimal b = new BigDecimal(strCell);
                double d = b.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
                strCell = String.valueOf (d);*/
            }
            return strCell;
        } else if(cell.getCellTypeEnum() == CellType.STRING){
            return String.valueOf(cell.getStringCellValue());
        }else{
            return "";
        }
    }

    @SuppressWarnings("static-access")
    public static String getValue(HSSFCell hssfCell) {
        String strCell = "";
        if (hssfCell.getCellTypeEnum() == CellType.BOOLEAN) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellTypeEnum() == CellType.NUMERIC||hssfCell.getCellTypeEnum() == CellType.FORMULA){
            if (XSSFDateUtil.isCellDateFormatted(hssfCell)) {
                //  如果是date类型则 ，获取该cell的date值
                strCell = new SimpleDateFormat("yyyy-MM-dd").format(XSSFDateUtil.getJavaDate(hssfCell.getNumericCellValue()));
            } else { // 纯数字
                DecimalFormat df = new DecimalFormat("0.00000");
                strCell = String.valueOf(df.format(hssfCell.getNumericCellValue()));
            }
            return strCell;
        } else if(hssfCell.getCellTypeEnum() == CellType.STRING){
            return String.valueOf(hssfCell.getStringCellValue());
        }else{
            return "";
        }
    }



    public static List<String> splitCell(String string) throws Exception {
        List<String> listSplit = new ArrayList<String>();
        Matcher matcher = Pattern.compile("[a-zA-Z]+?\\d+").matcher(string);// 用正则拆分成每个元素
        while (matcher.find()) {
            // System.out.println(matcher.group(0));
            listSplit.add(matcher.group(0));
        }
        return listSplit;
    }

    public static List<String> splitCellStr(String string) throws Exception {
        List<String> listSplit = new ArrayList<String>();
        Matcher matcher = Pattern.compile("[a-zA-Z]+?\\d+|[+\\-*/()]").matcher(string);// 用正则拆分成每个元素
        while (matcher.find()) {
            // System.out.println(matcher.group(0));
            listSplit.add(matcher.group(0));
        }
        return listSplit;
    }

    private static boolean parseExclude(String exclude,String symbols,Sheet poiSheet,Row row) throws Exception{
        boolean add = true;
        String ssingle = "^[a-zA-Z]+\\d+$";
        String scol = "^[a-zA-Z]+$";

        String[] arr = exclude.split(symbols);
        if(arr.length!=2){
            throw  new Exception("不取数规则错误："+exclude);
        }

        Pattern patternSingle = Pattern.compile(ssingle);
        Matcher singleMatcher = patternSingle.matcher(arr[0]);

        Pattern patternCol = Pattern.compile(scol);
        Matcher colMatcher = patternCol.matcher(arr[0]);

        if (singleMatcher.matches()) {      //单个单元格
            List<Integer> icell = ExcelCommon.parseCellInfo(arr[0]);
            int sicol = icell.get(0);
            int sirow = icell.get(1);
            Row vrow = poiSheet.getRow(sirow-1);
            Cell vcell =  vrow.getCell(sicol-1);
            String vvalue = null;
            if(vcell!=null){
                vvalue = getValue(vcell);
            }
            if(arr[1].equals("null")){
                if(StringUtils.isBlank(vvalue)){
                    add = false;
                }
            }else{
                if(vvalue.equals(arr[1])){
                    add = false;
                }
            }
        } else if (colMatcher.matches()) {          //一列
            int vcol = ExcelCommon.charToCol(arr[0]);
            Cell vcell =  row.getCell(vcol-1);
            String vvalue = null;
            if(vcell!=null){
                vvalue = getValue(vcell);
            }
            if(arr[1].equals("null")){
                if(StringUtils.isBlank(vvalue)){
                    add = false;
                }
            }else{
                if(vvalue.equals(arr[1])){
                    add = false;
                }
            }
        }
        return add;
    }

    public static List<DataJZD> readJZD(int bankId,String time, String filePath)throws Exception{
        List<DataJZD> jzdValue = new ArrayList<>();
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);

        Workbook wb = null;
        try {
            if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
                wb = new HSSFWorkbook(inputStream);
            } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
                wb = new XSSFWorkbook(inputStream);
            }
        }catch (Exception e){
            inputStream.close();
            e.printStackTrace();
        }

        Sheet poiSheet = wb.getSheetAt(0);

        if (poiSheet != null) {
            int cols=poiSheet.getLastRowNum();//获得总行数

            int num = 0;
            for (int j = 6; j < cols; j++) {
                Row row = poiSheet.getRow(j);
                String khmc = getValue(row.getCell(3));
//                System.out.println("khmc:"+khmc);
                if(khmc==null||StringUtils.isBlank(khmc)||khmc.equals("中国人民银行")){
                    continue;
                }else{
                    if(num>=10)
                        break;
                    DataJZD dataJZD = new DataJZD();
                    dataJZD.setBankId((long) bankId);
                    dataJZD.setTime(time);
                    dataJZD.setCustomeName(khmc);
                    dataJZD.setCustomerType(getValue(row.getCell(2)));
                    dataJZD.setFxzh(getValue(row.getCell(5)));
                    dataJZD.setYjzbjeb(getValue(row.getCell(6)));
                    jzdValue.add(dataJZD);
                    num++;
                }
            }
            wb.close();
            inputStream.close();
            return jzdValue;
        }else{
            wb.close();
            inputStream.close();
            return null;
        }
    }

    public static List<QnyjYuqi90East> readYuqi90(String filePath) throws Exception{
        List<QnyjYuqi90East> yuqi90Value = new ArrayList<>();
        Date date = DateUtils.getCurrentTime();
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);
        Workbook wb = null;
        try {
            if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
                wb = new HSSFWorkbook(inputStream);
            } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
                wb = new XSSFWorkbook(inputStream);
            }
        }catch (Exception e){
            inputStream.close();
            e.printStackTrace();
        }
        Sheet poiSheet = wb.getSheetAt(0);
        if (poiSheet != null) {
            int cols=poiSheet.getLastRowNum();//获得总行数
            int num = 0;
            for (int j = 1; j < cols; j++) {
                Row row = poiSheet.getRow(j);
                String bankName = getValue(row.getCell(0));
                String values = getValue(row.getCell(1));
                QnyjYuqi90East qnyjYuqi90East = new QnyjYuqi90East();
                qnyjYuqi90East.setBankname(bankName);
                qnyjYuqi90East.setDatavalue(values);
                qnyjYuqi90East.setParam(DateUtils.format(date,DateUtils.FULL_ST_FORMAT));
                qnyjYuqi90East.setDatatime(getValue(poiSheet.getRow(0).getCell(1)));
                yuqi90Value.add(qnyjYuqi90East);
            }
            wb.close();
            inputStream.close();
            return yuqi90Value;
        }else{
            wb.close();
            inputStream.close();
            return null;
        }
    }

    public static CheckRuleTable readExcelToCheckRuleTable(String filePath) throws Exception{
        log.info("readExcelToCheckRuleTable");
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);
        CheckRuleTable checkRuleTable = new CheckRuleTable();
        String fileName = filePath.substring(filePath.lastIndexOf("\\")+1,filePath.lastIndexOf("."));
        checkRuleTable.setRuleName(fileName);
        Workbook wb = null;
        try {
            if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
                wb = new HSSFWorkbook(inputStream);
            } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
                wb = new XSSFWorkbook(inputStream);
            }
        }catch (Exception e){
            inputStream.close();
            e.printStackTrace();
        }
        log.info("readExcelToCheckRuleTable  111");
        Sheet poiSheet = wb.getSheetAt(0);

        if (poiSheet != null) {
            int cols=poiSheet.getLastRowNum();//获得总行数
            if(getValue(poiSheet.getRow(0).getCell(0)).equals("序号")&&getValue(poiSheet.getRow(0).getCell(1)).equals("类型")&&
                    getValue(poiSheet.getRow(0).getCell(3)).equals("机构")&&
                    getValue(poiSheet.getRow(0).getCell(4)).equals("科目名称")&&getValue(poiSheet.getRow(0).getCell(5)).equals("表格")&&
                    getValue(poiSheet.getRow(0).getCell(6)).equals("单元格")&&getValue(poiSheet.getRow(0).getCell(7)).equals("项目名称")&&
                    getValue(poiSheet.getRow(0).getCell(8)).equals("校验关系")&&getValue(poiSheet.getRow(0).getCell(9)).equals("目标表")&&
                    getValue(poiSheet.getRow(0).getCell(10)).equals("目标单元格")&&getValue(poiSheet.getRow(0).getCell(11)).equals("目标名称")&&
                    getValue(poiSheet.getRow(0).getCell(12)).equals("精度")&&getValue(poiSheet.getRow(0).getCell(13)).equals("校验信息")&&
                    getValue(poiSheet.getRow(0).getCell(14)).equals("失败提示")&&getValue(poiSheet.getRow(0).getCell(15)).equals("成功提示"))
            {
                List<CheckRuleInfo> checkRuleInfos = new ArrayList<>();
                for (int i = 1; i < cols; i++) {
                    Row row = poiSheet.getRow(i);
                    CheckRuleInfo checkRuleInfo = new CheckRuleInfo();
                    checkRuleInfo.setType(getValue(row.getCell(1)));
                    checkRuleInfo.setTimes(getValue(row.getCell(2)));
                    checkRuleInfo.setBanks(getValue(row.getCell(3)));
                    checkRuleInfo.setSubjectName(getValue(row.getCell(4)));
                    checkRuleInfo.setSourceTable(DataRuleUtil.symbolNormalization(getValue(row.getCell(5))));
                    checkRuleInfo.setSource(DataRuleUtil.symbolNormalization(getValue(row.getCell(6))));
                    checkRuleInfo.setSourceName(getValue(row.getCell(7)));
                    checkRuleInfo.setRule(getValue(row.getCell(8)));
                    checkRuleInfo.setTarget(DataRuleUtil.symbolNormalization(getValue(row.getCell(10))));
                    checkRuleInfo.setTargetName(getValue(row.getCell(11)));
                    checkRuleInfo.setAccuracy(getValue(row.getCell(12)));
                    checkRuleInfo.setNote(getValue(row.getCell(13)));
                    checkRuleInfo.setSuccessMessage(getValue(row.getCell(14)));
                    checkRuleInfo.setFailMessage(getValue(row.getCell(15)));
                    checkRuleInfos.add(checkRuleInfo);
                }
                checkRuleTable.setCheckRuleInfoList(checkRuleInfos);
//                for(CheckRuleInfo check:checkRuleInfos){
//                    System.out.println(check.getTimes()+"\t"+check.getBanks()+"\t"+check.getSourceTable()+"\t"+check.getSource()+"\t"+check.getSourceName()+"\t"+check.getRule()+"\t"+
//                            check.getTarget()+"\t"+check.getTargetName()+"\t"+check.getAccuracy()+"\t"+check.getNote()+"\t"+check.getSuccessMessage()+"\t"+check.getFailMessage());
//                }
            }else{
                log.info("上传文件不符合审表模板规范！");
                throw new Exception("上传文件不符合审表模板规范！");
            }
            wb.close();
            inputStream.close();
            return checkRuleTable;
        }else{
            wb.close();
            inputStream.close();
            return null;
        }
    }
}
