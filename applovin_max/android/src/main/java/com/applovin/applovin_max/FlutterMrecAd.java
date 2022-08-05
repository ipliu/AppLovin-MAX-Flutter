package com.applovin.applovin_max;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.ads.MaxAdView;

import io.flutter.plugin.platform.PlatformView;
import io.flutter.util.Preconditions;

/** A wrapper for {@link MaxAdView}. */
class FlutterMrecAd extends FlutterAd implements FlutterAdLoadCallback {

    private static final String TAG = "FlutterMrecAd";

    @NonNull private final AdInstanceManager manager;
    @NonNull private final String adUnitId;
    @Nullable private final String placement;
    @Nullable private final String customData;
    @Nullable private MaxAdView adView;
    private boolean isLoaded = false;
    private boolean isDisplayed = false;

    /** Constructs the FlutterMrecAd. */
    public FlutterMrecAd(
            int adId,
            @NonNull AdInstanceManager manager,
            @NonNull String adUnitId,
            @Nullable final String placement,
            @Nullable final String customData) {
        super(adId);
        Preconditions.checkNotNull(manager);
        Preconditions.checkNotNull(adUnitId);
        this.manager = manager;
        this.adUnitId = adUnitId;
        this.placement = placement;
        this.customData = customData;
    }

    @Override
    public void onAdLoaded(MaxAd ad) {
        if (!isLoaded) {
            isLoaded = true;
            FlutterAd.FlutterResponseInfo responseInfo = new FlutterAd.FlutterResponseInfo(ad);
            manager.onAdLoaded(adId, responseInfo);
        }
    }

    @Override
    void load() {
        if (manager.getActivity() == null) {
            Log.e(TAG, "Tried to show mrec ad before activity was bound to the plugin.");
            return;
        }

        adView = new MaxAdView(
                adUnitId,
                MaxAdFormat.MREC,
                AppLovinMAX.getInstance().getSdk(),
                manager.getActivity());
        adView.setListener(new FlutterMrecAdListener(adId, manager, this));

        adView.setPlacement(placement);
        adView.setCustomData(customData);

        adView.loadAd();
    }

    @Nullable
    @Override
    public PlatformView getPlatformView() {
        if (adView == null) {
            return null;
        }
        if (!isDisplayed) {
            isDisplayed = true;
            manager.onAdDisplayed(adId);
        }
        return new FlutterPlatformView(adView);
    }

    @Override
    void dispose() {
        if (adView != null) {
            adView.destroy();
            adView.setListener(null);
            adView = null;
        }
    }

    @Nullable
    FlutterAdSize getAdSize() {
        if (adView == null || adView.getAdFormat().getSize() == null) {
            return null;
        }
        return new FlutterAdSize(adView.getAdFormat().getSize());
    }
}
