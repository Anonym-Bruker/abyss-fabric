package net.abyss.mixin;

import net.abyss.ExampleMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {
    @Shadow public abstract void send(Packet<?> packet);

    @Inject(at = @At("TAIL"), method = "send(Lnet/minecraft/network/Packet;)V", cancellable = true)
    public void send(Packet<?> packet, CallbackInfo ci){
        //ExampleMod.LOGGER.info(packet.getClass().getName());
        if(packet instanceof PlayerMoveC2SPacket){
            MinecraftClient mc = MinecraftClient.getInstance();
            //ExampleMod.LOGGER.info("MIXIN: X: " + mc.player.getX() + ", Y: " + mc.player.getY() + ", Z: " + mc.player.getZ());
        }
    }
}
