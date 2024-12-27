package com.github.liubin95.log2csv;

import com.intellij.DynamicBundle;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

final public class Log2csvBundle {
    @NonNls
    private static final String BUNDLE = "messages.Log2csvBundle";
    private static final DynamicBundle INSTANCE =
            new DynamicBundle(Log2csvBundle.class, BUNDLE);

    private Log2csvBundle() {
    }

    public static @NotNull @Nls String message(
            @NotNull @PropertyKey(resourceBundle = BUNDLE) String key,
            Object @NotNull ... params
    ) {
        return INSTANCE.getMessage(key, params);
    }

    public static Supplier<@Nls String> lazyMessage(
            @NotNull @PropertyKey(resourceBundle = BUNDLE) String key,
            Object @NotNull ... params
    ) {
        return INSTANCE.getLazyMessage(key, params);
    }
}
