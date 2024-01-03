package com.finals.friendsfinder.customizes;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.finals.friendsfinder.R;
import com.finals.friendsfinder.utilities.commons.Constants;

public class FinderView extends View {
    private static final String TAG = "ViewFinderView";

    private Rect mFramingRect;

    private static final float PORTRAIT_WIDTH_RATIO = 327f / 375;
    private static final float PORTRAIT_WIDTH_HEIGHT_RATIO = 513.0f / 327;

    private static final float TABLET_PORTRAIT_WIDTH_RATIO = 327f / 375;
    private static final float TABLET_PORTRAIT_WIDTH_HEIGHT_RATIO = 1.0f;

    private static final float LANDSCAPE_HEIGHT_RATIO = 5f / 8;
    private static final float LANDSCAPE_WIDTH_HEIGHT_RATIO = 1.4f;

    private static final float TABLET_LANDSCAPE_HEIGHT_RATIO = 5f / 8;
    private static final float TABLET_LANDSCAPE_WIDTH_HEIGHT_RATIO = 1.0f;

    private static final int MIN_DIMENSION_DIFF = 80;

    private static final float DEFAULT_SQUARE_DIMENSION_RATIO = 327f / 513;

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private int scannerAlpha = 4;
    private static final int POINT_SIZE = 10;
    private static final long ANIMATION_DELAY = 80l;

    private final int mDefaultLaserColor = getResources().getColor(R.color.viewfinder_laser);
    private final int mDefaultMaskColor = getResources().getColor(R.color.viewfinder_mask);
    private final int mDefaultBorderColor = getResources().getColor(R.color.viewfinder_border);
    private final int mDefaultBorderStrokeWidth = getResources().getInteger(R.integer.viewfinder_border_width);
    private final int mDefaultBorderLineLength = getResources().getInteger(R.integer.viewfinder_border_length);

    protected Paint mLaserPaint;
    protected Paint mFinderMaskPaint;
    protected Paint mBorderPaint;
    protected int mBorderLineLength;
    protected boolean mSquareViewFinder;
    private boolean mIsLaserEnabled = true;
    private int mViewFinderOffset = 0;
    private Paint transparentPaint;
    private Bitmap windowFrame;

    public FinderView(Context context) {
        super(context);
        init();
    }

    public FinderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_HARDWARE, null);
        //set up laser paint
        mLaserPaint = new Paint();
        mLaserPaint.setColor(mDefaultLaserColor);
        mLaserPaint.setStyle(Paint.Style.FILL);

        //finder mask paint
        mFinderMaskPaint = new Paint();
        mFinderMaskPaint.setColor(mDefaultMaskColor);

        transparentPaint = new Paint();
        transparentPaint.setColor(Color.TRANSPARENT);
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        //border paint
        mBorderPaint = new Paint();
        mBorderPaint.setColor(mDefaultBorderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mDefaultBorderStrokeWidth);
        mBorderPaint.setAntiAlias(true);

        mBorderLineLength = mDefaultBorderLineLength;
    }

    //    @Override
    public void setLaserColor(int laserColor) {
        mLaserPaint.setColor(laserColor);
    }

    //    @Override
    public void setMaskColor(int maskColor) {
        mFinderMaskPaint.setColor(maskColor);
    }

    //    @Override
    public void setBorderColor(int borderColor) {
        mBorderPaint.setColor(borderColor);
    }

    //    @Override
    public void setBorderStrokeWidth(int borderStrokeWidth) {
        mBorderPaint.setStrokeWidth(borderStrokeWidth);
    }

    //    @Override
    public void setBorderLineLength(int borderLineLength) {
        mBorderLineLength = borderLineLength;
    }

    //    @Override
    public void setLaserEnabled(boolean isLaserEnabled) {
        mIsLaserEnabled = isLaserEnabled;
    }

    //    @Override
    public void setBorderCornerRounded(boolean isBorderCornersRounded) {
        if (isBorderCornersRounded) {
            mBorderPaint.setStrokeJoin(Paint.Join.ROUND);
        } else {
            mBorderPaint.setStrokeJoin(Paint.Join.BEVEL);
        }
    }

    //    @Override
    public void setBorderAlpha(float alpha) {
        int colorAlpha = (int) (255 * alpha);
        mBorderPaint.setAlpha(colorAlpha);
    }

    //    @Override
    public void setBorderCornerRadius(int borderCornersRadius) {
        mBorderPaint.setPathEffect(new CornerPathEffect(borderCornersRadius));
    }

    //    @Override
    public void setViewFinderOffset(int offset) {
        mViewFinderOffset = offset;
    }

    // TODO: Need a better way to configure this. Revisit when working on 2.0
//    @Override
    public void setSquareViewFinder(boolean set) {
        mSquareViewFinder = set;
    }

    public void setupViewFinder() {
        updateFramingRect();
        invalidate();
    }

    public Rect getFramingRect() {
        return mFramingRect;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (getFramingRect() == null) {
            return;
        }

        drawViewFinderMask(canvas);
        drawViewFinderBorder(canvas);

        if (mIsLaserEnabled) {
            drawLaser(canvas);
        }
    }

    public void drawViewFinderMask(Canvas canvas) {
        if (windowFrame == null) {
            createWindowFrame(getWidth(), getHeight());
        }
        canvas.drawBitmap(windowFrame, 0, 0, null);
    }

    public void drawViewFinderBorder(Canvas canvas) {
        Rect framingRect = getFramingRect();
        float radius = getResources().getDimensionPixelSize(R.dimen.radius_finder);

        Path borderPath = createCornersPath(framingRect.left + radius / 2.0f,
                framingRect.top + radius / 2.0f,
                framingRect.right - radius / 2.0f,
                framingRect.bottom - radius / 2.0f,
                radius, radius / 2.0f);
        canvas.drawPath(borderPath, mBorderPaint);
    }

    protected void createWindowFrame(int width, int height) {
        windowFrame = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); // Create a new image we will draw over the map
        Canvas osCanvas = new Canvas(windowFrame); // Create a   canvas to draw onto the new image
        RectF outerRectangle = new RectF(0, 0, width, height);
        osCanvas.drawRect(outerRectangle, mFinderMaskPaint);

        Rect framingRect = getFramingRect();
        RectF rect = new RectF(framingRect.left + mDefaultBorderStrokeWidth / 2.0f,
                framingRect.top + mDefaultBorderStrokeWidth / 2.0f,
                framingRect.right - mDefaultBorderStrokeWidth / 2.0f,
                framingRect.bottom - mDefaultBorderStrokeWidth / 2.0f);
        float radius = getResources().getDimensionPixelSize(R.dimen.radius_mask);
        osCanvas.drawRoundRect(rect, radius, radius, transparentPaint);
    }

    private Path createCornersPath(float left, float top, float right,
                                   float bottom, float cornerRadius, float cornerLength) {
        Path path = new Path();

        // top left
        path.moveTo(left, (top + cornerRadius));
        path.arcTo(new RectF(left, top, left + cornerRadius, top + cornerRadius),
                180f,
                90f,
                true
        );

        path.moveTo(left + (cornerRadius / 2f), top);
        path.lineTo(left + (cornerRadius / 2f) + cornerLength, top);

        path.moveTo(left, top + (cornerRadius / 2f));
        path.lineTo(left, top + (cornerRadius / 2f) + cornerLength);

        // top right
        path.moveTo(right - cornerRadius, top);
        path.arcTo(new RectF(right - cornerRadius, top, right, top + cornerRadius),
                270f,
                90f,
                true
        );

        path.moveTo(right - (cornerRadius / 2f), top);
        path.lineTo(right - (cornerRadius / 2f) - cornerLength, top);

        path.moveTo(right, top + (cornerRadius / 2f));
        path.lineTo(right, top + (cornerRadius / 2f) + cornerLength);

        // bottom left
        path.moveTo(left, bottom - cornerRadius);
        path.arcTo(new RectF(left, bottom - cornerRadius, left + cornerRadius, bottom),
                90f,
                90f,
                true
        );

        path.moveTo(left + (cornerRadius / 2f), bottom);
        path.lineTo(left + (cornerRadius / 2f) + cornerLength, bottom);

        path.moveTo(left, bottom - (cornerRadius / 2f));
        path.lineTo(left, bottom - (cornerRadius / 2f) - cornerLength);

        // bottom right
        path.moveTo(left, bottom - cornerRadius);
        path.arcTo(new RectF(right - cornerRadius, bottom - cornerRadius, right, bottom),
                0f,
                90f,
                true
        );

        path.moveTo(right - (cornerRadius / 2f), bottom);
        path.lineTo(right - (cornerRadius / 2f) - cornerLength, bottom);

        path.moveTo(right, bottom - (cornerRadius / 2f));
        path.lineTo(right, bottom - (cornerRadius / 2f) - cornerLength);

        return path;
    }

    public void drawLaser(Canvas canvas) {
        Rect framingRect = getFramingRect();

        // Draw a red "laser scanner" line through the middle to show decoding is active
        mLaserPaint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
        scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
        int middle = framingRect.height() / 2 + framingRect.top;
        float heightLaser = getResources().getDimensionPixelSize(R.dimen.laser_height);
        canvas.drawRect(framingRect.left + (4 * heightLaser), middle - heightLaser / 2.0f,
                framingRect.right - (4 * heightLaser), middle + heightLaser / 2.0f, mLaserPaint);

        postInvalidateDelayed(ANIMATION_DELAY,
                framingRect.left - POINT_SIZE,
                framingRect.top - POINT_SIZE,
                framingRect.right + POINT_SIZE,
                framingRect.bottom + POINT_SIZE);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        updateFramingRect();
    }

    public synchronized void updateFramingRect() {
        Point viewResolution = new Point(getWidth(), getHeight());
        int width;
        int height;
        int orientation = DisplayUtils.getScreenOrientation(getContext());

        if (mSquareViewFinder) {
            if (orientation != Configuration.ORIENTATION_PORTRAIT) {
                height = (int) (getHeight() * DEFAULT_SQUARE_DIMENSION_RATIO);
                width = height;
            } else {
                width = (int) (getWidth() * DEFAULT_SQUARE_DIMENSION_RATIO);
                height = width;
            }
        } else {
            if (orientation != Configuration.ORIENTATION_PORTRAIT) {
                if (Constants.Companion.isTablet(getContext())
                        && Constants.Companion.isTabletXML(getContext())) {
                    height = (int) (getHeight() * TABLET_LANDSCAPE_HEIGHT_RATIO);
                    width = (int) (TABLET_LANDSCAPE_WIDTH_HEIGHT_RATIO * height);
                } else {
                    height = (int) (getHeight() * LANDSCAPE_HEIGHT_RATIO);
                    width = (int) (LANDSCAPE_WIDTH_HEIGHT_RATIO * height);
                }
            } else {
                if (Constants.Companion.isTablet(getContext())
                        && Constants.Companion.isTabletXML(getContext())) {
                    width = (int) (getWidth() * TABLET_PORTRAIT_WIDTH_RATIO);
                    height = (int) (TABLET_PORTRAIT_WIDTH_HEIGHT_RATIO * width);
                } else {
                    width = (int) (getWidth() * PORTRAIT_WIDTH_RATIO);
                    height = (int) (PORTRAIT_WIDTH_HEIGHT_RATIO * width);
                }
            }
        }

        if (width > getWidth()) {
            width = getWidth() - MIN_DIMENSION_DIFF;
        }

        if (height > getHeight()) {
            height = getHeight() - MIN_DIMENSION_DIFF;
        }

        int leftOffset = (viewResolution.x - width) / 2;
        int topOffset = (viewResolution.y - height) / 2;
        mFramingRect = new Rect(leftOffset + mViewFinderOffset, topOffset + mViewFinderOffset,
                leftOffset + width - mViewFinderOffset, topOffset + height - mViewFinderOffset);
    }
}

class DisplayUtils {
    public static Point getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenResolution = new Point();
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            display.getSize(screenResolution);
        } else {
            screenResolution.set(display.getWidth(), display.getHeight());
        }

        return screenResolution;
    }

    public static int getScreenOrientation(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if (display.getWidth() == display.getHeight()) {
            orientation = Configuration.ORIENTATION_SQUARE;
        } else {
            if (display.getWidth() < display.getHeight()) {
                orientation = Configuration.ORIENTATION_PORTRAIT;
            } else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }

}
