package com.think360.picsloot.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.think360.picsloot.activities.PicsLootActivity;
import com.think360.picsloot.api.AppController;
import com.think360.picsloot.fragments.FinishDeliveryOrderFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;



/**
 * Created by think360 on 09/12/17.
 */

public class ProgressRequestBody extends RequestBody {
    private File mFile;
    private String mPath;
    private UploadCallbacks mListener;
   // long uploaded=0;
    private static final int DEFAULT_BUFFER_SIZE = 2048;
   // private long uploaded = 0;
    public interface UploadCallbacks {
        void onProgressUpdate(int percentage);
        void onError();
        void onFinish();
    }

    public ProgressRequestBody(final File file, final  UploadCallbacks listener) {

        mFile = file;
        mListener = listener;
    }

    @Override
    public MediaType contentType() {
        // i want to upload only images
        return MediaType.parse("image/*");
    }

    @Override
    public long contentLength() throws IOException {

        return mFile.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
       // long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
       //  long uploaded = 0;


        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) !=-1) {

                // update progress on UI thread
                handler.post(new ProgressUpdater(PicsLootActivity.Companion.getUploaded(), AppController.getSharedPref().getLong("allImageSize",0)));

                PicsLootActivity.Companion.uploaded(read);

                sink.write(buffer, 0, read);
            }
        } finally {
            in.close();
        }
    }

    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;
        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            mListener.onProgressUpdate((int)(100*mUploaded / mTotal ));
        }
    }

}