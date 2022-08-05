package com.applovin.applovin_max;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;

class FlutterAdListener implements MaxAdListener {
    protected final int adId;
    @NonNull protected final AdInstanceManager manager;

    FlutterAdListener(int adId, @NonNull AdInstanceManager manager) {
        this.adId = adId;
        this.manager = manager;
    }

    @Override
    public void onAdLoaded(MaxAd ad) {
        manager.onAdLoaded(adId);
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

    FlutterBannerAdListener(int adId, @NonNull AdInstanceManager manager) {
        super(adId, manager);
    }

    @Override
    public void onAdExpanded(MaxAd ad) {
        manager.onAdExpanded(adId);
    }

    @Override
    public void onAdCollapsed(MaxAd ad) {
        manager.onAdCollapsed(adId);
    }
}
