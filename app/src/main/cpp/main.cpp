#include <jni.h>
#include <string>
#include <vector>
#include <android/log.h>
#include "config.h"

#define TAG "NativeEngine"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

RuntimeConfig g_config;

static const std::vector<std::string> kFeatureList = {
    "Physics Decoupling (60Hz)",
    "High Precision Audio Tick",
    "Dynamic LOD Streaming",
    "GPU Occlusion Culling",
    "Telemetry Event Logger"
};

extern "C" {

JNIEXPORT jobjectArray JNICALL
Java_com_example_NativeBridge_getFeatureList(JNIEnv* env, jclass /* clazz */) {
    jclass stringClass = env->FindClass("java/lang/String");
    if (!stringClass) {
        LOGE("Failed to find String class");
        return nullptr;
    }

    jobjectArray array = env->NewObjectArray(
        static_cast<jsize>(kFeatureList.size()),
        stringClass,
        nullptr
    );

    for (size_t i = 0; i < kFeatureList.size(); ++i) {
        jstring str = env->NewStringUTF(kFeatureList[i].c_str());
        env->SetObjectArrayElement(array, static_cast<jsize>(i), str);
        env->DeleteLocalRef(str);
    }

    return array;
}

JNIEXPORT void JNICALL
Java_com_example_NativeBridge_onFeatureToggled(
    JNIEnv* /* env */,
    jclass /* clazz */,
    jint featureIndex,
    jboolean enabled
) {
    LOGI("Feature toggled: index=%d, enabled=%d", featureIndex, enabled);
    switch (featureIndex) {
        case 0:
            g_config.feature_1_enabled.store(enabled, std::memory_order_relaxed);
            break;
        case 1:
            g_config.feature_2_enabled.store(enabled, std::memory_order_relaxed);
            break;
        case 2:
            g_config.debug_rendering.store(enabled, std::memory_order_relaxed);
            break;
        case 3:
            g_config.telemetry_enabled.store(enabled, std::memory_order_relaxed);
            break;
        default:
            LOGD("Unhandled feature index %d", featureIndex);
            break;
    }
}

JNIEXPORT void JNICALL
Java_com_example_NativeBridge_setFloatParam(
    JNIEnv* env,
    jclass /* clazz */,
    jstring paramKey,
    jfloat value
) {
    if (!paramKey) return;
    const char* key = env->GetStringUTFChars(paramKey, nullptr);
    if (key) {
        LOGI("Set float param [%s] = %f", key, value);
        if (std::string(key) == "param_a") {
            g_config.param_a.store(value, std::memory_order_relaxed);
        }
        env->ReleaseStringUTFChars(paramKey, key);
    }
}

JNIEXPORT void JNICALL
Java_com_example_NativeBridge_setIntParam(
    JNIEnv* env,
    jclass /* clazz */,
    jstring paramKey,
    jint value
) {
    if (!paramKey) return;
    const char* key = env->GetStringUTFChars(paramKey, nullptr);
    if (key) {
        LOGI("Set int param [%s] = %d", key, value);
        if (std::string(key) == "param_b") {
            g_config.param_b.store(value, std::memory_order_relaxed);
        }
        env->ReleaseStringUTFChars(paramKey, key);
    }
}

JNIEXPORT void JNICALL
Java_com_example_NativeBridge_setBoolParam(
    JNIEnv* env,
    jclass /* clazz */,
    jstring paramKey,
    jboolean value
) {
    if (!paramKey) return;
    const char* key = env->GetStringUTFChars(paramKey, nullptr);
    if (key) {
        LOGI("Set bool param [%s] = %d", key, value);
        if (std::string(key) == "feature_1") {
            g_config.feature_1_enabled.store(value, std::memory_order_relaxed);
        } else if (std::string(key) == "feature_2") {
            g_config.feature_2_enabled.store(value, std::memory_order_relaxed);
        }
        env->ReleaseStringUTFChars(paramKey, key);
    }
}

JNIEXPORT void JNICALL
Java_com_example_NativeBridge_resetConfigToDefaults(
    JNIEnv* /* env */,
    jclass /* clazz */
) {
    LOGI("Resetting RuntimeConfig to engine defaults");
    g_config.Reset();
}

JNIEXPORT jobjectArray JNICALL
Java_com_panda_monitor_NativeBridge_getFeatureList(JNIEnv* env, jclass clazz) {
    return Java_com_example_NativeBridge_getFeatureList(env, clazz);
}

JNIEXPORT void JNICALL
Java_com_panda_monitor_NativeBridge_onFeatureToggled(
    JNIEnv* env,
    jclass clazz,
    jint featureIndex,
    jboolean enabled
) {
    Java_com_example_NativeBridge_onFeatureToggled(env, clazz, featureIndex, enabled);
}

JNIEXPORT void JNICALL
Java_com_panda_monitor_NativeBridge_setFloatParam(
    JNIEnv* env,
    jclass clazz,
    jstring paramKey,
    jfloat value
) {
    Java_com_example_NativeBridge_setFloatParam(env, clazz, paramKey, value);
}

JNIEXPORT void JNICALL
Java_com_panda_monitor_NativeBridge_setIntParam(
    JNIEnv* env,
    jclass clazz,
    jstring paramKey,
    jint value
) {
    Java_com_example_NativeBridge_setIntParam(env, clazz, paramKey, value);
}

JNIEXPORT void JNICALL
Java_com_panda_monitor_NativeBridge_setBoolParam(
    JNIEnv* env,
    jclass clazz,
    jstring paramKey,
    jboolean value
) {
    Java_com_example_NativeBridge_setBoolParam(env, clazz, paramKey, value);
}

JNIEXPORT void JNICALL
Java_com_panda_monitor_NativeBridge_resetConfigToDefaults(
    JNIEnv* env,
    jclass clazz
) {
    Java_com_example_NativeBridge_resetConfigToDefaults(env, clazz);
}

} // extern "C"
