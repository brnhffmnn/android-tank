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

package de.slowpoke.androidtank.content;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;

import android.os.BadParcelableException;
import android.os.Parcelable;
import android.util.Log;

/**
 * Interface for classes whose instances want to be {@link Parcelable} and also
 * be persistable to an {@link DataOutput}. Classes implementing the Parcelable
 * interface must also have a static field called <code>CREATOR</code>, which is
 * an object implementing the {@link Persistable.Persister} interface.
 * 
 * <p>
 * A typical implementation of Parcelable is:
 * </p>
 * 
 * <pre>
 * public class MyParcelable implements
 * 		Persistable {
 * 	private int mData;
 * 
 * 	public int describeContents() {
 * 		return 0;
 * 	}
 * 
 * 	public void writeToParcel(Parcel out,
 * 			int flags) {
 * 		out.writeInt(mData);
 * 	}
 * 
 * 	public void writeToPersist(DataOutput output) throws IOException {
 * 		output.writeInt(mData);
 * 	}
 * 
 * 	public static final Persistable.Persister&lt;MyParcelable&gt; CREATOR = new Persistable.Persister&lt;MyParcelable&gt;() {
 * 		public MyParcelable createFromParcel(Parcel in) {
 * 			return new MyParcelable(in);
 * 		}
 * 
 * 		public MyParcelable createFromPersist(DataInput in) throws IOException {
 * 			return new MyParcelable(in);
 * 		}
 * 
 * 		public MyParcelable[] newArray(int size) {
 * 			return new MyParcelable[size];
 * 		}
 * 	};
 * 
 * 	private MyParcelable(Parcel in) {
 * 		mData = in.readInt();
 * 	}
 * 
 * 	private MyParcelable(ObjectInput in) throws IOException {
 * 		mData = in.readInt();
 * 	}
 * }
 * </pre>
 */
public interface Persistable extends
		Parcelable {

	/**
	 * Flatten this object in to {@link ObjectOutput}.
	 * 
	 * @param output
	 *            The {@link ObjectOutput} in which the object should be
	 *            written.
	 *
	 */
	public void writeToPersist(DataOutput output) throws IOException;

	/**
	 * Interface that must be implemented and provided as a public CREATOR field
	 * that generates instances of your {@link Persister} class from a
	 * {@link ObjectInput}.
	 */
	public interface Persister<T> extends
			Parcelable.Creator<T> {

		public T createFromPersist(DataInput source) throws IOException;

	}

	public static final class Helper {

		private static final String TAG = "Persistable.Helper";

		public static <T extends Persistable> Persistable.Persister<T> getPersistablePersister(Class<T> clazz,
				ClassLoader loader) {
			String name = clazz.getName();
			return getPersistablePersister(name, loader);
		}

		public static <T extends Persistable> Persistable.Persister<T> getPersistablePersister(String className,
				ClassLoader loader) {

			try {
				@SuppressWarnings("unchecked")
				Class<T> c = (Class<T>) (loader == null ? Class.forName(className) : Class.forName(className, true, loader));
				Field f = c.getField("CREATOR");

				@SuppressWarnings("unchecked")
				Persistable.Persister<T> creator = (Persistable.Persister<T>) f.get(null);

				if (creator == null) {
					throw new BadParcelableException("Persistable protocol requires a Persistable.Persister object called CREATOR on class " + className);
				}

				return creator;
			} catch (IllegalAccessException e) {
				Log.e(TAG, "Illegal access when unmarshalling: " + className, e);
				throw new BadParcelableException("IllegalAccessException when unmarshalling: " + className);
			} catch (ClassNotFoundException e) {
				Log.e(TAG, "Class not found when unmarshalling: " + className, e);
				throw new BadParcelableException("ClassNotFoundException when unmarshalling: " + className);
			} catch (ClassCastException e) {
				throw new BadParcelableException("Persistable protocol requires a Persistable.Persister object called CREATOR on class " + className);
			} catch (NoSuchFieldException e) {
				throw new BadParcelableException("Persistable protocol requires a Persistable.Persister object called CREATOR on class " + className);
			} catch (NullPointerException e) {
				throw new BadParcelableException("Persistable protocol requires the CREATOR object to be static on class " + className);
			}
		}

	}

}
