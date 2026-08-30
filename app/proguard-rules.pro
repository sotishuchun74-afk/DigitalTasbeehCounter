# Room Database rules
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>();
}
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Coroutines and Serialization
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
