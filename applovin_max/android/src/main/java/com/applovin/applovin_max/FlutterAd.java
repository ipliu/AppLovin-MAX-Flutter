package com.applovin.applovin_max;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applovin.mediation.MaxError;

import java.util.Objects;

import io.flutter.plugin.platform.PlatformView;

abstract class FlutterAd {

    protected final int adId;

    FlutterAd(int adId) {
        this.adId = adId;
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
