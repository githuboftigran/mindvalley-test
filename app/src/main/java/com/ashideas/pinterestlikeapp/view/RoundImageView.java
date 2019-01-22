package com.ashideas.pinterestlikeapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundImageView extends ImageView {

    private Path roundPath;
    private RectF bounds;

    public RoundImageView(Context context) {
        super(context);
        init();
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        roundPath = new Path();
        bounds = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        bounds.right = getWidth();
        bounds.bottom = getHeight();

        roundPath.addOval(bounds, Path.Direction.CW);
        canvas.clipPath(roundPath);
        super.onDraw(canvas);
    }
}
