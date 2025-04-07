package net.timardo.mcsessions.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("MCSessions");
    private static int discriminator;

    public static void init() {
        INSTANCE.registerMessage(PacketInitializeSessionConnection.class, PacketInitializeSessionConnection.class, discriminator++, Side.SERVER);
        INSTANCE.registerMessage(PacketInitializeSessionConnection.class, PacketInitializeSessionConnection.class, discriminator++, Side.CLIENT);
    }
  
    /*
     * Not really needed methods
     */
    public static void sendToAll(IMessage message) {
        INSTANCE.sendToAll(message);
    }

    public static void sendTo(IMessage message, EntityPlayerMP player) {
        INSTANCE.sendTo(message, player);
    }
    
    public static void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
        INSTANCE.sendToAllAround(message, point);
    }
  
    public static void sendToDimension(IMessage message, int dimensionId) {
        INSTANCE.sendToDimension(message, dimensionId);
    }
  
    public static void sendToServer(IMessage message) {
        INSTANCE.sendToServer(message);
    }
}