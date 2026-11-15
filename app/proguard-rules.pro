# Keep Kotlinx Serialization rules
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.akram.babsharhisab.**$$serializer { *; }
-keepclassmembers class com.akram.babsharhisab.** {
    *** Companion;
}
-keepclasseswithmembers class com.akram.babsharhisab.** {
    kotlinx.serialization.KSerializer serializer(...);
}
# Keep Room entities
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**
