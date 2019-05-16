package com.example.lp.myapplication.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by xiaoyuren on 2018/3/7.
 * 项目名称：didano-robot
 * 类描述：Zip格式压缩、解压
 * company：www.didano.cn
 * email：vin.qin@didano.cn
 * 创建时间：2018/3/7 15:37
 */

public class ZipUtil {
    /**
     * @param file      待解压的文件 格式必须为.zip
     * @param outputDir 输出解压文件到该目录
     * @throws IOException
     */
    public static boolean unzip(File file, String outputDir) {
        boolean ret = false;
        if(file == null)
            return ret;
        try {
            /**
             * 支持中文
             */
            ZipFile zipFile = new ZipFile(file);
            Enumeration<ZipEntry> entris = (Enumeration<ZipEntry>)zipFile.entries();
            ZipEntry zipEntry;
            File tmpFile;
            BufferedOutputStream bos;
            InputStream is;
            byte[] buf = new byte[1024];
            int len;
            while (entris.hasMoreElements()) {
                zipEntry = entris.nextElement();
                /**
                 * 支持解压多层目录
                 */
                tmpFile = new File(outputDir + File.separator+ zipEntry.getName());

                if (zipEntry.isDirectory()) {
                    /**
                     * 目录
                     */
                    if (!tmpFile.exists()) {
                        tmpFile.mkdir();
                    }
                } else {
                    /**
                     * 文件
                     */
                    if (!tmpFile.exists()) {
                        tmpFile.createNewFile();
                    }
                    is = zipFile.getInputStream(zipEntry);
                    bos = new BufferedOutputStream(new FileOutputStream(tmpFile));
                    while ((len = is.read(buf)) > 0) {
                        bos.write(buf, 0, len);
                    }
                    bos.flush();
                    bos.close();
                }
            }
            ret = true;
        } catch (Exception e) {
        }

        return ret;
    }
}
