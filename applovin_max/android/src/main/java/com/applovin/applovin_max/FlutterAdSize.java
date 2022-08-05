package com.applovin.applovin_max;

import androidx.annotation.NonNull;

import com.applovin.sdk.AppLovinSdkUtils;

public class FlutterAdSize {
    @NonNull final AppLovinSdkUtils.Size size;
    final int width;
    final int height;

    FlutterAdSize(int width, int height) {
        this(new AppLovinSdkUtils.Size(width, height));
    }

    FlutterAdSize(@NonNull AppLovinSdkUtils.Size size) {
        this.size = size;
        this.width = size.getWidth();
        this.height = size.getHeight();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof FlutterAdSize)) {
            return false;
        }

        final FlutterAdSize that = (FlutterAdSize) o;

        if (width != that.width) {
            return false;
        }
        return height == that.height;
    }

    @Override
    public int hashCode() {
        return size.hashCode();
    }

    public AppLovinSdkUtils.Size getAdSize() {
        return size;
    }
}
