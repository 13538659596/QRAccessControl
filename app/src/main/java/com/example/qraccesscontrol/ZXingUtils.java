package com.example.qraccesscontrol;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 *   生成条形码和二维码的工具
 */
public class ZXingUtils {
    /**
     * 生成二维码 要转换的地址或字符串,可以是中文
     *
     * @param url
     * @param width
     * @param height
     * @return
     */
    public static Bitmap createQRImage(String url, final int width, final int height) {
        try {
            // 判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url,
                    BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成条形码
     *
     * @param context
     * @param contents
     *            需要生成的内容
     * @param desiredWidth
     *            生成条形码的宽带
     * @param desiredHeight
     *            生成条形码的高度
     * @param displayCode
     *            是否在条形码下方显示内容
     * @return
     */
    public static Bitmap creatBarcode(Context context, String contents,
                                      int desiredWidth, int desiredHeight, boolean displayCode) {
        Bitmap ruseltBitmap = null;
        /**
         * 图片两端所保留的空白的宽度
         */
        int marginW = 20;
        /**
         * 条形码的编码类型
         */
        BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;

        if (displayCode) {
            Bitmap barcodeBitmap = encodeAsBitmap(contents, barcodeFormat,
                    desiredWidth, desiredHeight);
            Bitmap codeBitmap = creatCodeBitmap(contents, desiredWidth + 2
                    * marginW, desiredHeight, context);
            ruseltBitmap = mixtureBitmap(barcodeBitmap, codeBitmap, new PointF(
                    0, desiredHeight));
        } else {
            ruseltBitmap = encodeAsBitmap(contents, barcodeFormat,
                    desiredWidth, desiredHeight);
        }

        return ruseltBitmap;
    }

    /**
     * 生成条形码的Bitmap
     *
     * @param contents
     *            需要生成的内容
     * @param format
     *            编码格式
     * @param desiredWidth
     * @param desiredHeight
     * @return
     * @throws WriterException
     */
    protected static Bitmap encodeAsBitmap(String contents,
                                           BarcodeFormat format, int desiredWidth, int desiredHeight) {
        final int WHITE = 0xFFFFFFFF;
        final int BLACK = 0xFF000000;

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = null;
        try {
            result = writer.encode(contents, format, desiredWidth,
                    desiredHeight, null);
        } catch (WriterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        // All are 0, or black, by default
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 生成显示编码的Bitmap
     *
     * @param contents
     * @param width
     * @param height
     * @param context
     * @return
     */
    protected static Bitmap creatCodeBitmap(String contents, int width,
                                            int height, Context context) {
        TextView tv = new TextView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setText(contents);
        tv.setHeight(height);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setWidth(width);
        tv.setDrawingCacheEnabled(true);
        tv.setTextColor(Color.BLACK);
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

        tv.buildDrawingCache();
        Bitmap bitmapCode = tv.getDrawingCache();
        return bitmapCode;
    }

    /**
     * 将两个Bitmap合并成一个
     *
     * @param first
     * @param second
     * @param fromPoint
     *            第二个Bitmap开始绘制的起始位置（相对于第一个Bitmap）
     * @return
     */
    protected static Bitmap mixtureBitmap(Bitmap first, Bitmap second,
                                          PointF fromPoint) {
        if (first == null || second == null || fromPoint == null) {
            return null;
        }
        int marginW = 20;
        Bitmap newBitmap = Bitmap.createBitmap(
                first.getWidth() + second.getWidth() + marginW,
                first.getHeight() + second.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas cv = new Canvas(newBitmap);
        cv.drawBitmap(first, marginW, 0, null);
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
        cv.save();
        cv.restore();

        return newBitmap;
    }

    /**
     * 用于创建一个二维码
     * @param content
     * @param width
     * @param height
     */
    public static Bitmap createQRCode(String content, int width, int height) {

        try {
            //1,创建实例化对象
            QRCodeWriter writer = new QRCodeWriter() ;
            //2,设置字符集
            Map<EncodeHintType, String> map = new HashMap<EncodeHintType, String>();
            map.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            //3，通过encode方法将内容写入矩阵对象
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height,map);
            //4，定义一个二维码像素点的数组，向每个像素点中填充颜色
            int[] pixels = new int[width*height];
            //5,往每一像素点中填充颜色（像素没数据则用黑色填充，没有则用彩色填充，不过一般用白色）
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (matrix.get(j, i)) {
                        pixels[i*width+j] = 0xff000000;
                    }else {
                        pixels[i*width+j] = 0xffffffff;
                    }
                }
            }
            //6,创建一个指定高度和宽度的空白bitmap对象
            Bitmap bm_QR = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            //7，将每个像素的颜色填充到bitmap对象
            bm_QR.setPixels(pixels, 0, width, 0, 0, width, height);

          /*  //8，创建一个bitmap对象用于作为其图标
            Bitmap bm_login = BitmapFactory.decodeResource(getResources(), R.drawable.img_kf_qq);
            //9，创建一个方法在二维码上添加一张图片
            if (addLogin(bm_QR,bm_login) != null) {
                imageView_main.setImageBitmap(addLogin(bm_QR,bm_login));
            }*/
        return bm_QR;

        } catch (WriterException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 用于向创建的二维码中添加一个logn
     * @param bm_QR
     * @param bm_login
     * @return
     */
    private Bitmap addLogin(Bitmap bm_QR, Bitmap bm_login) {
        if (bm_QR == null) {
            return null;
        }
        if (bm_login == null) {
            return bm_QR ;
        }

        //获取图片的宽高
        int bm_QR_Width = bm_QR.getWidth() ;
        int bm_QR_Height = bm_QR.getHeight();
        int bm_login_Width = bm_login.getWidth() ;
        int bm_login_Height = bm_login.getHeight();

        //设置logn的大小为二维码整体大小的1/5
        float scale_login = bm_QR_Width*1.0f /5/bm_login_Width ;
        Bitmap bitmap = Bitmap.createBitmap(bm_QR_Width, bm_QR_Height, Bitmap.Config.ARGB_8888);

        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bm_QR, 0, 0, null);
            canvas.scale(scale_login, scale_login, bm_QR_Width / 2, bm_QR_Height / 2);
            canvas.drawBitmap(bm_login, (bm_QR_Width - bm_login_Width) / 2, (bm_QR_Height - bm_login_Height) / 2, null);

            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }

        return bitmap;

    }
}
