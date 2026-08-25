#ifndef MENU_SETUP_H
#define MENU_SETUP_H

#include <jni.h>
#include <unistd.h>
#include <string.h>
#include <dlfcn.h>
// TIDAK include obfuscate.h/Logger.h/Utils.h di sini
// Main.cpp sudah include mereka SEBELUM #include "Menu.h"

bool titleValid = true, headingValid = true, iconValid = true, settingsValid = true, isLeeched = false, initValid = true;

void *antiLeech(void *) {
    sleep(20);
    // ANTI-LEECH CRASH CODE DIHAPUS!
    return NULL;
}

void setText(JNIEnv *env, jobject obj, const char* text) {
    jclass html = env->FindClass("android/text/Html");
    jmethodID fromHtml = env->GetStaticMethodID(html, "fromHtml", "(Ljava/lang/String;)Landroid/text/Spanned;");
    jclass textView = env->FindClass("android/widget/TextView");
    jmethodID setTextM = env->GetMethodID(textView, "setText", "(Ljava/lang/CharSequence;)V");
    jstring jstr = env->NewStringUTF(text);
    env->CallVoidMethod(obj, setTextM, env->CallStaticObjectMethod(html, fromHtml, jstr));
}

extern "C" {

JNIEXPORT void JNICALL
Java_uk_lgl_modmenu_FloatingModMenuService_setTitleText(JNIEnv *env, jobject thiz, jobject obj) {
    setText(env, obj, "<b><font color='#FFD700'>XIEPANDA MOD</font></b>");
    
    jclass textViewClass = env->FindClass("android/widget/TextView");
    jmethodID setTextSize = env->GetMethodID(textViewClass, "setTextSize", "(IF)V");
    env->CallVoidMethod(obj, setTextSize, 2, 26.0f);
    
    jclass colorClass = env->FindClass("android/graphics/Color");
    jmethodID parseColor = env->GetStaticMethodID(colorClass, "parseColor", "(Ljava/lang/String;)I");
    jstring whiteStr = env->NewStringUTF("#FFFFFF");
    jint white = env->CallStaticIntMethod(colorClass, parseColor, whiteStr);
    jmethodID setTextColor = env->GetMethodID(textViewClass, "setTextColor", "(I)V");
    env->CallVoidMethod(obj, setTextColor, white);
    
    titleValid = true;
}

JNIEXPORT void JNICALL
Java_uk_lgl_modmenu_FloatingModMenuService_setHeadingText(JNIEnv *env, jobject thiz, jobject obj) {
    setText(env, obj, "<b><marquee><p style=\"font-size:30\">"
                     "<p style=\"color:#00FFFF;\">XIEPANDA MOD BUSSID</p> | "
                     "GOD EDITION | 10 FITUR UTAMA</p></marquee></b>");
    
    jclass textViewClass = env->FindClass("android/widget/TextView");
    jmethodID setTextSize = env->GetMethodID(textViewClass, "setTextSize", "(IF)V");
    env->CallVoidMethod(obj, setTextSize, 2, 14.0f);
    
    jclass colorClass = env->FindClass("android/graphics/Color");
    jmethodID parseColor = env->GetStaticMethodID(colorClass, "parseColor", "(Ljava/lang/String;)I");
    jstring cyanStr = env->NewStringUTF("#00FFFF");
    jint cyan = env->CallStaticIntMethod(colorClass, parseColor, cyanStr);
    jmethodID setTextColor = env->GetMethodID(textViewClass, "setTextColor", "(I)V");
    env->CallVoidMethod(obj, setTextColor, cyan);
    
    headingValid = true;
}

JNIEXPORT jstring JNICALL
Java_uk_lgl_modmenu_FloatingModMenuService_Icon(JNIEnv *env, jobject thiz) {
    iconValid = true;
    return env->NewStringUTF("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");
}

JNIEXPORT jstring JNICALL
Java_uk_lgl_modmenu_FloatingModMenuService_IconWebViewData(JNIEnv *env, jobject thiz) {
    iconValid = true;
    return NULL;
}

JNIEXPORT jobjectArray JNICALL
Java_uk_lgl_modmenu_FloatingModMenuService_settingsList(JNIEnv *env, jobject activityObject) {
    jobjectArray ret;

    const char *features[] = {
            "Category_Settings",
            "-1_Toggle_Save feature preferences",
            "-3_Toggle_Auto size vertically",
            "Category_Logcat",
            "RichTextView_Save logcat if a bug occured and sent it to the modder.",
            "-4_Button_Save logcat to file",
            "-5_Button_Clear logcat",
            "Category_Menu",
            "-6_Button_Close settings",
    };

    int Total_Feature = (sizeof features / sizeof features[0]);
    ret = (jobjectArray)
            env->NewObjectArray(Total_Feature, env->FindClass("java/lang/String"),
                                env->NewStringUTF(""));
    for (int i = 0; i < Total_Feature; i++)
        env->SetObjectArrayElement(ret, i, env->NewStringUTF(features[i]));

    settingsValid = true;
    return (ret);
}

JNIEXPORT jboolean JNICALL
Java_uk_lgl_modmenu_FloatingModMenuService_IsGameLibLoaded(JNIEnv *env, jobject thiz) {
    void* handle = dlopen("libil2cpp.so", RTLD_NOW);
    if (handle) { dlclose(handle); return JNI_TRUE; }
    return JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_uk_lgl_modmenu_FloatingModMenuService_Init(JNIEnv *env, jobject thiz, jobject ctx, jobject title, jobject subtitle) {
    initValid = true;
}

JNIEXPORT void JNICALL
Java_uk_lgl_modmenu_Main_CheckOverlayPermission(JNIEnv *env, jclass thiz, jobject ctx) {
    jclass settings = env->FindClass("android/provider/Settings");
    jmethodID canDraw = env->GetStaticMethodID(settings, "canDrawOverlays", "(Landroid/content/Context;)Z");
    jboolean canDrawResult = env->CallStaticBooleanMethod(settings, canDraw, ctx);
    if (!canDrawResult) {
        jclass intentClass = env->FindClass("android/content/Intent");
        jmethodID intentInit = env->GetMethodID(intentClass, "<init>", "(Ljava/lang/String;Landroid/net/Uri;)V");
        jstring action = env->NewStringUTF("android.settings.action.MANAGE_OVERLAY_PERMISSION");
        jclass uriClass = env->FindClass("android/net/Uri");
        jmethodID parse = env->GetStaticMethodID(uriClass, "parse", "(Ljava/lang/String;)Landroid/net/Uri;");
        jstring pkgStr = env->NewStringUTF("package:com.android.support");
        jobject pkgUri = env->CallStaticObjectMethod(uriClass, parse, pkgStr);
        jobject intentObj = env->NewObject(intentClass, intentInit, action, pkgUri);
        jclass contextClass = env->FindClass("android/content/Context");
        jmethodID startActivity = env->GetMethodID(contextClass, "startActivity", "(Landroid/content/Intent;)V");
        env->CallVoidMethod(ctx, startActivity, intentObj);
    }
}

} // extern "C"

#endif // MENU_SETUP_H
