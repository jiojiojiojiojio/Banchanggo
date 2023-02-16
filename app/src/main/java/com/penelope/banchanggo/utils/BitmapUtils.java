package com.penelope.banchanggo.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;

public class BitmapUtils {

    // URI 로부터 절대경로 구하기

    public static String getRealPathFromUri(Context context, Uri contentUri) {

        Cursor cursor = null;
        String path = null;

        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver()
                    .query(contentUri, proj, null, null, null);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(columnIndex);
        }  finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    // 절대경로로부터 비트맵 가져오기

    public static Bitmap getBitmapFromPath(String path) {

        File image = new File(path);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) {

        Bitmap bitmap = null;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(
                        context.getContentResolver(), uri
                ));
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(
                        context.getContentResolver(), uri
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

}
