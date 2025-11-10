# Add project specific ProGuard rules here.
-keep class com.example.qqmessageimage.** { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
