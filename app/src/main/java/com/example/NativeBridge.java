package com.example;

import android.util.Log;

/**
 * NativeBridge handles communication between the Android UI layer
 * and the native C++ engine / runtime configuration layer.
 */
public final class NativeBridge {

    private static final String TAG = "NativeBridge";
    private static boolean isLibraryLoaded = false;

    static {
        try {
            System.loadLibrary("native-lib");
            isLibraryLoaded = true;
            Log.i(TAG, "Native library 'native-lib' loaded successfully.");
        } catch (UnsatisfiedLinkError e) {
            Log.w(TAG, "Native library 'native-lib' could not be loaded: " + e.getMessage());
            isLibraryLoaded = false;
        }
    }

    private NativeBridge() {
        // Prevent instantiation
    }

    /**
     * Checks whether the native library is loaded and ready.
     *
     * @return true if loaded, false otherwise.
     */
    public static boolean isLoaded() {
        return isLibraryLoaded;
    }

    // =========================================================================
    // Native JNI Method Declarations
    // =========================================================================

    /**
     * Retrieves the list of available engine feature names/descriptors.
     *
     * @return Array of feature names.
     */
    public static native String[] getFeatureList();

    /**
     * Toggles an engine feature by its index in the feature table.
     *
     * @param featureIndex Index of the feature.
     * @param enabled      New boolean toggle state.
     */
    public static native void onFeatureToggled(int featureIndex, boolean enabled);

    /**
     * Sets a floating-point parameter in the native RuntimeConfig struct.
     *
     * @param paramKey Key identifier (or enum/index).
     * @param value    Float value to set.
     */
    public static native void setFloatParam(String paramKey, float value);

    /**
     * Sets an integer parameter in the native RuntimeConfig struct.
     *
     * @param paramKey Key identifier (or enum/index).
     * @param value    Integer value to set.
     */
    public static native void setIntParam(String paramKey, int value);

    /**
     * Sets a boolean configuration parameter in the native RuntimeConfig struct.
     *
     * @param paramKey Key identifier.
     * @param value    Boolean value to set.
     */
    public static native void setBoolParam(String paramKey, boolean value);

    /**
     * Resets all runtime configuration parameters to their engine defaults.
     */
    public static native void resetConfigToDefaults();

    // =========================================================================
    // Safe Helper Wrappers (handles cases where native lib is unavailable)
    // =========================================================================

    public static String[] safeGetFeatureList() {
        if (!isLibraryLoaded) {
            return new String[] {
                "Feature 1 (Engine Default)",
                "Feature 2 (Engine Default)",
                "Physics Simulation Mode",
                "Telemetry Logging"
            };
        }
        try {
            return getFeatureList();
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed calling native getFeatureList: " + e.getMessage());
            return new String[0];
        }
    }

    public static void safeToggleFeature(int featureIndex, boolean enabled) {
        if (!isLibraryLoaded) {
            Log.d(TAG, "Simulated feature toggle [" + featureIndex + "] = " + enabled);
            return;
        }
        try {
            onFeatureToggled(featureIndex, enabled);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed calling native onFeatureToggled: " + e.getMessage());
        }
    }

    public static void safeSetFloatParam(String paramKey, float value) {
        if (!isLibraryLoaded) {
            Log.d(TAG, "Simulated float param update [" + paramKey + "] = " + value);
            return;
        }
        try {
            setFloatParam(paramKey, value);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed calling native setFloatParam: " + e.getMessage());
        }
    }

    public static void safeSetIntParam(String paramKey, int value) {
        if (!isLibraryLoaded) {
            Log.d(TAG, "Simulated int param update [" + paramKey + "] = " + value);
            return;
        }
        try {
            setIntParam(paramKey, value);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed calling native setIntParam: " + e.getMessage());
        }
    }

    public static void safeSetBoolParam(String paramKey, boolean value) {
        if (!isLibraryLoaded) {
            Log.d(TAG, "Simulated bool param update [" + paramKey + "] = " + value);
            return;
        }
        try {
            setBoolParam(paramKey, value);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed calling native setBoolParam: " + e.getMessage());
        }
    }
}
