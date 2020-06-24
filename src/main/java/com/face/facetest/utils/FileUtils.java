package com.face.facetest.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zs
 * @Date: 2020/6/10 17:54
 * @Description:
 */
public class FileUtils {

    public static void appendToFile(String path,String content){
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(path, true);
            writer.write(content+"\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void appendToFile(String path,List<String> contents){
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(path, true);
            if(contents != null && contents.size() > 0){
                for (String con : contents) {
                    writer.write(con+"\r\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static List<String> getAlls(String path){
        File file = new File(path);
        boolean exists = file.exists();
        ArrayList<String> result = null;
        if(exists){
            BufferedReader reader = null;
            result = new ArrayList<>();
            try {
                // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
                reader = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    result.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    public static void remove(String path){
        File file = new File(path);
        boolean exists = file.exists();
        if(exists){
            file.delete();
        }
    }
}
