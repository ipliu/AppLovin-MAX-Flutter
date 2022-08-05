import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

import 'ad_instance_manager.dart';
import 'ad_listeners.dart';

/// Error information about why an ad operation failed.
class AdError {
  /// Creates an [AdError] with the given [code] and [message].
  @protected
  AdError(this.code, this.message, this.mediatedCode, this.mediatedMessage);

  /// The error code for the error.
  final int code;

  /// The error message for the error.
  final String message;

  /// The mediated network's error code for the error.
  final int mediatedCode;

  /// The mediated network's error message for the error.
  final String mediatedMessage;

  @override
  String toString() {
    return '$runtimeType(code: $code, message: $message, '
        'mediatedCode: $mediatedCode, mediatedMessage: $mediatedMessage)';
  }
}

/// Contains information about the loaded ad.
class ResponseInfo {
  /// Constructs a [ResponseInfo] with the [networkName] and [networkPlacement].
  @protected
  const ResponseInfo({
    required this.networkName,
    required this.networkPlacement,
    this.placement,
    this.creativeId,
    this.revenue,
    this.dspName});

  /// The ad network for which this ad was loaded from.
  final String networkName;

  /// The ad network placement for which this ad was loaded from.
  final String networkPlacement;

  /// The ad placement which was set for this ad.
  final String? placement;

  /// The ad's creative ID, if available.
  final String? creativeId;

  /// The ad's revenue amount, or 0 if it does not exist.
  final String? revenue;

  /// The DSP network that provided the loaded ad when the ad is served through
  /// AppLovin Exchange.
  final String? dspName;

  @override
  String toString() {
    return '$runtimeType(networkName: $networkName, '
        'networkPlacement: $networkPlacement, '
        'placement: $placement, '
        'creativeId: $creativeId, '
        'revenue: $revenue, '
        'dspName: $dspName)';
  }
}

/// [AdSize] represents the size of a banner ad.
class AdSize {
  /// Constructs an [AdSize] with the given [width] and [height].
  const AdSize({
    required this.width,
    required this.height,
  });

  /// The vertical span of an ad.
  final int height;

  /// The horizontal span of an ad.
  final int width;

  static const AdSize banner = AdSize(width: 320, height: 50);
  static const AdSize leader = AdSize(width: 728, height: 90);
  static const AdSize mrec = AdSize(width: 300, height: 250);

  @override
  bool operator ==(Object other) {
    return other is AdSize && width == other.width && height == other.height;
  }

  @override
  int get hashCode => width.hashCode * 31 + height.hashCode;

}

/// The base class for all ads.
///
/// A valid [adUnitId] is required.
abstract class Ad {
  /// Default constructor, used by subclasses.
  Ad({required this.adUnitId, this.placementId});

  /// Ad Unit ID
  final String adUnitId;

  /// Ad Placement ID
  final String? placementId;

  /// Frees the plugin resources associated with this ad.
  Future<void> dispose() {
    return instanceManager.disposeAd(this);
  }

  /// Contains information about the loaded ad.
  ResponseInfo? responseInfo;
}

/// Base class for mobile [Ad] that has an in-line view.
///
/// A valid [adUnitId] and [listener] are required.
abstract class AdWithView extends Ad {
  /// Default constructor, used by subclasses.
  AdWithView({
    required String adUnitId, String? placementId, required this.listener})
      : super(adUnitId: adUnitId, placementId: placementId);

  /// The [AdWithViewListener] for the ad.
  final AdWithViewListener listener;

  /// Starts loading this ad.
  ///
  /// Loading callbacks are sent to this [Ad]'s [listener].
  Future<void> load();
}

/// Displays an [Ad] as a Flutter widget.
///
/// This widget takes ads inheriting from [AdWithView]
/// (e.g. [BannerAd]) and allows them to be added to the Flutter
/// widget tree.
///
/// Must call `load()` first before showing the widget. Otherwise, a
/// [PlatformException] will be thrown.
class AdWidget extends StatefulWidget {
  /// Default constructor for [AdWidget].
  ///
  /// [ad] must be loaded before this is added to the widget tree.
  const AdWidget({Key? key, required this.ad}) : super(key: key);

  /// Ad to be displayed as a widget.
  final AdWithView ad;

  @override
  _AdWidgetState createState() => _AdWidgetState();
}

class _AdWidgetState extends State<AdWidget> {
  bool _adIdAlreadyMounted = false;
  bool _adLoadNotCalled = false;

  @override
  void initState() {
    super.initState();
    final int? adId = instanceManager.adIdFor(widget.ad);
    if (adId != null) {
      if (instanceManager.isWidgetAdIdMounted(adId)) {
        _adIdAlreadyMounted = true;
      }
      instanceManager.mountWidgetAdId(adId);
    } else {
      _adLoadNotCalled = true;
    }
  }

  @override
  void dispose() {
    super.dispose();
    final int? adId = instanceManager.adIdFor(widget.ad);
    if (adId != null) {
      instanceManager.unmountWidgetAdId(adId);
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_adIdAlreadyMounted) {
      throw FlutterError.fromParts(<DiagnosticsNode>[
        ErrorSummary('This AdWidget is already in the Widget tree'),
        ErrorHint(
            'If you placed this AdWidget in a list, make sure you create a new instance '
                'in the builder function with a unique ad object.'),
        ErrorHint(
            'Make sure you are not using the same ad object in more than one AdWidget.'),
      ]);
    }
    if (_adLoadNotCalled) {
      throw FlutterError.fromParts(<DiagnosticsNode>[
        ErrorSummary(
            'AdWidget requires Ad.load to be called before AdWidget is inserted into the tree'),
        ErrorHint(
            'Parameter ad is not loaded. Call Ad.load before AdWidget is inserted into the tree.'),
      ]);
    }
    if (defaultTargetPlatform == TargetPlatform.android) {
      AdSize? size;
      if (widget.ad is BannerAd) {
        size = (widget.ad as BannerAd).size;
      }
      return SizedBox(
        width: size?.width.toDouble(),
        height: size?.height.toDouble(),
        child: AndroidView(
          viewType: 'applovin_max/ad_widget',
          layoutDirection: TextDirection.ltr,
          creationParams: instanceManager.adIdFor(widget.ad),
          creationParamsCodec: const StandardMessageCodec(),
        ),
      );
    }

    return UiKitView(
      viewType: 'applovin_max/ad_widget',
      creationParams: instanceManager.adIdFor(widget.ad),
      creationParamsCodec: const StandardMessageCodec(),
    );
  }
}

/// A banner ad.
///
/// This ad can either be overlaid on top of all flutter widgets as a static
/// view or displayed as a typical Flutter widget. To display as a widget,
/// instantiate an [AdWidget] with this as a parameter.
class BannerAd extends AdWithView {
  /// Creates a [BannerAd].
  ///
  /// A valid [adUnitId] and nonnull [listener] is required.
  BannerAd({
    required String adUnitId,
    required this.listener,
    required this.size,
    String? placementId,
  }) : super(adUnitId: adUnitId, placementId: placementId, listener: listener);

  /// Represents the size of a banner ad.
  final AdSize size;

  /// A listener for receiving events in the ad lifecycle.
  @override
  final BannerAdListener listener;

  @override
  Future<void> load() async {
    await instanceManager.loadBannerAd(this);
  }

  /// Returns the AdSize of the associated platform ad object.
  ///
  /// The dimensions of the [AdSize] returned here may differ from [size],
  /// depending on what type of [AdSize] was used.
  /// The future will resolve to null if [load] has not been called yet.
  Future<AdSize?> getPlatformAdSize() async {
    return await instanceManager.getAdSize(this);
  }
}

/// A mrec ad.
///
/// This ad can either be overlaid on top of all flutter widgets as a static
/// view or displayed as a typical Flutter widget. To display as a widget,
/// instantiate an [AdWidget] with this as a parameter.
class MrecAd extends AdWithView {
  /// Creates a [MrecAd].
  ///
  /// A valid [adUnitId] and nonnull [listener] is required.
  MrecAd({
    required String adUnitId,
    required this.listener,
    required this.size,
    String? placementId,
  }) : super(adUnitId: adUnitId, placementId: placementId, listener: listener);

  /// Represents the size of a mrec ad.
  final AdSize size;

  /// A listener for receiving events in the ad lifecycle.
  @override
  final MrecAdListener listener;

  @override
  Future<void> load() async {
    await instanceManager.loadMrecAd(this);
  }

  /// Returns the AdSize of the associated platform ad object.
  ///
  /// The dimensions of the [AdSize] returned here may differ from [size],
  /// depending on what type of [AdSize] was used.
  /// The future will resolve to null if [load] has not been called yet.
  Future<AdSize?> getPlatformAdSize() async {
    return await instanceManager.getAdSize(this);
  }
}