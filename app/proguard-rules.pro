# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Preserve native JNI functions and native LLM inference engine
-keepclasseswithmembernames class * {
    native <methods>;
}

-keep class com.example.ai.NativeLLMEngine { *; }
-keep class com.example.ai.LocalSlmEngine { *; }
-keep class com.example.security.** { *; }
-keep class com.example.model.** { *; }

# Strip sensitive crashlytics packet logs / cryptographic keys in release
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
}
