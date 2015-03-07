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

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * A drawable that can be rotated.
 */
public class RotatedDrawable extends Drawable implements Drawable.Callback {

    private final RotatedState mState;

    /**
     * Create a new rotated drawable.
     */
    public RotatedDrawable(Drawable drawable) {
        this(null, null);
        mState.mDrawable = drawable;
    }

    /**
     * Create a new rotated drawable with the specified state. A copy of
     * this state is used as the internal state for the newly created
     * drawable.
     *
     * @param rotateState the state for this drawable
     */
    private RotatedDrawable(RotatedState rotateState, Resources res) {
        mState = new RotatedState(rotateState, this, res);
    }

    @Override
    public void draw(Canvas canvas) {
        final RotatedState st = mState;
        final Drawable d = st.mDrawable;
        final Rect bounds = d.getBounds();
        final int w = bounds.right - bounds.left;
        final int h = bounds.bottom - bounds.top;
        final float px = w * 0.5f;
        final float py = h * 0.5f;

        final int saveCount = canvas.save();
        canvas.rotate(st.mCurrentDegrees, px + bounds.left, py + bounds.top);
        d.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    public float getDegrees() {
        return mState.mCurrentDegrees;
    }

    public void setDegrees(float degrees) {
        if (mState.mCurrentDegrees != degrees) {
            mState.mCurrentDegrees = degrees;
            invalidateSelf();
        }
    }

    /**
     * @return The drawable rotated by this RotatedDrawable
     */
    public Drawable getDrawable() {
        return mState.mDrawable;
    }

    @Override
    public int getChangingConfigurations() {
        return super.getChangingConfigurations()
                | mState.mChangingConfigurations
                | mState.mDrawable.getChangingConfigurations();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public int getAlpha() {
        return mState.mDrawable.getAlpha();
    }

    @Override
    public void setAlpha(int alpha) {
        mState.mDrawable.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mState.mDrawable.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return mState.mDrawable.getOpacity();
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.scheduleDrawable(this, what, when);
        }
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        final Callback callback = getCallback();
        if (callback != null) {
            callback.unscheduleDrawable(this, what);
        }
    }

    @Override
    public boolean getPadding(Rect padding) {
        return mState.mDrawable.getPadding(padding);
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        mState.mDrawable.setVisible(visible, restart);
        return super.setVisible(visible, restart);
    }

    @Override
    public boolean isStateful() {
        return mState.mDrawable.isStateful();
    }

    @Override
    protected boolean onStateChange(int[] state) {
        final boolean changed = mState.mDrawable.setState(state);
        onBoundsChange(getBounds());
        return changed;
    }

    @Override
    protected boolean onLevelChange(int level) {
        return mState.mDrawable.setLevel(level);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        mState.mDrawable.setBounds(bounds.left, bounds.top,
                bounds.right, bounds.bottom);
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
    public ConstantState getConstantState() {
        if (mState.canConstantState()) {
            mState.mChangingConfigurations = getChangingConfigurations();
            return mState;
        }
        return null;
    }

    final static class RotatedState extends ConstantState {
        Drawable mDrawable;
        float mCurrentDegrees;
        int mChangingConfigurations;
        boolean mCanConstantState;
        boolean mCheckedConstantState;

        public RotatedState(RotatedState orig, RotatedDrawable owner, Resources res) {
            if (orig != null) {
                if (res != null) {
                    mDrawable = orig.mDrawable.getConstantState().newDrawable(res);
                } else {
                    mDrawable = orig.mDrawable.getConstantState().newDrawable();
                }
                mDrawable.setCallback(owner);
                mDrawable.setBounds(orig.mDrawable.getBounds());
                mDrawable.setLevel(orig.mDrawable.getLevel());
                mCurrentDegrees = orig.mCurrentDegrees;
            }
        }

        @Override
        public Drawable newDrawable() {
            return new RotatedDrawable(this, null);
        }

        @Override
        public Drawable newDrawable(Resources res) {
            return new RotatedDrawable(this, res);
        }

        @Override
        public int getChangingConfigurations() {
            return mChangingConfigurations;
        }

        public boolean canConstantState() {
            if (!mCheckedConstantState) {
                mCanConstantState = mDrawable.getConstantState() != null;
                mCheckedConstantState = true;
            }

            return mCanConstantState;
        }
    }
}
