#include <list>
#include <vector>
#include <string.h>
#include <pthread.h>
#include <cstring>
#include <jni.h>
#include <unistd.h>
#include <fstream>
#include <iostream>
#include <stdlib.h>
#include <stdint.h>
#include "Includes/Logger.h"
#include "Includes/obfuscate.h"
#include "Includes/Utils.h"
#include "KittyMemory/MemoryPatch.h"
#include "Menu.h"

#if defined(__aarch64__)
#include <And64InlineHook/And64InlineHook.hpp>
#else
#include <Substrate/SubstrateHook.h>
#include <Substrate/CydiaSubstrate.h>
#endif

struct My_Patches {
    MemoryPatch UnlockAllBus;
    MemoryPatch GodMode;
    MemoryPatch FreeMP;
    MemoryPatch AntiReport;
    MemoryPatch BensinGratis;
} hexPatches;

bool bUangX100 = false;
bool bServerAddRp = false;
bool bStaminaUnlimited = false;
bool bLemparBus = false;
bool bInstantWin = false;

struct Vec3 { float x, y, z; };

#define targetLibName OBFUSCATE("libil2cpp.so")

void (*old_AddCache)(void *inst, int value);
void AddCache(void *inst, int value) {
    if (bUangX100) value *= 100;
    old_AddCache(inst, value);
}

void (*old_AddRp)(void *inst, int value, void *cb, void *err);
void AddRp(void *inst, int value, void *cb, void *err) {
    if (bServerAddRp) value = 1000000000;
    old_AddRp(inst, value, cb, err);
}

int (*old_GetStamina)(void *inst);
int GetStamina(void *inst) {
    if (bStaminaUnlimited) return 999;
    return old_GetStamina(inst);
}

void (*old_SetNetPos)(void *inst, Vec3 value);
void SetNetPos(void *inst, Vec3 value) {
    if (bLemparBus) {
        value.y += 500.0f;
        value.x += (rand() % 200 - 100);
        value.z += (rand() % 200 - 100);
    }
    old_SetNetPos(inst, value);
}

void (*old_FinishRace)(void *inst, void *player, float time);
void FinishRace(void *inst, void *player, float time) {
    if (bInstantWin) time = 0.001f;
    old_FinishRace(inst, player, time);
}

void *hack_thread(void *) {
    LOGI(OBFUSCATE("XiePanDa Mod Loading..."));

    do {
        sleep(1);
    } while (!isLibraryLoaded(targetLibName));

    LOGI(OBFUSCATE("libil2cpp.so loaded!"));

#if !defined(__aarch64__)
    hexPatches.UnlockAllBus = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2369008", 'a')),
        OBFUSCATE("01 00 A0 E3 1E FF 2F E1"));

    hexPatches.GodMode = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2376ABC", 'b')),
        OBFUSCATE("00 00 A0 E3 1E FF 2F E1"));

    hexPatches.FreeMP = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2375CC8", 'c')),
        OBFUSCATE("00 00 A0 E3 1E FF 2F E1"));

    hexPatches.AntiReport = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2379FA0", 'd')),
        OBFUSCATE("1E FF 2F E1"));

    hexPatches.BensinGratis = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2283F1C", 'e')),
        OBFUSCATE("00 00 A0 E3 1E FF 2F E1"));

    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x21C8204),
                   (void *) AddCache, (void **) &old_AddCache);

    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x226F518),
                   (void *) AddRp, (void **) &old_AddRp);

    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x226D4DC),
                   (void *) GetStamina, (void **) &old_GetStamina);

    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x217C4E4),
                   (void *) SetNetPos, (void **) &old_SetNetPos);

    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x2204738),
                   (void *) FinishRace, (void **) &old_FinishRace);

    LOGI(OBFUSCATE("All hooks installed!"));
#endif

    return NULL;
}

extern "C" {

JNIEXPORT jobjectArray
JNICALL
Java_uk_lgl_modmenu_FloatingModMenuService_getFeatureList(JNIEnv *env, jobject context) {
    jobjectArray ret;

    MakeToast(env, context, OBFUSCATE("Maino Games - GOD Edition"), Toast::LENGTH_LONG);

    const char *features[] = {
        OBFUSCATE("Category_XIEPANDA MOD - 10 FITUR UTAMA"),
        OBFUSCATE("Toggle_Uang x100 Masuk"),
        OBFUSCATE("Toggle_SERVER +1 MILIAR"),
        OBFUSCATE("Toggle_Unlock All Bus"),
        OBFUSCATE("Toggle_GOD MODE MP"),
        OBFUSCATE("Toggle_Free Masuk MP"),
        OBFUSCATE("Toggle_Anti Report"),
        OBFUSCATE("Toggle_Bensin Harga 0"),
        OBFUSCATE("Toggle_Stamina Unlimited"),
        OBFUSCATE("Toggle_Lempar Bus ke Langit"),
        OBFUSCATE("Toggle_Instant Win Race"),
    };

    int Total_Feature = (sizeof features / sizeof features[0]);
    ret = (jobjectArray)
            env->NewObjectArray(Total_Feature, env->FindClass(OBFUSCATE("java/lang/String")),
                                env->NewStringUTF(""));

    for (int i = 0; i < Total_Feature; i++)
        env->SetObjectArrayElement(ret, i, env->NewStringUTF(features[i]));

    // ANTI-LEECH DIHAPUS!
    // pthread_t ptid;
    // pthread_create(&ptid, NULL, antiLeech, NULL);

    return (ret);
}

JNIEXPORT void JNICALL
Java_uk_lgl_modmenu_Preferences_Changes(JNIEnv *env, jclass clazz, jobject obj,
                                        jint featNum, jstring featName, jint value,
                                        jboolean boolean, jstring str) {
    const char *featureName = env->GetStringUTFChars(featName, 0);
    const char *TextInput;
    if (str != NULL)
        TextInput = env->GetStringUTFChars(str, 0);
    else
        TextInput = "Empty";

    LOGD(OBFUSCATE("Feature: %d | Bool: %d"), featNum, boolean);

    switch (featNum) {
        case 0:
            bUangX100 = boolean;
            break;
        case 1:
            bServerAddRp = boolean;
            break;
        case 2:
            if (boolean) {
                hexPatches.UnlockAllBus.Modify();
            } else {
                hexPatches.UnlockAllBus.Restore();
            }
            break;
        case 3:
            if (boolean) {
                hexPatches.GodMode.Modify();
            } else {
                hexPatches.GodMode.Restore();
            }
            break;
        case 4:
            if (boolean) {
                hexPatches.FreeMP.Modify();
            } else {
                hexPatches.FreeMP.Restore();
            }
            break;
        case 5:
            if (boolean) {
                hexPatches.AntiReport.Modify();
            } else {
                hexPatches.AntiReport.Restore();
            }
            break;
        case 6:
            if (boolean) {
                hexPatches.BensinGratis.Modify();
            } else {
                hexPatches.BensinGratis.Restore();
            }
            break;
        case 7:
            bStaminaUnlimited = boolean;
            break;
        case 8:
            bLemparBus = boolean;
            break;
        case 9:
            bInstantWin = boolean;
            break;
    }
}

}

__attribute__((constructor))
void lib_main() {
    pthread_t ptid;
    pthread_create(&ptid, NULL, hack_thread, NULL);
}
