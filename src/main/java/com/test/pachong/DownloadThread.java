package com.test.pachong;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import javax.swing.JOptionPane;

public class DownloadThread extends Thread 
{
    public static final String DOWNLOAD_PATH= "/opt/dt";
    
    String url;
    boolean isOver=false;
    public DownloadThread(String url) {
        this.url = url.trim();
        this.setName(Tools.getURLFileName(url));
    }

    public void run() {
        try {
            
            URL url = new URL(this.url);

            InputStream in = url.openStream();

        //  BufferedInputStream bin = new BufferedInputStream(in);

            File d = new File(DOWNLOAD_PATH + File.separator + "pachongDown");
            if (d.exists() == false) {
                d.mkdir();
            }

            Date date=new Date();
            File pachongDown = new File(DOWNLOAD_PATH + File.separator + "pachongDown",
                    date.getTime()+"_"+Tools.getURLFileName(this.url));
            if (!pachongDown.exists()) {
                pachongDown.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(pachongDown);
//          BufferedOutputStream bfos = new BufferedOutputStream(fos);

            int b = 0;
            //byte buf[] = new byte[1024];

            while ((b = in.read()) != -1) {
                fos.write(b);
            }
            fos.flush();
            //bfos.flush();

            in.close();
            fos.close();

            //bin.close();
            //bfos.close();

        } catch (Exception e) {
          isOver=true;
            JOptionPane.showMessageDialog(null, "下载地资源找不到！问题出在服务器上！");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        isOver=true;
    }
}