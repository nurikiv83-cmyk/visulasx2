package com.visualsx.mixin;

import com.visualsx.FreeLook;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MouseMixin {
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true, require = 0)
    private void visualsx$look(double dx, double dy, CallbackInfo ci) {
        if (!((Object) this instanceof ClientPlayerEntity)) return;
        FreeLook.mouseHooked = true;
        if (FreeLook.active()) {
            FreeLook.turn(dx, dy);
            ci.cancel();
        }
    }
}
