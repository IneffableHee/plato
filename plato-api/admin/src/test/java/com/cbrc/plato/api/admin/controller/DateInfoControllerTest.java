package com.cbrc.plato.api.admin.controller;

import com.cbrc.plato.util.datarule.report.DataRuleUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateInfoControllerTest {

    static String time = "201903";
    public static void main(String[] args) throws Exception{
        String str = "G01_II[C15]/(G01_II[C11]+G01_II[C12]]+G01_II[C13])";
        String str1 = "hbzzl<G01_II[C11]+G01_II[C12]]+G01_II[C13]>";
        System.out.println(str1.replace(DataRuleUtil.OPRATION,"").replace("<","").replace(">",""));
        System.out.println(DataRuleUtil.isDataRule(str));
//        String path = "D:/fileManager/file/excle/都匀融通村镇银行有限责任公司/2019-09-30/都匀融通村镇银行有限责任公司_G14a 最大十家金融机构同业融出情况表{15年启用}_境内汇总数据_2019-09-30_人民币.xls";
//        Set<String> cells = new HashSet<>();
//        cells.add("B18");
//        cells.add("B19");
//        cells.add("B1");
//        Map<String,String> values = ExcelRead.readExcelCells(path,cells,0);
//        for(Map.Entry<String, String> entry : values.entrySet()){
//            String mapKey = entry.getKey();
//            String mapValue = entry.getValue();
//            System.out.println(mapKey+":"+mapValue);
//        }
    }
}