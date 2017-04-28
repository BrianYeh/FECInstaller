package installer.com.firich.fecinstaller;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    String strTagUtil = "MainActivity.";

    private boolean bDebugOn = true;
    private boolean g_bIsDesktop = false;
    private void dump_trace( String bytTrace)
    {
        if (bDebugOn)
            Log.d(strTagUtil, bytTrace);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SetDesktopFlag();
    }

    boolean SetDesktopFlag()
    {
        String strVersion = Build.DISPLAY;
        boolean contains_android5 = strVersion.contains("Edelweiss-T 5.1");
        boolean contains_android5_D = strVersion.contains("Edelweiss-D 5.1");
        if (contains_android5_D){
            g_bIsDesktop = true;
            return true;
        }
        else {
            g_bIsDesktop = false;
            return false;
        }
    }


    public  void install_factory_tool_click(View view)
    {
/*
        1.
        usbdisk 有4個 usbdisk1~4
        無法得知目前 usbdisk 位置...所以找到 fec_install.txt then instll all related apks and copy related files....
        */

        String strUSBDiskInstallPath = "/storage/usbdisk";
        fecMarFilekUtil fecMarFilekUtil_instalMark =  new fecMarFilekUtil();
        fecMarFilekUtil_instalMark.findUDiskFecInstallFileName();
        if ( fecMarFilekUtil_instalMark.isfindUDiskFecInstallFileName()){
            strUSBDiskInstallPath = fecMarFilekUtil_instalMark.getUSBDiskPath() + "/install";
            //ex: /storage/usbdisk/install/
            dump_trace("USB Install Path="+ strUSBDiskInstallPath);
            boolean bTestInstallAPKS =false;
            Install_APK_Silently2(strUSBDiskInstallPath, "battery_test-signed.apk"); //test only
            if (bTestInstallAPKS) {
                if (g_bIsDesktop) {
                    Install_APK(strUSBDiskInstallPath, "FactoryTools_Desktop_1009.apk");
                } else {
                    Install_APK(strUSBDiskInstallPath, "FactoryTools_Tablet_1009.apk");
                }
                Install_APK(strUSBDiskInstallPath, "battery_test-signed.apk");
                Install_APK(strUSBDiskInstallPath, "cputemp.apk");
                Install_APK(strUSBDiskInstallPath, "eGalaxCalibrator_v0.12-config.apk");
                Install_APK(strUSBDiskInstallPath, "eGalaxSensorTester_v0.25_PCAP3188UR_1278_v04_0000_C000_FEC_TS.APK");

                Install_APK(strUSBDiskInstallPath, "firichsdk_test.apk");
                Install_APK(strUSBDiskInstallPath, "Meridian_v2.5.5c.apk");
                Install_APK(strUSBDiskInstallPath, "StabilityTest_v2.7.apk");
            }

            //Copy copy related files....
            /*
            adb shell mkdir -p /data/fec_config

            adb push fec_config_Intel_desktop/fec_config /data/fec_config
                ex: /storage/usbdisk/install/fec_config_Intel_desktop/fec_config
            adb shell mkdir -p /storage/sdcard0/Video
            adb push ToMissYou.mp3 /storage/sdcard0/Video
             */


            File fec_config_Dir = new File("/data/fec_config");
            ///storage/sdcard0/fec_config
            //File fec_config_Dir = new File("/storage/sdcard0/fec_config");
            if (!fec_config_Dir.exists()){
                fec_config_Dir.mkdir();
                dump_trace("fec_config_Dir.mkdir="+ fec_config_Dir.toString());
            }


        }
    }

    //http://stackoverflow.com/questions/26926274/install-android-apk-without-prompt
    //public void Install_APK_Silently(String filename){
    public void Install_APK_Silently(String installPath, String strAPK){
        String apkPath=installPath + "/apk/";
        String apkFileFullPath = apkPath + strAPK;

        File file = new File(apkFileFullPath);
        if(file.exists()){
            try {
                String command;
                //command = "adb install -r " + apkFileFullPath; // /storage/usbdisk/install/apk/.....apk
                command = "pm install -r " + apkFileFullPath; // /storage/usbdisk/install/apk/.....apk
                dump_trace("Install_APK_Silently: command=" + command);
                Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
                proc.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String Install_APK_Silently2(String installPath, String strAPK)
    {
        String apkPath=installPath + "/apk/";
        String apkAbsolutePath = apkPath + strAPK;

        //File file = new File(apkFileFullPath);

        String[] args = { "pm", "install", "-r", apkAbsolutePath };
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write('\n');
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        dump_trace("Install_APK_Silently2= result = " + result);
        return result;
    }

    public  void Install_APK(String installPath, String strAPK)
    {
        //http://stackoverflow.com/questions/16353892/how-to-programmatically-install-an-apk-from-a-service

        String apkPath=installPath + "/apk/";
        String apkFileFullPath = apkPath + strAPK;
        //File apkfile = new File(apkFileFullPath);
        File apkfile = new File(apkPath, strAPK);
        if (!apkfile.exists()) {
            return;
        }
            dump_trace("apkfile.exists URI=" + "file://" + apkfile.toString());
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.setDataAndType(
                Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        startActivity(installIntent);
    }
}
