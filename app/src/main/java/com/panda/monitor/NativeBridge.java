package com.panda.monitor;

import android.util.Log;

/**
 * NativeBridge in package com.panda.monitor handles communication
 * between the Android monitoring/configuration UI and the native C++ runtime layer.
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
        // Prevent direct instantiation
    }

    /**
     * Checks if the native shared library was successfully loaded into the process.
     *
     * @return true if loaded and ready for JNI invocations, false otherwise.
     */
    public static boolean isLoaded() {
        return isLibraryLoaded;
    }

    // =========================================================================
    // JNI Native Method Declarations
    // =========================================================================

    /**
     * Retrieves an array of feature names and engine subsystem descriptions.
     *
     * @return String array of available runtime features.
     */
    public static native String[] getFeatureList();

    /**
     * Toggles an engine feature by its index in the feature table.
     *
     * @param featureIndex The zero-based index of the feature.
     * @param enabled      The new enabled/disabled state.
     */
    public static native void onFeatureToggled(int featureIndex, boolean enabled);

    /**
     * Updates a floating-point parameter in the native RuntimeConfig struct.
     *
     * @param paramKey Key identifier (e.g. "param_a", "time_scale").
     * @param value    The float value to store.
     */
    public static native void setFloatParam(String paramKey, float value);

    /**
     * Updates an integer parameter in the native RuntimeConfig struct.
     *
     * @param paramKey Key identifier (e.g. "param_b", "max_fps").
     * @param value    The int value to store.
     */
    public static native void setIntParam(String paramKey, int value);

    /**
     * Updates a boolean parameter in the native RuntimeConfig struct.
     *
     * @param paramKey Key identifier.
     * @param value    The boolean value to store.
     */
    public static native void setBoolParam(String paramKey, boolean value);

    /**
     * Resets all runtime configurations back to engine defaults.
     */
    public static native void resetConfigToDefaults();

    // =========================================================================
    // Safe Helper Wrappers (with fallbacks when running without native lib)
    // =========================================================================

    public static String[] safeGetFeatureList() {
        if (!isLibraryLoaded) {
            return new String[] {
                "Physics Decoupling (60Hz)",
                "High Precision Audio Tick",
                "Dynamic LOD Streaming",
                "GPU Occlusion Culling",
                "Telemetry Event Logger"
            };
        }
        try {
            return getFeatureList();
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native getFeatureList error: " + e.getMessage());
            return new String[0];
        }
    }

    public static void safeToggleFeature(int featureIndex, boolean enabled) {
        if (!isLibraryLoaded) {
            Log.d(TAG, "Fallback: Feature [" + featureIndex + "] set to " + enabled);
            return;
        }
        try {
            onFeatureToggled(featureIndex, enabled);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native onFeatureToggled error: " + e.getMessage());
        }
    }

    public static void safeSetFloatParam(String paramKey, float value) {
        if (!isLibraryLoaded) {
            Log.d(TAG, "Fallback: Float param [" + paramKey + "] set to " + value);
            return;
        }
        try {
            setFloatParam(paramKey, value);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native setFloatParam error: " + e.getMessage());
        }
    }

    public static void safeSetIntParam(String paramKey, int value) {
        if (!isLibraryLoaded) {
            Log.d(TAG, "Fallback: Int param [" + paramKey + "] set to " + value);
            return;
        }
        try {
            setIntParam(paramKey, value);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native setIntParam error: " + e.getMessage());
        }
    }

    public static void safeSetBoolParam(String paramKey, boolean value) {
        if (!isLibraryLoaded) {
            Log.d(TAG, "Fallback: Bool param [" + paramKey + "] set to " + value);
            return;
        }
        try {
            setBoolParam(paramKey, value);
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native setBoolParam error: " + e.getMessage());
        }
    }
}
