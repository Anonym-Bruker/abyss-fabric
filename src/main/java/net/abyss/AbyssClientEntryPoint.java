package net.abyss;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.lwjgl.glfw.GLFW;

public class AbyssClientEntryPoint implements ClientModInitializer {

    private static KeyBinding keyBindingUp = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.abyss.up", // The translation key of the keybinding's name
        InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
        GLFW.GLFW_KEY_R, // The keycode of the key
        "category.abyss.test" // The translation key of the keybinding's category.
        ));

    private static KeyBinding keyBindingDown = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.abyss.down", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_Z, // The keycode of the key
            "category.abyss.test" // The translation key of the keybinding's category.
    ));

    private static KeyBinding keyBindingTeleport = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.abyss.teleport", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_O, // The keycode of the key
            "category.abyss.test" // The translation key of the keybinding's category.
    ));

    private static KeyBinding keyBindingFly = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.abyss.flyingmode", // The translation key of the keybinding's name
            InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
            GLFW.GLFW_KEY_U, // The keycode of the key
            "category.abyss.test" // The translation key of the keybinding's category.
    ));


    private boolean flyingMode = false;

    @Override
    public void onInitializeClient(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            //MinecraftClient minecraftClient = MinecraftClient.getInstance();

            ClientPlayerEntity playerEntity = client.player;
            World world = client.world;

            HitResult hit = client.crosshairTarget;
            if(hit != null){
                switch(hit.getType()) {
                    case MISS:
                        //nothing near enough
                        break;
                    case BLOCK:
                        BlockHitResult blockHit = (BlockHitResult) hit;
                        BlockPos blockPos = blockHit.getBlockPos();
                        BlockState blockState = client.world.getBlockState(blockPos);
                        Block block = blockState.getBlock();
                        //int i = block.STATE_IDS.getRawId(blockState);
                        //int i1 = i == -1 ? 0 : i;
                        //world.syncWorldEvent(playerEntity, 2001, blockPos, i1);
                        //world.emitGameEvent(GameEvent.BLOCK_DESTROY, blockPos, GameEvent.Emitter.of(playerEntity, blockState));
                        //Block MY_BLOCK = new Block(FabricBlockSettings.copy(block).nonOpaque());

                        BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
                        //BlockRenderLayerMap.INSTANCE.putBlock(MY_BLOCK, RenderLayer.getTranslucent());
                        BlockRenderType renderType = blockState.getRenderType();
                        ExampleMod.LOGGER.info("Seeing a block, renderType: " + renderType.toString() + "; block name: " + block.getName());
                        break;
                    case ENTITY:
                        EntityHitResult entityHit = (EntityHitResult) hit;
                        Entity entity = entityHit.getEntity();
                        ExampleMod.LOGGER.info("Seeing an entity");
                        break;
                }
            }

            while (keyBindingFly.wasPressed()) {
                flyingMode = !flyingMode;
                ExampleMod.LOGGER.info("FLYING MODE SET TO:  " + flyingMode);
                playerEntity.sendMessage(Text.literal("FLYING MODE SET TO: " + flyingMode), false);
            }
            while (keyBindingTeleport.wasPressed()) {
                double x = playerEntity.getX();
                double y = playerEntity.getY();
                double z = playerEntity.getZ();
                ExampleMod.LOGGER.info("X: " + x + ", Y: " + y + ", Z: " + z);
                double motionY = 10.0;
                y = y + motionY;

                playerEntity.sendMessage(Text.literal("Teleporting UP, yeah!!!! Height: " + y), false);
                playerEntity.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x , y , z, false));
            }
            if(flyingMode){
                while (keyBindingUp.wasPressed()) {
                    double x = playerEntity.getX();
                    double y = playerEntity.getY();
                    double z = playerEntity.getZ();
                    ExampleMod.LOGGER.info("X: " + x + ", Y: " + y + ", Z: " + z);
                    double motionY = 0.5;
                    y = y + motionY;
                    playerEntity.sendMessage(Text.literal("Flying up baby!!!! Height: " + y), false);
                    playerEntity.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x , y , z, false));
                    Vec3d vec3d = new Vec3d(0, motionY, 0);
                    playerEntity.move(MovementType.PLAYER, vec3d);
                }
                while (keyBindingDown.wasPressed()) {
                    double x = playerEntity.getX();
                    double y = playerEntity.getY();
                    double z = playerEntity.getZ();
                    ExampleMod.LOGGER.info("X: " + x + ", Y: " + y + ", Z: " + z);
                    double motionY = -0.5;
                    y = y + motionY;
                    playerEntity.sendMessage(Text.literal("FLying down!!!! Height: " + y), false);
                    playerEntity.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x , y , z, false));
                    Vec3d vec3d = new Vec3d(0, motionY, 0);
                    playerEntity.move(MovementType.PLAYER, vec3d);
                }
                if(playerEntity != null) {
                    Vec3d vec3dVelocity = new Vec3d(playerEntity.getVelocity().x, 0, playerEntity.getVelocity().z);
                    playerEntity.setVelocity(vec3dVelocity);
                    double x = playerEntity.getX();
                    double y = playerEntity.getY();
                    double z = playerEntity.getZ();
                    playerEntity.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x , y - 0.1, z, false));
                }
            }
        });
        //ExampleMod.LOGGER.info("************RUNNING********************");
    }
}
