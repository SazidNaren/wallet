package vis.com.au.Utility;

import java.util.Date;

public class TodayListView {

    public String fileName;
    public String docId;
    public Date upLoadedTime;
    public String uploadedDate;
    public boolean isFirst=false;
    public boolean isFolder=false;
    public String filePath,count;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setIsFolder(boolean isFolder) {
        this.isFolder = isFolder;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setIsFirst(boolean isDateVisible) {
        this.isFirst = isDateVisible;
    }

    public String getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(String uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getUpLoadedTime() {
        return upLoadedTime;
    }

    public void setUpLoadedTime(Date upLoadedTime) {
        this.upLoadedTime = upLoadedTime;
    }


}
