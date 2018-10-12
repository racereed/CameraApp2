package camera1.themaestrochef.com.cameraapp.Utilities;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

import java.io.File;
import java.io.FileOutputStream;

import camera1.themaestrochef.com.cameraapp.R;


public class ImageHelper {

    public static Bitmap getLastTakenImage(Context context) {
        // Find the last picture
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };

        final Cursor cursor = context.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

        // Put it in the image view
        if (cursor != null && cursor.moveToFirst()) {
            String imageLocation = cursor.getString(1);
            File imageFile = new File(imageLocation);
            if (imageFile.exists()) {   // TODO: is there a better way to do this?
                return BitmapFactory.decodeFile(imageLocation);
            }
        }
        return null;
    }

    public static Bitmap flipImage(Bitmap bitmap) {
        //Moustafa: fix issue of image reflection due to front camera settings
        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    /**
     * Embeds an image watermark over a source image to produce
     * a watermarked one.
     * @param watermarkImageFile The image file used as the watermark.
     * @param sourceImageFile The source image file.
     * @param destImageFile The output image file.
     */
    /**
     * Adds a watermark on the given image.
     */
    public static Bitmap addWatermark(Resources res, Bitmap source, Context context) {
        int w, h;
        Canvas c;
        Paint paint;
        Bitmap bmp, watermark;
        Matrix matrix;
        float scale;
        RectF r;
        w = source.getWidth();
        h = source.getHeight();
        // Create the new bitmap
        bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
        // Copy the original bitmap into the new one
        c = new Canvas(bmp);
        c.drawBitmap(source, 0, 0, paint);
        // Load the watermark
//        watermark = BitmapFactory.decodeResource(context.getResources(), R.mipmap
//                .ic_launcher);

//        watermark= BitmapFactory.decodeResource( context.getResources(), R.drawable.ic_camera);
        watermark = getBitmapFromVectorDrawable(context, R.mipmap.ic_launcher);

//        Drawable vectorDrawable = VectorDrawableCompat.create(res, R.mipmap.ic_launcher,  context.getTheme());
//        watermark = ((BitmapDrawable) vectorDrawable).getBitmap();
        // Scale the watermark to be approximately 15% of the source image height
        scale = (float) (((float) h * 0.15) / (float) watermark.getHeight());
//         Create the matrix
        matrix = new Matrix();
        matrix.postScale(scale, scale);
        // Determine the post-scaled size of the watermark
        r = new RectF(0, 0, watermark.getWidth(), watermark.getHeight());
        matrix.mapRect(r);

        // Move the watermark to the bottom right corner
//        matrix.postTranslate(w - r.width(), h - r.height());
//        Move the watermark to the top left corner
        matrix.postTranslate(50, 50);

        // Draw the watermark
        c.drawBitmap(watermark, matrix, paint);
        // Free up the bitmap memory
        watermark.recycle();
        return bmp;
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}
