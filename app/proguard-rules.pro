# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable

# Hide the original source file name for security
-renamesourcefileattribute SourceFile

# Jetpack Compose - Keep only runtime essentials
-keep class androidx.compose.runtime.ComposerKt {
    *** isTraceInProgress(...);
    *** traceEventStart(...);
    *** traceEventEnd(...);
}
-keep class androidx.compose.runtime.Composer
-keep class androidx.compose.runtime.CompositionLocal
-dontwarn androidx.compose.**

# Lifecycle and ViewModel
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(...);
}

# Navigation Component
-keep class * extends androidx.navigation.Navigator

# Keep Room Database entities and DAOs
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-dontwarn androidx.room.paging.**

# Gson - Keep type adapters and serialized fields
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep model/data classes (adjust package names as needed)
-keep class com.karlof002.quran.data.models.** { *; }
-keep class com.karlof002.quran.data.entities.** { *; }

# Coil - Keep only essential image loading classes
-keep class coil.ImageLoader
-keep class coil.request.** { *; }
-dontwarn coil.**

# Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Coroutines - specific rules only
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Google Play Core
-keep class com.google.android.play.core.appupdate.** { *; }
-keep class com.google.android.play.core.install.** { *; }
-keep class com.google.android.play.core.tasks.** { *; }
-dontwarn com.google.android.play.core.**

# Keep ViewModels, Repositories, and DAOs in your app
-keep class com.karlof002.quran.ui.** extends androidx.lifecycle.ViewModel { *; }
-keep class com.karlof002.quran.data.repository.** { *; }
-keep interface com.karlof002.quran.data.dao.** { *; }

# Keep your main activities and services
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Remove logging in release builds (optional - uncomment if desired)
# -assumenosideeffects class android.util.Log {
#     public static *** d(...);
#     public static *** v(...);
#     public static *** i(...);
# }
