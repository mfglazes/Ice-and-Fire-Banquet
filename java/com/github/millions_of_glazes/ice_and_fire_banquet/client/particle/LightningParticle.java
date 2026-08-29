package com.github.millions_of_glazes.ice_and_fire_banquet.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LightningParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private float baseSize;

    protected LightningParticle(ClientLevel level, double x, double y, double z,
                                double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = sprites;
        this.lifetime = 5;
        this.quadSize = 0.02F;
        this.baseSize = this.quadSize;
        this.setSpriteFromAge(sprites);
        this.gravity = 0.0F;
        this.friction = 0.9F;
        this.hasPhysics = false;
    }

    @Override
    public void tick() {
        super.tick();
        // 旋转：每 tick 增加 0.1 弧度
        this.roll += 7F;
    }

    @Override
    public float getQuadSize(float partialTick) {
        float ageRatio = (this.age + partialTick) / this.lifetime;
        if (ageRatio < 0.5F) {
            return this.baseSize * (1.0F + ageRatio * 2.0F);
        } else {
            float fade = (ageRatio - 0.5F) * 2.0F;
            return this.baseSize * (2.0F * (1.0F - fade * fade));
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        float f = ((float)this.age + partialTick) / this.lifetime;
        f = Mth.clamp(f, 0.0F, 1.0F);
        int i = super.getLightColor(partialTick);
        int j = i & 255;
        int k = i >> 16 & 255;
        j += (int)(f * 15.0F * 16.0F);
        if (j > 240) j = 240;
        return j | k << 16;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    // 内部 Provider 类必须为 public static
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprites) {
            this.sprite = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new LightningParticle(level, x, y, z, vx, vy, vz, this.sprite);
        }
    }
}