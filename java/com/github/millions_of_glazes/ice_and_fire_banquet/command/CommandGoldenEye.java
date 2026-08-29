package com.github.millions_of_glazes.ice_and_fire_banquet.command;

import com.github.millions_of_glazes.ice_and_fire_banquet.common.utility.GoldenEyeUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommandGoldenEye {
    private static final Map<String, List<String>> COLORS = Map.of(
            "fire", List.of("red", "green", "bronze", "gray"),
            "ice", List.of("blue", "white", "sapphire", "silver"),
            "lightning", List.of("electric", "amethyst", "copper", "black")
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("goldeneye")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("type", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            builder.suggest("fire");
                            builder.suggest("ice");
                            builder.suggest("lightning");
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("color", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    String typeStr = ctx.getArgument("type", String.class).toLowerCase(Locale.ROOT);
                                    List<String> colors = COLORS.get(typeStr);
                                    if (colors != null) colors.forEach(builder::suggest);
                                    else COLORS.values().forEach(l -> l.forEach(builder::suggest));
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("gender", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            builder.suggest("male");
                                            builder.suggest("female");
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("level", IntegerArgumentType.integer(1, 5))
                                                .then(Commands.argument("duration", IntegerArgumentType.integer(1))
                                                        .executes(ctx -> execute(ctx, 2.5))
                                                        .then(Commands.argument("speedMultiplier", DoubleArgumentType.doubleArg(0.1, 10.0))
                                                                .executes(ctx -> execute(ctx, DoubleArgumentType.getDouble(ctx, "speedMultiplier")))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int execute(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx, double speedMult) {
        String typeStr = StringArgumentType.getString(ctx, "type").toLowerCase(Locale.ROOT);
        String colorStr = StringArgumentType.getString(ctx, "color").toLowerCase(Locale.ROOT);
        String genderStr = StringArgumentType.getString(ctx, "gender").toLowerCase(Locale.ROOT);
        int level = IntegerArgumentType.getInteger(ctx, "level");
        int durationSec = IntegerArgumentType.getInteger(ctx, "duration");

        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) {
            ctx.getSource().sendFailure(Component.translatable("command.ice_and_fire_banquet.player_only"));
            return 0;
        }

        String dragonType = switch (typeStr) {
            case "fire", "f" -> "fire";
            case "ice", "i" -> "ice";
            case "lightning", "l" -> "lightning";
            default -> {
                ctx.getSource().sendFailure(Component.translatable("command.ice_and_fire_banquet.invalid_type"));
                yield null;
            }
        };
        if (dragonType == null) return 0;

        int variant = parseVariant(dragonType, colorStr);
        if (variant == -1) {
            ctx.getSource().sendFailure(Component.translatable(
                    "command.ice_and_fire_banquet.invalid_color", String.join(", ", COLORS.get(dragonType))));
            return 0;
        }

        boolean isMale = switch (genderStr) {
            case "male", "m" -> true;
            case "female", "f" -> false;
            default -> {
                ctx.getSource().sendFailure(Component.translatable("command.ice_and_fire_banquet.invalid_gender"));
                yield false;
            }
        };

        // 调用叠加逻辑，自动合并等级和时间并更新 Capability
        GoldenEyeUtils.applyGoldenEye(
                player,
                dragonType,
                variant,
                isMale,
                level,
                durationSec,
                speedMult
        );

        ctx.getSource().sendSuccess(
                () -> Component.translatable(
                        "command.ice_and_fire_banquet.success",
                        dragonType,
                        COLORS.get(dragonType).get(variant),
                        isMale ? "male" : "female",
                        level,
                        durationSec,
                        speedMult
                ),
                true
        );
        return 1;
    }

    private static int parseVariant(String dragonType, String color) {
        List<String> valid = COLORS.get(dragonType);
        if (valid == null) return -1;
        try {
            int i = Integer.parseInt(color);
            if (i >= 0 && i < valid.size()) return i;
        } catch (NumberFormatException ignored) {}
        for (int i = 0; i < valid.size(); i++) {
            if (valid.get(i).equalsIgnoreCase(color) || valid.get(i).replace("_", "").equalsIgnoreCase(color.replace("_", "")))
                return i;
        }
        return -1;
    }
}
