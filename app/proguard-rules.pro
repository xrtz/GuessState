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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep line numbers for readable stack traces once crash reporting lands (docs/ROADMAP.md
# "Идеи на потом" — AppMetrica или аналог), at effectively no size cost now.
-keepattributes SourceFile,LineNumberTable

# kotlinx.serialization (docs/ARCHITECTURE.md §5: cities.json / us_states_albers.json DTOs)
# — the library's own documented rules, https://github.com/Kotlin/kotlinx.serialization#android :
# each @Serializable class's generated $serializer companion is only reached reflectively, so R8
# would otherwise strip it as unused.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keep,includedescriptorclasses class ru.xrtz.whichstate.**$$serializer { *; }
-keepclassmembers class ru.xrtz.whichstate.** {
    *** Companion;
}
-keepclasseswithmembers class ru.xrtz.whichstate.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Yandex Mobile Ads ships its own consumer proguard.txt inside the AAR, applied automatically —
# nothing to add here (docs/ROADMAP.md Phase 11: "правила ProGuard (serialization, Yandex Ads)").