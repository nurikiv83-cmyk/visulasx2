package com.visualsx.mixin;

import com.visualsx.FreeLook;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public class CameraMixin {
    @ModifyArgs(method = "update",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"),
        require = 0)
    private void visualsx$rot(Args args) {
        FreeLook.cameraHooked = true;
        if (!FreeLook.active()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        float origYaw = args.get(0);
        // oldindan qarash (F5 ikki marta) chaqiruvida yaw 180 ga farq qiladi
        boolean front = Math.abs(MathHelper.wrapDegrees(origYaw - mc.player.getYaw())) > 90f;
        args.set(0, front ? FreeLook.yaw + 180f : FreeLook.yaw);
        args.set(1, front ? -FreeLook.pitch : FreeLook.pitch);
    }
}
