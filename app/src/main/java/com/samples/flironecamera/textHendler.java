/*******************************************************************
 * @title FLIR THERMAL SDK
 * @file FrameDataHolder.java
 * @Author FLIR Systems AB
 *
 * @brief Container class that holds references to Bitmap images
 *
 * Copyright 2019:    FLIR Systems
 ********************************************************************/

package com.samples.flironecamera;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class textHendler {
    public static final String Data_Path = "/SIPL_FlirOne/data";
    public static final String Data_txt = "/data";
    public static final String Log_Path = "/SIPL_FlirOne/log";
    public static final String Log_txt = "/log";

    public static final String Fusion_Mode_Line = "FUSION_MODE";
    public static final String To_Record_Line = "TO_WE_RECORD"; // "T" or "F"
    public static final String Record_Dir_Name = "RECORD_DIR_NAME";
    public static final String Record_Frame_Counter = "RECORD_FRAME_COUNTER";

    // todo: make the hole class run on a fread
    public static void append_txt(String Path, String name, String append_line, String append_data) {
        String FullPath = Path + name + ".txt";
        // stream
        InputStream inputStream = null;
        String data = "";

        // create path
        File filepath = Environment.getExternalStorageDirectory();
        File file = new File(filepath.getAbsolutePath()+FullPath);

        // read the txt
        try {
            inputStream = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            String text = "";

            while ((text = br.readLine()) != null) {
                if((text.split(":"))[0].equals("DATE")) {
                    String timeStamp = new SimpleDateFormat("dd-MM-yyyy__HH-mm-ss-SSS").format(new Date());
                    text = "DATE:" + timeStamp;
                }
                sb.append(text).append("\n");
            }

            //append data
            text = append_line + ":" + append_data;
            sb.append(text).append("\n");

            data = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close stream
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // save the data that was change
        if(!data.equals("")) {
            save_txt(Path, name, data);
        }
    }

    public static void clear_txt(String Path, String name) {
        save_txt(Path, name, "");
    }

    public static void save_txt(String Path, String name, String data) {
        // stream
        OutputStream outputStream = null;

        // create path
        File filepath = Environment.getExternalStorageDirectory();
        File dir = new File(filepath.getAbsolutePath()+Path);
        dir.mkdir();

        // create files path
        File file = new File(dir, name+".txt");

        // save the text
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(data.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close stream
            if(outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String load_txt(String Path) {
        // stream
        FileInputStream inputStream = null;
        String data = "";

        // create path
        File filepath = Environment.getExternalStorageDirectory();
        File file = new File(filepath.getAbsolutePath()+Path);

        // read the txt
        try {
            inputStream = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            data = sb.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close stream
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //return the txt that was read
        return data;
    }

    public static void update_line_txt(String Path,String FileName , String line_name, String rewrite) {
        String FullPath = Path + FileName + ".txt";
        // stream
        InputStream inputStream = null;
        String data = "";
        boolean rewrite_flag = false;

        // create path
        File filepath = Environment.getExternalStorageDirectory();
        File file = new File(filepath.getAbsolutePath()+FullPath);

        // read the txt
        try {
            inputStream = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            String text = "";

            while ((text = br.readLine()) != null) {
                if((text.split(":"))[0].equals("DATE")) {
                    String timeStamp = new SimpleDateFormat("dd-MM-yyyy__HH-mm-ss-SSS").format(new Date());
                    text = "DATE:" + timeStamp;
                }
                else if((text.split(":"))[0].equals(line_name)) {
                    text = line_name + ":" + rewrite;
                    rewrite_flag = true;
                }
                sb.append(text).append("\n");
            }

            // if there is no line with this name
            if(!rewrite_flag) {
                text = line_name + ":" + rewrite;
                sb.append(text).append("\n");
            }
            data = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close stream
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // save the data that was change
        if(!data.equals("")) {
            save_txt(Path, FileName, data);
        }
    }

    public static String find_line_txt(String Path,String FileName , String line_name) {
        String FullPath = Path + FileName + ".txt";
        // stream
        InputStream inputStream = null;
        String data = null;

        // create path
        File filepath = Environment.getExternalStorageDirectory();
        File file = new File(filepath.getAbsolutePath()+FullPath);

        // read the txt
        try {
            inputStream = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);
            String text = "";

            // find the line
            while ((text = br.readLine()) != null) {
                if((text.split(":"))[0].equals(line_name)) {
                    data = (text.split(":"))[1];
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close stream
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return data;
    }
}
