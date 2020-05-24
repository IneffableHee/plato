package com.cbrc.plato.util.excel;

import com.cbrc.plato.util.datarule.checktable.CheckRuleInfo;
import com.cbrc.plato.util.datarule.checktable.CheckRuleTable;
import com.cbrc.plato.util.datarule.datainfo.DataJZD;
import com.cbrc.plato.util.datarule.model.QnyjYuqi90East;
import com.cbrc.plato.util.datarule.report.DataRuleUtil;
import com.cbrc.plato.util.fileutil.FileUtil;
import com.cbrc.plato.util.time.DateUtils;
import com.cbrc.plato.util.utilpo.Common;
import jxl.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
public class JXLExcelRead {

    public static Map<String,String> readExcelCells(String filePath, Set<String> cellSet, int sheet) throws Exception {
        if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            return  readXlsCells(filePath ,cellSet,0);
        } else {
            return null;
        }
    }

    public static Map<String,String> readReportMouldCells(String filePath, int sheet) throws Exception {
        if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            Map<String,String> reports = new LinkedHashMap<String,String>();
            File file = new File(filePath);
            // 读取xls文件流
            InputStream is = new FileInputStream(file.getAbsolutePath());
            Workbook wb = null;
            try {
                wb = Workbook.getWorkbook(is);
            }catch (Exception e){
                is.close();
                e.printStackTrace();
            }

            Sheet jxlSheet = wb.getSheet(sheet);
            if(jxlSheet == null)
                return null;

            int cols = jxlSheet.getColumns();
            int rows = jxlSheet.getRows();

            // 列循环
            for (int j = 0; j < cols; j++) {
                //行循环
                for (int k = 0; k < rows; k++) {
                    Cell excelCell = jxlSheet.getCell(j, k);
                    String cellValue = getValue(excelCell);
                    if(cellValue.startsWith("@js-")){
                        reports.put(ExcelCommon.parseCellInfo(k,j),ExcelCommon.SymbolsCNtoEN(cellValue));
                    }
                }
            }

            wb.close();
            is.close();
            return reports;
        } else {
            return null;
        }
    }

    public static String getColMax(String filePath , int sheet,String col) throws  Exception{
        if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(FileUtil.getPostfix(filePath).trim())) {
            Map<String,String> reports = new LinkedHashMap<String,String>();
            File file = new File(filePath);
            // 读取xls文件流
            InputStream is = new FileInputStream(file.getAbsolutePath());
            Workbook wb = null;
            try {
                wb = Workbook.getWorkbook(is);
            }catch (Exception e){
                is.close();
                e.printStackTrace();
            }

            Sheet jxlSheet = wb.getSheet(sheet);
            if(jxlSheet == null)
                return null;

            int rows = jxlSheet.getRows();

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

            //行循环
            for (int k = 0; k < rows; k++) {
                Cell excelCell = jxlSheet.getCell(icol-1, k);
                if(excelCell.getType() == CellType.NUMBER){
                    values.add(Double.valueOf(excelCell.getContents()));
                }
            }

            wb.close();
            is.close();
            return Collections.max(values).toString();
        } else {
            return null;
        }
    }

    public static void readExcel(String path)throws Exception{
        int lie = 0;
        String retur = null;
        System.out.println("项目路径" + System.getProperty("user.dir"));
        File file = new File("D:/fileManager/fileUpload/excle/file/黔南市全金融机构合计/2019-01-31/黔南市全金融机构合计_GF01 资产负债项目统计表{18年启用}_境内汇总数据_2019-01-31_人民币.xls");
        System.out.println("文件路径：" + file + "\n" + "文件绝对路径：" + file.getAbsolutePath());
        // 读取xls文件流
        InputStream is = new FileInputStream(file.getAbsolutePath());
        // 使用jxl
        Workbook rwb = Workbook.getWorkbook(is);
        // 获取当前excel中共有几个表
        Sheet[] sheets = rwb.getSheets();
        // 获取表数
        int pages = sheets.length;
        System.out.println("表数：" + pages);
        for (int i = 0; i < pages; i++) {
            Sheet sheet = sheets[i];
            // 有多少列
            int cols = sheet.getColumns();
            System.out.println("有" + cols + "列");
            // 有多少行
            int rows = sheet.getRows();
            System.out.println("有" + rows + "行");
            // 列循环
            for (int j = 0; j < cols; j++) {
                //行循环
                for(int k = 0 ; k <rows ; k++){
                    //定位坐标，从（0，0开始）
                    Cell excelRows = sheet.getCell(j, k);
                    //取坐标值
                    String e = excelRows.getContents();
                    System.out.print(e+"\t");
                    //如果找到相应的交易编码，保存所在列值
//                    if(code.equals(e)){
//                        lie = j;
//                    }
                }
                System.out.println();
            }
            //取出对应交易编码的头部信息并返回
            retur = sheet.getCell(lie,0).getContents();
            if(retur==null){
                System.out.println("-----------在对应协议模板中未找到相对应的业务-----------");
            }
        }
    }

    public static Map<String,String> readXlsCells(String filePath , Set<String> cellSet,int sheet) throws Exception {
        Map<String,String> value = new LinkedHashMap<String,String>();
        File file = new File(filePath);
        // 读取xls文件流
        InputStream is = new FileInputStream(file.getAbsolutePath());
        Workbook wb = null;
        try {
            wb = Workbook.getWorkbook(is);
        }catch (Exception e){
            is.close();
            log.info(e.getMessage());
            throw new Exception(e.getMessage());
        }
        Sheet jxlSheet = wb.getSheet(sheet);
        if(jxlSheet == null)
            return null;

        int cols = jxlSheet.getColumns();
        int rows = jxlSheet.getRows();

        for(String cell:cellSet){
            if(cell.contains("max")){       //处理取最大值的情况
                try {
                    value.put(cell,getColMax(jxlSheet,cell));
                }catch (Exception e){
                    wb.close();
                    is.close();
                    System.out.println(e);
                    return null;
                }
            }else if(cell.contains("min")){
                try {
                    value.put(cell,getColMin(jxlSheet,cell));
                }catch (Exception e){
                    wb.close();
                    is.close();
                    System.out.println(e);
                    return null;
                }
            } else{
                List<Integer> cellInfo = null;
                cellInfo = ExcelCommon.parseCellInfo(cell);
                if(cellInfo==null){
                    log.info(cell+"错误，不符合格式！");
                    value.put(cell,"ruleError");
                    continue;
//                    wb.close();
//                    is.close();
//                    throw new Exception(cell+"取数规则错误，不符合格式！");
                }
//            log.info("cell:"+cell+","+cellInfo.get(1)+","+cellInfo.get(0));
                if(cols<cellInfo.get(0)||rows<cellInfo.get(1)){
                    value.put(cell,"out");
                    System.out.println("单元格不存在值或超出限制："+cell+",第"+(cellInfo.get(0))+"列，第"+(cellInfo.get(1))+"行。共"+cols+"列，"+cols+"行.");
                    continue;
                }
                Cell jxlCell = jxlSheet.getCell(cellInfo.get(0)-1, cellInfo.get(1)-1);
                value.put(cell,getValue(jxlCell));
            }
        }
        wb.close();
        is.close();
        return value;
    }

    public static String getValue(Cell cell) {
        if(cell == null)
            return null;
        String cellValue = "";
        if (cell.getType() == CellType.DATE) {
            DateCell dc = (DateCell) cell;
            Date data = dc.getDate();
            SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
            cellValue = dataFormat.format(data);
        }else if(cell.getType().equals("Number")||cell.getType() == CellType.NUMBER ){
            NumberCell ncell = (NumberCell) cell;
//            ncell.getValue();
//            String value = cell.getContents();
//            System.out.println("Number:"+ncell.getValue());
//            if(value.contains("%")){
//                value = value.replaceAll("%", "");
//                Float f = Float.valueOf(value);
//                value = String.valueOf(f/100);
//            }else {
//                NumberCell numberCell = (NumberCell) cell;
//                Double numberValue = numberCell.getValue ();
//                value = numberValue.toString ();
//            }
//            DecimalFormat df = new DecimalFormat("0.000000");
            DecimalFormat df = new DecimalFormat("#.#######");
            cellValue = df.format(ncell.getValue());
//            cellValue = String.valueOf(ncell.getValue());

        } else {
            cellValue = cell.getContents();
        }
        return cellValue;
    }

    public static String getColMin(Sheet sheet, String cell) throws Exception{
        try {
            return (String.valueOf(Collections.min(getColValues(sheet,cell,"min"))));
        }catch (Exception e){
            throw new Exception(e);
        }

    }

    public static String getColMax(Sheet sheet, String cell) throws Exception{
        try {
            return (String.valueOf(Collections.max(getColValues(sheet,cell,"max"))));
        }catch (Exception e){
            throw new Exception(e);
        }
    }

    public static ArrayList<Double> getColValues(Sheet sheet, String cell, String type)throws Exception{
        Pattern number = Pattern.compile(DataRuleUtil.NUMBER);

        int cols = sheet.getColumns();
        int rows = sheet.getRows();

        String mcell = cell.replace(type,"");
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
            if(pattern.matcher(maxCell).matches()){
                List<Integer> cellInfo = ExcelCommon.parseCellInfo(maxCell+"1");
//                System.out.println(maxCell+"1:"+cellInfo.get(0)+","+cellInfo.get(1));
                if(cols<cellInfo.get(0)-1||rows<cellInfo.get(1)-1){
                    throw new Exception("单元格不存在值或超出限制："+cell+",第"+(cellInfo.get(0))+"列，第"+(cellInfo.get(1))+"行。共"+cols+"列，"+cols+"行.");
                }
                ArrayList<Double> values = new ArrayList<>();
                for(int i=0;i<rows;i++){
                    Cell jxlCell = sheet.getCell(cellInfo.get(0)-1,i);

                    int add = 0;
                    for(List<String>  entry:excludes){
                        Cell excludeCell = null;
                        if(rowPattern.matcher(entry.get(0)).matches()){
                            List<Integer> ecellInfo = ExcelCommon.parseCellInfo(entry.get(0)+"1");
                            try {
                                excludeCell = sheet.getCell(ecellInfo.get(0)-1,i);
                            }catch (Exception e){
                                continue;
                            }

                        }else if(singlePattern.matcher(entry.get(0)).matches()){
                            List<Integer> ecellInfo = ExcelCommon.parseCellInfo(entry.get(0));
                            try {
                                excludeCell = sheet.getCell(ecellInfo.get(0)-1,cellInfo.get(0)-1);
                            }catch (Exception e){
                                continue;
                            }
                        }

                        String evalue = null;
                        try {
                            evalue = getValue(excludeCell);
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
                            if(excludeStr.substring(1).equals("null")){
                                evalue = evalue.replace(" ","");
                                if(evalue==null||StringUtils.isBlank(evalue)){
                                    add = 1;
                                    continue;
                                }
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

                    String cellValue = getValue(jxlCell);
                    if(number.matcher(cellValue).matches()){
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
                for(int i=0;i<rows;i++){
                    Cell jxlCell = sheet.getCell(cellInfo.get(0)-1,i);
                    String cellValue = getValue(jxlCell);
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

    public static List<DataJZD> readJZD(int bankId,String time,String filePath) throws Exception{
        List<DataJZD> jzdValue = new ArrayList<>();
        File file = new File(filePath);
        // 读取xls文件流
        InputStream is = new FileInputStream(file.getAbsolutePath());
        Workbook wb = null;
        try {
            wb = Workbook.getWorkbook(is);
        }catch (Exception e){
            is.close();
            e.printStackTrace();
        }

        Sheet jxlSheet = wb.getSheet(0);
        if(jxlSheet == null)
            return null;

        int num =0;
        int rows = jxlSheet.getRows();
        for (int k = 6; k < rows; k++) {
            String khmc = getValue(jxlSheet.getCell(3,k));
            if(khmc==null||StringUtils.isBlank(khmc)||khmc.equals("中国人民银行")){
                continue;
            }else{
                if(num>=10)
                    break;
                DataJZD dataJZD = new DataJZD();
                dataJZD.setBankId((long) bankId);
                dataJZD.setTime(time);
                dataJZD.setCustomeName(khmc);
                dataJZD.setCustomerType(getValue(jxlSheet.getCell(2,k)));
                dataJZD.setFxzh(getValue(jxlSheet.getCell(5,k)));
                dataJZD.setYjzbjeb(getValue(jxlSheet.getCell(6,k)));
                jzdValue.add(dataJZD);
                num++;
//                String customerType = getValue(jxlSheet.getCell(2,k));
//                String fxzh = getValue(jxlSheet.getCell(5,k));
//                String yjzbjeb = getValue(jxlSheet.getCell(6,k));
//                System.out.println(khmc+"\t"+customerType+"\t"+fxzh+"\t"+yjzbjeb);
            }
        }
        wb.close();
        is.close();
        return jzdValue;
    }

    public static List<QnyjYuqi90East> readYuqi90(String filePath) throws Exception{
        List<QnyjYuqi90East> yuqi90Value = new ArrayList<QnyjYuqi90East>();
        Date date = DateUtils.getCurrentTime();
        File file = new File(filePath);
        // 读取xls文件流
        InputStream is = new FileInputStream(file.getAbsolutePath());
        Workbook wb = null;
        try {
            wb = Workbook.getWorkbook(is);
        }catch (Exception e){
            is.close();
            e.printStackTrace();
        }
        Sheet jxlSheet = wb.getSheet(0);
        if(jxlSheet == null)
            return null;
        int num =0;
        int rows = jxlSheet.getRows();
        for (int k = 1; k < rows; k++) {
            String bankName = getValue(jxlSheet.getCell(0,k));
            String values = getValue(jxlSheet.getCell(1,k));
            QnyjYuqi90East qnyjYuqi90East = new QnyjYuqi90East();
            qnyjYuqi90East.setBankname(bankName);
            qnyjYuqi90East.setDatavalue(values);
            qnyjYuqi90East.setParam(DateUtils.format(date,DateUtils.FULL_ST_FORMAT));
            qnyjYuqi90East.setDatatime(getValue(jxlSheet.getCell(1,0)));
            yuqi90Value.add(qnyjYuqi90East);
        }
        wb.close();
        is.close();
        return yuqi90Value;
    }

    public static CheckRuleTable readExcelToCheckRuleTable(String filePath) throws Exception{
        CheckRuleTable checkRuleTable = new CheckRuleTable();
        String fileName = filePath.substring(filePath.lastIndexOf("\\")+1,filePath.lastIndexOf("."));
        checkRuleTable.setRuleName(fileName);

        File file = new File(filePath);
        InputStream is = new FileInputStream(file.getAbsolutePath());
        Workbook wb = null;
        try {
            wb = Workbook.getWorkbook(is);
        }catch (Exception e){
            is.close();
            e.printStackTrace();
        }

        Sheet sheet = wb.getSheet(0);
        if(sheet == null)
            return null;

        int rows = sheet.getRows();
        if(getValue(sheet.getCell(0,0)).equals("序号")&&getValue(sheet.getCell(1,0)).equals("类型")&&
                getValue(sheet.getCell(3,0)).equals("机构")&&
                getValue(sheet.getCell(4,0)).equals("科目名称")&&getValue(sheet.getCell(5,0)).equals("表格")&&
                getValue(sheet.getCell(6,0)).equals("单元格")&&getValue(sheet.getCell(7,0)).equals("项目名称")&&
                getValue(sheet.getCell(8,0)).equals("校验关系")&&getValue(sheet.getCell(9,0)).equals("目标表")&&
                getValue(sheet.getCell(10,0)).equals("目标单元格")&&getValue(sheet.getCell(11,0)).equals("目标名称")&&
                getValue(sheet.getCell(12,0)).equals("精度")&&getValue(sheet.getCell(13,0)).equals("校验信息")&&
                getValue(sheet.getCell(14,0)).equals("失败提示")&&getValue(sheet.getCell(15,0)).equals("成功提示"))
        {
            List<CheckRuleInfo> checkRuleInfos = new ArrayList<>();
            for (int j = 0; j < rows; j++) {
                CheckRuleInfo checkRuleInfo = new CheckRuleInfo();
                checkRuleInfo.setType(getValue(sheet.getCell(1,j)));
                checkRuleInfo.setTimes(getValue(sheet.getCell(2,j)));
                checkRuleInfo.setBanks(getValue(sheet.getCell(3,j)));
                checkRuleInfo.setSubjectName(getValue(sheet.getCell(4,j)));
                checkRuleInfo.setSourceTable(getValue(sheet.getCell(5,j)));
                checkRuleInfo.setSource(getValue(sheet.getCell(6,j)));
                checkRuleInfo.setSourceName(getValue(sheet.getCell(7,j)));
                checkRuleInfo.setRule(getValue(sheet.getCell(8,j)));
                checkRuleInfo.setTarget(getValue(sheet.getCell(9,j)));
                checkRuleInfo.setTargetName(getValue(sheet.getCell(10,j)));
                checkRuleInfo.setAccuracy(getValue(sheet.getCell(11,j)));
                checkRuleInfo.setNote(getValue(sheet.getCell(12,j)));
                checkRuleInfo.setSuccessMessage(getValue(sheet.getCell(13,j)));
                checkRuleInfo.setFailMessage(getValue(sheet.getCell(14,j)));
                checkRuleInfos.add(checkRuleInfo);
            }
            checkRuleTable.setCheckRuleInfoList(checkRuleInfos);
            wb.close();
            is.close();
            return checkRuleTable;
        }else{
            wb.close();
            is.close();
            throw new Exception("上传文件不符合审表模板规范！");
        }
    }
}
