package com.visualsx.mixin;

import com.visualsx.TpsTracker;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class NetworkHandlerMixin {
    @Inject(method = "onWorldTimeUpdate", at = @At("TAIL"), require = 0)
    private void visualsx$tps(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        TpsTracker.onTimeUpdate();
    }
}
