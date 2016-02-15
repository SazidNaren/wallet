package stripe.example.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.jar.Attributes;

/**
 * Created by sajid on 18/1/16.
 */
   public class RoundImageViewClipped extends ImageView {
        private static final int RADIUS = 32;
    private Rect mRect;
    private RectF fRect;
    private Path mClip;

        public RoundImageViewClipped(Context context) {
            super(context);
//        setBackgroundColor(0xffffffff);
            mRect = new Rect();
            mClip = new Path();
        }
    public RoundImageViewClipped(Context context,AttributeSet attributes) {
        super(context,attributes);
//        setBackgroundColor(0xffffffff);
        mRect = new Rect();
        mClip = new Path();
    }
        @Override
        public void onDraw(Canvas canvas) {
            Drawable dr = getDrawable();
            if (dr != null) {
                fRect=new RectF();
                mRect.set(dr.getBounds());
                getImageMatrix().mapRect(fRect);
                mRect.offset(getPaddingLeft(), getPaddingTop());
                mClip.reset();

                float currentSize =100;

                float[] corners = new float[]{0,0,0,0,0,0,0,0,0};
                fRect.set(mRect.left, mRect.top, mRect.left + currentSize, mRect.top + currentSize);
                mClip.addRoundRect(fRect, corners, Path.Direction.CCW);
                canvas.clipPath(mClip);
                super.onDraw(canvas);
            }
        }
    }
