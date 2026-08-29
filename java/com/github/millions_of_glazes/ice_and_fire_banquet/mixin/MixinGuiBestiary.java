package com.github.millions_of_glazes.ice_and_fire_banquet.mixin;

import com.github.alexthe666.iceandfire.client.gui.bestiary.GuiBestiary;
import com.github.millions_of_glazes.ice_and_fire_banquet.common.registry.item.ModItems;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiBestiary.class, remap = false)
public class MixinGuiBestiary {

    // ---- 暴露原类的私有静态字段和私有方法 ----
    @Shadow
    private static ResourceLocation DRAWINGS_0;   // 如果你需要使用原版的绘图纹理

    @Shadow
    private void drawImage(GuiGraphics ms, ResourceLocation texture, int x, int y,
                           int u, int v, int width, int height, float scale) {}

    @Shadow
    private void drawItemStack(GuiGraphics ms, ItemStack stack, int x, int y, float scale) {}

    // imageFromTxt 中的语言文件路径（第 1 个 new ResourceLocation）
    @Redirect(method = "imageFromTxt",
            at = @At(value = "NEW", target = "net/minecraft/resources/ResourceLocation",
                    args = "(Ljava/lang/String;)V", ordinal = 0))
    private ResourceLocation redirectImageFileLoc(String resourceName) {
        return adjustResourceLocation(resourceName);
    }

    // imageFromTxt 中的回退语言文件路径（第 2 个 new ResourceLocation）
    @Redirect(method = "imageFromTxt",
            at = @At(value = "NEW", target = "net/minecraft/resources/ResourceLocation",
                    args = "(Ljava/lang/String;)V", ordinal = 1))
    private ResourceLocation redirectImageBackupLoc(String resourceName) {
        return adjustResourceLocation(resourceName);
    }

    // imageFromTxt 中的纹理路径（第 3 个 new ResourceLocation）
    @Redirect(method = "imageFromTxt",
            at = @At(value = "NEW", target = "net/minecraft/resources/ResourceLocation",
                    args = "(Ljava/lang/String;)V", ordinal = 2))
    private ResourceLocation redirectImageTextureLoc(String resourceName) {
        return adjustTextureResourceLocation(resourceName);
    }

    // writeFromTxt 中的语言文件路径（第 1 个）
    @Redirect(method = "writeFromTxt",
            at = @At(value = "NEW", target = "net/minecraft/resources/ResourceLocation",
                    args = "(Ljava/lang/String;)V", ordinal = 0))
    private ResourceLocation redirectWriteFileLoc(String resourceName) {
        return adjustResourceLocation(resourceName);
    }

    // writeFromTxt 中的回退语言文件路径（第 2 个）
    @Redirect(method = "writeFromTxt",
            at = @At(value = "NEW", target = "net/minecraft/resources/ResourceLocation",
                    args = "(Ljava/lang/String;)V", ordinal = 1))
    private ResourceLocation redirectWriteBackupLoc(String resourceName) {
        return adjustResourceLocation(resourceName);
    }

    /**
     * 语言文件路径替换：当页面是 COOKERY 时，将命名空间改为 ice_and_fire_banquet
     */
    private ResourceLocation adjustResourceLocation(String resourceName) {
        GuiBestiary gui = (GuiBestiary) (Object) this;
        if (gui.pageType != null && "COOKERY".equals(gui.pageType.name())) {
            String path = resourceName.substring(resourceName.indexOf(':') + 1);
            return new ResourceLocation("ice_and_fire_banquet", path);
        }
        return new ResourceLocation(resourceName);
    }

    /**
     * 纹理路径替换：当页面是 COOKERY 时，将命名空间改为 ice_and_fire_banquet
     */
    private ResourceLocation adjustTextureResourceLocation(String resourceName) {
        GuiBestiary gui = (GuiBestiary) (Object) this;
        if (gui.pageType != null && "COOKERY".equals(gui.pageType.name())) {
            String path = resourceName.substring(resourceName.indexOf(':') + 1);
            return new ResourceLocation("ice_and_fire_banquet", path);
        }
        return new ResourceLocation(resourceName);
    }

    /**
     * 在 drawPerPage 方法末尾注入 COOKERY 页面的自定义绘制
     * 注意：原方法在最后调用了 writeFromTxt，所以我们的绘制会在文本之前（作为背景插图），
     * 如果想让图片覆盖在文字上方，可以改用 @At("TAIL") 并把注入点改到 writeFromTxt 调用之后
     */
    @Inject(method = "drawPerPage", at = @At("HEAD"))
    private void onDrawPerPage(GuiGraphics ms, int bookPages, CallbackInfo ci) {
        GuiBestiary gui = (GuiBestiary) (Object) this;
        if (gui.pageType == null || !"COOKERY".equals(gui.pageType.name())) {
            return; // 不是 COOKERY 页面则不处理
        }

        // 自定义绘制示例
        if (bookPages == 0) {
            // 绘制一张 128x128 的图片到右页中部
            ResourceLocation myImage = new ResourceLocation("ice_and_fire_banquet", "textures/item/logo.png");
            // drawImage 的参数：ms, texture, x, y, u, v, width, height, scale(512f表示原尺寸)
            ms.pose().pushPose();
            RenderSystem.setShaderTexture(0, myImage);
            // 直接绘制 64x64 的纹理，不缩放
            ms.blit(myImage, 125, 15, 0, 0, 64, 64, 64, 64);
            ms.pose().popPose();

            // 在左页绘制物品
            this.drawItemStack(ms, new ItemStack(ModItems.SILVER_KNIFE.get()), 20, 85, 2.0f);
            this.drawItemStack(ms, new ItemStack(ModItems.COPPER_KNIFE.get()), 60, 85, 2.0f);
        }
        else if (bookPages == 1) {
            // 第二页的绘制...
        }
    }
}
