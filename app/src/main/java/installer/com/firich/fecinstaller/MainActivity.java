package installer.com.firich.fecinstaller;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    String strTagUtil = "MainActivity.";

    private boolean bDebugOn = true;
    private boolean g_bIsDesktop = false;

    private void dump_trace(String bytTrace) {
        if (bDebugOn)
            Log.d(strTagUtil, bytTrace);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SetDesktopFlag();
        this.mHandlerUIMsg = new Handler();
    }

    boolean SetDesktopFlag() {
        String strVersion = Build.DISPLAY;
        boolean contains_android5 = strVersion.contains("Edelweiss-T 5.1");
        boolean contains_android5_D = strVersion.contains("Edelweiss-D 5.1");
        if (contains_android5_D) {
            g_bIsDesktop = true;
            return true;
        } else {
            g_bIsDesktop = false;
            return false;
        }
    }


    public void install_factory_tool_click(View view) {
/*
        1.
        usbdisk 有4個 usbdisk1~4
        無法得知目前 usbdisk 位置...所以找到 fec_install.txt then instll all related apks and copy related files....
        */

        String strUSBDiskInstallPath = "/storage/usbdisk";
        fecMarFilekUtil fecMarFilekUtil_instalMark = new fecMarFilekUtil();
        fecMarFilekUtil_instalMark.findUDiskFecInstallFileName();
        if (fecMarFilekUtil_instalMark.isfindUDiskFecInstallFileName()) {
            strUSBDiskInstallPath = fecMarFilekUtil_instalMark.getUSBDiskPath() + "/install";
            //ex: /storage/usbdisk/install/
            dump_trace("USB Install Path=" + strUSBDiskInstallPath);

            Install_APK_Silently2_ALL(strUSBDiskInstallPath);

            Copy_FEC_Config_Data(strUSBDiskInstallPath);


            PostUIUpdateLog("Install Done.", R.id.textViewInstall);
        }
    }

    public void Copy_FEC_Config_Data(String strUSBDiskInstallPath)
    {
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
        if (!fec_config_Dir.exists()) {
            fec_config_Dir.mkdir();
            dump_trace("fec_config_Dir.mkdir=" + fec_config_Dir.toString());
        }
        File file = new File("/data/fec_config");
        file.setReadable(true, false);
        //file.setExecutable(true, false);
        file.setWritable(true, false);

        //fec_config_Intel_desktop/

        String src_fec_config_path = "/fec_config_Intel/fec_config"; //default is tablet.

        if (g_bIsDesktop)
        {
            src_fec_config_path = "/fec_config_Intel_desktop/fec_config";
        }
        String srcPath=strUSBDiskInstallPath + src_fec_config_path;
        String destPath = "/data/fec_config";

        dump_trace("srcPath="+srcPath);
        dump_trace("destPath="+destPath);
        File src_dir = new File(srcPath);
        File des_dir = new File(destPath);
        try {
            copyDirectory(src_dir, des_dir);
        } catch (IOException e) {
            e.printStackTrace();
            dump_trace("Copy feconfig dir fail!!");
        }
        //Copy_config_data(strUSBDiskInstallPath); fail..will cause exception!!

        //adb push fec_config_Intel/fec_config /data/fec_config

        //adb shell mkdir -p /storage/sdcard0/Video
        //adb push ToMissYou.mp3 /storage/sdcard0/Video

        File ToMissYouDir = new File("/storage/sdcard0/Video");
        ///storage/sdcard0/fec_config
        //File fec_config_Dir = new File("/storage/sdcard0/fec_config");
        if (!ToMissYouDir.exists()) {
            ToMissYouDir.mkdir();
            dump_trace("ToMissYouDir.mkdir=" + ToMissYouDir.toString());
            ToMissYouDir.setReadable(true, false);
            ToMissYouDir.setWritable(true, false);
        }

    }
    public void Install_APK_With_Prompt(String strUSBDiskInstallPath) {
        boolean bTestInstallAPKS = false;
        //With prompt to confirm to install APK.
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
    }

    public void Install_APK_Silently2_ALL(String strUSBDiskInstallPath) {
        boolean bTestInstallAPKS = false;
        if (bTestInstallAPKS) {
            if (g_bIsDesktop) {
                Install_APK_Silently2(strUSBDiskInstallPath, "FactoryTools_Desktop_1009.apk");
            } else {
                Install_APK_Silently2(strUSBDiskInstallPath, "FactoryTools_Tablet_1009.apk");
            }
            Install_APK_Silently2(strUSBDiskInstallPath, "battery_test-signed.apk");
            Install_APK_Silently2(strUSBDiskInstallPath, "cputemp.apk");
            Install_APK_Silently2(strUSBDiskInstallPath, "eGalaxCalibrator_v0.12-config.apk");
            Install_APK_Silently2(strUSBDiskInstallPath, "eGalaxSensorTester_v0.25_PCAP3188UR_1278_v04_0000_C000_FEC_TS.APK");

            Install_APK_Silently2(strUSBDiskInstallPath, "firichsdk_test.apk");
            Install_APK_Silently2(strUSBDiskInstallPath, "Meridian_v2.5.5c.apk");
            Install_APK_Silently2(strUSBDiskInstallPath, "StabilityTest_v2.7.apk");
        }


    }

    //http://stackoverflow.com/questions/26926274/install-android-apk-without-prompt
    //public void Install_APK_Silently(String filename){
    public void Install_APK_Silently(String installPath, String strAPK) {
        String apkPath = installPath + "/apk/";
        String apkFileFullPath = apkPath + strAPK;

        File file = new File(apkFileFullPath);
        if (file.exists()) {
            try {
                String command;
                //command = "adb install -r " + apkFileFullPath; // /storage/usbdisk/install/apk/.....apk
                command = "pm install -r " + apkFileFullPath; // /storage/usbdisk/install/apk/.....apk
                dump_trace("Install_APK_Silently: command=" + command);
                Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
                proc.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean execute_chmode_command(){

    Process process = null;
    DataOutputStream dataOutputStream = null;

    try{
        process = Runtime.getRuntime().exec("su");
        dataOutputStream = new DataOutputStream(process.getOutputStream());
        dataOutputStream.writeBytes("chmod 666 /data/fec_config\n");
        dataOutputStream.writeBytes("exit\n");
        dataOutputStream.flush();
        process.waitFor();
    }catch(Exception e)
    {
        dump_trace("chmod command fail!"+e.toString());
        return false;
    }
    finally{
        try {
            if (dataOutputStream != null) {
                dataOutputStream.close();
            }
            process.destroy();
        } catch (Exception e) {
            dump_trace("chmod command fail!"+e.toString());
            return false;
        }
    }
    return true;
    }
    public String execute_shell_command(String[] args)
    {
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

    // If targetLocation does not exist, it will be created.
    //http://stackoverflow.com/questions/5715104/copy-files-from-a-folder-of-sd-card-into-another-folder-of-sd-card
    public void copyDirectory(File sourceLocation , File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
            }

            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            // make sure the directory we plan to store the recording in exists
            File directory = targetLocation.getParentFile();
            if (directory != null && !directory.exists() && !directory.mkdirs()) {
                throw new IOException("Cannot create dir " + directory.getAbsolutePath());
            }

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            dump_trace("copyDirectory:sourceLocation"+sourceLocation.getAbsolutePath());
            dump_trace("copyDirectory:targetLocation"+targetLocation.getAbsolutePath());
            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            //File fileMode = new File(targetLocation);
            boolean readableOK = targetLocation.setReadable(true, false);
            dump_trace("copyDirectory: set readable:"+ targetLocation.getAbsolutePath() + "="+readableOK);
            //file.setExecutable(true, false);
            targetLocation.setWritable(true, false);
        }
    }

    public String Copy_config_data(String installPath)
    {
        String srcPath=installPath + "/fec_config_Intel/fec_config";
        String destPath = "/data";

        //File file = new File(apkFileFullPath);

        dump_trace("src path="+ srcPath);
        dump_trace("dest path="+ destPath);
        //adb push fec_config_Intel/fec_config /data/fec_config
        // su -c cp -r /storage/usbdisk/install/fec_config_Intel/fec_config /data
        String[] args = { "cp", "-r", srcPath, destPath };
        String result = "";
        result = execute_shell_command(args);

        //String[] argsChmod = { "su","-c","chmod", "-R", "666", "/data/fec_config"};
        //   chmod 666 /data/fec_config
        //result = execute_shell_command(argsChmod);
        //execute_chmode_command();



        return result;
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

    private Handler mHandlerUIMsg = null; //Brian
    private void PostUIUpdateLog(final String msg, final int TextViewID)
    {
        this.mHandlerUIMsg.post(new Runnable()
        {
            public void run()
            {
                //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                final TextView textViewResultDeviceID = (TextView) findViewById(TextViewID);
                dump_trace("PostUIUpdateLog:TextViewID="+TextViewID);
                textViewResultDeviceID.setText(msg);
            }
        });
    }
}
