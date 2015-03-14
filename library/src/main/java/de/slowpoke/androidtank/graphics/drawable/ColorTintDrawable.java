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
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;

/**
 * A Drawable that tints another drawable.
 */
public class ColorTintDrawable extends Drawable {

    private final ColorTintState mState;

    public ColorTintDrawable(Resources res, Drawable drawable, @ColorRes int colorTint) {
        this(drawable, res.getColor(colorTint));
    }

    public ColorTintDrawable(Drawable drawable, int colorTint) {
        this(null);
        mState.mDrawable = drawable.mutate();
        mState.mColorFilter = new PorterDuffColorFilter(colorTint, PorterDuff.Mode.SRC_IN);
        mState.mDrawable.setColorFilter(mState.mColorFilter);
    }

    private ColorTintDrawable(ColorTintState state) {
        mState = new ColorTintState(state);
    }

    @Override
    public int getIntrinsicWidth() {
        return mState.mDrawable.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mState.mDrawable.getIntrinsicHeight();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        mState.mDrawable.setBounds(bounds);
    }

    @Override
    public void draw(Canvas canvas) {
        mState.mDrawable.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
        mState.mDrawable.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mState.mColorFilter = cf;
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public ConstantState getConstantState() {
        return mState;
    }

    private static final class ColorTintState extends ConstantState {

        Drawable mDrawable;
        ColorFilter mColorFilter;

        ColorTintState(ColorTintState orig) {
            if (orig != null) {
                mDrawable = orig.mDrawable;
                mColorFilter = orig.mColorFilter;
            }
        }

        @Override
        public Drawable newDrawable() {
            return new ColorTintDrawable(this);
        }

        @Override
        public int getChangingConfigurations() {
            return 0;
        }
    }
}