package com.applovin.applovin_max;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;

/**
 * <p>When an Ad is loaded from Dart, an equivalent ad object is created and maintained here to
 * provide access until the ad is disposed.
 */
class AdInstanceManager {
    @Nullable private Activity activity;

    @NonNull private final Map<Integer, FlutterAd> ads;
    @NonNull private final MethodChannel channel;

    /**
     * Initializes the ad instance manager. We only need a method channel to start loading ads, but an
     * activity must be present in order to attach any ads to the view hierarchy.
     */
    AdInstanceManager(@NonNull MethodChannel channel) {
        this.channel = channel;
        this.ads = new HashMap<>();
    }

    void setActivity(@Nullable Activity activity) {
        this.activity = activity;
    }

    @Nullable
    Activity getActivity() {
        return activity;
    }

    @Nullable
    FlutterAd adForId(int id) {
        return ads.get(id);
    }

    @Nullable
    Integer adIdFor(@NonNull FlutterAd ad) {
        for (Integer adId : ads.keySet()) {
            if (ads.get(adId) == ad) {
                return adId;
            }
        }
        return null;
    }

    void trackAd(@NonNull FlutterAd ad, int adId) {
        if (ads.get(adId) != null) {
            throw new IllegalArgumentException(
                    String.format("Ad for following adId already exists: %d", adId));
        }
        ads.put(adId, ad);
    }

    void disposeAd(int adId) {
        if (!ads.containsKey(adId)) {
            return;
        }
        FlutterAd ad = ads.get(adId);
        if (ad != null) {
            ad.dispose();
        }
        ads.remove(adId);
    }

    void disposeAllAds() {
        for (Map.Entry<Integer, FlutterAd> entry : ads.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().dispose();
            }
        }
        ads.clear();
    }

    void onAdLoaded(int adId, FlutterAd.FlutterResponseInfo responseInfo) {
        Map<Object, Object> arguments = new HashMap<>();
        arguments.put("adId", adId);
        arguments.put("eventName", "onAdLoaded");
        arguments.put("responseInfo", responseInfo);
        invokeOnAdEvent(arguments);
    }

    void onAdLoadFailed(int adId, @NonNull FlutterAd.FlutterAdError error) {
        Map<Object, Object> arguments = new HashMap<>();
        arguments.put("adId", adId);
        arguments.put("eventName", "onAdLoadFailed");
        arguments.put("adError", error);
        invokeOnAdEvent(arguments);
    }

    void onAdDisplayed(int id) {
        Map<Object, Object> arguments = new HashMap<>();
        arguments.put("adId", id);
        arguments.put("eventName", "onAdDisplayed");
        invokeOnAdEvent(arguments);
    }

    void onAdDisplayFailed(int adId, @NonNull FlutterAd.FlutterAdError error) {
        Map<Object, Object> arguments = new HashMap<>();
        arguments.put("adId", adId);
        arguments.put("eventName", "onAdDisplayFailed");
        arguments.put("adError", error);
        invokeOnAdEvent(arguments);
    }

    void onAdHidden(int adId) {
        Map<Object, Object> arguments = new HashMap<>();
        arguments.put("adId", adId);
        arguments.put("eventName", "onAdHidden");
        invokeOnAdEvent(arguments);
    }

    void onAdClicked(int id) {
        Map<Object, Object> arguments = new HashMap<>();
        arguments.put("adId", id);
        arguments.put("eventName", "onAdClicked");
        invokeOnAdEvent(arguments);
    }

    void onAdExpanded(int id) {
        Map<Object, Object> arguments = new HashMap<>();
        arguments.put("adId", id);
        arguments.put("eventName", "onAdExpanded");
        invokeOnAdEvent(arguments);
    }

    void onAdCollapsed(int id) {
        Map<Object, Object> arguments = new HashMap<>();
        arguments.put("adId", id);
        arguments.put("eventName", "onAdCollapsed");
        invokeOnAdEvent(arguments);
    }

    /** Invoke the method channel using the UI thread. Otherwise the message gets silently dropped. */
    private void invokeOnAdEvent(final Map<Object, Object> arguments) {
        new Handler(Looper.getMainLooper())
                .post(() -> channel.invokeMethod("onAdEvent", arguments));
    }
}
