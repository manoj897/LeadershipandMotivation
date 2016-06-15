package com.inmobi.manojkrishnan.LeadershipAndMotivation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.inmobi.manojkrishnan.LeadershipAndMotivation.network.NetworkUtils;
import com.inmobi.manojkrishnan.LeadershipAndMotivation.utils.KeyValueStore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by manoj.krishnan on 5/24/16.
 */
public class QuotesFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
    private ViewGroup mContainer;
    private ImageView mImageContainer;
    private volatile static Picasso sPicasso;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private ImageView mShare;
    private KeyValueStore mKeyValueStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_section_quotes, container, false);
        mContainer = (ViewGroup) view.findViewById(R.id.container);
        mImageContainer = (ImageView) view.findViewById(R.id.Quote_image);
        mShare = (ImageView) view.findViewById(R.id.share);
        startPicasso(QuotesFragment.this.getContext());
        if (!NetworkUtils.isNetworkAvailable(QuotesFragment.this.getActivity())) {
            Toast.makeText(QuotesFragment.this.getActivity(), "Please connect to network and launch again",
                    Toast.LENGTH_LONG).show();
            mShare.setVisibility(View.INVISIBLE);
                return view;
        }

        mKeyValueStore = KeyValueStore.getInstance(QuotesFragment.this.getActivity().getApplicationContext(), "QuotesCounter");
        if(mKeyValueStore.getInt("counter",0) == 0) {
            mKeyValueStore.putInt("counter", 1);
            Log.d("QuotesFragment", "count when firs time " + mKeyValueStore.getInt("counter", 1));

        }
        Log.d("testQuotes", "counter value == " + String.valueOf(mKeyValueStore.getInt("counter", 1)));
        sPicasso.load("http://motivationpics.s3-ap-southeast-1.amazonaws.com/"+mKeyValueStore.getInt("counter",1)+".jpg").resize(500,400).into(mImageContainer);

        mShare.setVisibility(View.VISIBLE);
        mImageContainer.setScaleType(ImageView.ScaleType.FIT_XY);
        mShare.setOnClickListener(this);
        return view;

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission()) {
                requestPermission();
            }
        }

    }

    @Override
    public void onClick(View v) {
        Log.d("testintent", "onCLick received");
        int i = v.getId();
        switch (i) {
            case R.id.share:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!checkPermission()) {
                        requestPermission();
                    }

                }

                // Get access to the URI for the bitmap
                Uri bmpUri = getLocalBitmapUri(mImageContainer);
                if (bmpUri != null) {
                    // Construct a ShareIntent with link to image
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    shareIntent.setType("image/jpeg");
                    // Launch sharing dialog for image
                    startActivity(Intent.createChooser(shareIntent, "Share Image"));

                }
                break;

        }
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(QuotesFragment.this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(QuotesFragment.this.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(QuotesFragment.this.getActivity(), "Write permission allows us to share data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(QuotesFragment.this.getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(mImageContainer,"Permission Granted, Now you can access location data.",Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(mImageContainer, "Permission Denied, You cannot access location data.", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file =  new File(Environment.getExternalStorageDirectory()+ File.separator +"share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private static void startPicasso(Context context) {
        Picasso picasso = sPicasso;
        if (null == picasso) {
            synchronized (MainActivity.class) {
                picasso = sPicasso;
                if (null == picasso) {
                    sPicasso = new Picasso.Builder(context.getApplicationContext()).build();
                }
            }
        }
    }
}
