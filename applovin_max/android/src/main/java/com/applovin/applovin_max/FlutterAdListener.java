package com.applovin.applovin_max;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;

import java.lang.ref.WeakReference;

/** Callback type to notify when an ad successfully loads. */
interface FlutterAdLoadCallback {
    void onAdLoaded(MaxAd ad);
}

class FlutterAdListener implements MaxAdListener {
    protected final int adId;
    @NonNull protected final AdInstanceManager manager;

    FlutterAdListener(int adId, @NonNull AdInstanceManager manager) {
        this.adId = adId;
        this.manager = manager;
    }

    @Override
    public void onAdLoaded(MaxAd ad) {
        FlutterAd.FlutterResponseInfo responseInfo = new FlutterAd.FlutterResponseInfo(ad);
        manager.onAdLoaded(adId, responseInfo);
    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        manager.onAdLoadFailed(adId, new FlutterAd.FlutterAdError(error));
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {
        manager.onAdDisplayed(adId);
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        manager.onAdDisplayFailed(adId, new FlutterAd.FlutterAdError(error));
    }

    @Override
    public void onAdHidden(MaxAd ad) {
        manager.onAdHidden(adId);
    }

    @Override
    public void onAdClicked(MaxAd ad) {
        manager.onAdClicked(adId);
    }
}

/**
 * Ad listener for banner ads.
 */
class FlutterBannerAdListener extends FlutterAdListener implements MaxAdViewAdListener {

    @NonNull final WeakReference<FlutterAdLoadCallback> adLoadCallbackWeakReference;

    FlutterBannerAdListener(
            int adId, @NonNull AdInstanceManager manager, FlutterAdLoadCallback adLoadCallback) {
        super(adId, manager);
        adLoadCallbackWeakReference = new WeakReference<>(adLoadCallback);
    }

    @Override
    public void onAdLoaded(MaxAd ad) {
        if (adLoadCallbackWeakReference.get() != null) {
            adLoadCallbackWeakReference.get().onAdLoaded(ad);
        }
    }

    /**
     *  DO NOT USE - THIS IS RESERVED FOR FULLSCREEN ADS ONLY
     *  AND WILL BE REMOVED IN A FUTURE SDK RELEASE
     */
    @Override
    public void onAdDisplayed(MaxAd ad) { }

    /**
     *  DO NOT USE - THIS IS RESERVED FOR FULLSCREEN ADS ONLY
     *  AND WILL BE REMOVED IN A FUTURE SDK RELEASE
     */
    @Override
    public void onAdHidden(MaxAd ad) { }

    @Override
    public void onAdExpanded(MaxAd ad) {
        manager.onAdExpanded(adId);
    }

    @Override
    public void onAdCollapsed(MaxAd ad) {
        manager.onAdCollapsed(adId);
    }
}
