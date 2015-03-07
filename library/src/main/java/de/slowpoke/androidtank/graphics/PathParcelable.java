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

package de.slowpoke.androidtank.graphics;

import java.util.ArrayDeque;
import java.util.Arrays;
import android.annotation.TargetApi;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * An implementation of {@link Path} which can be parceled. Note that methods
 * with {@link Path} parameters are not supported in this implementation.
 * 
 * @author Brian
 * @version 1.0
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class PathParcelable extends android.graphics.Path implements Parcelable {

	private final ArrayDeque<PathAction> actions;
	
	/**
	 * Create a new {@link PathParcelable} with default capacity.
	 */
	public PathParcelable() {
		this(1024);
	}

	/**
	 * Create a new {@link PathParcelable}.
	 * 
	 * @param initSize
	 *            the initial capacity.
	 */
	public PathParcelable(int initSize) {
		super();
		super.incReserve(initSize);
		actions = new ArrayDeque<PathAction>(initSize);
	}

	public PathParcelable(Parcel in) {
		this(in.readInt());

		PathAction[] array = (PathAction[]) in.readArray(getClass().getClassLoader());
		actions.addAll(Arrays.asList(array));

		setFillType(FillType.valueOf(in.readString()));
		restoreFromParcel();
	}

	@Override
	public void moveTo(float x, float y) {
		super.moveTo(x, y);
		actions.add(new MoveTo(x, y));
	}

	private void perform(final MoveTo action) {
		super.moveTo(action.x, action.y);
	}

	@Override
	public void lineTo(float x, float y) {
		super.lineTo(x, y);
		actions.add(new LineTo(x, y));
	}

	private void perform(final LineTo action) {
		super.lineTo(action.x, action.y);
	}

	@Override
	public void cubicTo(float x1, float y1, float x2, float y2, float x3, float y3) {
		super.cubicTo(x1, y1, x2, y2, x3, y3);
		actions.add(new CubicTo(x1, y1, x2, y2, x3, y3));
	}

	private void perform(final CubicTo actions) {
		super.cubicTo(actions.x1, actions.y1, actions.x2, actions.y2, actions.x3, actions.y3);
	}

	@Override
	public void arcTo(RectF oval, float startAngle, float sweepAngle, boolean forceMoveTo) {
		super.arcTo(oval, startAngle, sweepAngle, forceMoveTo);
		actions.add(new ArcTo(oval, startAngle, sweepAngle, forceMoveTo));
	}

	@Override
	public void arcTo(RectF oval, float startAngle, float sweepAngle) {
		super.arcTo(oval, startAngle, sweepAngle);
		actions.add(new ArcTo(oval, startAngle, sweepAngle, false));
	}

	private void perform(final ArcTo action) {
		super.arcTo(action.oval, action.startAngle, action.sweepAngle, action.forceMoveTo);
	}

	@Override
	public void addArc(RectF oval, float startAngle, float sweepAngle) {
		super.addArc(oval, startAngle, sweepAngle);
		actions.add(new AddArc(oval, startAngle, sweepAngle));
	}

	private void perform(final AddArc action) {
		super.addArc(action.oval, action.startAngle, action.sweepAngle);
	}

	@Override
	public void addCircle(float x, float y, float radius, Direction dir) {
		super.addCircle(x, y, radius, dir);
		actions.add(new AddCircle(x, y, radius, dir));
	}

	private void perform(final AddCircle action) {
		super.addCircle(action.x, action.y, action.radius, action.dir);
	}

	@Override
	public void addOval(RectF oval, Direction dir) {
		super.addOval(oval, dir);
		actions.add(new AddOval(oval, dir));
	}

	private void perform(final AddOval action) {
		super.addOval(action.oval, action.dir);
	}

	/**
	 * Unsupported.
	 * @deprecated This method is not supported.
	 */
	@Override
	@Deprecated
	public void addPath(android.graphics.Path src) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported.
	 * @deprecated This method is not supported.
	 */
	@Override
	@Deprecated
	public void addPath(android.graphics.Path src, float dx, float dy) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported.
	 * @deprecated This method is not supported.
	 */
	@Override
	@Deprecated
	public void addPath(android.graphics.Path src, Matrix matrix) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addRect(float left, float top, float right, float bottom, Direction dir) {
		super.addRect(left, top, right, bottom, dir);
		actions.add(new AddRect(left, top, right, bottom, dir));
	}

	@Override
	public void addRect(RectF rect, Direction dir) {
		super.addRect(rect, dir);
		actions.add(new AddRect(rect, dir));
	}

	private void perform(final AddRect action) {
		super.addRect(action.rect, action.dir);
	}

	@Override
	public void addRoundRect(RectF rect, float rx, float ry, Direction dir) {
		super.addRoundRect(rect, rx, ry, dir);
		actions.add(new AddRoundedRect(rect, rx, ry, dir));
	}

	private void perform(final AddRoundedRect action) {
		super.addRoundRect(action.rect, action.rx, action.ry, action.dir);
	}

	@Override
	public void addRoundRect(RectF rect, float[] radii, Direction dir) {
		super.addRoundRect(rect, radii, dir);
		actions.add(new AddRoundedRectCorners(rect, radii, dir));
	}

	private void perform(final AddRoundedRectCorners action) {
		super.addRoundRect(action.rect, action.radii, action.dir);
	}

	@Override
	public void quadTo(float x1, float y1, float x2, float y2) {
		super.quadTo(x1, y1, x2, y2);
		actions.add(new QuadTo(x1, y1, x2, y2));
	}

	private void perform(final QuadTo action) {
		super.quadTo(action.x1, action.y1, action.x2, action.y2);
	}

	@Override
	public void rCubicTo(float x1, float y1, float x2, float y2, float x3, float y3) {
		super.rCubicTo(x1, y1, x2, y2, x3, y3);
		actions.add(new RCubicTo(x1, y1, x2, y2, x3, y3));
	}

	private void perform(final RCubicTo action) {
		super.rCubicTo(action.x1, action.y1, action.x2, action.y2, action.x3, action.y3);
	}

	@Override
	public void rLineTo(float dx, float dy) {
		super.rLineTo(dx, dy);
		actions.add(new RLineTo(dx, dy));
	}

	private void perform(final RLineTo action) {
		super.rLineTo(action.x, action.y);
	}

	@Override
	public void rMoveTo(float dx, float dy) {
		super.rMoveTo(dx, dy);
		actions.add(new RMoveTo(dx, dy));
	}

	private void perform(final RMoveTo action) {
		super.rMoveTo(action.x, action.y);
	}

	@Override
	public void rQuadTo(float dx1, float dy1, float dx2, float dy2) {
		super.rQuadTo(dx1, dy1, dx2, dy2);
		actions.add(new RQuadTo(dx1, dy1, dx2, dy2));
	}

	private void perform(final RQuadTo action) {
		super.rQuadTo(action.x1, action.y1, action.x2, action.y2);
	}

	@Override
	public void offset(float dx, float dy) {
		super.offset(dx, dy);
		actions.add(new Offset(dx, dy));
	}

	private void perform(final Offset action) {
		super.offset(action.x, action.y);
	}

	/**
	 * Unsupported.
	 * @deprecated This method is not supported.
	 */
	@Override
	@Deprecated
	public void offset(float dx, float dy, android.graphics.Path dst) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported.
	 * @deprecated This method is not supported.
	 */
	@Override
	@Deprecated
	public void set(android.graphics.Path src) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reset() {
		super.reset();
		actions.clear();
	}

	@Override
	public void rewind() {
		super.rewind();
		actions.clear();
	}

	/**
	 * Unsupported.
	 * @deprecated This method is not supported.
	 */
	@Override
	@Deprecated
	public void transform(Matrix matrix, android.graphics.Path dst) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void transform(Matrix matrix) {
		super.transform(matrix);
		this.actions.add(new MatrixTransformation(matrix));
	}

	private void perform(final MatrixTransformation action) {
		super.transform(action.matrix);
	}

	@Override
	public void close() {
		super.close();
		this.actions.add(new Close());
	}

	private void perform(final Close action) {
		super.close();
	}

	@Override
	public void setLastPoint(float dx, float dy) {
		super.setLastPoint(dx, dy);
		this.actions.add(new LastPoint(dx, dy));
	}

	private void perform(final LastPoint action) {
		super.setLastPoint(action.x, action.y);
	}

	@Override
	public void incReserve(int extraPtCount) {
		super.incReserve(extraPtCount);
		//actions.ensureCapacity(actions.size() + extraPtCount);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actions == null) ? 0 : actions.hashCode());
		result = prime * result + getFillType().hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PathParcelable)) {
			return false;
		}
		PathParcelable other = (PathParcelable) obj;
		if (actions == null) {
			if (other.actions != null) {
				return false;
			}
		} else if (!actions.equals(other.actions)) {
			return false;
		}

		if (getFillType() != other.getFillType()) {
			return false;
		}

		return true;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(actions.size());
		dest.writeArray(actions.toArray(new PathAction[actions.size()]));
		dest.writeString(getFillType().name());
	}

	public static final Parcelable.Creator<PathParcelable> CREATOR = new Parcelable.Creator<PathParcelable>() {
		public PathParcelable createFromParcel(Parcel in) {
			return new PathParcelable(in);
		}

		public PathParcelable[] newArray(int size) {
			return new PathParcelable[size];
		}
	};

	private void restoreFromParcel() {
		for (PathAction action : actions) {
			switch (action.getType()) {
			case MOVE_TO:
				perform((MoveTo) action);
				break;

			case R_MOVE_TO:
				perform((RMoveTo) action);
				break;

			case LINE_TO:
				perform((LineTo) action);
				break;

			case R_LINE_TO:
				perform((RLineTo) action);
				break;

			case CUPIC_TO:
				perform((CubicTo) action);
				break;

			case R_CUBIC_TO:
				perform((RCubicTo) action);
				break;

			case ARC_TO:
				perform((ArcTo) action);
				break;

			case QUAD_TO:
				perform((QuadTo) action);
				break;

			case R_QUAD_TO:
				perform((RQuadTo) action);
				break;

			case ADD_ARC:
				perform((AddArc) action);
				break;

			case ADD_CIRCLE:
				perform((AddCircle) action);
				break;

			case ADD_OVAL:
				perform((AddOval) action);
				break;

			case ADD_RECT:
				perform((AddRect) action);
				break;

			case ADD_ROUNDED_RECT:
				perform((AddRoundedRect) action);
				break;

			case ADD_ROUNDED_RECT_CORNERS:
				perform((AddRoundedRectCorners) action);
				break;

			case OFFSET:
				perform((Offset) action);
				break;

			case MATRIX_TRANSFORMATION:
				perform((MatrixTransformation) action);
				break;

			case CLOSE:
				perform((Close) action);
				break;

			case LAST_POINT:
				perform((LastPoint) action);
				break;
			}
		}
	}

	private static enum ActionType {
		LINE_TO,
		MOVE_TO,
		CUPIC_TO,
		ARC_TO,
		QUAD_TO,
		R_CUBIC_TO,
		R_QUAD_TO,
		R_LINE_TO,
		R_MOVE_TO,
		ADD_ARC,
		ADD_CIRCLE,
		ADD_OVAL,
		ADD_RECT,
		ADD_ROUNDED_RECT,
		ADD_ROUNDED_RECT_CORNERS,
		OFFSET,
		MATRIX_TRANSFORMATION,
		CLOSE,
		LAST_POINT;
	};

	private static abstract class PathAction implements Parcelable {

		public abstract ActionType getType();

		@Override
		public int describeContents() {
			return getType().ordinal();
		}

		@Override
		public boolean equals(Object other) {
			if (other == null) {
				return false;
			}

			if (!(other instanceof PathAction)) {
				return false;
			}

			return this.getType() == ((PathAction) other).getType();
		}

		@Override
		public int hashCode() {
			return 31 + getType().hashCode();
		}
	}

	private static class AddArc extends ArcTo {

		public AddArc(final RectF oval, float startAngle, float sweepAngle) {
			super(oval, startAngle, sweepAngle, false);
		}

		public AddArc(Parcel in) {
			super(in);
		}

		@Override
		public ActionType getType() {
			return ActionType.ADD_ARC;
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<AddArc> CREATOR = new Parcelable.Creator<AddArc>() {
			public AddArc createFromParcel(Parcel in) {
				return new AddArc(in);
			}

			public AddArc[] newArray(int size) {
				return new AddArc[size];
			}
		};
	}

	private static class AddCircle extends AddDirectionalFigure {

		public final float x, y;
		public final float radius;

		public AddCircle(float x, float y, float radius, Direction dir) {
			super(dir);
			this.x = x;
			this.y = y;
			this.radius = radius;

		}

		public AddCircle(Parcel in) {
			super(in);
			this.x = in.readFloat();
			this.y = in.readFloat();
			this.radius = in.readFloat();
		}

		@Override
		public ActionType getType() {
			return ActionType.ADD_CIRCLE;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeFloat(x);
			dest.writeFloat(y);
			dest.writeFloat(radius);
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<AddCircle> CREATOR = new Parcelable.Creator<AddCircle>() {
			public AddCircle createFromParcel(Parcel in) {
				return new AddCircle(in);
			}

			public AddCircle[] newArray(int size) {
				return new AddCircle[size];
			}
		};

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + Float.floatToIntBits(radius);
			result = prime * result + Float.floatToIntBits(x);
			result = prime * result + Float.floatToIntBits(y);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!super.equals(obj)) {
				return false;
			}
			if (!(obj instanceof AddCircle)) {
				return false;
			}
			AddCircle other = (AddCircle) obj;
			if (Float.floatToIntBits(radius) != Float.floatToIntBits(other.radius)) {
				return false;
			}
			if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) {
				return false;
			}
			if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) {
				return false;
			}
			return true;
		}
	}

	private static abstract class AddDirectionalFigure extends PathAction {

		public final Direction dir;

		public AddDirectionalFigure(Direction dir) {
			this.dir = dir;
		}

		public AddDirectionalFigure(Parcel in) {
			this(Direction.valueOf(in.readString()));
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(dir.name());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((dir == null) ? 0 : dir.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!super.equals(obj)) {
				return false;
			}
			if (!(obj instanceof AddDirectionalFigure)) {
				return false;
			}
			AddDirectionalFigure other = (AddDirectionalFigure) obj;
			if (dir != other.dir) {
				return false;
			}
			return true;
		}
	}

	private static class AddOval extends AddDirectionalFigure {

		public final RectF oval;

		public AddOval(RectF oval, Direction dir) {
			super(dir);
			this.oval = oval;
		}

		public AddOval(Parcel in) {
			super(in);
			this.oval = (RectF) in.readParcelable(null);
		}

		@Override
		public ActionType getType() {
			return ActionType.ADD_OVAL;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeParcelable(oval, flags);
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<AddOval> CREATOR = new Parcelable.Creator<AddOval>() {
			public AddOval createFromParcel(Parcel in) {
				return new AddOval(in);
			}

			public AddOval[] newArray(int size) {
				return new AddOval[size];
			}
		};

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((oval == null) ? 0 : oval.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!super.equals(obj)) {
				return false;
			}
			if (!(obj instanceof AddOval)) {
				return false;
			}
			AddOval other = (AddOval) obj;
			if (oval == null) {
				if (other.oval != null) {
					return false;
				}
			} else if (!oval.equals(other.oval)) {
				return false;
			}
			return true;
		}
	}

	private static class AddRect extends AddDirectionalFigure {

		public final RectF rect;

		public AddRect(RectF rect, Direction dir) {
			super(dir);
			this.rect = rect;
		}

		public AddRect(float left, float top, float right, float bottom, Direction dir) {
			this(new RectF(left, top, right, bottom), dir);
		}

		public AddRect(Parcel in) {
			super(in);
			this.rect = (RectF) in.readParcelable(null);
		}

		@Override
		public ActionType getType() {
			return ActionType.ADD_RECT;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeParcelable(rect, flags);
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<AddRect> CREATOR = new Parcelable.Creator<AddRect>() {
			public AddRect createFromParcel(Parcel in) {
				return new AddRect(in);
			}

			public AddRect[] newArray(int size) {
				return new AddRect[size];
			}
		};

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((rect == null) ? 0 : rect.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!super.equals(obj)) {
				return false;
			}
			if (!(obj instanceof AddRect)) {
				return false;
			}
			AddRect other = (AddRect) obj;
			if (rect == null) {
				if (other.rect != null) {
					return false;
				}
			} else if (!rect.equals(other.rect)) {
				return false;
			}
			return true;
		}
	}

	private static class AddRoundedRect extends AddRect {

		public final float rx, ry;

		public AddRoundedRect(RectF rect, float rx, float ry, Direction dir) {
			super(rect, dir);
			this.rx = rx;
			this.ry = ry;
		}

		public AddRoundedRect(Parcel in) {
			super(in);
			this.rx = in.readFloat();
			this.ry = in.readFloat();
		}

		@Override
		public ActionType getType() {
			return ActionType.ADD_ROUNDED_RECT;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeFloat(rx);
			dest.writeFloat(ry);
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<AddRoundedRect> CREATOR = new Parcelable.Creator<AddRoundedRect>() {
			public AddRoundedRect createFromParcel(Parcel in) {
				return new AddRoundedRect(in);
			}

			public AddRoundedRect[] newArray(int size) {
				return new AddRoundedRect[size];
			}
		};

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + Float.floatToIntBits(rx);
			result = prime * result + Float.floatToIntBits(ry);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!super.equals(obj)) {
				return false;
			}
			if (!(obj instanceof AddRoundedRect)) {
				return false;
			}
			AddRoundedRect other = (AddRoundedRect) obj;
			if (Float.floatToIntBits(rx) != Float.floatToIntBits(other.rx)) {
				return false;
			}
			if (Float.floatToIntBits(ry) != Float.floatToIntBits(other.ry)) {
				return false;
			}
			return true;
		}
	}

	private static class AddRoundedRectCorners extends AddRect {

		public final float[] radii;

		public AddRoundedRectCorners(RectF rect, float[] radii, Direction dir) {
			super(rect, dir);
			this.radii = radii;
		}

		public AddRoundedRectCorners(Parcel in) {
			super(in);
			this.radii = new float[8];
			in.readFloatArray(this.radii);
		}

		@Override
		public ActionType getType() {
			return ActionType.ADD_ROUNDED_RECT_CORNERS;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeFloatArray(this.radii);
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<AddRoundedRectCorners> CREATOR = new Parcelable.Creator<AddRoundedRectCorners>() {
			public AddRoundedRectCorners createFromParcel(Parcel in) {
				return new AddRoundedRectCorners(in);
			}

			public AddRoundedRectCorners[] newArray(int size) {
				return new AddRoundedRectCorners[size];
			}
		};

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + Arrays.hashCode(radii);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!super.equals(obj)) {
				return false;
			}
			if (!(obj instanceof AddRoundedRectCorners)) {
				return false;
			}
			AddRoundedRectCorners other = (AddRoundedRectCorners) obj;
			if (!Arrays.equals(radii, other.radii)) {
				return false;
			}
			return true;
		}
	}

	private static class ArcTo extends PathAction {

		public final RectF oval;
		public final float startAngle, sweepAngle;
		public final boolean forceMoveTo;

		public ArcTo(RectF oval, float startAngle, float sweepAngle, boolean forceMoveTo) {
			this.oval = oval;
			this.startAngle = startAngle;
			this.sweepAngle = sweepAngle;
			this.forceMoveTo = forceMoveTo;
		}

		public ArcTo(Parcel in) {
			this.oval = (RectF) in.readParcelable(null);
			this.startAngle = in.readFloat();
			this.sweepAngle = in.readFloat();
			this.forceMoveTo = in.readInt() == 1;
		}

		@Override
		public ActionType getType() {
			return ActionType.ARC_TO;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeParcelable(oval, flags);
			dest.writeFloat(startAngle);
			dest.writeFloat(sweepAngle);
			dest.writeInt(forceMoveTo ? 1 : 0);
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<ArcTo> CREATOR = new Parcelable.Creator<ArcTo>() {
			public ArcTo createFromParcel(Parcel in) {
				return new ArcTo(in);
			}

			public ArcTo[] newArray(int size) {
				return new ArcTo[size];
			}
		};

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + (forceMoveTo ? 1231 : 1237);
			result = prime * result + ((oval == null) ? 0 : oval.hashCode());
			result = prime * result + Float.floatToIntBits(startAngle);
			result = prime * result + Float.floatToIntBits(sweepAngle);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!super.equals(obj)) {
				return false;
			}
			if (!(obj instanceof ArcTo)) {
				return false;
			}
			ArcTo other = (ArcTo) obj;
			if (forceMoveTo != other.forceMoveTo) {
				return false;
			}
			if (oval == null) {
				if (other.oval != null) {
					return false;
				}
			} else if (!oval.equals(other.oval)) {
				return false;
			}
			if (Float.floatToIntBits(startAngle) != Float.floatToIntBits(other.startAngle)) {
				return false;
			}
			if (Float.floatToIntBits(sweepAngle) != Float.floatToIntBits(other.sweepAngle)) {
				return false;
			}
			return true;
		}
	}

	private static class QuadTo extends PathAction {

		public final float x1, y1, x2, y2;

		public QuadTo(float x1, float y1, float x2, float y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}

		public QuadTo(Parcel in) {
			this.x1 = in.readFloat();
			this.y1 = in.readFloat();
			this.x2 = in.readFloat();
			this.y2 = in.readFloat();
		}

		@Override
		public ActionType getType() {
			return ActionType.QUAD_TO;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeFloat(x1);
			dest.writeFloat(y1);
			dest.writeFloat(x2);
			dest.writeFloat(y2);
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<QuadTo> CREATOR = new Parcelable.Creator<QuadTo>() {
			public QuadTo createFromParcel(Parcel in) {
				return new QuadTo(in);
			}

			public QuadTo[] newArray(int size) {
				return new QuadTo[size];
			}
		};

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + Float.floatToIntBits(x1);
			result = prime * result + Float.floatToIntBits(x2);
			result = prime * result + Float.floatToIntBits(y1);
			result = prime * result + Float.floatToIntBits(y2);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!super.equals(obj)) {
				return false;
			}
			if (!(obj instanceof QuadTo)) {
				return false;
			}
			QuadTo other = (QuadTo) obj;
			if (Float.floatToIntBits(x1) != Float.floatToIntBits(other.x1)) {
				return false;
			}
			if (Float.floatToIntBits(x2) != Float.floatToIntBits(other.x2)) {
				return false;
			}
			if (Float.floatToIntBits(y1) != Float.floatToIntBits(other.y1)) {
				return false;
			}
			if (Float.floatToIntBits(y2) != Float.floatToIntBits(other.y2)) {
				return false;
			}
			return true;
		}
	}

	private static class CubicTo extends QuadTo {

		public final float x3, y3;

		public CubicTo(float x1, float y1, float x2, float y2, float x3, float y3) {
			super(x1, y1, x2, y2);
			this.x3 = x3;
			this.y3 = y3;
		}

		public CubicTo(Parcel in) {
			super(in);
			this.x3 = in.readFloat();
			this.y3 = in.readFloat();
		}

		@Override
		public ActionType getType() {
			return ActionType.CUPIC_TO;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeFloat(x3);
			dest.writeFloat(y3);
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<CubicTo> CREATOR = new Parcelable.Creator<CubicTo>() {
			public CubicTo createFromParcel(Parcel in) {
				return new CubicTo(in);
			}

			public CubicTo[] newArray(int size) {
				return new CubicTo[size];
			}
		};

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + Float.floatToIntBits(x3);
			result = prime * result + Float.floatToIntBits(y3);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!super.equals(obj)) {
				return false;
			}
			if (!(obj instanceof CubicTo)) {
				return false;
			}
			CubicTo other = (CubicTo) obj;
			if (Float.floatToIntBits(x3) != Float.floatToIntBits(other.x3)) {
				return false;
			}
			if (Float.floatToIntBits(y3) != Float.floatToIntBits(other.y3)) {
				return false;
			}
			return true;
		}

	}

	private static class LineTo extends MoveTo {

		public LineTo(float x, float y) {
			super(x, y);
		}

		public LineTo(Parcel in) {
			super(in);
		}

		@Override
		public ActionType getType() {
			return ActionType.LINE_TO;
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<LineTo> CREATOR = new Parcelable.Creator<LineTo>() {
			public LineTo createFromParcel(Parcel in) {
				return new LineTo(in);
			}

			public LineTo[] newArray(int size) {
				return new LineTo[size];
			}
		};
	}

	private static class MoveTo extends PathAction {

		public final float x, y;

		public MoveTo(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public MoveTo(Parcel in) {
			this.x = in.readFloat();
			this.y = in.readFloat();
		}

		@Override
		public ActionType getType() {
			return ActionType.MOVE_TO;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeFloat(x);
			dest.writeFloat(y);
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<MoveTo> CREATOR = new Parcelable.Creator<MoveTo>() {
			public MoveTo createFromParcel(Parcel in) {
				return new MoveTo(in);
			}

			public MoveTo[] newArray(int size) {
				return new MoveTo[size];
			}
		};

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + Float.floatToIntBits(x);
			result = prime * result + Float.floatToIntBits(y);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!super.equals(obj)) {
				return false;
			}
			if (!(obj instanceof MoveTo)) {
				return false;
			}
			MoveTo other = (MoveTo) obj;
			if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) {
				return false;
			}
			if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) {
				return false;
			}
			return true;
		}
	}

	private static class RCubicTo extends CubicTo {

		public RCubicTo(float x1, float y1, float x2, float y2, float x3, float y3) {
			super(x1, y1, x2, y2, x3, y3);
		}

		public RCubicTo(Parcel in) {
			super(in);
		}

		@Override
		public ActionType getType() {
			return ActionType.R_CUBIC_TO;
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<RCubicTo> CREATOR = new Parcelable.Creator<RCubicTo>() {
			public RCubicTo createFromParcel(Parcel in) {
				return new RCubicTo(in);
			}

			public RCubicTo[] newArray(int size) {
				return new RCubicTo[size];
			}
		};
	}

	private static class RQuadTo extends QuadTo {

		public RQuadTo(float x1, float y1, float x2, float y2) {
			super(x1, y1, x2, y2);
		}

		public RQuadTo(Parcel in) {
			super(in);
		}

		@Override
		public ActionType getType() {
			return ActionType.R_QUAD_TO;
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<RQuadTo> CREATOR = new Parcelable.Creator<RQuadTo>() {
			public RQuadTo createFromParcel(Parcel in) {
				return new RQuadTo(in);
			}

			public RQuadTo[] newArray(int size) {
				return new RQuadTo[size];
			}
		};
	}

	private static class RLineTo extends LineTo {
		public RLineTo(float x, float y) {
			super(x, y);
		}

		public RLineTo(Parcel in) {
			super(in);
		}

		@Override
		public ActionType getType() {
			return ActionType.R_LINE_TO;
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<RLineTo> CREATOR = new Parcelable.Creator<RLineTo>() {
			public RLineTo createFromParcel(Parcel in) {
				return new RLineTo(in);
			}

			public RLineTo[] newArray(int size) {
				return new RLineTo[size];
			}
		};
	}

	private static class RMoveTo extends MoveTo {

		public RMoveTo(float x, float y) {
			super(x, y);
		}

		public RMoveTo(Parcel in) {
			super(in);
		}

		@Override
		public ActionType getType() {
			return ActionType.R_MOVE_TO;
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<RMoveTo> CREATOR = new Parcelable.Creator<RMoveTo>() {
			public RMoveTo createFromParcel(Parcel in) {
				return new RMoveTo(in);
			}

			public RMoveTo[] newArray(int size) {
				return new RMoveTo[size];
			}
		};
	}

	private static class Offset extends MoveTo {
		public Offset(float x, float y) {
			super(x, y);
		}

		public Offset(Parcel in) {
			super(in);
		}

		@Override
		public ActionType getType() {
			return ActionType.OFFSET;
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<Offset> CREATOR = new Parcelable.Creator<Offset>() {
			public Offset createFromParcel(Parcel in) {
				return new Offset(in);
			}

			public Offset[] newArray(int size) {
				return new Offset[size];
			}
		};

	}

	private static class MatrixTransformation extends PathAction {

		public final Matrix matrix;

		public MatrixTransformation(Matrix matrix) {
			this.matrix = matrix;
		}

		public MatrixTransformation(Parcel in) {
			this.matrix = new Matrix();
			final float[] values = new float[9];
			in.readFloatArray(values);
			this.matrix.setValues(values);
		}

		@Override
		public ActionType getType() {
			return ActionType.MATRIX_TRANSFORMATION;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			final float[] values = new float[9];
			matrix.getValues(values);
			dest.writeFloatArray(values);
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<MatrixTransformation> CREATOR = new Parcelable.Creator<MatrixTransformation>() {
			public MatrixTransformation createFromParcel(Parcel in) {
				return new MatrixTransformation(in);
			}

			public MatrixTransformation[] newArray(int size) {
				return new MatrixTransformation[size];
			}
		};

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();

			int matrixHashCode = 0;
			if (matrix != null) {
				final float[] values = new float[9];
				matrix.getValues(values);
				matrixHashCode = Arrays.hashCode(values);
			}

			result = prime * result + matrixHashCode;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!super.equals(obj)) {
				return false;
			}
			if (!(obj instanceof MatrixTransformation)) {
				return false;
			}
			MatrixTransformation other = (MatrixTransformation) obj;
			if (matrix == null) {
				if (other.matrix != null) {
					return false;
				}
			} else {
				final float[] values = new float[9];
				final float[] otherValues = new float[9];

				matrix.getValues(values);
				other.matrix.getValues(otherValues);

				if (!Arrays.equals(values, otherValues)) {
					return false;
				}

				return false;
			}
			return true;
		}

	}

	private static class Close extends PathAction {

		public Close() {
		}

		public Close(Parcel in) {
		}

		@Override
		public ActionType getType() {
			return ActionType.CLOSE;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<Close> CREATOR = new Parcelable.Creator<Close>() {
			public Close createFromParcel(Parcel in) {
				return new Close(in);
			}

			public Close[] newArray(int size) {
				return new Close[size];
			}
		};

	}

	private static class LastPoint extends MoveTo {

		public LastPoint(float x, float y) {
			super(x, y);
		}

		public LastPoint(Parcel in) {
			super(in);
		}

		@Override
		public ActionType getType() {
			return ActionType.LAST_POINT;
		}

		@SuppressWarnings("unused")
		public static final Parcelable.Creator<LastPoint> CREATOR = new Parcelable.Creator<LastPoint>() {
			public LastPoint createFromParcel(Parcel in) {
				return new LastPoint(in);
			}

			public LastPoint[] newArray(int size) {
				return new LastPoint[size];
			}
		};

	}

}
