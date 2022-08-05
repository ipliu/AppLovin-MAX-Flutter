package com.applovin.applovin_max;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;

import java.util.Objects;

import io.flutter.plugin.platform.PlatformView;

abstract class FlutterAd {

    protected final int adId;

    FlutterAd(int adId) {
        this.adId = adId;
    }

    /** Wrapper for {@link MaxAd}. */
    static class FlutterResponseInfo {
        /**
         * The ad network for which this ad was loaded from.
         */
        @NonNull private final String networkName;

        /**
         * The ad network placement for which this ad was loaded from.
         */
        @NonNull private final String networkPlacement;

        /**
         * The ad placement which was set for this ad.
         */
        @Nullable private final String placement;

        /**
         * The creative id tied to the ad, if any. You can report creative issues to the
         * corresponding ad network using this id.
         *
         * It may not be available until {@code MaxAdListener#onAdDisplayed()} is called.
         *
         * The ad's creative ID, if available.
         */
        @Nullable private final String creativeId;

        /**
         * The revenue amount tied to the ad.
         *
         * The ad's revenue amount, or 0 if it does not exist.
         */
        @Nullable private final String revenue;

        /**
         * The DSP network that provided the loaded ad when the ad is served through
         * AppLovin Exchange.
         */
        @Nullable private final String dspName;

        FlutterResponseInfo(@NonNull MaxAd ad) {
            this.networkName = ad.getNetworkName();
            this.networkPlacement = ad.getNetworkPlacement();
            this.placement = ad.getPlacement();
            this.creativeId = ad.getCreativeId();
            this.revenue = Double.toString(ad.getRevenue());
            this.dspName = ad.getDspName();
        }

        FlutterResponseInfo(@NonNull String networkName,
                            @NonNull String networkPlacement,
                            @Nullable String placement,
                            @Nullable String creativeId,
                            @Nullable String revenue,
                            @Nullable String dspName) {
            this.networkName = networkName;
            this.networkPlacement = networkPlacement;
            this.placement = placement;
            this.creativeId = creativeId;
            this.revenue = revenue;
            this.dspName = dspName;
        }

        @NonNull
        String getNetworkName() {
            return networkName;
        }

        @NonNull
        String getNetworkPlacement() {
            return networkPlacement;
        }

        @Nullable
        String getPlacement() {
            return placement;
        }

        @Nullable
        String getCreativeId() {
            return creativeId;
        }

        @Nullable
        String getRevenue() {
            return revenue;
        }

        @Nullable
        String getDspName() {
            return dspName;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == this) {
                return true;
            } else if (!(obj instanceof FlutterResponseInfo)) {
                return false;
            }

            FlutterResponseInfo that = (FlutterResponseInfo) obj;
            return Objects.equals(networkName, that.networkName)
                    && Objects.equals(networkPlacement, that.networkPlacement)
                    && Objects.equals(placement, that.placement)
                    && Objects.equals(creativeId, that.creativeId)
                    && Objects.equals(revenue, that.revenue)
                    && Objects.equals(dspName, that.dspName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    networkName, networkPlacement, placement, creativeId, revenue, dspName);
        }
    }

    /** Wrapper for {@link MaxError}. */
    static class FlutterAdError {
        final int code;
        @NonNull final String message;
        final int mediatedCode;
        @NonNull final String mediatedMessage;

        FlutterAdError(@NonNull MaxError error) {
            code = error.getCode();
            message = error.getMessage();
            mediatedCode = error.getMediatedNetworkErrorCode();
            mediatedMessage = error.getMediatedNetworkErrorMessage();
        }

        FlutterAdError(
                int code,
                @NonNull String message,
                int mediatedCode,
                @NonNull String mediatedMessage) {
            this.code = code;
            this.message = message;
            this.mediatedCode = mediatedCode;
            this.mediatedMessage = mediatedMessage;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (!(object instanceof FlutterAdError)) {
                return false;
            }

            final FlutterAdError that = (FlutterAdError) object;

            if (code != that.code) {
                return false;
            } else if (!message.equals(that.message)) {
                return false;
            } else if (mediatedCode != that.mediatedCode) {
                return false;
            }
            return mediatedMessage.equals(that.mediatedMessage);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code, message, mediatedCode, mediatedMessage);
        }
    }

    abstract void load();

    /**
     * Gets the PlatformView for the ad. Default behavior is to return null. Should be overridden by
     * ads with platform views, such as banner and native ads.
     */
    @Nullable
    PlatformView getPlatformView() {
        return null;
    }

    /**
     * Invoked when dispose() is called on the corresponding Flutter ad object. This perform any
     * necessary cleanup.
     */
    abstract void dispose();
}
