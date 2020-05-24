package com.cbrc.plato.core.basic.service.impl;

import com.cbrc.plato.core.basic.dao.ReportMouldMapper;
import com.cbrc.plato.core.basic.model.CellOperation;
import com.cbrc.plato.core.basic.model.PlatoExcelFile;
import com.cbrc.plato.core.basic.model.ReportCell;
import com.cbrc.plato.core.basic.model.ReportMould;
import com.cbrc.plato.core.basic.service.IExcelFileService;
import com.cbrc.plato.core.basic.service.IListCheckMouldService;
import com.cbrc.plato.util.excel.ExcelCommon;
import com.cbrc.plato.util.excel.ExcelRead;
import com.cbrc.plato.util.excel.XLSX2XLS;
import com.cbrc.plato.util.math.MathUtil;
import com.cbrc.plato.util.time.DateUtils;
import com.cbrc.plato.util.utilpo.Common;
import com.cbrc.plato.util.uuid.UuidUtil;
import com.github.pagehelper.util.StringUtil;
import gui.ava.html.Html2Image;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ListCheckMouldService implements IListCheckMouldService {
    @Autowired
    ReportMouldMapper reportMouldMapper;

    @Autowired
    IExcelFileService excelFileService;

    @Override
    public ReportMould getById(int id) {
        return this.reportMouldMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<ReportMould> getAll() {
        return this.reportMouldMapper.selectAll();
    }

    String math = "[+\\-*/]";              //匹配加减乘除运算
    String single = "^[a-zA-Z]+\\d+?.*?\\[[a-zA-Z]+\\d+\\]$";       //匹配单个单元格
//    String math = "[+\\-*/]";              //匹配加减乘除运算
//    String single = "^[a-zA-Z]+\\d+?.*?\\[.*?\\]$";       //匹配单个单元格
    String tableCell = "[a-zA-Z]+\\d+?.*?\\[.*?\\]";
    String operation = "avgy<.*?>|hbzzl<.*?>|znxs|max<.*?>|nc<.*?>|sy<.*?>|inc<.*?>";

    @Override
    public Set<String> getAllTableCellSet(List<ReportCell> reportCells) {

        Set<String> cellSet = new HashSet<>();
        for(ReportCell reportCell:reportCells){
            log.info("@@@@@@"+reportCell.getDataRule());
            String dataSource = reportCell.getDataRule();
            Set<String> tableCells = parseTableCell(dataSource);
            cellSet.addAll(tableCells);
        }
        return cellSet;
    }

    @Override
    public Set<String> getMonthTableCellSet(List<ReportCell> reportCells) {

        Set<String> cellList = new HashSet<>();
        for(ReportCell reportCell:reportCells){
            if(reportCell.isMonth() == 1){
                //            log.info("@@@@@@"+dataInfo.getDataSource());
                String dataSource = reportCell.getDataRule();
                Matcher vmatcher = Pattern.compile(operation).matcher(dataSource);// 不取avgy、hbzzl等期次运算
                while (vmatcher.find()) {
                    dataSource = dataSource.replace(vmatcher.group(0),"");
                }
                Pattern patternMath = Pattern.compile(math);
                Matcher mathMatcher = patternMath.matcher(dataSource);

                Pattern patternSingle = Pattern.compile(single);
                Matcher singletMatcher = patternSingle.matcher(dataSource);

                if (mathMatcher.find()) {            //数学运算
                    List<String> cell = new ArrayList<>();
                    try {
                        cell = ExcelCommon.splitTableCell(dataSource);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cellList.addAll(cell);
                } else if (singletMatcher.find()) {         //单个单元格取值
                    cellList.add(dataSource);
                }
            }
        }
        return cellList;
    }

    @Override
    public Set<String> getAllTableSet(List<ReportCell> reportCells) {
        Set<String> cellList = new HashSet<>();
        for(ReportCell reportCell:reportCells){
//            log.info("@@@@@@"+dataInfo.getDataSource());
            String dataSource = reportCell.getDataRule();
            Matcher vmatcher = Pattern.compile(operation).matcher(dataSource);// 不取avgy、hbzzl等期次运算
            while (vmatcher.find()) {
                dataSource = dataSource.replace(vmatcher.group(0),"");
            }
            Pattern patternMath = Pattern.compile(math);
            Matcher mathMatcher = patternMath.matcher(dataSource);
            Pattern patternSingle = Pattern.compile(single);
            Matcher singletMatcher = patternSingle.matcher(dataSource);

            if (mathMatcher.find()) {            //数学运算
                List<String> cell = new ArrayList<>();
                try {
                    cell = ExcelCommon.splitTableCell(dataSource);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cellList.addAll(cell);
            } else if (singletMatcher.find()) {         //单个单元格取值
                cellList.add(dataSource);
            }

            String str = reportCell.getExcludeRule();
            if(StringUtil.isNotEmpty(str)){
                Matcher matcher = Pattern.compile(tableCell).matcher(str);// 用正则拆分成每个元素
                while (matcher.find()) {
                    cellList.add(matcher.group(0));
                }
            }
        }

        Set<String> tableList = new HashSet<>();
        for(String cell:cellList){
            tableList.add(cell.substring(0, cell.indexOf("[")));
        }
        return tableList;
    }

    @Override
    public Set<String> getMonthTableSet(List<ReportCell> reportCells) {

        Set<String> cellList = new HashSet<>();
        for(ReportCell reportCell:reportCells){
            if(reportCell.isMonth() == 1){

                String dataSource = reportCell.getDataRule();
                Matcher vmatcher = Pattern.compile(operation).matcher(dataSource);// 不取avgy、hbzzl等期次运算
                while (vmatcher.find()) {
                    dataSource = dataSource.replace(vmatcher.group(0),"");
                }
                Pattern patternMath = Pattern.compile(math);
                Matcher mathMatcher = patternMath.matcher(dataSource);

                Pattern patternSingle = Pattern.compile(single);
                Matcher singletMatcher = patternSingle.matcher(dataSource);

                if (mathMatcher.find()) {            //数学运算
                    List<String> cell = new ArrayList<>();
                    try {
                        cell = ExcelCommon.splitTableCell(dataSource);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cellList.addAll(cell);
                } else if (singletMatcher.find()) {         //单个单元格取值
                    cellList.add(dataSource);
                }

                String str = reportCell.getExcludeRule();
                if(StringUtil.isNotEmpty(str)){
                    Matcher matcher = Pattern.compile(tableCell).matcher(str);// 用正则拆分成每个元素
                    while (matcher.find()) {
                        cellList.add(matcher.group(0));
                    }
                }
            }
        }

        Set<String> tableList = new HashSet<>();
        for(String cell:cellList){
            tableList.add(cell.substring(0, cell.indexOf("[")));
        }
        return tableList;
    }

    @Override
    public List<CellOperation> getAllOperation(List<ReportCell> reportCells) {
        List<CellOperation> cellOperations= new ArrayList<>();
        Map<String,String> operationMap = new HashMap<>();
        for(ReportCell reportCell:reportCells) {
            String dataSource = reportCell.getDataRule();
            Matcher omatcher = Pattern.compile(operation).matcher(dataSource);// 不取avgy、hbzzl等期次运算
            while (omatcher.find()) {
                String operation = omatcher.group(0);
                String excludeRule = reportCell.getExcludeRule();
                if(excludeRule!=null){
                    log.info("GGGGGGGG operation:"+operation+",excludeRule:"+excludeRule);
                }
                if(operationMap == null||!operationMap.containsKey(operation)){
                    operationMap.put(operation,excludeRule);
                    CellOperation cellOperation= new CellOperation();
                    cellOperation.setOperation(operation);
                    cellOperation.setExcludeRule(excludeRule);
                    cellOperation.setMonth (reportCell.isMonth ());
                    cellOperations.add(cellOperation);
                }else if(operationMap.containsKey(operation)){
                    if(operationMap.get(operation)==null&&excludeRule==null){
                        continue;
                    }else if(operationMap.get(operation)==null||excludeRule==null){
                        operationMap.put(operation,excludeRule);
                        CellOperation cellOperation= new CellOperation();
                        cellOperation.setOperation(operation);
                        cellOperation.setExcludeRule(excludeRule);
                        cellOperation.setMonth (reportCell.isMonth ());
                        cellOperations.add(cellOperation);
                    }else if(!operationMap.get(operation).equals(excludeRule)){
                        operationMap.put(operation,excludeRule);
                        CellOperation cellOperation= new CellOperation();
                        cellOperation.setOperation(operation);
                        cellOperation.setExcludeRule(excludeRule);
                        cellOperation.setMonth (reportCell.isMonth ());
                        cellOperations.add(cellOperation);
                    }
                }
            }
        }
        return cellOperations;
    }

    @Override
    public List<CellOperation> getMonthOperation(List<ReportCell> reportCells) {
        List<CellOperation> cellOperations= new ArrayList<>();
        Map<String,String> operationMap = new HashMap<>();
        for(ReportCell reportCell:reportCells) {
            if (reportCell.isMonth() == 1) {
                String dataSource = reportCell.getDataRule();
                Matcher omatcher = Pattern.compile(operation).matcher(dataSource);// 不取avgy、hbzzl等期次运算
                while (omatcher.find()) {
                    String operation = omatcher.group(0);
                    String excludeRule = reportCell.getExcludeRule();
                    if(operationMap == null||!operationMap.containsKey(operation)){
                        operationMap.put(operation,excludeRule);
                        CellOperation cellOperation= new CellOperation();
                        cellOperation.setOperation(operation);
                        cellOperation.setExcludeRule(excludeRule);
                        cellOperations.add(cellOperation);
                    }else if(operationMap.containsKey(operation)){
                        if(!operationMap.get(operation).equals(excludeRule)){
                            operationMap.put(operation,excludeRule);
                            CellOperation cellOperation= new CellOperation();
                            cellOperation.setOperation(operation);
                            cellOperation.setExcludeRule(excludeRule);
                            cellOperations.add(cellOperation);
                        }
                    }
                }
            }
        }
        return cellOperations;
    }

    @Override
    public Set<String> getMathOperationCells(String operation) {
        Set<String> cellList = new HashSet<>();
        if(operation.startsWith("hbzzl<")){
            operation = operation.replace("hbzzl<","");
            operation = operation.replace(">","");
        }else if(operation.startsWith("avgy<")){
            operation = operation.replace("avgy<","");
            operation = operation.replace(">","");
        }else if(operation.startsWith("max<")){
            operation = operation.replace("max<","");
            operation = operation.replace(">","");
        }else if(operation.startsWith("nc<")){
            operation = operation.replace("nc<","");
            operation = operation.replace(">","");
        }else if(operation.startsWith("sy<")){
            operation = operation.replace("sy<","");
            operation = operation.replace(">","");
        }else if(operation.startsWith("inc<")){
            operation = operation.replace("inc<","");
            operation = operation.replace(">","");
        }else{
            return null;
        }
        Matcher matcher = Pattern.compile(tableCell).matcher(operation);// 用正则拆分成每个元素
        while (matcher.find()) {
            cellList.add(matcher.group(0));
        }
        return cellList;
    }

    private String excel2html(String path){
        String content = null;
        try{
            InputStream input = new FileInputStream(path);
            HSSFWorkbook excelBook = new HSSFWorkbook();
            //判断Excel文件将07+版本转换为03版本
            if(path.endsWith(".xls")){  //Excel 2003
                excelBook = new HSSFWorkbook(input);
            }
            else if(path.endsWith(".xlsx")){  // Excel 2007/2010
                XLSX2XLS xlsx2XLS = new XLSX2XLS();
                XSSFWorkbook workbookOld = new XSSFWorkbook(input);
                xlsx2XLS.transformXSSF(workbookOld, excelBook);
            }

            ExcelToHtmlConverter excelToHtmlConverter = new ExcelToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
            //去掉Excel头行
            excelToHtmlConverter.setOutputColumnHeaders(false);
            //去掉Excel行号
            excelToHtmlConverter.setOutputRowNumbers(false);
            excelToHtmlConverter.setOutputHiddenColumns(true);
            excelToHtmlConverter.setOutputHiddenRows(true);

            excelToHtmlConverter.processWorkbook(excelBook);

            Document htmlDocument = excelToHtmlConverter.getDocument();

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            DOMSource domSource = new DOMSource(htmlDocument);
            StreamResult streamResult = new StreamResult(outStream);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();

            serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.METHOD, "html");

            serializer.transform(domSource, streamResult);
            outStream.close();

            //Excel转换成Html
            content = new String(outStream.toByteArray());
            content = content.replace("<h2>Sheet1</h2>","")
                    .replace("<h2>Sheet2</h2>","")
                    .replace("<h2>Sheet3</h2>","")
                    .replace("<h2>Sheet4</h2>","")
                    .replace("<h2>Sheet5</h2>","");
//            System.out.println(content);
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        String fileName= UuidUtil.getUUID();
        String filePath = Common.HTML_PATH +fileName+".html";
        BufferedInputStream bis = null;
        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filePath));
            out.write(content.getBytes());
//            log.debug("page content:\n{}...", content.substring(0, 200));
        } catch (Exception e) {
            return null;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    log.error("Error occurred, cause by: ", e);
                    return null;
                }
            }
        }
        return fileName;
    }

    private void html2image(String fileName){
        Html2Image html2Image = Html2Image.fromFile(new File(Common.HTML_PATH+fileName+".html"));
        html2Image.getImageRenderer().saveImage(Common.PICTURE_PATH+fileName+".png");
    }

    @Override
    public int insert(ReportMould reportMould) throws Exception{
        String fileName = excel2html(reportMould.getPath());
        if(StringUtils.isEmpty(fileName)){
            throw new Exception("Excel文件转网页失败！");
        }

        reportMould.setUpdateTime(DateUtils.getCurrentTime());
        reportMould.setWebFile(fileName);

        try {
            html2image(fileName);
            this.reportMouldMapper.insert(reportMould);
        }catch (Exception e){
            throw new Exception("Excel文件转图片失败！");
        }
        return 0;
    }

    @Override
    public int generationReport(ReportMould mould, List<ReportCell> reportCells,String outPath) throws Exception{
        String mpath = mould.getPath();
        File file = new File(mpath);
        InputStream inputStream = new FileInputStream(file);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
        // Read the Sheet
        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);

        int cols=xssfSheet.getRow(0).getPhysicalNumberOfCells();
        int rows=xssfSheet.getLastRowNum();//获得总行数

        if (xssfSheet != null) {
            for(ReportCell cell:reportCells){
//                log.info("cell :" +cell.getCell());
                List<Integer>  cellInfo = ExcelCommon.parseCellInfo(cell.getCell());
                XSSFRow xssfRow = xssfSheet.getRow(cellInfo.get(1)-1);
                XSSFCell xssfCell =  xssfRow.getCell(cellInfo.get(0)-1);
                if(xssfCell!=null){
                    CellStyle oldStyle = xssfCell.getCellStyle();
                    if(cell.isWarning()){
                        CellStyle cellStyle=xssfWorkbook.createCellStyle();
                        cellStyle.setVerticalAlignment(oldStyle.getVerticalAlignmentEnum());
                        cellStyle.setAlignment(oldStyle.getAlignmentEnum());
                        cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());// 设置背景色
                        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        cellStyle.setBorderRight(BorderStyle.THIN);
                        cellStyle.setBorderBottom(BorderStyle.THIN);
                        cellStyle.setBorderLeft(BorderStyle.THIN);
                        cellStyle.setBorderTop(BorderStyle.THIN);
                        cellStyle.setDataFormat(oldStyle.getDataFormat());
                        xssfCell.setCellStyle(cellStyle);
                    }
                    if(cell.getValue()!=null){
                        String value = cell.getValue();
                        if(MathUtil.isNumber(value)){
                            Double dvalue = Double.parseDouble(value);
                            String svalue = new BigDecimal(dvalue.toString()).toString();
                            xssfCell.setCellValue(Double.parseDouble(svalue));
                        }else{
                            xssfCell.setCellValue(value);
                        }
                    }
                }
            }
            FileOutputStream fo=new FileOutputStream(new File(outPath));
            xssfWorkbook.write(fo);
            xssfWorkbook.close();
            inputStream.close();
            fo.close();
        }else{
            xssfWorkbook.close();
            inputStream.close();
            return 0;
        }
        xssfWorkbook.close();
        inputStream.close();
        return 0;
    }

    @Override
    public Map<String, String> getTableCellValues(int bankId, Set<String> tableCells,Set<String> tables, String time) throws Exception{
        Map<String,String> tableCellValues = new HashMap<>();
        for(String table:tables){
//            log.info("--"+table+","+bankId+","+time);
            PlatoExcelFile platoExcelFile = null;
            try {
                  platoExcelFile = excelFileService.getByBankIdCodeAndTime(bankId, table, time);
            }catch (Exception e){
                throw new Exception("机构："+bankId+  "  表："+table+"  未上传,无法生成报表！");
            }
//            log.info ("platoExcelFile.getPath():"+ platoExcelFile.getPath());
//            if(platoExcelFile == null|| StringUtils.isEmpty(platoExcelFile.getPath())){
//                throw new Exception("机构："+bankId+  "表："+table+"  未上传,无法生成报表！");
//            }
            Set<String> cells = new LinkedHashSet<>();
            for(String tableCell:tableCells){
                if(tableCell.startsWith(table+"[")){
                    String tmp = tableCell.replace(table,"");
                    tmp = tmp.replace("[","");
                    tmp = tmp.replace("]","");
                    cells.add(tmp);
                }
            }
            for(int i=0;i<cells.size ();i++){
            }
            Map<String,String> cellValues = new HashMap<>();
            try {
                cellValues = ExcelRead.readExcelCells(platoExcelFile.getPath(),cells,0);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("机构："+bankId+" 表："+table+ "  未上传！");
            }
            if(cellValues == null)
                throw new Exception("机构："+bankId+"表："+table+"  未取到数！");
            for(String tableCell:tableCells){
                for(String cell:cells){
                    if(tableCell.equals(table+"["+cell+"]")){
                        tableCellValues.put(tableCell,cellValues.get(cell));
//                        log.info(tableCell+" value:"+cellValues.get(cell));
                    }
                }
            }
        }
        log.info (String.valueOf (tableCellValues.size ()));
        return tableCellValues;
    }



    /**
     *  getOperationValues1----审表
     * @param bankId
     * @param cellOperations
     * @param time
     * @return
     * @throws Exception
     */
    @Override
    public List<CellOperation> getOperationValues1(int bankId, List<CellOperation> cellOperations, String time) throws Exception{
        for(CellOperation cellOperation:cellOperations){
            String operationStr = cellOperation.getOperation();
        }
        for(CellOperation cellOperation:cellOperations){
            String operationStr = cellOperation.getOperation();
            log.info("cellOperation:"+operationStr);

                if(operationStr.startsWith("inc<")){
                Set<String> otableCells = this.getMathOperationCells(operationStr);
                if(otableCells == null){
                    throw new Exception("取数规则错误："+operationStr);
                }
                Set<String> otables =  new LinkedHashSet<>();
                for(String otableCell:otableCells){
                    otables.add(otableCell.substring(0, otableCell.indexOf("[")));
                    log.info ("======"+otableCell.substring(0, otableCell.indexOf("[")));
                }
                if(otables == null){
                    throw new Exception("取数规则错误："+operationStr);
                }
                List<String> dates = new ArrayList<>();
                String dates2 =  null;
                if(time.endsWith ("12")||time.endsWith ("09")||time.endsWith ("06")||time.endsWith ("03")){
                    dates2 = DateUtils.getLastQuarter (time);
                }
                dates.add(DateUtils.getLastMonth(time));
                dates.add(time);

                List<String> values = new ArrayList<>();
                for(String date:dates){
                    Map<String,String> oTableCellValues = new HashMap<>();
                    for(String otable:otables){
                        PlatoExcelFile platoExcelFile = excelFileService.getByBankIdCodeAndTime(bankId,otable,date);
                        if(platoExcelFile == null|| StringUtils.isEmpty(platoExcelFile.getPath())){
                            platoExcelFile = excelFileService.getByBankIdCodeAndTime(bankId,otable,dates2);
                        }
                        if(platoExcelFile == null|| StringUtils.isEmpty(platoExcelFile.getPath())){
                            throw new Exception("机构："+bankId+"表："+otable+"  未上传,无法生成报表！");
                        }

                        Set<String> ocells = new LinkedHashSet<>();
                        for(String ocell:otableCells){
                            if(ocell.startsWith(otable+"[")){
                                String tmp = ocell.replace(otable,"");
                                tmp = tmp.replace("[","");
                                tmp = tmp.replace("]","");
                                ocells.add(tmp);
                            }
                        }

                        Map<String,String> oCellValues = new HashMap<>();
                        try {
                            oCellValues = ExcelRead.readExcelCells(platoExcelFile.getPath(),ocells,0);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new Exception("机构："+bankId+"表："+ocells+e.getMessage());
                        }

                        if(oCellValues == null)
                            throw new Exception("机构："+bankId+"表："+ocells+"未取到数！");

                        for(String otableCell:otableCells){
                            log.info ("-------otableCell:"+otableCell);
                            for(String ocell:ocells){
                                if(otableCell.equals(otable+"["+ocell+"]")){
                                    oTableCellValues.put(otableCell,oCellValues.get(ocell));
                                    log.info("-AS------otableCell:"+otableCell+" value:"+oTableCellValues.get(otableCell));
                                }
                            }
                        }
                    }
                    String srcSuanshi = operationStr;
                    for(Map.Entry<String, String> entry : oTableCellValues.entrySet()){
                        String mapKey = entry.getKey();
                        String mapValue = entry.getValue();
                        srcSuanshi = srcSuanshi.replace(mapKey,mapValue);
                    }
                    srcSuanshi = srcSuanshi.replace("inc<","");
                    srcSuanshi = srcSuanshi.replace(">","");
                    double value = 0;
                    try {
                        value = MathUtil.jisuanStr(srcSuanshi);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new Exception("取数规则错误："+srcSuanshi);
                    }
                    values.add(String.valueOf(value));
                    log.info(">>>>>>>>>>: "+srcSuanshi+","+value);
                }
                String avgStr = "(" + values.get(1) + "-" + values.get(0)+")/"+values.get(1);
                log.info ("-----------avgStr公式：-----------"+avgStr+"-AS----" );
                try {
                    double avgvalue = MathUtil.jisuanStr(avgStr);
                    cellOperation.setValue(String.valueOf(avgvalue));
                    log.info(">>>>>>>>>>增幅： jisuan  "+avgStr+"=值:"+avgvalue);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception("取数规则错误："+operationStr);
                }
            }
            else {
                throw new Exception("取数规则错误："+operationStr);
            }
        }
        return cellOperations;
    }
    private Set<String> parseTableCell(String str){
        Set<String> tableCell = new HashSet<>();
        Matcher vmatcher = Pattern.compile(operation).matcher(str);
        while (vmatcher.find()) {
            str = str.replace(vmatcher.group(0),"");
        }
        Pattern patternMath = Pattern.compile(math);
        Matcher mathMatcher = patternMath.matcher(str);

        Pattern patternSingle = Pattern.compile(single);
        Matcher singletMatcher = patternSingle.matcher(str);

        if (mathMatcher.find()) {            //数学运算
            List<String> cell = new ArrayList<>();
            try {
                cell = ExcelCommon.splitTableCell(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tableCell.addAll(cell);
        } else if (singletMatcher.find()) {         //单个单元格取值
            tableCell.add(str);
        }
        return tableCell;
    }

    private Set<String> parseExcludeTableCell(String str){
        String math = "[+\\-*/=!,]";
        String ssingle = "[a-zA-Z]+\\d+?.*?\\[[a-zA-Z]+\\d+\\]";
        String scol = "^[a-zA-Z]+\\d+?.*?\\[[a-zA-Z]+\\]$";
        Set<String> tableCell = new HashSet<>();

        String [] dataStr = str.split(math);
        for(String s:dataStr){
            Pattern patternSingle = Pattern.compile(ssingle);
            Matcher singleMatcher = patternSingle.matcher(s);

            Pattern patternCol = Pattern.compile(scol);
            Matcher colMatcher = patternCol.matcher(s);

            if (singleMatcher.find()) {            //数学运算
                tableCell.add(singleMatcher.group(0));
            } else if (colMatcher.find()) {         //单个单元格取值
                tableCell.add(colMatcher.group(0));
            }

        }
        return tableCell;
    }

}
