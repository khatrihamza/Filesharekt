# ProGuard rules for Filesharekt

# Preserve app classes
-keep class com.example.filesharekt.** { *; }

# Preserve Room database
-keep class androidx.room.Room { public static <methods>; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# Preserve LiveData & ViewModel
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>();
}

# Preserve Kotlin
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**

# Preserve AndroidX
-keep class androidx.** { *; }
-dontwarn androidx.**

# Keep resource references
-keepclasseswithmembernames class * {
    native <methods>;
}
