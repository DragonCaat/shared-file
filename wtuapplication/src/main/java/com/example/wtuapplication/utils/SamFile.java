package com.example.wtuapplication.utils;

/**
 * Created by 梁 on 2017/9/20.
 */


import android.app.ProgressDialog;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class SamFile {
    private static final String TAG = "hhhh";
    private static String smburl = "smb://cj:cj@192.168.16.100/cj share";

    //zhoushun ----------------------------
    public static void createDir(String dir) throws Exception {
        SmbFile fp = new SmbFile(smburl + "//" + dir);
        Log.i(TAG, "createDir: ");
        System.out.println("fieldir+++++++++++++++++++++=" + smburl + "//" + dir);
        //File fp = new File("Z://"+dir);
        // 目录已存在创建文件夹
        if (fp.exists() && fp.isDirectory()) {

        } else {
            // 目录不存在的情况下，会抛出异常
            fp.mkdir();
        }
    }
    public static void delete(String urlPath) throws Exception
    {
        SmbFile file= new SmbFile(urlPath);
        file.delete();
    }

    public static void copyDir(String fileName, String target) throws Exception {
        InputStream in = null;
        OutputStream out = null;
        try {
            File fp = new File(fileName);

            SmbFile remoteFile = new SmbFile(smburl + "//" + fp.getName());

            in = new BufferedInputStream(new FileInputStream(fp));
            out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            // 刷新此缓冲的输出流
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void fileUpload(String urlPath,String fileName) {
        try{
            String newname = "";
            fileName = StringUtils.replace(fileName, "\\", "/");
            if (fileName.indexOf("/") > -1)
                newname = fileName.substring(fileName.lastIndexOf("/") + 1);
            else {
                newname = fileName;
            }
            SmbFileOutputStream file_out = null;
            file_out = new SmbFileOutputStream(urlPath+"//"+newname);
            File file_in = new File(fileName);
            InputStream in = new BufferedInputStream(new FileInputStream(file_in));
            OutputStream out = new BufferedOutputStream(file_out);
            byte[] buffer = new byte[1024];
            int len;

            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
            out.flush();
            out.close();
            in.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> listDir(String urlPath) throws MalformedURLException {
        ArrayList<String> fileNameList = new ArrayList<>();
        //String path = "smb://" +userName+":"+passWord+"@"+ ip;
        SmbFile fp = new SmbFile(urlPath);
        try {
            SmbFile[] fpTemp = fp.listFiles();
            for (int i = 0; i < fpTemp.length; i++) {
                fileNameList.add(fpTemp[i].getName());
                //Log.i(TAG, "hello" + fpTemp[i].getName());
            }
        } catch (SmbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fileNameList;
    }
    public static ArrayList<String> listDir_(String urlpath) throws MalformedURLException {
        ArrayList<String> fileNameList = new ArrayList<>();
        //String path = "smb://" +userName+":"+passWord+"@"+ ip+"/"+fileName+"/";
        Log.v("jjjj",urlpath);
        SmbFile fp = new SmbFile(urlpath);
        try {
            SmbFile[] fpTemp = fp.listFiles();
            for (int i = 0; i < fpTemp.length; i++) {
                    fileNameList.add(fpTemp[i].getName());
                //Log.i(TAG, "hello" + fpTemp[i].getName());
            }
        } catch (SmbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fileNameList;
    }
    public static String returnUrl(String ip,String userName,String passWord)
    {
        String urlPath = "smb://" +userName+":"+passWord+"@"+ ip;
       // "smb://" +userName+":"+passWord+"@"+ ip;
        return  urlPath;
    }
    public static String returnUrl_(String urlpath,String filename)
    {
        String urlPath = urlpath + "/"+filename+"/";
       // smb://" +userName+":"+passWord+"@"+ ip+"/" cj share/ /;
        return urlPath;

    }
}
