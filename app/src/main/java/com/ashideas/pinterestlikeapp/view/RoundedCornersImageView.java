package com.ashideas.pinterestlikeapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

public class RoundedCornersImageView extends ImageView {

    private Path roundPath;
    private RectF bounds;
    private float radius;

    public RoundedCornersImageView(Context context) {
        super(context);
        init(context);
    }

    public RoundedCornersImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoundedCornersImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        roundPath = new Path();
        bounds = new RectF();
        radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics());
    }

    @Override
    public void draw(Canvas canvas) {
        bounds.right = getWidth();
        bounds.bottom = getHeight();

        roundPath.reset();
        roundPath.addRoundRect(bounds, radius, radius, Path.Direction.CW);
        canvas.clipPath(roundPath);
        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
    }
}
