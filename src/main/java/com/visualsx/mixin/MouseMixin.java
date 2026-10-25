package com.visualsx.mixin;

import com.visualsx.FreeLook;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mouse.class)
public class MouseMixin {
    @Redirect(method = "updateMouse",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"),
        require = 0)
    private void visualsx$look(ClientPlayerEntity player, double dx, double dy) {
        FreeLook.mouseHooked = true;
        if (FreeLook.active()) FreeLook.turn(dx, dy);
        else player.changeLookDirection(dx, dy);
    }
}
