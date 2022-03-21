package com.samples.flironecamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.samples.flironecamera.MainActivity.TAG;

public class SaveImagesOnBackGround extends AsyncTask<MainActivity.ImageSaveParams,Void,Boolean> {
    // save progress of how many tasks are running on beckground
    private static int totalImages = 0;
    private static int finishImages = 0;
    private static int failImages = 0;
    public static String testTxt = "Status: Null";
    private final Lock lock = new ReentrantLock();
    private final Lock txtLock = new ReentrantLock();


    private TextView context;
    SaveImagesOnBackGround(TextView context) {
        this.context = context;
        totalImages++;
    }

    @Override
    protected Boolean doInBackground(MainActivity.ImageSaveParams... params) {
        // update static counters
        return saveImage(params[0].bitmap, params[0].path);
    }

    @Override
    protected void onPostExecute(Boolean saved) {
        if (saved) {
            Log.d(TAG, "saving image was success");
            lock.lock();
            try {
                finishImages++;
            }
            finally {
                lock.unlock();
            }
            //HANDLE SUCCESS
        } else {
            Log.d(TAG, "saving image fail");
            lock.lock();
            try {
                finishImages++;
                failImages++;
            }
            finally {
                lock.unlock();
            }
            //HANDLE ERROR
        }

        // publish result
        txtLock.lock();
        try {
            testTxt = "StatusRecord - total: " + totalImages + " finish:" + finishImages + " fail: " + failImages;
            context.setText(testTxt);
        }
        finally {
            txtLock.unlock();
        }
    }

    // call only after all the beckground process was finish
    static boolean resetCounters()
    {
        boolean success;
        if(totalImages == finishImages){
            totalImages = 0;
            failImages = 0;
            finishImages = 0;
            success = true;
        }
        else
        {
            Log.d(TAG, "beckground process yet to finish");
            success = false;
        }

        return success;
    }

    Boolean saveImage(Bitmap bitmap, File file)
    {
        boolean success = false;
        OutputStream outputStream = null;
        // create stream to the path and save the image
        try {
            // create the stream
            outputStream = new FileOutputStream(file);
            // save image
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            // success saving all the image
            // Toast.makeText(getApplicationContext(), "save in - " + file, Toast.LENGTH_SHORT).show();
            // Toast.makeText(getApplicationContext(), "bitmap size:" + dcSave.getByteCount(), Toast.LENGTH_SHORT).show();
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // Toast.makeText(getApplicationContext(), "can't access file stream", Toast.LENGTH_LONG).show();
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

        return success;
    }
}
