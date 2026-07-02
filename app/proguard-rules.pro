-keep class pl.torvinek.plusxmobile.MainActivity { public <init>(); }
-keep class pl.torvinek.plusxmobile.MainActivity$* { *; }

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
