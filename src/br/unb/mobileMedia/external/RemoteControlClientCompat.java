
package br.unb.mobileMedia.external;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.os.Looper;
import android.util.Log;

/**
 * A remote control client object is associated with a
 * media button event receiver.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class RemoteControlClientCompat {
	private static final String TAG = "RemoteControlCompat";
	private static Class sRemoteControlClientClass;
	private static Method sRCCEditMetadataMethod;
	private static Method sRCCSetPlayStateMethod;
	private static Method sRCCSetTransportControlFlags;
	private static boolean sHasRemoteControlAPIs = false;

	static {
		try {
			ClassLoader classLoader = RemoteControlClientCompat.class.getClassLoader();

			sRemoteControlClientClass = getActualRemoteControlClientClass(classLoader);

			for (Field field : RemoteControlClientCompat.class.getFields()) {
				try {
					Field realField  = sRemoteControlClientClass.getField(field.getName());
					Object realValue = realField.get(null);
					field.set(null, realValue);
				} catch (NoSuchFieldException e) {
					Log.w(TAG, "Could not get real field: " + field.getName());
				} catch (IllegalArgumentException e) {
					Log.w(TAG, "Error trying to pull field value for: " + field.getName() + " " + e.getMessage());
				} catch (IllegalAccessException e) {
					Log.w(TAG, "Error trying to pull field value for: " + field.getName() + " " + e.getMessage());
				}
			}

			sRCCEditMetadataMethod = sRemoteControlClientClass.getMethod("editMetadata", boolean.class);
			sRCCSetPlayStateMethod = sRemoteControlClientClass.getMethod("setPlaybackState", int.class);
			sRCCSetTransportControlFlags = sRemoteControlClientClass.getMethod("setTransportControlFlags", int.class);
			sHasRemoteControlAPIs = true;
		} catch (ClassNotFoundException e) {
			// Silently fail when running on an OS before ICS.
		} catch (NoSuchMethodException e) {
			// Silently fail when running on an OS before ICS.
		} catch (IllegalArgumentException e) {
			// Silently fail when running on an OS before ICS.
		} catch (SecurityException e) {
			// Silently fail when running on an OS before ICS.
		}
	}

	public static Class getActualRemoteControlClientClass(ClassLoader classLoader) 	throws ClassNotFoundException {
		return classLoader.loadClass("android.media.RemoteControlClient");
	}

	private Object mActualRemoteControlClient;

	public RemoteControlClientCompat(PendingIntent pendingIntent) {
		if (!sHasRemoteControlAPIs) {
			return;
		}
		try {
			mActualRemoteControlClient = sRemoteControlClientClass.getConstructor(PendingIntent.class).newInstance(pendingIntent);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public RemoteControlClientCompat(PendingIntent pendingIntent, Looper looper) {
		if (!sHasRemoteControlAPIs) {
			return;
		}
		try {
			mActualRemoteControlClient =
					sRemoteControlClientClass.getConstructor(PendingIntent.class, Looper.class)
					.newInstance(pendingIntent, looper);
		} catch (Exception e) {
			Log.e(TAG, "Error creating new instance of " + sRemoteControlClientClass.getName(), e);
		}
	}

	/**
	 * Class used to modify metadata in a RemoteControlClient object.
	 */
	public class MetadataEditorCompat {

		private Method mPutStringMethod;
		private Method mPutBitmapMethod;
		private Method mPutLongMethod;
		private Method mClearMethod;
		private Method mApplyMethod;

		private Object mActualMetadataEditor;

		/**
		 * The metadata key for the content artwork / album art.
		 */
		public final static int METADATA_KEY_ARTWORK = 100;

		private MetadataEditorCompat(Object actualMetadataEditor) {
			if (sHasRemoteControlAPIs && actualMetadataEditor == null) {
				throw new IllegalArgumentException("Remote Control API's exist, " +
						"should not be given a null MetadataEditor");
			}
			if (sHasRemoteControlAPIs) {
				Class metadataEditorClass = actualMetadataEditor.getClass();

				try {
					mPutStringMethod = metadataEditorClass.getMethod("putString",
							int.class, String.class);

					mPutBitmapMethod = metadataEditorClass.getMethod("putBitmap",
							int.class, Bitmap.class);

					mPutLongMethod = metadataEditorClass.getMethod("putLong",
							int.class, long.class);

					mClearMethod = metadataEditorClass.getMethod("clear", new Class[]{});
					mApplyMethod = metadataEditorClass.getMethod("apply", new Class[]{});

				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			mActualMetadataEditor = actualMetadataEditor;
		}

		/**
		 * Adds textual information to be displayed.
		 */
		public MetadataEditorCompat putString(int key, String value) {
			if (sHasRemoteControlAPIs) {
				try {
					mPutStringMethod.invoke(mActualMetadataEditor, key, value);
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			return this;
		}

		/**
		 * Sets the album / artwork picture to be displayed on the remote control.
		 */
		public MetadataEditorCompat putBitmap(int key, Bitmap bitmap) {
			if (sHasRemoteControlAPIs) {
				try {
					mPutBitmapMethod.invoke(mActualMetadataEditor, key, bitmap);

				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			return this;
		}

		/**
		 * Adds numerical information to be displayed.
		 */
		public MetadataEditorCompat putLong(int key, long value) {
			if (sHasRemoteControlAPIs) {
				try {
					mPutLongMethod.invoke(mActualMetadataEditor, key, value);
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			return this;
		}

		/**
		 * Clears all the metadata that has been set
		 */
		public void clear() {
			if (sHasRemoteControlAPIs) {
				try {
					mClearMethod.invoke(mActualMetadataEditor, (Object[]) null);
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}

		/**
		 * Associates all the metadata that has been set 
		 */
		public void apply() {
			if (sHasRemoteControlAPIs) {
				try {
					mApplyMethod.invoke(mActualMetadataEditor, (Object[]) null);
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
	}

	public MetadataEditorCompat editMetadata(boolean startEmpty) {
		Object metadataEditor;
		if (sHasRemoteControlAPIs) {
			try {
				metadataEditor = sRCCEditMetadataMethod.invoke(mActualRemoteControlClient,
						startEmpty);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			metadataEditor = null;
		}
		return new MetadataEditorCompat(metadataEditor);
	}

	/**
	 * Sets the current playback state.
	 */
	public void setPlaybackState(int state) {
		if (sHasRemoteControlAPIs) {
			try {
				sRCCSetPlayStateMethod.invoke(mActualRemoteControlClient, state);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Sets the flags for the media transport control buttons that this client supports.
	 */
	public void setTransportControlFlags(int transportControlFlags) {
		if (sHasRemoteControlAPIs) {
			try {
				sRCCSetTransportControlFlags.invoke(mActualRemoteControlClient,
						transportControlFlags);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public final Object getActualRemoteControlClientObject() {
		return mActualRemoteControlClient;
	}
}
