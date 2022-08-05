import 'package:applovin_max/src/ad_classes.dart';
import 'package:flutter/foundation.dart';

import 'ad_containers.dart';

/// The callback type to handle an event occurring for an [Ad].
typedef AdEventCallback = void Function(Ad ad);

/// The callback type to handle an error loading an [Ad].
typedef AdLoadErrorCallback = void Function(Ad ad, AdError error);

/// Shared event callbacks used in Native and Banner ads.
abstract class AdWithViewListener {
  /// Default constructor for [AdWithViewListener], meant to be used by subclasses.
  @protected
  const AdWithViewListener({
    this.onAdLoaded,
    this.onAdLoadFailed,
    this.onAdDisplayed,
    this.onAdDisplayFailed,
    this.onAdHidden,
    this.onAdClicked,
  });

  /// Called when an ad is successfully received.
  final AdEventCallback? onAdLoaded;

  /// Called when an ad request failed.
  final AdLoadErrorCallback? onAdLoadFailed;

  /// Called when an impression occurs on the ad.
  final AdEventCallback? onAdDisplayed;

  /// Called when an ad display failed.
  final AdLoadErrorCallback? onAdDisplayFailed;

  /// Called when the ad is hidden.
  final AdEventCallback? onAdHidden;

  /// Called when the ad is clicked.
  final AdEventCallback? onAdClicked;
}

/// A listener for receiving notifications for the lifecycle of a [BannerAd].
class BannerAdListener extends AdWithViewListener {
  /// Called when the ad has expanded full screen.
  final AdEventCallback? onAdExpanded;

  /// Called when the ad has collapsed back to its original size.
  final AdEventCallback? onAdCollapsed;

  /// Constructs a [BannerAdListener] that notifies for the provided event callbacks.
  ///
  /// Typically you will override [onAdLoaded] and [onAdLoadFailed]:
  /// ```dart
  /// BannerAdListener(
  ///   onAdLoaded: (ad) {
  ///     // Ad successfully loaded - display an AdWidget with the banner ad.
  ///   },
  ///   onAdLoadFailed: (ad, error) {
  ///     // Ad failed to load - log the error and dispose the ad.
  ///   },
  ///   ...
  /// )
  /// ```
  const BannerAdListener({
    AdEventCallback? onAdLoaded,
    AdLoadErrorCallback? onAdLoadFailed,
    AdEventCallback? onAdDisplayed,
    AdLoadErrorCallback? onAdDisplayFailed,
    AdEventCallback? onAdHidden,
    AdEventCallback? onAdClicked,
    this.onAdExpanded,
    this.onAdCollapsed,
  }) : super(
    onAdLoaded: onAdLoaded,
    onAdLoadFailed: onAdLoadFailed,
    onAdDisplayed: onAdDisplayed,
    onAdDisplayFailed: onAdDisplayFailed,
    onAdHidden: onAdHidden,
    onAdClicked: onAdClicked,
  );
}

/// A listener for receiving notifications for the lifecycle of a [MrecAd].
class MrecAdListener extends AdWithViewListener {
  /// Called when the ad has expanded full screen.
  final AdEventCallback? onAdExpanded;

  /// Called when the ad has collapsed back to its original size.
  final AdEventCallback? onAdCollapsed;

  /// Constructs a [MrecAdListener] that notifies for the provided event callbacks.
  ///
  /// Typically you will override [onAdLoaded] and [onAdLoadFailed]:
  /// ```dart
  /// MrecAdListener(
  ///   onAdLoaded: (ad) {
  ///     // Ad successfully loaded - display an AdWidget with the mrec ad.
  ///   },
  ///   onAdLoadFailed: (ad, error) {
  ///     // Ad failed to load - log the error and dispose the ad.
  ///   },
  ///   ...
  /// )
  /// ```
  const MrecAdListener({
    AdEventCallback? onAdLoaded,
    AdLoadErrorCallback? onAdLoadFailed,
    AdEventCallback? onAdDisplayed,
    AdLoadErrorCallback? onAdDisplayFailed,
    AdEventCallback? onAdHidden,
    AdEventCallback? onAdClicked,
    this.onAdExpanded,
    this.onAdCollapsed,
  }) : super(
    onAdLoaded: onAdLoaded,
    onAdLoadFailed: onAdLoadFailed,
    onAdDisplayed: onAdDisplayed,
    onAdDisplayFailed: onAdDisplayFailed,
    onAdHidden: onAdHidden,
    onAdClicked: onAdClicked,
  );
}

/// Base Ad Listener
abstract class AdListener {
  final Function(MaxAd ad) onAdLoadedCallback;
  final Function(String adUnitId, MaxError error) onAdLoadFailedCallback;
  final Function(MaxAd ad) onAdClickedCallback;

  const AdListener({
    required this.onAdLoadedCallback,
    required this.onAdLoadFailedCallback,
    required this.onAdClickedCallback,
  });
}

// Fullscreen Ad Listener
abstract class FullscreenAdListener extends AdListener {
  final Function(MaxAd ad) onAdDisplayedCallback;
  final Function(MaxAd ad, MaxError error) onAdDisplayFailedCallback;
  final Function(MaxAd ad) onAdHiddenCallback;

  const FullscreenAdListener({
    required Function(MaxAd ad) onAdLoadedCallback,
    required Function(String adUnitId, MaxError error) onAdLoadFailedCallback,
    required Function(MaxAd ad) onAdClickedCallback,
    required this.onAdDisplayedCallback,
    required this.onAdDisplayFailedCallback,
    required this.onAdHiddenCallback,
  }) : super(onAdLoadedCallback: onAdLoadedCallback, onAdLoadFailedCallback: onAdLoadFailedCallback, onAdClickedCallback: onAdClickedCallback);
}

// AdView Ad (Banner / MREC) Listener
class AdViewAdListener extends AdListener {
  final Function(MaxAd ad) onAdExpandedCallback;
  final Function(MaxAd ad) onAdCollapsedCallback;

  const AdViewAdListener({
    required Function(MaxAd ad) onAdLoadedCallback,
    required Function(String adUnitId, MaxError error) onAdLoadFailedCallback,
    required Function(MaxAd ad) onAdClickedCallback,
    required this.onAdExpandedCallback,
    required this.onAdCollapsedCallback,
  }) : super(onAdLoadedCallback: onAdLoadedCallback, onAdLoadFailedCallback: onAdLoadFailedCallback, onAdClickedCallback: onAdClickedCallback);
}

// Interstitial Ad Listener
class InterstitialListener extends FullscreenAdListener {
  const InterstitialListener({
    required Function(MaxAd ad) onAdLoadedCallback,
    required Function(String adUnitId, MaxError error) onAdLoadFailedCallback,
    required Function(MaxAd ad) onAdDisplayedCallback,
    required Function(MaxAd ad, MaxError error) onAdDisplayFailedCallback,
    required Function(MaxAd ad) onAdClickedCallback,
    required Function(MaxAd ad) onAdHiddenCallback,
  }) : super(
          onAdLoadedCallback: onAdLoadedCallback,
          onAdLoadFailedCallback: onAdLoadFailedCallback,
          onAdDisplayedCallback: onAdDisplayedCallback,
          onAdDisplayFailedCallback: onAdDisplayFailedCallback,
          onAdClickedCallback: onAdClickedCallback,
          onAdHiddenCallback: onAdHiddenCallback,
        );
}

// Rewarded Ad Listener
class RewardedAdListener extends FullscreenAdListener {
  final Function(MaxAd ad, MaxReward reward) onAdReceivedRewardCallback;

  const RewardedAdListener({
    required Function(MaxAd ad) onAdLoadedCallback,
    required Function(String adUnitId, MaxError error) onAdLoadFailedCallback,
    required Function(MaxAd ad) onAdDisplayedCallback,
    required Function(MaxAd ad, MaxError error) onAdDisplayFailedCallback,
    required Function(MaxAd ad) onAdClickedCallback,
    required Function(MaxAd ad) onAdHiddenCallback,
    required this.onAdReceivedRewardCallback,
  }) : super(
          onAdLoadedCallback: onAdLoadedCallback,
          onAdLoadFailedCallback: onAdLoadFailedCallback,
          onAdDisplayedCallback: onAdDisplayedCallback,
          onAdDisplayFailedCallback: onAdDisplayFailedCallback,
          onAdClickedCallback: onAdClickedCallback,
          onAdHiddenCallback: onAdHiddenCallback,
        );
}
