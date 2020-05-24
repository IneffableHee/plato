package com.cbrc.plato.core.basic.service.impl;

import com.cbrc.plato.core.basic.dao.ReportMouldMapper;
import com.cbrc.plato.core.basic.model.*;
import com.cbrc.plato.core.basic.service.IBankService;
import com.cbrc.plato.core.basic.service.IExcelFileService;
import com.cbrc.plato.core.basic.service.IReportMouldService;
import com.cbrc.plato.util.datarule.report.ReportTable;
import com.cbrc.plato.util.datarule.report.SourceCell;
import com.cbrc.plato.util.datarule.report.SourceMatrix;
import com.cbrc.plato.util.datarule.report.SourceTable;
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
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
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
public class ReportMouldService implements IReportMouldService {
    @Autowired
    ReportMouldMapper reportMouldMapper;

    @Autowired
    IBankService bankService;

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

    @Override
    public List<ReportMould> getByUserId(Integer uid) {
        return this.reportMouldMapper.selectByUserId(uid);
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
//            log.info("@@@@@@"+dataInfo.getDataSource());
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
                    cellOperations.add(cellOperation);
                }else if(operationMap.containsKey(operation)){
                    if(operationMap.get(operation)==null&&excludeRule==null){
                        continue;
                    }else if(operationMap.get(operation)==null||excludeRule==null){
                        operationMap.put(operation,excludeRule);
                        CellOperation cellOperation= new CellOperation();
                        cellOperation.setOperation(operation);
                        cellOperation.setExcludeRule(excludeRule);
                        cellOperations.add(cellOperation);
                    }else if(!operationMap.get(operation).equals(excludeRule)){
                        operationMap.put(operation,excludeRule);
                        CellOperation cellOperation= new CellOperation();
                        cellOperation.setOperation(operation);
                        cellOperation.setExcludeRule(excludeRule);
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

    @Override
    public int deleteById(Integer id) {
        return this.reportMouldMapper.deleteByPrimaryKey(id);
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
            log.error("Error occurred, cause by: ", e);
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
        log.info("path "+reportMould.getPath());
        String fileName = excel2html(reportMould.getPath());
        log.info("fileName "+reportMould.getPath());
        if(StringUtils.isEmpty(fileName)){
            log.info("excel2html fail");
            throw new Exception("Excel文件转网页失败！");
        }
;
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
                        if(cell.getValue()!=null){
                            if(MathUtil.isNumber(cell.getValue())){
                                if(cell.getValue().contains(".")){
                                    cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
                                }else{
                                    cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0"));
                                }
                            }
                        }
                        xssfCell.setCellStyle(cellStyle);
                    }
                    if(cell.getValue()!=null){
                        String value = cell.getValue();
                        if(MathUtil.isNumber(value)){
                            xssfCell.setCellValue(Double.parseDouble(value));
                        }else if(value.equals("NaN")){
                            xssfCell.setCellValue(0.00);
                        }else if(value.equals("Infinity")){
                            XSSFCellStyle style = xssfCell.getCellStyle();
                            XSSFFont font=style.getFont();
                            font.setColor(Font.COLOR_RED);
                            style.setFont(font);
                            xssfCell.setCellStyle(style);
                            xssfCell.setCellValue(value);
                        }else{
                            xssfCell.setCellValue(value);
                        }
                    }
                }
            }
            FileOutputStream fo=new FileOutputStream(new File(outPath));
            xssfWorkbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
            xssfWorkbook.setForceFormulaRecalculation(true);
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
    public int generationReport(ReportMould mould, ReportTable reportTable, String outPath,String name,String pareTime) throws Exception {
        String mpath = mould.getPath();
        File file = new File(mpath);
        InputStream inputStream = new FileInputStream(file);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
        // Read the Sheet
        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);

        if (xssfSheet != null) {
            for(com.cbrc.plato.util.datarule.report.ReportCell cell:reportTable.getReportCells()){
//                log.info("cell :" +cell.getCell());
                List<Integer>  cellInfo = ExcelCommon.parseCellInfo(cell.getCell());
                XSSFRow xssfRow = xssfSheet.getRow(cellInfo.get(1)-1);
                XSSFCell xssfCell =  xssfRow.getCell(cellInfo.get(0)-1);
                if(xssfCell!=null){
                    CellStyle oldStyle = xssfCell.getCellStyle();
                    if(cell.isWarning()){
                        CellStyle cellStyle=xssfWorkbook.createCellStyle();
                        cellStyle.cloneStyleFrom(oldStyle);
//                        cellStyle.setVerticalAlignment(oldStyle.getVerticalAlignmentEnum());
//                        cellStyle.setAlignment(oldStyle.getAlignmentEnum());
                        cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());// 设置背景色
                        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//                        cellStyle.setBorderRight(BorderStyle.THIN);
//                        cellStyle.setBorderBottom(BorderStyle.THIN);
//                        cellStyle.setBorderLeft(BorderStyle.THIN);
//                        cellStyle.setBorderTop(BorderStyle.THIN);
//                        cellStyle.setDataFormat(oldStyle.getDataFormat());
                        xssfCell.setCellStyle(cellStyle);
                    }
                    if(cell.getValue()!=null){
                        String value = cell.getValue();
                        if(MathUtil.isNumber(value)){
                            Double dvalue = Double.parseDouble(value);
                            String svalue = new BigDecimal(dvalue.toString()).toString();
                            xssfCell.setCellValue(Double.parseDouble(svalue));
                        }else if(value.equals("NaN")){
                            xssfCell.setCellValue(0.00);
                        }else if(value.equals("Infinity")){
                            XSSFCellStyle style = xssfWorkbook.createCellStyle();
                            style.cloneStyleFrom(oldStyle);
                            XSSFFont font=xssfWorkbook.createFont();
                            font.setColor(Font.COLOR_RED);
                            style.setFont(font);
                            xssfCell.setCellStyle(style);
                            xssfCell.setCellValue(value);
                        }else if(value.contains("name")){
                            xssfCell.setCellValue(name);
                        }else if(value.contains("time")){
                            xssfCell.setCellValue(pareTime);
                        }else{
                            xssfCell.setCellValue(value);
                        }
                    }else{
                        xssfCell.setCellValue("notUpload");
                    }
                }
            }
            FileOutputStream fo=new FileOutputStream(new File(outPath));
            xssfWorkbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
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
    public SourceMatrix getSourceMatrix(SourceMatrix sourceMatrix) throws Exception{
        for(SourceTable sourceTable:sourceMatrix.getSourceTables()){
            log.info(sourceTable.getBankId()+sourceTable.getTable()+sourceTable.getTime());
            PlatoExcelFile platoExcelFile = excelFileService.getByBankIdCodeAndTime(sourceTable.getBankId(),sourceTable.getTable(),sourceTable.getTime());
            if(platoExcelFile == null|| StringUtils.isEmpty(platoExcelFile.getPath())){
                SourceTable notUpload = new SourceTable();
                Bank bank = bankService.getById(sourceTable.getBankId());
                if(bank == null)
                    continue;
                notUpload.setBankName(bank.getName());
                notUpload.setTime(sourceTable.getTime());
                notUpload.setTable(sourceTable.getTable());
                sourceMatrix.getNotUplaod().add(notUpload);
                sourceTable.setSourceCells(null);
                /*for(SourceCell sourceCell:sourceTable.getSourceCells()){
                    sourceCell.setValue("notUpload");
                }*/
                System.out.println("机构："+bankService.getById(sourceTable.getBankId()).getName()+",表："+sourceTable.getTable()+"，第"+sourceTable.getTime()+"期未上传,无法生成报表！");
                //throw new Exception("机构："+bankService.getById(sourceTable.getBankId()).getName()+",表："+sourceTable.getTable()+"，第"+sourceTable.getTime()+"期未上传,无法生成报表！");
                continue;
            }
            Map<String,String> cellValues = new HashMap<>();
            try {
                log.info("Read "+sourceTable.getTable());
                cellValues = ExcelRead.readExcelCells(platoExcelFile.getPath(),sourceTable.getCells(),0);
                for(SourceCell sourceCell:sourceTable.getSourceCells()){
                    log.info(sourceTable.getBankName()+sourceTable.getTime()+"cell: "+sourceTable.getTable()+"["+sourceCell.getCell()+"], value:"+cellValues.get(sourceCell.getCell()));
                    sourceCell.setValue(cellValues.get(sourceCell.getCell()));
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                SourceTable notUpload = new SourceTable();
                notUpload.setBankName(bankService.getById(sourceTable.getBankId()).getName());
                notUpload.setTime(sourceTable.getTime());
                notUpload.setTable(sourceTable.getTable());
                sourceMatrix.getNotUplaod().add(notUpload);
                //sourceTable.setSourceCells(null);
                if(e.getMessage().contains("取数规则错误")){
                    for(SourceCell sourceCell:sourceTable.getSourceCells()){
                        log.info("ruleError:"+sourceTable.getTable());
                        sourceCell.setValue("ruleError");
                    }
                }else{
                    for(SourceCell sourceCell:sourceTable.getSourceCells()){
                        log.info("notUpload:"+sourceTable.getTable());
                        sourceCell.setValue("notUpload");
                    }
                    System.out.println("机构："+bankService.getById(sourceTable.getBankId()).getName()+",表："+sourceTable.getTable()+"，第"+sourceTable.getTime()+"期"+e.getMessage());
                }
                continue;
                //e.printStackTrace();
                //throw new Exception("机构："+bankService.getById(sourceTable.getBankId()).getName()+",表："+sourceTable.getTable()+"，第"+sourceTable.getTime()+"期"+e.getMessage());
            }
        }
        return sourceMatrix;
    }

    @Override
    public ReportTable getBankIds(ReportTable reportTable) throws Exception {
        List<com.cbrc.plato.util.datarule.report.ReportCell> reportCells = reportTable.getReportCells();
        for(com.cbrc.plato.util.datarule.report.ReportCell reportCell:reportCells){
            if(reportCell.getBankName()!=null){
                Bank bank = bankService.getByName(reportCell.getBankName());
                if(bank == null){
                    throw new Exception("机构："+reportCell.getBankName()+"命名不符合规范！");
                }
                reportCell.setBankId(bank.getId());
            }else {
                throw new Exception("规则："+reportCell.getRuleSource()+"未指定机构或书写不规范！");
            }
        }
        return null;
    }

    @Override
    public Map<String, String> getTableCellValues(int bankId, Set<String> tableCells,Set<String> tables, String time) throws Exception{
        Map<String,String> tableCellValues = new HashMap<>();
        for(String table:tables){
            log.info("--"+table);
            PlatoExcelFile platoExcelFile = excelFileService.getByBankIdCodeAndTime(bankId,table,time);
            log.info ("platoExcelFile.getPath():"+ platoExcelFile.getPath());
            if(platoExcelFile == null|| StringUtils.isEmpty(platoExcelFile.getPath())){
                throw new Exception("机构："+bankService.getById(bankId).getName()+",表："+table+"未上传,无法生成报表！");
            }
            Set<String> cells = new LinkedHashSet<>();
            for(String tableCell:tableCells){
                if(tableCell.startsWith(table+"[")){
                    String tmp = tableCell.replace(table,"");
                    tmp = tmp.replace("[","");
                    tmp = tmp.replace("]","");
//                    log.info("-"+tmp);
                    cells.add(tmp);
                }
            }

            Map<String,String> cellValues = new HashMap<>();
            try {
                cellValues = ExcelRead.readExcelCells(platoExcelFile.getPath(),cells,0);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("机构："+bankService.getById(bankId).getName()+"表："+table+e.getMessage());
            }

            if(cellValues == null)
                throw new Exception("机构："+bankService.getById(bankId).getName()+"表："+table+"未取到数！");

            for(String tableCell:tableCells){
                for(String cell:cells){
                    if(tableCell.equals(table+"["+cell+"]")){
                        tableCellValues.put(tableCell,cellValues.get(cell));
                        log.info(tableCell+" value:"+cellValues.get(cell));
                    }
                }
            }
        }
        return tableCellValues;
    }

    @Override
    public List<CellOperation> getOperationValues(int bankId, List<CellOperation> cellOperations, String time) throws Exception{
        log.info ("----------------------------cellOperations.size ()-"+cellOperations.size ());
        for(CellOperation cellOperation:cellOperations){
            String operationStr = cellOperation.getOperation();
            log.info("cellOperation:"+operationStr);
            if(operationStr.equals("znxs")){
                Date date = DateUtils.StrToDate(time,"yyyyMM");
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                double month = c.get(Calendar.MONTH)+1;
                double value = month/12;
                String svalue = String.valueOf(value);
                cellOperation.setValue(svalue.substring(0,4));
            }else if(operationStr.startsWith("inc<")){
               log.info ("-----sdsf----");
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
                dates.add(DateUtils.getLastMonth(time));
                dates.add(time);
                List<String> values = new ArrayList<>();
                for(String date:dates){
                    Map<String,String> oTableCellValues = new HashMap<>();
                    for(String otable:otables){
                        PlatoExcelFile platoExcelFile = excelFileService.getByBankIdCodeAndTime(bankId,otable,date);
                        if(platoExcelFile == null|| StringUtils.isEmpty(platoExcelFile.getPath())){
                            throw new Exception("机构："+bankService.getById(bankId).getName()+"表："+otable+"未上传,无法生成报表！");
                        }

                        Set<String> ocells = new LinkedHashSet<>();
                        for(String ocell:otableCells){
                            if(ocell.startsWith(otable+"[")){
                                String tmp = ocell.replace(otable,"");
                                tmp = tmp.replace("[","");
                                tmp = tmp.replace("]","");
                                log.info("-"+tmp);
                                ocells.add(tmp);
                            }
                        }

                        Map<String,String> oCellValues = new HashMap<>();
                        try {
                            oCellValues = ExcelRead.readExcelCells(platoExcelFile.getPath(),ocells,0);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new Exception("机构："+bankService.getById(bankId).getName()+"表："+ocells+e.getMessage());
                        }

                        if(oCellValues == null)
                            throw new Exception("机构："+bankService.getById(bankId).getName()+"表："+ocells+"未取到数！");

                        for(String otableCell:otableCells){
                            for(String ocell:ocells){
                                if(otableCell.equals(otable+"["+ocell+"]")){
                                    oTableCellValues.put(otableCell,oCellValues.get(ocell));
                                    log.info("-------otableCell:"+otableCell+" value:"+oTableCellValues.get(otableCell));
                                }
                            }
                        }
                    }
                    String srcSuanshi = operationStr;
                    for(Map.Entry<String, String> entry : oTableCellValues.entrySet()){
                        String mapKey = entry.getKey();
                        String mapValue = entry.getValue();
                        srcSuanshi = srcSuanshi.replace(mapKey,mapValue);
                        log.info(srcSuanshi+"==========="+mapKey+" value:"+mapValue);
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
//                    log.info(">>>>>>>>>> "+srcSuanshi+","+value);
                }

                String avgStr = "(" + values.get(1) + "-" + values.get(0)+")/"+values.get(1);
                log.info ("-----------avgStr-----------"+avgStr);

                try {
                    double avgvalue = MathUtil.jisuanStr(avgStr);
                    cellOperation.setValue(String.valueOf(avgvalue));
                    log.info(">>>>>>>>>>增幅： jisuan  "+avgStr+"=值:"+avgvalue);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception("取数规则错误："+operationStr);
                }

            }else if(operationStr.startsWith("hbzzl<")){
                Set<String> otableCells = this.getMathOperationCells(operationStr);
                if(otableCells == null){
                    throw new Exception("取数规则错误："+operationStr);
                }
//                log.info(">>>>>>>>>> "+DateUtils.getLastMonth(time));
                Set<String> otables =  new LinkedHashSet<>();
                for(String otableCell:otableCells){
                    otables.add(otableCell.substring(0, otableCell.indexOf("[")));
                }
                if(otables == null){
                    throw new Exception("取数规则错误："+operationStr);
                }

                List<String> dates = new ArrayList<>();
                dates.add(DateUtils.getLastMonth(time));
                dates.add(time);
                List<String> values = new ArrayList<>();
                for(String date:dates){
                    Map<String,String> oTableCellValues = new HashMap<>();
                    for(String otable:otables){
                        PlatoExcelFile platoExcelFile = excelFileService.getByBankIdCodeAndTime(bankId,otable,date);
                        if(platoExcelFile == null|| StringUtils.isEmpty(platoExcelFile.getPath())){
                            throw new Exception("机构："+bankService.getById(bankId).getName()+"表："+otable+"未上传,无法生成报表！");
                        }

                        Set<String> ocells = new LinkedHashSet<>();
                        for(String ocell:otableCells){
                            if(ocell.startsWith(otable+"[")){
                                String tmp = ocell.replace(otable,"");
                                tmp = tmp.replace("[","");
                                tmp = tmp.replace("]","");
                                log.info("-"+tmp);
                                ocells.add(tmp);
                            }
                        }

                        Map<String,String> oCellValues = new HashMap<>();
                        try {
                            oCellValues = ExcelRead.readExcelCells(platoExcelFile.getPath(),ocells,0);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new Exception("机构："+bankService.getById(bankId).getName()+"表："+ocells+e.getMessage());
                        }

                        if(oCellValues == null)
                            throw new Exception("机构："+bankService.getById(bankId).getName()+"表："+ocells+"未取到数！");

                        for(String otableCell:otableCells){
                            for(String ocell:ocells){
                                if(otableCell.equals(otable+"["+ocell+"]")){
                                    oTableCellValues.put(otableCell,oCellValues.get(ocell));
                                    log.info("-------"+otableCell+" value:"+oTableCellValues.get(otableCell));
                                }
                            }
                        }
                    }
                    String srcSuanshi = operationStr;
                    for(Map.Entry<String, String> entry : oTableCellValues.entrySet()){
                        String mapKey = entry.getKey();
                        String mapValue = entry.getValue();
                        srcSuanshi = srcSuanshi.replace(mapKey,mapValue);
                        log.info(srcSuanshi+"==========="+mapKey+" value:"+mapValue);
                    }
                    srcSuanshi = srcSuanshi.replace("hbzzl<","");
                    srcSuanshi = srcSuanshi.replace(">","");
                    double value = 0;
                    try {
                        value = MathUtil.jisuanStr(srcSuanshi);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new Exception("取数规则错误："+srcSuanshi);
                    }
                    values.add(String.valueOf(value));
//                    log.info(">>>>>>>>>> "+srcSuanshi+","+value);
                }

                String avgStr = "(" + values.get(0) + "-" + values.get(1)+")/"+values.get(0);

                try {
                    double avgvalue = MathUtil.jisuanStr(avgStr);
                    cellOperation.setValue(String.valueOf(avgvalue));
                    log.info(">>>>>>>>>>hbzzl jisuan  "+avgStr+":"+avgvalue);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception("取数规则错误："+operationStr);
                }
            }
            else if(operationStr.startsWith("avgy<")){
                Set<String> otableCells = this.getMathOperationCells(operationStr);
                if(otableCells == null){
                    throw new Exception("取数规则错误："+operationStr);
                }
//                log.info(">>>>>>>>>> "+DateUtils.getYearFirstMonth(time));
                Set<String> otables =  new LinkedHashSet<>();
                for(String otableCell:otableCells){
                    otables.add(otableCell.substring(0, otableCell.indexOf("[")));
                }
                if(otables == null){
                    throw new Exception("取数规则错误："+operationStr);
                }
                String firtstMonth = DateUtils.getYearFirstMonth(time);
                List<String> dates = new ArrayList<>();
                dates.add(firtstMonth);
                dates.add(time);
                List<String> values = new ArrayList<>();
                for(String date:dates){
                    Map<String,String> oTableCellValues = new HashMap<>();
                    for(String otable:otables){
                        PlatoExcelFile platoExcelFile = excelFileService.getByBankIdCodeAndTime(bankId,otable,date);
                        if(platoExcelFile == null|| StringUtils.isEmpty(platoExcelFile.getPath())){
                            throw new Exception("机构："+bankId+"表："+otable+"未上传,无法生成报表！");
                        }

                        Set<String> ocells = new LinkedHashSet<>();
                        for(String ocell:otableCells){
                            if(ocell.startsWith(otable+"[")){
                                String tmp = ocell.replace(otable,"");
                                tmp = tmp.replace("[","");
                                tmp = tmp.replace("]","");
//                                log.info("-"+tmp);
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
                            for(String ocell:ocells){
                                if(otableCell.equals(otable+"["+ocell+"]")){
                                    oTableCellValues.put(otableCell,oCellValues.get(ocell));
                                    log.info("-------"+otableCell+" value:"+oTableCellValues.get(otableCell));
                                }
                            }
                        }
                    }
                    String srcSuanshi = operationStr;
                    for(Map.Entry<String, String> entry : oTableCellValues.entrySet()){
                        String mapKey = entry.getKey();
                        String mapValue = entry.getValue();
                        srcSuanshi = srcSuanshi.replace(mapKey,mapValue);
//                        log.info(srcSuanshi+"==========="+mapKey+" value:"+mapValue);
                    }
                    srcSuanshi = srcSuanshi.replace("avgy<","");
                    srcSuanshi = srcSuanshi.replace(">","");
                    double value = 0;
                    try {
                        value = MathUtil.jisuanStr(srcSuanshi);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new Exception("取数规则错误："+srcSuanshi);
                    }
                    values.add(String.valueOf(value));
//                    log.info(">>>>>>>>>> "+srcSuanshi+","+value);
                }

                String avgStr = null;
                for(int i=0;i<values.size();i++){
                    if(i == 0){
                        avgStr = "(" + values.get(i);
                    }else{
                        avgStr += "+" + values.get(i);
                    }
                }
                avgStr+=")/"+values.size();

                try {
                    double avgvalue = MathUtil.jisuanStr(avgStr);
                    cellOperation.setValue(String.valueOf(avgvalue));
                    log.info("jisuan  "+avgStr+":"+avgvalue);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception("取数规则错误："+operationStr);
                }
            }
            else if(operationStr.startsWith("max<")){
                if(cellOperation.getExcludeRule()==null){
                    List<String> otableCells = new ArrayList<>(this.getMathOperationCells(operationStr));
                    if(otableCells == null||otableCells.size()!=1){
                        throw new Exception("取数规则错误："+operation);
                    }

                    String table =  otableCells.get(0).substring(0, otableCells.get(0).indexOf("["));
                    String col = otableCells.get(0).replace(table+"[","");
                    col = col.replace("]","");

                    PlatoExcelFile platoExcelFile = excelFileService.getByBankIdCodeAndTime(bankId,table,time);
                    if(platoExcelFile == null|| StringUtils.isEmpty(platoExcelFile.getPath())){
                        throw new Exception("机构："+bankId+"表："+table+"未上传,无法生成报表！");
                    }
                    String maxValue = ExcelRead.readColMax(platoExcelFile.getPath(),0,col);
//                    log.info("BBBBBBBBBBB "+operationStr+","+maxValue);
                    cellOperation.setValue(String.valueOf(maxValue));
                }else{
//                    log.info("maxxxxxxxxxxxxx");
                    List<String> otableCells = new ArrayList<>(this.getMathOperationCells(operationStr));
                    if(otableCells == null||otableCells.size()!=1){
                        throw new Exception("取数规则错误："+operation);
                    }

                    String table =  otableCells.get(0).substring(0, otableCells.get(0).indexOf("["));
                    String col = otableCells.get(0).replace(table+"[","");
                    col = col.replace("]","");

                    PlatoExcelFile platoExcelFile = excelFileService.getByBankIdCodeAndTime(bankId,table,time);
                    if(platoExcelFile == null|| StringUtils.isEmpty(platoExcelFile.getPath())){
                        throw new Exception("机构："+bankId+"表："+table+"未上传,无法生成报表！");
                    }


                    String excludeStr = cellOperation.getExcludeRule();
                    Set<String> tables = new HashSet<>();
                    Set<String> tableCells = parseExcludeTableCell(excludeStr);
                    log.info("excludeStr : "+excludeStr);
                    for(String tableCell:tableCells){
                        tables.add(tableCell.substring(0, tableCell.indexOf("[")));
                    }
                    if(tables.size()!=1||!tables.iterator().next().equals(table)){
                        throw new Exception("不取数规则错误！ "+excludeStr);
                    }

                    Set<String> excludeStrs = new HashSet<>();
                    if(excludeStr.indexOf(",")!=-1){
                        String[] strArray = excludeStr.split(",");
                        for(String s:strArray){
                            s = s.replace(table,"");
                            s = s.replace("[","");
                            s = s.replace("]","");
                            excludeStrs.add(s);
                        }
                    }else{
                        String s = excludeStr;
                        s = s.replace(table,"");
                        s = s.replace("[","");
                        s = s.replace("]","");
                        excludeStrs.add(s);
                    }

                    String maxValue = ExcelRead.readColMaxWhithExclude(platoExcelFile.getPath(),0,col,excludeStrs);
                    log.info("BBBBBBBBBBB "+operationStr+","+maxValue);
                    cellOperation.setValue(String.valueOf(maxValue));
//                    log.info("BBBBBBBBBBB "+operationStr+",Exclude:"+cellOperation.getExcludeRule());
                }
            }
            else if(operationStr.startsWith("nc<")){
                cellOperation.setValue(String.valueOf(0));
            }else if(operationStr.startsWith("sy<")){
                Set<String> otableCells = this.getMathOperationCells(operationStr);
                if(otableCells == null){
                    throw new Exception("取数规则错误："+operationStr);
                }
//                log.info(">>>>>>>>>> "+DateUtils.getLastMonth(time));
                Set<String> otables =  new LinkedHashSet<>();
                for(String otableCell:otableCells){
                    otables.add(otableCell.substring(0, otableCell.indexOf("[")));
                }
                if(otables == null){
                    throw new Exception("取数规则错误："+operationStr);
                }

                String syTime = DateUtils.getLastMonth(time);
                Map<String,String> oTableCellValues = new HashMap<>();
                for(String otable:otables){
                    PlatoExcelFile platoExcelFile = excelFileService.getByBankIdCodeAndTime(bankId,otable,syTime);
                    if(platoExcelFile == null|| StringUtils.isEmpty(platoExcelFile.getPath())){
                        throw new Exception("机构："+bankId+"表："+otable+"未上传,无法生成报表！");
                    }

                    Set<String> ocells = new LinkedHashSet<>();
                    for(String ocell:otableCells){
                        if(ocell.startsWith(otable+"[")){
                            String tmp = ocell.replace(otable,"");
                            tmp = tmp.replace("[","");
                            tmp = tmp.replace("]","");
                            log.info("-"+tmp);
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
                        for(String ocell:ocells){
                            if(otableCell.equals(otable+"["+ocell+"]")){
                                oTableCellValues.put(otableCell,oCellValues.get(ocell));
                                log.info("-------"+otableCell+" value:"+oTableCellValues.get(otableCell));
                            }
                        }
                    }
                }
                String srcSuanshi = operationStr;
                for(Map.Entry<String, String> entry : oTableCellValues.entrySet()){
                    String mapKey = entry.getKey();
                    String mapValue = entry.getValue();
                    srcSuanshi = srcSuanshi.replace(mapKey,mapValue);
                    log.info(srcSuanshi+"==========="+mapKey+" value:"+mapValue);
                }
                srcSuanshi = srcSuanshi.replace("sy<","");
                srcSuanshi = srcSuanshi.replace(">","");
                double value = 0;
                try {
                    value = MathUtil.jisuanStr(srcSuanshi);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception("取数规则错误："+srcSuanshi);
                }
                cellOperation.setValue(String.valueOf(value));
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
