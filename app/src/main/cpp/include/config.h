#pragma once

#include <atomic>

// Runtime configuration structure with atomic fields for thread-safe access
struct RuntimeConfig {
    std::atomic<bool> feature_1_enabled{false};
    std::atomic<bool> feature_2_enabled{false};
    std::atomic<float> param_a{1.0f};
    std::atomic<int> param_b{0};
    std::atomic<bool> telemetry_enabled{true};
    std::atomic<bool> debug_rendering{false};

    void Reset() {
        feature_1_enabled.store(false);
        feature_2_enabled.store(false);
        param_a.store(1.0f);
        param_b.store(0);
        telemetry_enabled.store(true);
        debug_rendering.store(false);
    }
};

extern RuntimeConfig g_config;
