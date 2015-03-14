/*
 * Copyright 2015 Brian Hoffmann, slowpoke.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.slowpoke.androidtank.graphics.drawable;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;

/**
 * A Drawable that cuts out a circle.
 */
public class CircleCutOutColorDrawable extends Drawable {

    private final CircleCutOutColorState mState;

    public CircleCutOutColorDrawable(Resources res, @ColorRes int backgroundColor) {
        this(res.getColor(backgroundColor));
    }

    public CircleCutOutColorDrawable(int backgroundColor) {
        this(null);
        mState.setColor(backgroundColor);
    }

    private CircleCutOutColorDrawable(CircleCutOutColorState state) {
        mState = new CircleCutOutColorState(state);
    }

    /**
     * Set the circle that will be cut out.
     *
     * @param center     center position
     * @param diameterPx diameter of circle
     */
    public void setCircle(PointF center, float diameterPx) {
        mState.mCenter = center;
        mState.mRadius = diameterPx * 0.5f;
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        mState.mPath.reset();
        mState.mPath.addRect(0, 0, canvas.getWidth(), canvas.getHeight(), Path.Direction.CW);

        if (mState.mCenter != null) {
            mState.mPath.addCircle(mState.mCenter.x, mState.mCenter.y, mState.mRadius, Path.Direction.CCW);
        }

        canvas.drawPath(mState.mPath, mState.mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mState.mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mState.mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        switch (mState.mPaint.getAlpha()) {
            case 255:
                return PixelFormat.OPAQUE;
            case 0:
                return PixelFormat.TRANSPARENT;
        }
        return PixelFormat.TRANSLUCENT;
    }

    final static class CircleCutOutColorState extends ConstantState {

        private final Paint mPaint;
        private final Path mPath;

        private PointF mCenter;
        private float mRadius;

        CircleCutOutColorState(CircleCutOutColorState state) {
            if (state != null) {
                mPaint = state.mPaint;
                mPath = state.mPath;
                mCenter = new PointF(state.mCenter.x, state.mCenter.y);
                mRadius = state.mRadius;
            } else {
                mPaint = new Paint();
                mPaint.setAntiAlias(true);
                mPaint.setStyle(Paint.Style.FILL);

                mPath = new Path();
                mPath.setFillType(Path.FillType.EVEN_ODD);
            }
        }

        public void setColor(int color) {
            mPaint.setColor(color);
        }

        @Override
        public Drawable newDrawable() {
            return new CircleCutOutColorDrawable(this);
        }

        @Override
        public int getChangingConfigurations() {
            return 0;
        }

    }
}