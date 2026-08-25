package com.panda.monitor;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

/**
 * Floating window debug overlay service with drag-and-drop capability.
 * Communicates with NativeBridge to toggle runtime engine features.
 */
public class OverlayService extends Service {

    private static final String CHANNEL_ID = "OverlayServiceChannel";
    private static final int NOTIFICATION_ID = 101;

    private WindowManager windowManager;
    private View overlayView;
    private WindowManager.LayoutParams params;

    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private boolean isExpanded = true;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, buildNotification());
        initOverlayWindow();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Runtime Debug Overlay Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }

    private Notification buildNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Engine Debug Overlay")
                .setContentText("Floating configuration panel active")
                .setSmallIcon(android.R.drawable.ic_menu_preferences)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initOverlayWindow() {
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) return;

        int layoutType;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            @SuppressWarnings("deprecation")
            int deprecatedType = WindowManager.LayoutParams.TYPE_PHONE;
            layoutType = deprecatedType;
        }

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 40;
        params.y = 120;

        overlayView = buildOverlayLayout();
        windowManager.addView(overlayView, params);
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private View buildOverlayLayout() {
        LinearLayout rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));

        // Background styling
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#E6212121")); // Dark translucent
        bg.setCornerRadius(dpToPx(14));
        bg.setStroke(dpToPx(1), Color.parseColor("#424242"));
        rootLayout.setBackground(bg);

        // Header / Drag Handle Bar
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setGravity(Gravity.CENTER_VERTICAL);
        headerLayout.setPadding(0, 0, 0, dpToPx(8));

        TextView titleView = new TextView(this);
        titleView.setText("⚙ Engine Debug Panel");
        titleView.setTextColor(Color.WHITE);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f
        );
        headerLayout.addView(titleView, titleParams);

        Button toggleCollapseBtn = new Button(this);
        toggleCollapseBtn.setText("—");
        toggleCollapseBtn.setTextColor(Color.WHITE);
        toggleCollapseBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        toggleCollapseBtn.setBackgroundColor(Color.TRANSPARENT);
        headerLayout.addView(toggleCollapseBtn);

        Button closeBtn = new Button(this);
        closeBtn.setText("✕");
        closeBtn.setTextColor(Color.parseColor("#FF5252"));
        closeBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        closeBtn.setBackgroundColor(Color.TRANSPARENT);
        closeBtn.setOnClickListener(v -> stopSelf());
        headerLayout.addView(closeBtn);

        rootLayout.addView(headerLayout);

        // Content Area with toggles
        final LinearLayout contentContainer = new LinearLayout(this);
        contentContainer.setOrientation(LinearLayout.VERTICAL);

        ScrollView scrollView = new ScrollView(this);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                dpToPx(240), dpToPx(220)
        );
        scrollView.setLayoutParams(scrollParams);

        LinearLayout listLayout = new LinearLayout(this);
        listLayout.setOrientation(LinearLayout.VERTICAL);

        String[] features = NativeBridge.safeGetFeatureList();
        for (int i = 0; i < features.length; i++) {
            final int featureIndex = i;
            String featureName = features[i];

            LinearLayout itemRow = new LinearLayout(this);
            itemRow.setOrientation(LinearLayout.HORIZONTAL);
            itemRow.setGravity(Gravity.CENTER_VERTICAL);
            itemRow.setPadding(0, dpToPx(4), 0, dpToPx(4));

            TextView featureLabel = new TextView(this);
            featureLabel.setText(featureName);
            featureLabel.setTextColor(Color.parseColor("#E0E0E0"));
            featureLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f
            );
            itemRow.addView(featureLabel, labelParams);

            @SuppressLint("UseSwitchCompatOrMaterialCode")
            Switch featureSwitch = new Switch(this);
            featureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                NativeBridge.safeToggleFeature(featureIndex, isChecked);
            });
            itemRow.addView(featureSwitch);

            listLayout.addView(itemRow);
        }

        scrollView.addView(listLayout);
        contentContainer.addView(scrollView);
        rootLayout.addView(contentContainer);

        // Collapse / Expand toggle action
        toggleCollapseBtn.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            contentContainer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            toggleCollapseBtn.setText(isExpanded ? "—" : "＋");
        });

        // Drag and drop touch listener on Header Bar
        headerLayout.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = params.x;
                    initialY = params.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);
                    windowManager.updateViewLayout(overlayView, params);
                    return true;

                default:
                    return false;
            }
        });

        return rootLayout;
    }

    private int dpToPx(int dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return Math.round(dp * (metrics.densityDpi / 160f));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayView != null && windowManager != null) {
            windowManager.removeView(overlayView);
            overlayView = null;
        }
    }
}
