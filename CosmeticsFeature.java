package com.visualsx;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;

/** Faqat o'z qahramoningizda ko'rinadigan kosmetika (qanotlar va shlyapalar). */
public class CosmeticsFeature extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    private final ItemRenderState itemState = new ItemRenderState();

    public CosmeticsFeature(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light,
                       PlayerEntityRenderState state, float yaw, float pitch) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || state.id != mc.player.getId() || state.invisible) return;

        MenuScreen.Cosmetics.load();
        boolean[] on = MenuScreen.Cosmetics.ON;

        // Qanotlar (orqada, ikki tomonda)
        ItemStack wing = on[0] ? new ItemStack(Items.PHANTOM_MEMBRANE)
            : on[1] ? new ItemStack(Items.FEATHER)
            : on[2] ? new ItemStack(Items.ELYTRA) : null;
        if (wing != null) {
            for (int side = -1; side <= 1; side += 2) {
                matrices.push();
                matrices.translate(side * 0.2f, 0.35f, 0.22f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(side * 35f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
                matrices.scale(0.9f * side, -0.9f, -0.9f);
                draw(wing, matrices, queue, light, ItemDisplayContext.FIXED);
                matrices.pop();
            }
        }

        // Shlyapalar (bosh ustida, bosh bilan birga burilib)
        ItemStack hat = on[3] ? new ItemStack(Items.GOLDEN_HELMET)
            : on[4] ? new ItemStack(Items.NETHERITE_HELMET)
            : on[5] ? new ItemStack(Items.RABBIT_FOOT) : null;
        if (hat != null) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.relativeHeadYaw));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(state.pitch));
            matrices.translate(0.0f, -0.25f, 0.0f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));
            matrices.scale(0.625f, -0.625f, -0.625f);
            draw(hat, matrices, queue, light, ItemDisplayContext.HEAD);
            matrices.pop();
        }
    }

    private void draw(ItemStack stack, MatrixStack matrices, OrderedRenderCommandQueue queue,
                      int light, ItemDisplayContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        itemState.clear();
        mc.getItemModelManager().updateForLivingEntity(itemState, stack, ctx, mc.player);
        itemState.render(matrices, queue, light, OverlayTexture.DEFAULT_UV, 0);
    }
}
