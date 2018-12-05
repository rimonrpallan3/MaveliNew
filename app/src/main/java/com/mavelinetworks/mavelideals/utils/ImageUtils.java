package com.mavelinetworks.mavelideals.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.appconfig.AppContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Droideve on 9/26/2016.
 */

public class ImageUtils {



    public static Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            if(AppConfig.APP_DEBUG)
                e.printStackTrace();
            return null;

        }
    }


    public static ImageUtils mInstant;

    public static ImageUtils getInstant(){
        if(mInstant==null){
            mInstant = new ImageUtils();
        }
        return mInstant;
    }



    public Uri getCorrectOrientation(Context context, Uri myUri){

        return getImageUri(context,prepareOrientationBitmap(context,myUri),myUri.toString());
    }

    public static int ORIENTATION_ROTATE_270;
    public static int ORIENTATION_ROTATE_180;
    public static int ORIENTATION_ROTATE_90;
    public static int ORIENTATION_NORMAL;


    public int getRotation(Context context, Uri myUri){

        int rotation = 0;

        try {

            File imageFile = new File(myUri.toString());
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
            }


            return rotation;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return rotation;
    }

    public Uri setToNextRotation(Context context, Uri myUri){

        int rotateTo = 0;

        try {
            context.getContentResolver().notifyChange(myUri, null);

            File imageFile = new File(myUri.toString());
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotateTo = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotateTo = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotateTo = 270;
                    break;
            }


            Log.e("rotateTo",rotateTo+"");


            Bitmap rotattedBitmap= BitmapFactory.decodeFile(myUri.toString());
            Matrix matrix = new Matrix();
            matrix.postRotate(rotateTo);
            //get convert bitmap to another rotation
            Bitmap bitmap = Bitmap.createBitmap(rotattedBitmap, 0, 0, rotattedBitmap.getWidth(), rotattedBitmap.getHeight(), matrix,       true);
            return getImageUri(context,bitmap,myUri.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }


        return myUri;
    }

    public Uri setRotation(Context context, Uri myUri, int rotate){

        int currentRotation =0;

        try {
            context.getContentResolver().notifyChange(myUri, null);

            File imageFile = new File(myUri.toString());
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    currentRotation = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    currentRotation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    currentRotation = 90;
                    break;
            }



            Bitmap rotattedBitmap= BitmapFactory.decodeFile(myUri.toString());
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            //get convert bitmap to another rotation
            Bitmap bitmap = Bitmap.createBitmap(rotattedBitmap, 0, 0, rotattedBitmap.getWidth(), rotattedBitmap.getHeight(), matrix,       true);

            return getImageUri(context,bitmap,myUri.toString());


        } catch (IOException e) {
            e.printStackTrace();
        }


       return myUri;
    }


    public Bitmap prepareOrientationBitmap(Context context, Uri myUri){
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(myUri, null);

            File imageFile = new File(myUri.toString());
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Bitmap rotattedBitmap= BitmapFactory.decodeFile(myUri.toString());
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            return Bitmap.createBitmap(rotattedBitmap, 0, 0, rotattedBitmap.getWidth(), rotattedBitmap.getHeight(), matrix,       true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public Uri getImageUri(Context inContext, Bitmap inImage, String dest) {

        //yteArrayOutputStream bytes = new ByteArrayOutputStream();
        //inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage,
//                inContext.getResources().getString(R.string.app_name), null);
        try {
            FileOutputStream out = new FileOutputStream(dest);
            inImage.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Uri.parse(dest);
    }




    public String compressImage(Context mContext, String imageUri) {

        String filePath = getRealPathFromURI(imageUri,mContext);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {               imgRatio = maxHeight / actualHeight;                actualWidth = (int) (imgRatio * actualWidth);               actualHeight = (int) maxHeight;             } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);
//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }




    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI, Context context) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }       final float totalPixels = width * height;       final float totalReqPixelsCap = reqWidth * reqHeight * 2;       while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }



    public static class PrepareImagesData extends AsyncTask<Uri,Uri,Uri> {


        private Context context;
        private String pathFile;
        private String pathDest;
        private ProgressDialog progress;

        private OnCompressListner onCompressed;


        public PrepareImagesData(Context context, String pathFile, String pathDest){
            this.context = context;
            this.pathDest = pathDest;
            this.pathFile = pathFile;
        }

        public PrepareImagesData(Context context, String pathFile, String pathDest, OnCompressListner onCompressed){
            this.context = context;
            this.pathDest = pathDest;
            this.pathFile = pathFile;

            setListener(onCompressed);



        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {


            }catch (Exception e){

            }

        }

        @Override
        protected Uri doInBackground(Uri... params) {


            if(AppContext.DEBUG){
                Log.e("pathFile",pathFile);
                Log.e("pathDest",pathDest);
            }

            File file = ImageHelper.compressForUpload(pathFile,pathDest,ImageHelper.IMAGE_MAX_WIDTH,
                    ImageHelper.IMAGE_QUALITY_HIGH);

            if(file.exists()){

                Uri correctOrientationFile = Uri.parse(file.getPath());
                return correctOrientationFile;
            }else{
                return null;
            }

        }

        @Override
        protected void onPostExecute(Uri uri) {

            if(AppContext.DEBUG){
                Log.e("fileCompressed",uri.getPath());
            }

            if(onCompressed!=null){
                onCompressed.onCompressed(uri.toString(),pathFile.toString());
            }

            super.onPostExecute(uri);
        }

        public void setListener(OnCompressListner listner){

            this.onCompressed = listner;

        }

        public interface OnCompressListner{
            public void onCompressed(String newPath, String oldPath);
        }



    }
}
