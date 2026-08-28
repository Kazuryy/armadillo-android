# Add project specific ProGuard rules here.

# com.google.crypto.tink (via androidx.security.crypto) references errorprone
# annotations that are compile-time only and not on the runtime classpath.
-dontwarn com.google.errorprone.annotations.**
