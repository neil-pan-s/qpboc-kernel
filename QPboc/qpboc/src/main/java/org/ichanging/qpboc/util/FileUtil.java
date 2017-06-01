package org.ichanging.qpboc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ChangingP on 16/6/24.
 */
public class FileUtil {

    //写/data/data/<应用程序名>目录上的文件数据
    public static void writeAidFile(String fileName,byte[] data)
    {

        try{

            File file = new File(fileName);

            if (!file.exists())
            {
                file.createNewFile();
            }

            FileOutputStream fout = new FileOutputStream(fileName);

            fout.write(data,0,data.length);

            fout.close();
        }

        catch(Exception e){
            e.printStackTrace();
        }
    }

    //读/data/data/<应用程序名>目录上的文件数据
    public static byte[] readAidFile(String fileName)
    {
        byte [] buffer = null;

        try{
            File file = new File(fileName);
            if (!file.exists())
            {
                return null;
            }

            FileInputStream fis = new FileInputStream(file);

            int length = fis.available();
            buffer = new byte[length];
            fis.read(buffer);
            fis.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return buffer;
    }
}
