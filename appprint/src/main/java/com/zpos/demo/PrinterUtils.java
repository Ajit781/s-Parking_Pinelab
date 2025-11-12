package com.zpos.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by yyzz on 2018/8/2.
 */
public class PrinterUtils {

    public static Bitmap convertViewToBitmap(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

    public static Bitmap convertViewToBitmap2(View view) {
        Bitmap b = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);
        // Drawable bgDrawable = view.getBackground();
        // if (bgDrawable != null)
        // bgDrawable.draw(c);
        // else
        c.drawColor(Color.WHITE);
        view.draw(c);
        return b;
    }

    public static Bitmap convertViewToBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static Bitmap convertViewToBitmap(Context context, int layout) {
        View view = View.inflate(context, layout, null);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        return convertViewToBitmap(view);
    }

    public static Bitmap qualityCompress(Bitmap bmp, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        return BitmapFactory.decodeStream(isBm, null, null);
    }

    public static float calcRatio(float w, float h, float ww, float hh) {
        float ratio = 1.0f;
        if (w == 0) {
            ratio = h / hh;
        } else if (hh == 0) {
            ratio = w / ww;
        } else {
            if (w > h && w > ww) {
                ratio = w / ww;
            } else if (w < h && h > hh) {
                // 如果图片高度比宽度大，以高度为基准
                ratio = h / hh;
            }
        }
        if (ratio <= 0)
            ratio = 1;
        return ratio;
    }

    public static Bitmap compressBitmap(String filePath, int width) {
        return compressBitmap(filePath, width, 0, false);
    }

    public static Bitmap compressBitmap(String filePath, int width, int height, boolean isQuality) {
        Log.e( "filePath  ", "" + filePath);
        Bitmap bitmap = decodeBitmap(filePath, width, height);
        Bitmap result = compressBitmapSize(bitmap, width, height);
        if (isQuality) {
            int quality = 60;
            return qualityCompress(result, quality);
        }
        return result;
    }

    private static Bitmap decodeBitmap(String filePath, int width, int height) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opt);
        int w = opt.outWidth;
        int h = opt.outHeight;
        opt.inJustDecodeBounds = false;
        opt.inSampleSize = (int) calcRatio(w, h, width, height);
        return BitmapFactory.decodeFile(filePath, opt);
    }

    public static Bitmap compressBitmapSize(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float ratio = calcRatio(w, h, width, height);
        int realW = (int) (w / ratio);
        int realH = (int) (h / ratio);
        Bitmap result = Bitmap.createBitmap(realW, realH, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, realW, realH);
        Log.d("PrinterUtils", "ratio:" + ratio);
        Log.d("PrinterUtils", "w:" + w / ratio);
        Log.d("PrinterUtils", "h:" + h / ratio);
        canvas.drawBitmap(bitmap, null, rect, null);
        return result;
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @param size  大小 kb
     * @return
     */
    public static Bitmap compressBitmap(Bitmap image, int size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 90;
        while (baos.toByteArray().length > 1024 * size) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
            if (options < 0) {
                break;
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        return BitmapFactory.decodeStream(isBm, null, null);
    }

    /**
     * 根据给定的宽和高进行拉伸
     *
     * @param origin    原图
     * @param newWidth  新图的宽
     * @param newHeight 新图的高
     * @return new Bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    /**
     * 裁剪
     *
     * @param bitmap 原图
     * @return 裁剪后的图像
     */
    public static Bitmap cropBitmap(Bitmap bitmap, int startX, int startY, int cropWidth, int cropHeight) {
        return Bitmap.createBitmap(bitmap, startX, startY, cropWidth, cropHeight, null, false);
    }

    /**
     * 选择变换
     *
     * @param origin 原图
     * @param alpha  旋转角度，可正可负
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    /**
     * 偏移效果
     *
     * @param origin 原图
     * @return 偏移后的bitmap
     */
    public static Bitmap skewBitmap(Bitmap origin) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.postSkew(-0.6f, -0.3f);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    public static void saveImg2SDCard(Bitmap bitmap, String path, String photoName) {
        if (checkSDCardAvailable()) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File photoFile = new File(path, photoName + ".png");
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (bitmap != null) {
                    if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)) {
                        fileOutputStream.flush();
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static List<Bitmap> split(Bitmap bitmap, int height) {
        List<Bitmap> pieces = new ArrayList<Bitmap>();
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int count = (h % height == 0 ? h / height : (h / height + 1));
        int last = h % height;
        int add = 0;
        for (int i = 0; i < count; i++) {
            if (last > 0 && i == count - 1) {
                height = last;
            }
            Bitmap b = Bitmap.createBitmap(bitmap, 0, add, w, height);
            pieces.add(b);
            add += height;
        }
        return pieces;
    }
}
