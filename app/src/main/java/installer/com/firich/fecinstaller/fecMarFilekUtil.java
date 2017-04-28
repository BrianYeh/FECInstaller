package installer.com.firich.fecinstaller;

import android.os.Build;

import java.io.File;

/**
 * Created by brianyeh on 2017/4/26.
 */
public class fecMarFilekUtil {
    boolean findFeclogFileFlag = false;
    String strLogFileName="";
    String strUSBDiskPath="/storage/usbdisk/";

    public fecMarFilekUtil()
    {

    }
    public String getUSBDiskPath()
    {
        return  strUSBDiskPath;
    }
    public boolean isfindUDiskFecInstallFileName()
    {
        return findFeclogFileFlag;
    }
    public String findUDiskFecInstallFileName()
    {
        // check feclog file exist?
        String strStorage="/storage/";
        String path = "/storage/udisk/fec_install.txt";
        String strfeclog = "fec_install.txt";
        String strUDisk4_4 = "udisk";
        String strUSBDisk5_1 = "usbdisk";
        String strDisk=strUDisk4_4;
        String strDiskNum=strUDisk4_4;
        boolean findFeclogFile = false;
        int diskNumber = 1;
        File logFile;
        File logRealFile;


        String strVersion = Build.DISPLAY;
        boolean contains_android4 = strVersion.contains("4.4.3 2.0.0-rc2.");
        boolean contains_android5 = strVersion.contains("Edelweiss-T 5.1");
        boolean contains_android5_D = strVersion.contains("Edelweiss-D 5.1");

        if (contains_android5|| contains_android5_D ){
            strDisk = strUSBDisk5_1;
            strDiskNum = strUSBDisk5_1;
        }
        path = strStorage + strDiskNum + "/" + strfeclog;
        strUSBDiskPath = strStorage + strDiskNum;
        logFile = new File(path);

        do {
            if (logFile.exists()) {
                findFeclogFile = true;
                findFeclogFileFlag = true;
                break;
            }
            diskNumber++;
            if (diskNumber > 1) {
                if (contains_android4) {
                    strDisk = strDiskNum + "_" + Integer.toString(diskNumber);
                }else {
                    strDisk = strDiskNum + Integer.toString(diskNumber); //ex:usbdisk1 , usbdisk2 ..
                }
            }
            path = strStorage + strDisk + "/" + strfeclog; //ex: "/storage/udisk/fec_install.txt"
            strUSBDiskPath = strStorage + strDisk;
            logFile = new File(path);
        } while (diskNumber < 9);
        return strUSBDiskPath;

    }
}
