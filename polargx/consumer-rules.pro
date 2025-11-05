# Keep all public classes in the library
-keep public class com.library.polargx.** { *; }

# Keep Koin classes and annotations
-keep class org.koin.** { *; }
-keep interface org.koin.** { *; }
-keepclassmembers class * {
    @org.koin.core.annotation.* <methods>;
}

# Keep kotlinx.serialization classes
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep serializable classes
-keep,includedescriptorclasses class com.library.polargx.**$$serializer { *; }
-keepclassmembers class com.library.polargx.** {
    *** Companion;
}
-keepclasseswithmembers class com.library.polargx.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep data classes used for serialization
-keep class com.library.polargx.models.** { *; }
-keep class com.library.polargx.api.** { *; }

# Keep coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Keep Ktor classes
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { *; }

# Keep file operations and I/O
-keepclassmembers class * extends java.io.File { *; }

# Keep TrackingEventQueue specifically
-keep class com.library.polargx.session.TrackingEventQueue { *; }
-keepclassmembers class com.library.polargx.session.TrackingEventQueue { *; }

# Keep UserSession
-keep class com.library.polargx.session.UserSession { *; }
-keepclassmembers class com.library.polargx.session.UserSession { *; }

