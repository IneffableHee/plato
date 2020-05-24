package com.cbrc.plato.util.excel;



import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.cbrc.plato.util.excel.POIExcelRead.getValue;

public class ExcelUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger (ExcelUtils.class);

//    public static List<String> excelToShopIdList(MultipartFile file , String fileName) throws IOException {
//        List<String> list = new ArrayList<> ();
//        InputStream inputStream = file.getInputStream();
//
//        Workbook workbook = null;
//        try {
//            workbook = WorkbookFactory.create (inputStream);
//            inputStream.close ();
//            //工作表对象
//            Sheet sheet = workbook.getSheetAt (0);
//            //总行数
//            int rowLength = sheet.getLastRowNum () + 1;
//            //工作表的列
//            Row row = sheet.getRow (0);
//            //总列数
//            int colLength = row.getLastCellNum ();
//            //得到指定的单元格
//            Cell cell = row.getCell (0);
//            for (int i = 1; i < rowLength; i++) {
//                row = sheet.getRow (i);
//                for (int j = 0; j < colLength; j++) {
//                    cell = row.getCell (j);
//                    if (cell != null) {
////                        cell.setCellType (Cell.CELL_TYPE_STRING);
//                        cell.setCellType(CellType.STRING);
//                        String data = cell.getStringCellValue ();
//                        data = data.trim ();
//                        if (StringUtils.isNumeric (data))
//                            list.add (data);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            LOGGER.error ("parse excel file error :", e);
//        }
//        System.out.println ("------------123："+list);
//        return list;
//    }


    public static  Map<String,String> readReportMouldCells(MultipartFile file  ) throws Exception {
        Map<String, String> value = new LinkedHashMap<String, String> ();
//        File file = new File (filePath);
        InputStream inputStream =file.getInputStream ();

        Workbook wb = WorkbookFactory.create (inputStream);
//        try {
//            if (Common.OFFICE_EXCEL_2003_POSTFIX.equals (FileUtil.getPostfix (file.getName ()).trim ())) {
//                wb = new HSSFWorkbook (inputStream);
//            } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals (FileUtil.getPostfix (file.getName ()).trim ())) {
//                wb = new XSSFWorkbook (inputStream);
//            }
//        } catch (Exception e) {
//            inputStream.close ();
//            e.printStackTrace ();
//        }
        // Read the Sheet
        Sheet poiSheet = wb.getSheetAt (0);
        if (poiSheet != null) {
            int rows = poiSheet.getRow (0).getPhysicalNumberOfCells ();
            int cols = poiSheet.getLastRowNum ();//获得总行数
            // 列循环
            for (int j = 1; j < cols; j++) {
                //行循环
                int k = 3;
//                for( int k = 3; k<rows;k++){
                Row row = poiSheet.getRow (j);
                Cell cell = row.getCell (k);
                if (cell != null) {
                    String cvalue = getValue (cell);
                    if (!StringUtils.isEmpty (cvalue)) {
                        String isMonth = getValue (row.getCell (k - 2));
                        String checkRule = getValue (row.getCell (k + 1));
                        String checkTarget = getValue (row.getCell (k + 2));
                        String projectName = getValue (row.getCell (k - 1));
                        String projectTargetName = getValue (row.getCell (k + 3));
                        value.put (ExcelCommon.parseCellInfo (j, k), ExcelCommon.SymbolsCNtoEN (isMonth + "," + cvalue + "," + checkRule + ",") + checkTarget + "," + projectName + "," + projectTargetName);
                    }
//                    }
                }
            }
            wb.close ();
            inputStream.close ();
            return value;
        } else {
            wb.close ();
            inputStream.close ();
            return null;
        }
    }
}
