#include <list>
#include <vector>
#include <string.h>
#include <pthread.h>
#include <cstring>
#include <jni.h>
#include <unistd.h>
#include <fstream>
#include <iostream>
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

// fancy struct for patches for kittyMemory
struct My_Patches {
    MemoryPatch TarifBesar, GajianBesar, BusPenuh, AksesoriGratis;
    MemoryPatch TransaksiGratis, DanaCukup, UnlockAllBus;
    MemoryPatch FuelBypass, BensinHarga0, FreeTol, FreeDerek, RepairGratis;
    MemoryPatch Stamina, SideJobInstan;
    MemoryPatch GodMode, InstantEngine, NoSlip;
    MemoryPatch FreeMP, BobolRoom, MPUnlimited, ModMode;
    MemoryPatch KeluarInstan, SkipTunggu, BypassMaint;
    MemoryPatch AntiReport, AntiShutdown, AntiReportLivery;
    MemoryPatch RuteValid, Jarak999, BypassMap, NoPopupDL, KameraBebas;
    MemoryPatch SkipAds, SkipRewardAds, HornGratis, LiveryGratis, UnlockLivery;
} hexPatches;

bool feature1 = false, feature2 = false, featureHookToggle = false;
int sliderValue = 1, level = 0;
void *instanceBtn;

// Hook flags
bool bUangX100 = false, bLightBus = false, bBanLem = false;
bool bAntiRoll = false, bRemInstan = false, bKemudi89 = false;
bool bMundurCepat = false, bMotorTorque = false;
bool bFakeMoney = false, bUang999jt = false, bSavePerm = false;
bool bServerAddRp = false, bGajiX100 = false, bSetCashCore = false;
bool bGoldLoad = false, bUnlockItem = false, bStaminaHook = false;
bool bLemparBus = false, bLemparDriver = false, bFreezeDriver = false;
bool bInstantWin = false, bJarakDekat = false, bAntiKick = false;

void (*AddMoneyExample)(void *instance, int amount);

//Target lib here
#define targetLibName OBFUSCATE("libil2cpp.so")

// ============================================================
// HOOKS BUSSID (ARM32)
// ============================================================

// Uang x100 (_AddCache)
void (*old_AddCache)(void *inst, int value);
void AddCache(void *inst, int value) {
    if (bUangX100) value *= 100;
    old_AddCache(inst, value);
}

// Set Uang 999jt (_SetCache)
void (*old_SetCache)(void *inst, int value);
void SetCache(void *inst, int value) {
    if (bUang999jt) value = 999999999;
    old_SetCache(inst, value);
}

// Save Permanen
void (*old_SaveMoney)(void *inst, int cash, int gold);
void SaveMoney(void *inst, int cash, int gold) {
    if (bSavePerm) { cash = 999999999; gold = 999999; }
    old_SaveMoney(inst, cash, gold);
}

// Fake Uang Display
void (*old_SetCashUI)(void *inst, int cash, int gold);
void SetCashUI(void *inst, int cash, int gold) {
    if (bFakeMoney) { cash = 999999999; gold = 999999; }
    old_SetCashUI(inst, cash, gold);
}

// SERVER +1 Miliar (AddRp)
void (*old_AddRp)(void *inst, int value, void *cb, void *err);
void AddRp(void *inst, int value, void *cb, void *err) {
    if (bServerAddRp) value = 1000000000;
    old_AddRp(inst, value, cb, err);
}

// Gaji x100 (FarePaymentSimple)
void (*old_FarePay)(void *inst, int earning, void *mode, void *cb, void *err);
void FarePay(void *inst, int earning, void *mode, void *cb, void *err) {
    if (bGajiX100) earning *= 100;
    old_FarePay(inst, earning, mode, cb, err);
}

// Set Cash Core
void (*old_SetPlayerCash)(void *inst, int cash);
void SetPlayerCash(void *inst, int cash) {
    if (bSetCashCore) cash = 999999999;
    old_SetPlayerCash(inst, cash);
}

// Gold Saat Load (ConvertFromDictionary)
void* (*old_ConvertDict)(void *inst, void *data);
void* ConvertDict(void *inst, void *data) {
    void* player = old_ConvertDict(inst, data);
    if (bGoldLoad && player != NULL)
        *(int*)((uintptr_t)player + 0x20) = 999999;
    return player;
}

// Unlock All Item (IsInventoryHasId)
bool (*old_HasItem)(void *inst, void *itemId);
bool HasItem(void *inst, void *itemId) {
    if (bUnlockItem) return true;
    return old_HasItem(inst, itemId);
}

// Stamina Unlimited
int (*old_GetStamina)(void *inst);
int GetStamina(void *inst) {
    if (bStaminaHook) return 999;
    return old_GetStamina(inst);
}

// FISIKA BUS
float (*old_getMass)(void *inst);
float getMass(void *inst) { return bLightBus ? 1.0f : old_getMass(inst); }

float (*old_getFriction)(void *inst);
float getFriction(void *inst) { return bBanLem ? 0.9f : old_getFriction(inst); }

float (*old_getAntiRoll)(void *inst);
float getAntiRoll(void *inst) { return bAntiRoll ? 1.0f : old_getAntiRoll(inst); }

float (*old_getBrake)(void *inst);
float getBrake(void *inst) { return bRemInstan ? 50000.0f : old_getBrake(inst); }

float (*old_getSteer)(void *inst);
float getSteer(void *inst) { return bKemudi89 ? 89.0f : old_getSteer(inst); }

float (*old_getRevSpeed)(void *inst);
float getRevSpeed(void *inst) { return bMundurCepat ? 200.0f : old_getRevSpeed(inst); }

float (*old_getMotorTorque)(void *inst);
float getMotorTorque(void *inst) { return bMotorTorque ? 99999.0f : old_getMotorTorque(inst); }

// WARFARE: Lempar Bus (set_position)
struct Vec3 { float x, y, z; };
void (*old_SetNetPos)(void *inst, Vec3 value);
void SetNetPos(void *inst, Vec3 value) {
    if (bLemparBus) {
        value.y += 500.0f;
        value.x += (rand() % 200 - 100);
        value.z += (rand() % 200 - 100);
    }
    old_SetNetPos(inst, value);
}

// Local Player Capture
void* (*old_GetLocalPlayer)();
void* GetLocalPlayer() {
    void* p = old_GetLocalPlayer();
    instanceBtn = p;
    return p;
}

// DRIVER: Lempar/Freeze Musuh
void (*old_DriverNetFixed)(void *inst);
void DriverNetFixed(void *inst) {
    old_DriverNetFixed(inst);
    if (inst == NULL || inst == instanceBtn) return;
    if (bLemparDriver) {
        *(float*)((uintptr_t)inst + 0x54) = 999.0f;
    }
    if (bFreezeDriver) {
        memset((void*)((uintptr_t)inst + 0x58), 0, 12);
    }
}

// INSTANT WIN RACE
void (*old_FinishRace)(void *inst, void *player, float time);
void FinishRace(void *inst, void *player, float time) {
    if (bInstantWin) time = 0.001f;
    old_FinishRace(inst, player, time);
}

// JARAK DEKAT (Link ctor)
void (*old_LinkCtor)(void *inst, void *target, float distance);
void LinkCtor(void *inst, void *target, float distance) {
    if (bJarakDekat) distance = 0.1f;
    old_LinkCtor(inst, target, distance);
}

// ANTI-KICK
void (*old_OnKickPlayer)(void *inst, void *evt);
void OnKickPlayer(void *inst, void *evt) {
    if (bAntiKick && evt != NULL) {
        void* targetRef = *(void**)((uintptr_t)evt + 0x8);
        if (targetRef == instanceBtn) return;
    }
    old_OnKickPlayer(inst, evt);
}

// ============================================================
// HACK THREAD
// ============================================================
void *hack_thread(void *) {
    LOGI(OBFUSCATE("pthread created"));

    do {
        sleep(1);
    } while (!isLibraryLoaded(targetLibName));

    LOGI(OBFUSCATE("%s has been loaded"), (const char *) targetLibName);

#if defined(__aarch64__)
    // ARM64 tidak dipakai (HP 32-bit) — biarkan kosong
#else
    // ============================================================
    // MEMORY PATCHES BUSSID (ARM32)
    // ============================================================
    
    // EKONOMI
    hexPatches.TarifBesar = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x233DA18", 'a')),
        OBFUSCATE("FF 3F A0 E3 1E FF 2F E1"));
    hexPatches.GajianBesar = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x233DB54", 'b')),
        OBFUSCATE("FF 3F A0 E3 1E FF 2F E1"));
    hexPatches.BusPenuh = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x233DD28", 'c')),
        OBFUSCATE("63 00 A0 E3 1E FF 2F E1"));
    hexPatches.AksesoriGratis = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x233D3D4", 'd')),
        OBFUSCATE("00 00 A0 E3 1E FF 2F E1"));
    hexPatches.TransaksiGratis = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2284EC4", 'e')),
        OBFUSCATE("00 00 A0 E3 1E FF 2F E1"));
    hexPatches.DanaCukup = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2285CA4", 'f')),
        OBFUSCATE("01 00 A0 E3 1E FF 2F E1"));
    hexPatches.UnlockAllBus = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2369008", 'g')),
        OBFUSCATE("01 00 A0 E3 1E FF 2F E1"));

    // BENSIN & LAYANAN
    hexPatches.FuelBypass = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2368788", 'h')),
        OBFUSCATE("00 00 A0 E3 1E FF 2F E1"));
    hexPatches.BensinHarga0 = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2283F1C", 'i')),
        OBFUSCATE("00 00 A0 E3 1E FF 2F E1"));
    hexPatches.FreeTol = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2375EE0", 'j')),
        OBFUSCATE("00 00 A0 E3 1E FF 2F E1"));
    hexPatches.FreeDerek = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2375EF0", 'k')),
        OBFUSCATE("00 00 A0 E3 1E FF 2F E1"));
    hexPatches.RepairGratis = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x211B8C4", 'l')),
        OBFUSCATE("00 00 A0 E3 1E FF 2F E1"));
    hexPatches.Stamina = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x226D4DC", 'm')),
        OBFUSCATE("E7 03 A0 E3 1E FF 2F E1"));
    hexPatches.SideJobInstan = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x22873B4", 'n')),
        OBFUSCATE("00 00 A0 E3 1E FF 2F E1"));

    // FISIKA
    hexPatches.GodMode = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2376ABC", 'o')),
        OBFUSCATE("00 00 A0 E3 1E FF 2F E1"));
    hexPatches.InstantEngine = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x23879F8", 'p')),
        OBFUSCATE("1E FF 2F E1"));
    hexPatches.NoSlip = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2204BC0", 'q')),
        OBFUSCATE("1E FF 2F E1"));

    // MULTIPLAYER
    hexPatches.FreeMP = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2375CC8", 'r')),
        OBFUSCATE("00 00 A0 E3 1E FF 2F E1"));
    hexPatches.BobolRoom = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2170DB8", 's')),
        OBFUSCATE("00 00 A0 E3 1E FF 2F E1"));
    hexPatches.MPUnlimited = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2368948", 't')),
        OBFUSCATE("9F 86 01 E3 1E FF 2F E1"));
    hexPatches.ModMode = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2373890", 'u')),
        OBFUSCATE("01 00 A0 E3 1E FF 2F E1"));
    hexPatches.KeluarInstan = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x22272E8", 'v')),
        OBFUSCATE("01 00 A0 E3 1E FF 2F E1"));
    hexPatches.SkipTunggu = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2376828", 'w')),
        OBFUSCATE("00 00 A0 E3 1E FF 2F E1"));
    hexPatches.BypassMaint = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2294E50", 'x')),
        OBFUSCATE("1E FF 2F E1"));

    // PROTEKSI
    hexPatches.AntiReport = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2379FA0", 'y')),
        OBFUSCATE("1E FF 2F E1"));
    hexPatches.AntiShutdown = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x222B438", 'z')),
        OBFUSCATE("1E FF 2F E1"));
    hexPatches.AntiReportLivery = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x226449C", 'A')),
        OBFUSCATE("1E FF 2F E1"));

    // RUTE & PETA
    hexPatches.RuteValid = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x22371C8", 'B')),
        OBFUSCATE("01 00 A0 E3 1E FF 2F E1"));
    hexPatches.Jarak999 = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2236D08", 'C')),
        OBFUSCATE("FF C3 9C E3 1E FF 2F E1"));
    hexPatches.BypassMap = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x224343C", 'D')),
        OBFUSCATE("01 00 A0 E3 1E FF 2F E1"));
    hexPatches.NoPopupDL = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2240F44", 'E')),
        OBFUSCATE("01 00 A0 E3 1E FF 2F E1"));
    hexPatches.KameraBebas = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x22327B4", 'F')),
        OBFUSCATE("1E FF 2F E1"));

    // UTILITAS
    hexPatches.SkipAds = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x233A5CC", 'G')),
        OBFUSCATE("1E FF 2F E1"));
    hexPatches.SkipRewardAds = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x239D1C4", 'H')),
        OBFUSCATE("1E FF 2F E1"));
    hexPatches.HornGratis = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2125014", 'I')),
        OBFUSCATE("1E FF 2F E1"));
    hexPatches.LiveryGratis = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2131C54", 'J')),
        OBFUSCATE("1E FF 2F E1"));
    hexPatches.UnlockLivery = MemoryPatch::createWithHex(targetLibName,
        string2Offset(OBFUSCATE_KEY("0x2385B80", 'K')),
        OBFUSCATE("01 00 A0 E3 1E FF 2F E1"));

    // ============================================================
    // INSTALL HOOKS (MSHookFunction)
    // ============================================================
    
    // EKONOMI HOOKS
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x21C8204),
                   (void *) AddCache, (void **) &old_AddCache);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x21C82E4),
                   (void *) SetCache, (void **) &old_SetCache);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x21C83EC),
                   (void *) SaveMoney, (void **) &old_SaveMoney);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x21FEF3C),
                   (void *) SetCashUI, (void **) &old_SetCashUI);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x226F518),
                   (void *) AddRp, (void **) &old_AddRp);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x2271D20),
                   (void *) FarePay, (void **) &old_FarePay);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x2281838),
                   (void *) SetPlayerCash, (void **) &old_SetPlayerCash);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x226E55C),
                   (void *) ConvertDict, (void **) &old_ConvertDict);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x226EFF8),
                   (void *) HasItem, (void **) &old_HasItem);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x226D4DC),
                   (void *) GetStamina, (void **) &old_GetStamina);

    // FISIKA HOOKS
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x2174BF0),
                   (void *) getMass, (void **) &old_getMass);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x2174D3C),
                   (void *) getFriction, (void **) &old_getFriction);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x2174DA4),
                   (void *) getAntiRoll, (void **) &old_getAntiRoll);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x217506C),
                   (void *) getBrake, (void **) &old_getBrake);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x2174E0C),
                   (void *) getSteer, (void **) &old_getSteer);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x2174FA4),
                   (void *) getRevSpeed, (void **) &old_getRevSpeed);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x2206C14),
                   (void *) getMotorTorque, (void **) &old_getMotorTorque);

    // WARFARE HOOKS
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x217C4E4),
                   (void *) SetNetPos, (void **) &old_SetNetPos);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x218A9B8),
                   (void *) GetLocalPlayer, (void **) &old_GetLocalPlayer);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x218B510),
                   (void *) DriverNetFixed, (void **) &old_DriverNetFixed);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x222AD0C),
                   (void *) OnKickPlayer, (void **) &old_OnKickPlayer);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x2204738),
                   (void *) FinishRace, (void **) &old_FinishRace);
    MSHookFunction((void *) getAbsoluteAddress(targetLibName, 0x2248D44),
                   (void *) LinkCtor, (void **) &old_LinkCtor);

    LOGI(OBFUSCATE("Done - All hooks installed"));
#endif

    return NULL;
}

//JNI calls
extern "C" {

JNIEXPORT jobjectArray
JNICALL
Java_uk_lgl_modmenu_FloatingModMenuService_getFeatureList(JNIEnv *env, jobject context) {
    jobjectArray ret;

    MakeToast(env, context, OBFUSCATE("Modded by Maino Games"), Toast::LENGTH_LONG);

    const char *features[] = {
            OBFUSCATE("Category_EKONOMI"),
            OBFUSCATE("Toggle_Tarif Besar"),
            OBFUSCATE("Toggle_Gajian Besar"),
            OBFUSCATE("Toggle_Bus Penuh 99"),
            OBFUSCATE("Toggle_Aksesori Gratis"),
            OBFUSCATE("Toggle_Uang x100 Masuk"),
            OBFUSCATE("Toggle_Uang 999 Juta"),
            OBFUSCATE("Toggle_SAVE Permanen"),
            OBFUSCATE("Toggle_Fake Uang Miliar"),
            OBFUSCATE("Toggle_SERVER +1 Miliar"),
            OBFUSCATE("Toggle_Gaji x100 Server"),
            OBFUSCATE("Toggle_Set Cash Core"),
            OBFUSCATE("Toggle_Semua Transaksi Gratis"),
            OBFUSCATE("Toggle_Dana Selalu Cukup"),
            OBFUSCATE("Toggle_Unlock All Bus"),
            OBFUSCATE("Toggle_Gold Saat Login"),
            OBFUSCATE("Toggle_Unlock All Item"),

            OBFUSCATE("Category_BENSIN & LAYANAN"),
            OBFUSCATE("Toggle_Fuel Bypass"),
            OBFUSCATE("Toggle_Bensin Harga 0"),
            OBFUSCATE("Toggle_Free Tol"),
            OBFUSCATE("Toggle_Free Derek"),
            OBFUSCATE("Toggle_Repair Gratis"),
            OBFUSCATE("Toggle_Stamina Unlimited"),
            OBFUSCATE("Toggle_Side Job Instan"),

            OBFUSCATE("Category_FISIKA BUS"),
            OBFUSCATE("Toggle_Light Bus"),
            OBFUSCATE("Toggle_Ban Anti-Slip"),
            OBFUSCATE("Toggle_Anti Terbalik"),
            OBFUSCATE("Toggle_Rem Instan"),
            OBFUSCATE("Toggle_Kemudi 89 Derajat"),
            OBFUSCATE("Toggle_Mundur Ngebut"),
            OBFUSCATE("Toggle_Motor Torque"),
            OBFUSCATE("Toggle_GOD MODE"),
            OBFUSCATE("Toggle_Instant Engine"),
            OBFUSCATE("Toggle_No Slip Physics"),

            OBFUSCATE("Category_MULTIPLAYER"),
            OBFUSCATE("Toggle_Free Masuk MP"),
            OBFUSCATE("Toggle_Bobol Room Password"),
            OBFUSCATE("Toggle_MP Unlimited Time"),
            OBFUSCATE("Toggle_Mod Mode Rahasia"),
            OBFUSCATE("Toggle_Keluar Room Instan"),
            OBFUSCATE("Toggle_Skip Tunggu Player"),
            OBFUSCATE("Toggle_Bypass Maintenance"),

            OBFUSCATE("Category_PROTEKSI"),
            OBFUSCATE("Toggle_Anti Report"),
            OBFUSCATE("Toggle_Anti Kick"),
            OBFUSCATE("Toggle_Anti Shutdown"),
            OBFUSCATE("Toggle_Anti Report Livery"),

            OBFUSCATE("Category_WARFARE MUSUH"),
            OBFUSCATE("Toggle_Lempar Bus ke Langit"),
            OBFUSCATE("Toggle_Lempar Driver Musuh"),
            OBFUSCATE("Toggle_Freeze Driver Musuh"),

            OBFUSCATE("Category_RACE & RUTE"),
            OBFUSCATE("Toggle_Instant Win Race"),
            OBFUSCATE("Toggle_Rute Valid Apapun"),
            OBFUSCATE("Toggle_Jarak 999rb Gajian"),
            OBFUSCATE("Toggle_Jarak Dekat Instan"),
            OBFUSCATE("Toggle_Bypass Cek Map"),
            OBFUSCATE("Toggle_No Popup Download"),
            OBFUSCATE("Toggle_Kamera Map Bebas"),

            OBFUSCATE("Category_UTILITAS"),
            OBFUSCATE("Toggle_Skip All Ads"),
            OBFUSCATE("Toggle_Skip Reward Ads"),
            OBFUSCATE("Toggle_Horn Custom Gratis"),
            OBFUSCATE("Toggle_Livery Custom Gratis"),
            OBFUSCATE("Toggle_Unlock Livery All"),
    };

    int Total_Feature = (sizeof features / sizeof features[0]);
    ret = (jobjectArray)
            env->NewObjectArray(Total_Feature, env->FindClass(OBFUSCATE("java/lang/String")),
                                env->NewStringUTF(""));

    for (int i = 0; i < Total_Feature; i++)
        env->SetObjectArrayElement(ret, i, env->NewStringUTF(features[i]));

    pthread_t ptid;
    pthread_create(&ptid, NULL, antiLeech, NULL);

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

    LOGD(OBFUSCATE("Feature name: %d - %s | Value: = %d | Bool: = %d | Text: = %s"), featNum,
         featureName, value, boolean, TextInput);

    switch (featNum) {
        // ===== EKONOMI (0-16) =====
        case 0: hexPatches.TarifBesar.Modify(); if(!boolean) hexPatches.TarifBesar.Restore(); break;
        case 1: hexPatches.GajianBesar.Modify(); if(!boolean) hexPatches.GajianBesar.Restore(); break;
        case 2: hexPatches.BusPenuh.Modify(); if(!boolean) hexPatches.BusPenuh.Restore(); break;
        case 3: hexPatches.AksesoriGratis.Modify(); if(!boolean) hexPatches.AksesoriGratis.Restore(); break;
        case 4: bUangX100 = boolean; break;
        case 5: bUang999jt = boolean; break;
        case 6: bSavePerm = boolean; break;
        case 7: bFakeMoney = boolean; break;
        case 8: bServerAddRp = boolean; break;
        case 9: bGajiX100 = boolean; break;
        case 10: bSetCashCore = boolean; break;
        case 11: hexPatches.TransaksiGratis.Modify(); if(!boolean) hexPatches.TransaksiGratis.Restore(); break;
        case 12: hexPatches.DanaCukup.Modify(); if(!boolean) hexPatches.DanaCukup.Restore(); break;
        case 13: hexPatches.UnlockAllBus.Modify(); if(!boolean) hexPatches.UnlockAllBus.Restore(); break;
        case 14: bGoldLoad = boolean; break;
        case 15: bUnlockItem = boolean; break;

        // ===== BENSIN (16-23) =====
        case 16: hexPatches.FuelBypass.Modify(); if(!boolean) hexPatches.FuelBypass.Restore(); break;
        case 17: hexPatches.BensinHarga0.Modify(); if(!boolean) hexPatches.BensinHarga0.Restore(); break;
        case 18: hexPatches.FreeTol.Modify(); if(!boolean) hexPatches.FreeTol.Restore(); break;
        case 19: hexPatches.FreeDerek.Modify(); if(!boolean) hexPatches.FreeDerek.Restore(); break;
        case 20: hexPatches.RepairGratis.Modify(); if(!boolean) hexPatches.RepairGratis.Restore(); break;
        case 21: bStaminaHook = boolean; break;
        case 22: hexPatches.SideJobInstan.Modify(); if(!boolean) hexPatches.SideJobInstan.Restore(); break;

        // ===== FISIKA (23-33) =====
        case 23: bLightBus = boolean; break;
        case 24: bBanLem = boolean; break;
        case 25: bAntiRoll = boolean; break;
        case 26: bRemInstan = boolean; break;
        case 27: bKemudi89 = boolean; break;
        case 28: bMundurCepat = boolean; break;
        case 29: bMotorTorque = boolean; break;
        case 30: hexPatches.GodMode.Modify(); if(!boolean) hexPatches.GodMode.Restore(); break;
        case 31: hexPatches.InstantEngine.Modify(); if(!boolean) hexPatches.InstantEngine.Restore(); break;
        case 32: hexPatches.NoSlip.Modify(); if(!boolean) hexPatches.NoSlip.Restore(); break;

        // ===== MULTIPLAYER (33-40) =====
        case 33: hexPatches.FreeMP.Modify(); if(!boolean) hexPatches.FreeMP.Restore(); break;
        case 34: hexPatches.BobolRoom.Modify(); if(!boolean) hexPatches.BobolRoom.Restore(); break;
        case 35: hexPatches.MPUnlimited.Modify(); if(!boolean) hexPatches.MPUnlimited.Restore(); break;
        case 36: hexPatches.ModMode.Modify(); if(!boolean) hexPatches.ModMode.Restore(); break;
        case 37: hexPatches.KeluarInstan.Modify(); if(!boolean) hexPatches.KeluarInstan.Restore(); break;
        case 38: hexPatches.SkipTunggu.Modify(); if(!boolean) hexPatches.SkipTunggu.Restore(); break;
        case 39: hexPatches.BypassMaint.Modify(); if(!boolean) hexPatches.BypassMaint.Restore(); break;

        // ===== PROTEKSI (40-43) =====
        case 40: hexPatches.AntiReport.Modify(); if(!boolean) hexPatches.AntiReport.Restore(); break;
        case 41: bAntiKick = boolean; break;
        case 42: hexPatches.AntiShutdown.Modify(); if(!boolean) hexPatches.AntiShutdown.Restore(); break;
        case 43: hexPatches.AntiReportLivery.Modify(); if(!boolean) hexPatches.AntiReportLivery.Restore(); break;

        // ===== WARFARE (44-46) =====
        case 44: bLemparBus = boolean; break;
        case 45: bLemparDriver = boolean; break;
        case 46: bFreezeDriver = boolean; break;

        // ===== RACE & RUTE (47-54) =====
        case 47: bInstantWin = boolean; break;
        case 48: hexPatches.RuteValid.Modify(); if(!boolean) hexPatches.RuteValid.Restore(); break;
        case 49: hexPatches.Jarak999.Modify(); if(!boolean) hexPatches.Jarak999.Restore(); break;
        case 50: bJarakDekat = boolean; break;
        case 51: hexPatches.BypassMap.Modify(); if(!boolean) hexPatches.BypassMap.Restore(); break;
        case 52: hexPatches.NoPopupDL.Modify(); if(!boolean) hexPatches.NoPopupDL.Restore(); break;
        case 53: hexPatches.KameraBebas.Modify(); if(!boolean) hexPatches.KameraBebas.Restore(); break;

        // ===== UTILITAS (54-58) =====
        case 54: hexPatches.SkipAds.Modify(); if(!boolean) hexPatches.SkipAds.Restore(); break;
        case 55: hexPatches.SkipRewardAds.Modify(); if(!boolean) hexPatches.SkipRewardAds.Restore(); break;
        case 56: hexPatches.HornGratis.Modify(); if(!boolean) hexPatches.HornGratis.Restore(); break;
        case 57: hexPatches.LiveryGratis.Modify(); if(!boolean) hexPatches.LiveryGratis.Restore(); break;
        case 58: hexPatches.UnlockLivery.Modify(); if(!boolean) hexPatches.UnlockLivery.Restore(); break;
    }
}
}

__attribute__((constructor))
void lib_main() {
    pthread_t ptid;
    pthread_create(&ptid, NULL, hack_thread, NULL);
}
