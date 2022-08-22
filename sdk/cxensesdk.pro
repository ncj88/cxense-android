##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized
-keep class io.piano.android.cxense.model.* { <fields>; }

# Adapt information about Kotlin file facades.
-adaptresourcefilecontents **.kotlin_module

# Preserve Kotlin metadata.
-keep class kotlin.Metadata { *; }

# Temporarily disable optimization on Kotlin classes.
-keep,includecode,allowobfuscation,allowshrinking @kotlin.Metadata class ** { *; }

-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}
