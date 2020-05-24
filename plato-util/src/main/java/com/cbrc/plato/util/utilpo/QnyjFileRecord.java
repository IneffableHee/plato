package com.cbrc.plato.util.utilpo;

import java.io.Serializable;
import java.util.Date;

/* 文件上传记录pojo
* @author fangtao
* @date 2019-04-15
*/
public class QnyjFileRecord implements Serializable {
   /**
    * 主键，自增
    */
   private Integer fileRecordId;

   /**
    * 文件名称
    */
   private String fileName;

   /**
    * 文件保存路径
    */
   private String filePath;

   /**
    * 创建时间
    */
   private Date uploadTime;

   /**
    * 上传用户
    */
   private String uploadUser;

   /**
    * 文件类型
    */
   private String fileType;

   /**
    * 文件状态（0—不启用，1—启用）
    */
   private Integer fileStatus;

   /**
    * 备用参数2
    */
   private String param1;

   /**
    * 备用参数2
    */
   private String param2;

   private static final long serialVersionUID = 1L;

   public Integer getFileRecordId() {
       return fileRecordId;
   }

   public void setFileRecordId(Integer fileRecordId) {
       this.fileRecordId = fileRecordId;
   }

   public String getFileName() {
       return fileName;
   }

   public void setFileName(String fileName) {
       this.fileName = fileName;
   }

   public String getFilePath() {
       return filePath;
   }

   public void setFilePath(String filePath) {
       this.filePath = filePath;
   }

   public Date getUploadTime() {
       return uploadTime;
   }

   public void setUploadTime(Date uploadTime) {
       this.uploadTime = uploadTime;
   }

   public String getUploadUser() {
       return uploadUser;
   }

   public void setUploadUser(String uploadUser) {
       this.uploadUser = uploadUser;
   }

   public String getFileType() {
       return fileType;
   }

   public void setFileType(String fileType) {
       this.fileType = fileType;
   }

   public Integer getFileStatus() {
       return fileStatus;
   }

   public void setFileStatus(Integer fileStatus) {
       this.fileStatus = fileStatus;
   }

   public String getParam1() {
       return param1;
   }

   public void setParam1(String param1) {
       this.param1 = param1;
   }

   public String getParam2() {
       return param2;
   }

   public void setParam2(String param2) {
       this.param2 = param2;
   }

   @Override
   public boolean equals(Object that) {
       if (this == that) {
           return true;
       }
       if (that == null) {
           return false;
       }
       if (getClass() != that.getClass()) {
           return false;
       }
       QnyjFileRecord other = (QnyjFileRecord) that;
       return (this.getFileRecordId() == null ? other.getFileRecordId() == null : this.getFileRecordId().equals(other.getFileRecordId()))
           && (this.getFileName() == null ? other.getFileName() == null : this.getFileName().equals(other.getFileName()))
           && (this.getFilePath() == null ? other.getFilePath() == null : this.getFilePath().equals(other.getFilePath()))
           && (this.getUploadTime() == null ? other.getUploadTime() == null : this.getUploadTime().equals(other.getUploadTime()))
           && (this.getUploadUser() == null ? other.getUploadUser() == null : this.getUploadUser().equals(other.getUploadUser()))
           && (this.getFileType() == null ? other.getFileType() == null : this.getFileType().equals(other.getFileType()))
           && (this.getFileStatus() == null ? other.getFileStatus() == null : this.getFileStatus().equals(other.getFileStatus()))
           && (this.getParam1() == null ? other.getParam1() == null : this.getParam1().equals(other.getParam1()))
           && (this.getParam2() == null ? other.getParam2() == null : this.getParam2().equals(other.getParam2()));
   }

   @Override
   public int hashCode() {
       final int prime = 31;
       int result = 1;
       result = prime * result + ((getFileRecordId() == null) ? 0 : getFileRecordId().hashCode());
       result = prime * result + ((getFileName() == null) ? 0 : getFileName().hashCode());
       result = prime * result + ((getFilePath() == null) ? 0 : getFilePath().hashCode());
       result = prime * result + ((getUploadTime() == null) ? 0 : getUploadTime().hashCode());
       result = prime * result + ((getUploadUser() == null) ? 0 : getUploadUser().hashCode());
       result = prime * result + ((getFileType() == null) ? 0 : getFileType().hashCode());
       result = prime * result + ((getFileStatus() == null) ? 0 : getFileStatus().hashCode());
       result = prime * result + ((getParam1() == null) ? 0 : getParam1().hashCode());
       result = prime * result + ((getParam2() == null) ? 0 : getParam2().hashCode());
       return result;
   }

   @Override
   public String toString() {
       StringBuilder sb = new StringBuilder();
       sb.append(getClass().getSimpleName());
       sb.append(" [");
       sb.append("Hash = ").append(hashCode());
       sb.append(", fileRecordId=").append(fileRecordId);
       sb.append(", fileName=").append(fileName);
       sb.append(", filePath=").append(filePath);
       sb.append(", uploadTime=").append(uploadTime);
       sb.append(", uploadUser=").append(uploadUser);
       sb.append(", fileType=").append(fileType);
       sb.append(", fileStatus=").append(fileStatus);
       sb.append(", param1=").append(param1);
       sb.append(", param2=").append(param2);
       sb.append(", serialVersionUID=").append(serialVersionUID);
       sb.append("]");
       return sb.toString();
   }
}