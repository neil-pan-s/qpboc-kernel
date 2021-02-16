package org.ichanging.qpboc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ChangingP on 16/6/24.
 */
public class FileUtil {

    //Write file data on the /data/data/<application name> directory
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

    //Read file data on the /data/data/<application name> directory
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
