package bhav.commit.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;

import bhav.commit.R;
import bhav.commit.data.api.Comic;
import bhav.commit.ui.FeedActivity;

public class ImageDownloader {

    public final static int REQUEST_WRITE_EXT_CODE = 1001;
    private static final String TAG = ImageDownloader.class.getSimpleName();

    public static void getImage(Comic c, Activity host, int errorRetryCount) {
        if(errorRetryCount == 0) {
            Snackbar.make(((FeedActivity) host).base,
                    "Storage Error: Failed to create directories", Snackbar.LENGTH_LONG).show();
            Log.e(TAG, "Storage Error: Failed to create directories");
            return;
        }
        if (ContextCompat.checkSelfPermission(host, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            File direct = new File(Environment.getExternalStorageDirectory()
                    + "/Pictures/CommitStrips");

            if (!direct.exists()) {
                if (!direct.mkdirs()) getImage(c, host, errorRetryCount-1);
            }
            DownloadManager mgr = (DownloadManager) host.getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(c.image);
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);

            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI
                            | DownloadManager.Request.NETWORK_MOBILE) //todo: make user changeable
                    .setAllowedOverRoaming(true).setTitle("Downloading: " + c.title)
                    .setDescription(String.valueOf(c.id))
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir("/Pictures/CommitStrips",
                            String.valueOf(c.id)+ "-" + c.title + ".png");

            mgr.enqueue(request);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(host,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                displayRationaleDialog(host);

            } else {
                requestStoragePermission(host);
            }
        }

    }

    private static void requestStoragePermission(Activity host) {
        ActivityCompat.requestPermissions(
                host,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_EXT_CODE
        );
    }

    private static void displayRationaleDialog(Activity host) {
        new AlertDialog.Builder(host)
                .setIcon(R.drawable.ic_permission_external_storage)
                .setMessage(host.getString(R.string.permission_rationale_sd_card))
                .setTitle(host.getString(R.string.permission_dialog_title))
                .setPositiveButton("Proceed", (dialog, which) -> requestStoragePermission(host))
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    Snackbar.make(((FeedActivity) host).base,
                            "Permission Denied: Downloading unavailable",
                            Snackbar.LENGTH_LONG).show();
                })
                .setCancelable(false)
                .show();
    }
}
