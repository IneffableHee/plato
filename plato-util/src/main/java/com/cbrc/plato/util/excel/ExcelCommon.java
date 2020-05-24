package com.cbrc.plato.util.excel;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Slf4j
public class ExcelCommon {

    /*A11 转行列*/
    public static List<Integer> parseCellInfo(String cell){
        Matcher matcher = Pattern.compile("[a-zA-Z](.*)\\d+").matcher(cell);
        if(!matcher.matches()){
            log.info(cell+"错误，不符合格式！");
            return null;
        }
        Matcher matcher1 = Pattern.compile("[a-zA-Z]+|\\d+").matcher(cell);
        List<String> scellInfo = new ArrayList<>();
        List<Integer> icellInfo = new ArrayList<>();
        while (matcher1.find()) {
            scellInfo.add(matcher1.group(0));
        }
        String col = scellInfo.get(0).toUpperCase();
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
        icellInfo.add(icol);
        icellInfo.add(Integer.parseInt(scellInfo.get(1)));
        return icellInfo;
    }

    /*ABCD 转列*/
    public static int charToCol(String col){
        int icol = 0;
        for(int i=0;i<col.length();i++){
            if(col.length()==1){
                icol = col.charAt(0)-'A'+1;
            }else if(col.length()==2){
                icol = (col.charAt(0)-'A'+1)*26 + col.charAt(1) -'A' +1;
            }else if(col.length()==3){
                icol = (col.charAt(0)-'A'+1)*26*26 + (col.charAt(1)-'A'+1)*26 + col.charAt(2) -'A' +1;
            }
        }
        return icol;
    }

    /*行列转 A11*/
    public static String parseCellInfo(int col,int row){
        String s = null;
        col+=1;
        row+=1;
        if(row < 27){
            char crow = (char)(row+64);
            s = String.valueOf(crow);
        }else if(row < 26*27+1){
            if(row%26 == 0){
                char crow1 = (char)(row/26+63);
                char crow2 = 'Z';
                s = String.valueOf(crow1)+String.valueOf(crow2);
            }else{
                char crow1 = (char)(int)(row/26+64);
                char crow2 = (char)(row%26+64);
                s = String.valueOf(crow1)+String.valueOf(crow2);
            }
        }else{
            if(row%26 == 0){
                char crow1 = (char)(row/(26*26)+64);
                int t = (int)row/(26*26);
                char crow2 = (char)((row-t*26*26)/26+63);
                char crow3 = 'Z';
                s = String.valueOf(crow1)+String.valueOf(crow2)+String.valueOf(crow3);
            }else{
                char crow1 = (char)(int)(row/(26*26)+64);
                int t = (int)row/(26*26);
                char crow2 = (char)((row-t*26*26)/26+64);
                char crow3 = (char)(row%26+64);
                s = String.valueOf(crow1)+String.valueOf(crow2)+String.valueOf(crow3);
            }
        }
        return s+col;
    }

    /*取数规则中，中文字符转英文*/
    public static String SymbolsCNtoEN(String str){
        str =str.replaceAll(" ","");
        str =str.replaceAll("，",",");
        str =str.replaceAll("（","(");
        str =str.replaceAll("）",")");
        str =str.replaceAll("！","!");
        return str;
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

    public static List<String> splitTableCell(String string) {
        List<String> listSplit = new ArrayList<String>();
        Matcher matcher = Pattern.compile("[a-zA-Z]+\\d+?.*?\\[[a-zA-Z]+?\\d+\\]").matcher(string);// 用正则拆分成每个元素
        while (matcher.find()) {
//            System.out.println(matcher.group(0));
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
}
