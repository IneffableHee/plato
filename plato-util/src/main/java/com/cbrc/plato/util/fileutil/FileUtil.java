package com.cbrc.plato.util.fileutil;

import com.cbrc.plato.util.datarule.report.SourceTable;
import com.cbrc.plato.util.response.PlatoBasicResult;
import com.cbrc.plato.util.response.PlatoResult;
import com.cbrc.plato.util.utilpo.Common;
import com.cbrc.plato.util.utilpo.ExcelInfo;
import com.cbrc.plato.util.utilpo.FileModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cbrc.plato.util.utilpo.Common.EMPTY;
import static com.cbrc.plato.util.utilpo.Common.POINT;

/**
 * @author fangtao
 * @date 2019-04-12
 */
@Slf4j
public class FileUtil {
    /*
    文件上传
     */
    public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
        File targetFile = new File(filePath);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath+fileName);
        out.write(file);
        out.flush();
        out.close();
    }


    /**
     * 解压上传的zip文件，将解压出来的EXCEL文件存储到 EXCEL根目录/期次/机构 目录下
     * @param zipFile
     */
    public static List<ExcelInfo> unZipFiles(ZipFile zipFile,String path,String type)throws IOException{
        log.info("**--**--unZipFiles");
        List<ExcelInfo> excelInfos = new ArrayList<>();

        Enumeration<?> entries = zipFile.getEntries();

        List<String> directory = new ArrayList<>();
        while (entries.hasMoreElements()) {

            ZipEntry entry = (ZipEntry) entries.nextElement();

            log.info("解压：" + entry.getName());
            // 如果是文件夹，就创建个文件夹
            if (entry.isDirectory()) {
                log.info("*****文件夹:"+entry.getName()+"-"+entry.getName().length());
                directory.add(entry.getName());
//                String dirPath = destDirPath + "/" + entry.getName();
//                File dir = new File(dirPath);
//                dir.mkdirs();

            } else {
                // 如果是文件，就先创建一个文件，然后用io流把内容copy过去

                String excelName = null;
                if(directory.size()>0){
                    log.info("---------directory:"+directory+" , entry.getName():"+entry.getName());
                    for(String fpath :directory){
                        if(entry.getName().indexOf(fpath)!=-1){
                            excelName = entry.getName().replaceAll(fpath,"");
                            log.info("22---------directory:"+directory+" , excelName:"+excelName);
                        }
                    }
                }
//                if(!excelName.equals(entry.getName()))
                log.info("=============="+excelName+" , "+entry.getName());
                log.info("*****文件:"+entry.getName());
                String fileType = getPostfix(entry.getName());
                log.info("**文件类型："+fileType);

                if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(fileType) || Common.OFFICE_EXCEL_2003_POSTFIX.equals(fileType)){
                    Map info = null;
                    try {
                        info = parseExcel(excelName==null?entry.getName().trim():excelName);
                    } catch (Exception e) {
                        throw new IOException(excelName);
                    }
                    if (info == null)
                        return null;

                    log.info("-------excelInfo:"+info.toString());
                    // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                    File targetFile = new File(path +"/"+ type+"/"+entry.getName().trim());
                    // 保证这个文件的父文件夹必须要存在
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();
                    // 将压缩文件内容写入到这个文件中
                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    int len;
                    byte[] buf = new byte[1024];
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    // 关流顺序，先打开的后关闭
                    fos.close();
                    is.close();

                    ExcelInfo excelInfo = new ExcelInfo();
                    excelInfo.setExcelCode(info.get("excelCode").toString());       //标号
                    excelInfo.setExcelTime(info.get("excelTimes").toString());      //期次
                    excelInfo.setBankName(info.get("bankName").toString());         //机构名称
                    excelInfo.setFileName(info.get("excelName").toString());        //名称
                    excelInfos.add(excelInfo);
                }else{
                    log.info("**不是Excel文件");
                    continue;
                }

            }
        }

        log.info("******************解压完毕********************");
        return  excelInfos;
    }


    /**
     * 解压文件到指定目录
     * @param zipFile
     * @param descDir
     */
    public static List<FileModel> unZipFiles(ZipFile zipFile, String descDir)throws IOException{
        log.info("--unZipFiles:"+descDir);
        File pathFile = new File(descDir);
        FileModel fileModel = null;;
        List<FileModel> fileModelList = new ArrayList<>();
        if(!pathFile.exists()){
            pathFile.mkdirs();
        }
        for(Enumeration entries = zipFile.getEntries(); entries.hasMoreElements();){
            fileModel = new FileModel();
            ZipEntry entry = (ZipEntry)entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zipFile.getInputStream(entry);
            String outPath = (descDir+zipEntryName).replaceAll("\\*", "/");;
            //判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if(!file.exists()){
                file.mkdirs();
            }
            //判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if(new File(outPath).isDirectory()){
                continue;
            }
            //输出文件路径信息
            log.info("outPath==="+outPath);
            OutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while((len=in.read(buf1))>0){
                out.write(buf1,0,len);
            }
            Map map = FileUtil.subStringExcel(zipEntryName);
            String fileCode = map.get("fileCode").toString().trim();
            String fileSurface = map.get("fileSurface").toString().trim();
            fileModel.setFileCode(fileCode);
            fileModel.setFileSurface(fileSurface);
            fileModel.setFileName(zipEntryName);
            fileModel.setFilePath(outPath.trim());
            fileModelList.add(fileModel);
            in.close();
            out.close();
        }
        log.info("******************解压完毕********************");
        return  fileModelList;
    }
    /*
        对zip文件名进行截取需要字符串
    */
    public static Map subStringZip(String name) {
        Map map = new HashMap();
        String[] tmp = name.split("_");
        String name1 = tmp[0];
        String name2 = tmp[1];
        String name3 = tmp[2];
        String name4 = name3.substring(0,6);
        map.put("optName",name1.trim());
        map.put("fileType",name2.trim());
        map.put("fileDate",name4.trim());
        return map;
    }

    /*
        从Excel文件名称解析Excel表格信息
        黔南市城市商业银行合计_GF01_II 附注第II部分：贷款质量五级分类情况简表{16年启用}_境内汇总数据_2019-01-31_人民币
    */
    public static Map parseExcel(String excelFileName) throws Exception{
        Map map = new HashMap();
        String[] tmp = excelFileName.split(" ");
        String str1 = tmp[0];
        String str2 = tmp[1];
        log.info("111");
        String bankName = StringUtils.substringBefore(str1, "_");
        log.info("222");
        String excelCode = StringUtils.substringAfter(str1, "_");
        log.info("333:"+str2);
        String excelTimes = "";
        String timeformat = "\\d{4}(\\-|\\/|\\.)\\d{1,2}\\1\\d{1,2}";
        Pattern op =Pattern.compile(timeformat);
        Matcher om = op.matcher(str2);
        if(om.find()){
            excelTimes = om.group();
        }

        String excelName = StringUtils.substringBefore(str2, "_");;
        map.put("bankName",bankName);       //机构名称
        map.put("excelCode",excelCode);     //标号
        map.put("excelName", excelName);     //名称
        map.put("excelTime",excelTimes);   //期次
        log.info("bankName:"+bankName+"excelCode:"+excelCode+"excelName:"+ excelName+"excelTime:"+excelTimes);
        return map;
    }

    public static SourceTable parseTableInfoFromFileName(String fileName){
        if(fileName==null||!fileName.contains(" "))
            return null;
        //G01_I 表外业务情况表{19年启用}_福泉富民村镇银行股份有限公司_2019-12-31_1
//        fileName = DataRuleUtil.symbolNormalization(fileName);
        SourceTable sourceTable = new SourceTable();
        String[] tmp = fileName.split(" ");
        String str1 = tmp[0];
        String str2 = tmp[1];


        if(!str2.contains("_"))
            return null;

        String[] info = str2.split("_");
        if(info==null||info.length!=4)
            return null;

        sourceTable.setTable(str1);
        sourceTable.setBankName(info[1]);

        String timeformat = "\\d{4}(\\-|\\/|\\.)\\d{1,2}\\1\\d{1,2}";
        Pattern op =Pattern.compile(timeformat);
        Matcher om = op.matcher(info[2]);
        if(om.find()){
            sourceTable.setTime(om.group().substring(0,om.group().lastIndexOf("-")).replace("-",""));
            sourceTable.setFileName(fileName);
        }else{
            return null;
        }
        return sourceTable;
    }
    /*
    对xls/xlsx文件名进行截取需要字符串
*/
    public static Map subStringExcel(String name) {
        Map map = new HashMap();
        if(name.contains(":") || name.contains("：")){
            if(name.contains("：")){
                String[] tmp = name.split(" ");
                String str1 = tmp[0];
                String str2 = tmp[1];
                String[] tmp5 = str1.split("_");
                String str5;
                if(tmp5.length <=2){
                    str5 = tmp5[1];
                }else{
                    str5 = tmp5[1] + "_" + tmp5[2];
                }
                String fileCode = str5.trim();
                String[] tmp6 = str2.split("：");
                String str6 = tmp6[1];
                String[] tmp7 = str6.split("_");
                String fileName = tmp7[0].trim();
                map.put("fileCode",fileCode.trim());
                map.put("fileSurface",fileName.trim());
                return map;
            }else{
                String[] tmp = name.split(" ");
                String str1 = tmp[0];
                String str2 = tmp[1];
                String[] tmp5 = str1.split("_");
                String str5;
                if(tmp5.length <=2){
                    str5 = tmp5[1];
                }else{
                    str5 = tmp5[1] + "_" + tmp5[2];
                }
                String fileCode = str5.trim();
                String[] tmp6 = str2.split(":");
                String str6 = tmp6[1];
                String[] tmp7 = str6.split("_");
                String fileName = tmp7[0].trim();
                map.put("fileCode",fileCode.trim());
                map.put("fileSurface",fileName.trim());
                return map;
            }
        }else{
            String[] tmp = name.split(" ");
            String str1 = tmp[0];
            String str2 = tmp[1];
            String[] tmp5 = str1.split("_");
            String str5;
            if(tmp5.length <= 2){
               str5 = tmp5[1];
            }else{
               str5 = tmp5[1] + "_" + tmp5[2];
            }
            String fileCode = str5.trim();
            String[] tmp7 = str2.split("_");
            String fileName = tmp7[0].trim();
            map.put("fileCode",fileCode.trim());
            map.put("fileSurface",fileName.trim());
            return map;
        }
    }

    /*
    判断文件是否存在
     */
    public static boolean delFile(File file) {
        if (!file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                delFile(f);
            }
        }
        return file.delete();
    }
    /*
    获取文件后缀
     */
    public static String getPostfix(String path) {
        if (path == null || EMPTY.equals(path.trim())) {
            return EMPTY;
        }
        if (path.contains(POINT)) {
            return path.substring(path.lastIndexOf(POINT) + 1, path.length());
        }
        return EMPTY;
    }

    public static Boolean isExcelFile(String fileName){
        if(fileName.endsWith(".xls")||fileName.endsWith(".xlsx"))
            return true;
        return false;
    }
    /*
    单个文件下载
     */
    public static PlatoBasicResult downloadfile(HttpServletResponse response, HttpServletRequest request, String fileName, String path) {
        if (fileName != null) {
            //设置文件路径
            File file = new File(path);
            if (file.exists()) {
                response.setHeader("content-type", "application/octet-stream");
                response.setContentType("application/octet-stream");
                try {
                    //获取浏览器类型 :Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.16) Gecko/20110319 Firefox/3.6.16 (.NET CLR 3.5.30729)
                    String agent = request.getHeader("USER-AGENT");
                    String downLoadName = null;
                    if (null != agent && -1 != agent.indexOf("MSIE"))   //IE
                    {
                        downLoadName = URLEncoder.encode(fileName, "UTF-8");
                    }
                    else if (null != agent && -1 != agent.indexOf("Mozilla")) //Firefox
                    {
                        downLoadName = new String(fileName.getBytes("UTF-8"),"iso-8859-1");
                    }
                    else
                    {
                        downLoadName = URLEncoder.encode(fileName, "UTF-8");
                    }
                    response.setHeader("Content-Disposition", "attachment;filename="+ downLoadName);
                    response.setContentType("application/json;charset=UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return PlatoResult.successResult("excel文件下载成功！");
            }else{
                return PlatoResult.failResult("您所勾选的文件"+fileName+"不存在，请联系管理员协助解决！");
            }
        }else{
            return PlatoResult.failResult("您所勾选的文件名不存在，请联系管理员协助解决！");
        }
    }

    /*
   取值分割
    */
    public static  Map<String,String> getValue(String value) {
        Map<String,String> map = new HashMap<String,String>();
        String fileX = value.substring(0,1);
        String fileY = value.substring(1,value.length());
        switch(fileX)
        {
            case "A":
                fileX = "1";
                break;
            case "B":
                fileX = "2";
                break;
            case "C":
                fileX = "3";
                break;
            case "D":
                fileX = "4";
                break;
            case "E":
                fileX = "5";
                break;
            case "F":
                fileX = "6";
                break;
            case "G":
                fileX = "7";
                break;
            case "H":
                fileX = "8";
                break;
            case "I":
                fileX = "9";
                break;
            case "J":
                fileX = "10";
                break;
            case "K":
                fileX = "11";
                break;
            case "L":
                fileX = "12";
                break;
            case "M":
                fileX = "13";
                break;
            case "N":
                fileX = "14";
                break;
            case "O":
                fileX = "15";
                break;
            case "P":
                fileX = "16";
                break;

        }
        map.put("fileX",fileX);
        map.put("fileY",fileY);
        return map;
    }


    /*解析模板zip文件名，获取excel信息*/
    public static List<ExcelInfo> unZipMouldExcel(ZipFile zipFile) throws IOException {
        List<ExcelInfo> excelInfos = new ArrayList<>();
        Enumeration<?> entries = zipFile.getEntries();
        List<String> directory = new ArrayList<>();

        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();

            // 如果是文件夹，获取文件夹名称
            if (entry.isDirectory()) {
                log.info("*****文件夹:"+entry.getName()+"-"+entry.getName().length());
                directory.add(entry.getName());
            } else {
                String excelName = null;
                if(directory.size()>0){
                    log.info("---------directory:"+directory+" , entry.getName():"+entry.getName());
                    for(String fpath :directory){
                        if(entry.getName().indexOf(fpath)!=-1){
                            excelName = entry.getName().replaceAll(fpath,"");
                            log.info("22---------directory:"+directory+" , excelName:"+excelName);
                        }
                    }
                }

                log.info("=============="+excelName+" , "+entry.getName());

                String fileType = getPostfix(entry.getName());
                if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(fileType) || Common.OFFICE_EXCEL_2003_POSTFIX.equals(fileType)){
                    Map info = null;
                    try {
                        info = parseExcel(excelName==null?entry.getName().trim():excelName);
                    } catch (Exception e) {
                        throw new IOException(excelName);
                    }
                    if (info == null)
                        return null;

                    log.info("-------excelInfo:"+info.toString());

                    ExcelInfo excelInfo = new ExcelInfo();
                    excelInfo.setExcelCode(info.get("excelCode").toString());       //标号
                    excelInfo.setExcelTime(info.get("excelTimes").toString());      //期次
                    excelInfo.setBankName(info.get("bankName").toString());         //机构名称
                    excelInfo.setFileName(info.get("excelName").toString());        //名称
                    excelInfos.add(excelInfo);
                }else{
                    log.info("**不是Excel文件");
                    continue;
                }

            }
        }
        return  excelInfos;
    }


    //静态方法：三个参数：文件的二进制，文件路径，文件名
    //通过该方法将在指定目录下添加指定文件
    public static void saveFile(byte[] file,String filePath,String fileName) throws IOException {
        //目标目录
        File targetfile = new File(filePath);

        //判断文件是否已经存在
        if (targetfile.exists()) {
            targetfile.delete();
        }

        if(!targetfile.getParentFile().exists()) {
            targetfile.getParentFile().mkdirs();
        }

        //二进制流写入
        FileOutputStream out = new FileOutputStream(filePath+fileName);
        out.write(file);
        out.flush();
        out.close();
    }

    /*
      仅用于模板上传
      从Zip文件名称解析模板信息
      城市商业银行_贵阳银行_季报
  */
    public static Map parseZipMould(String zipName){
        log.info("zipName:"+zipName);
        Map map = new HashMap();
        int bankId = 0;
        String bankName = null;
        String mouldType = null;

        String[] tmp = zipName.split("_");
        if(tmp.length!=3)
            return null;

        if(!tmp[1].equals("汇总")&&!tmp[1].equals("单机构")){
            return null;
        }

        if(!tmp[2].equals("月报")&&!tmp[2].equals("季报")){
            return null;
        }

        if(tmp[1].equals("汇总")){            //机构名称
            map.put("groupName",tmp[0]+"汇总");
        }else {
            map.put("groupName",tmp[0]);
        }

        map.put("mouldStatistic",tmp[1]);      //模板类型（汇总、单机构）
        map.put("mouldType",tmp[2]);      //模板频率（月报、季报）
        return map;
    }

    /*
      仅用于模板上传
      从Zip文件名称解析文件压缩包信息
      都匀融通村镇银行_季报_201901
  */
    public static Map parseZipExcel(String zipName){
        log.info("zipName:"+zipName);
        Map map = new HashMap();
        int bankId = 0;
        String bankName = null;
        String mouldType = null;

        String[] tmp = zipName.split("_");
//        log.info("split lem:"+tmp.length);
        if(tmp.length!=3)
            return null;

        if(!tmp[1].equals("汇总")&&!tmp[1].equals("单机构")){
            return null;
        }

        if(!tmp[2].equals("月报")&&!tmp[2].equals("季报")){
            return null;
        }

        if(tmp[1].equals("汇总")){            //机构名称
            map.put("groupName",tmp[0]+"汇总");
        }else {
            map.put("groupName",tmp[0]);
        }

        map.put("mouldStatistic",tmp[1]);      //模板类型（汇总、单机构）
        map.put("mouldType",tmp[2]);      //模板频率（月报、季报）
        return map;
    }
    /**
     * 删除方法一
     * @param str
     * @param delChar
     * @return
     */
    public static String deleteString0(String str, char delChar){
        String delStr = "";
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i) != delChar){
                delStr += str.charAt(i);
            }
        }
        return delStr;
    }

    /**
     * 获取目录下所有文件名
     */

    public static void getAllFileName(String path,ArrayList<String> fileNameList) {
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                fileNameList.add(tempList[i].getName());
            }
            if (tempList[i].isDirectory()) {
                getAllFileName(tempList[i].getAbsolutePath(),fileNameList);
            }
        }
        return;
    }

    /**
     * 删除目录下所有文件
     */
    public static void removeDir(String dir) {
        File file = new File(dir);
        delFile(file);
        return;
//        File dirfile = new File(dir);
//        File[] files=dirfile.listFiles();
//        if(files == null||files.length==0){
//            return;
//        }
//        for(File file:files){
//            System.out.println(file.getAbsolutePath());
//            if(file.isDirectory()){
//                removeDir(file.getAbsolutePath());
//            }else{
//                file.delete();
//            }
//        }
    }
}
