package com.example.kristoffer.graphimaging;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

import java.io.File;

//Source: https://stackoverflow.com/questions/4646913/android-how-to-use-mediascannerconnection-scanfile
class SingleMediaScanner implements MediaScannerConnectionClient {

    private final MediaScannerConnection mMediaScannerConnection;
    private final File mFile;

    public SingleMediaScanner(Context context, File file) {
        mFile = file;
        mMediaScannerConnection = new MediaScannerConnection(context, this);
        mMediaScannerConnection.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mMediaScannerConnection.scanFile(mFile.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        mMediaScannerConnection.disconnect();
    }
}
