package com.github.millions_of_glazes.ice_and_fire_banquet.mixin;

import com.github.alexthe666.iceandfire.enums.EnumBestiaryPages;
import com.google.common.collect.ImmutableList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.IntStream;

@Mixin(value = EnumBestiaryPages.class, remap = false)
public abstract class MixinEnumBestiaryPages {

    @Unique
    private static final String TARGET_FIELD_VALUES = "$VALUES";
    @Unique
    private static final String TARGET_FIELD_ALL_PAGES = "ALL_PAGES";
    @Unique
    private static final String TARGET_FIELD_ALL_INDEXES = "ALL_INDEXES";

    // Unsafe 字段偏移量缓存
    @Unique
    private static long offsetName;
    @Unique
    private static long offsetOrdinal;
    @Unique
    private static long offsetPages;
    @Unique
    private static long offsetValues;
    @Unique
    private static long offsetAllPages;
    @Unique
    private static long offsetAllIndexes;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClassInit(CallbackInfo ci) {
        try {
            // 1. 获取 Unsafe 实例
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);

            // 2. 获取字段偏移量（只获取 Field 对象，不调用 setAccessible）
            Field nameField = Enum.class.getDeclaredField("name");
            Field ordinalField = Enum.class.getDeclaredField("ordinal");
            Field pagesField = EnumBestiaryPages.class.getDeclaredField("pages");
            Field valuesField = EnumBestiaryPages.class.getDeclaredField(TARGET_FIELD_VALUES);
            Field allPagesField = EnumBestiaryPages.class.getDeclaredField(TARGET_FIELD_ALL_PAGES);
            Field allIndexesField = EnumBestiaryPages.class.getDeclaredField(TARGET_FIELD_ALL_INDEXES);

            offsetName = unsafe.objectFieldOffset(nameField);
            offsetOrdinal = unsafe.objectFieldOffset(ordinalField);
            offsetPages = unsafe.objectFieldOffset(pagesField);
            offsetValues = unsafe.staticFieldOffset(valuesField);
            offsetAllPages = unsafe.staticFieldOffset(allPagesField);
            offsetAllIndexes = unsafe.staticFieldOffset(allIndexesField);

            // 3. 绕过构造器分配 COOKERY 实例
            EnumBestiaryPages cookery = (EnumBestiaryPages) unsafe.allocateInstance(EnumBestiaryPages.class);

            // 4. 用 Unsafe 直接写入字段值（无需 setAccessible）
            unsafe.putObject(cookery, offsetName, "COOKERY");
            unsafe.putInt(cookery, offsetOrdinal, EnumBestiaryPages.values().length); // 27
            unsafe.putInt(cookery, offsetPages, 5); //页数

            // 5. 构建新的 $VALUES 数组，并用 Unsafe 替换静态字段
            EnumBestiaryPages[] original = EnumBestiaryPages.values();
            EnumBestiaryPages[] newArray = Arrays.copyOf(original, original.length + 1);
            newArray[original.length] = cookery;
            Object base = unsafe.staticFieldBase(valuesField);
            unsafe.putObject(base, offsetValues, newArray);

            // 6. 更新 ALL_PAGES 和 ALL_INDEXES
            base = unsafe.staticFieldBase(allPagesField);
            unsafe.putObject(base, offsetAllPages, ImmutableList.copyOf(newArray));

            base = unsafe.staticFieldBase(allIndexesField);
            unsafe.putObject(base, offsetAllIndexes,
                    ImmutableList.copyOf(IntStream.range(0, newArray.length).iterator()));

        }
        catch (Exception e) {
            throw new RuntimeException("Failed to add COOKERY to EnumBestiaryPages", e);
        }
    }
}
