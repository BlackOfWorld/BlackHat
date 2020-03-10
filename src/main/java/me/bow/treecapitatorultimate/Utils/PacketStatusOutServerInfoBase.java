package me.bow.treecapitatorultimate.Utils;

import net.minecraft.server.v1_15_R1.PacketDataSerializer;
import net.minecraft.server.v1_15_R1.PacketStatusOutListener;
import net.minecraft.server.v1_15_R1.PacketStatusOutServerInfo;
import net.minecraft.server.v1_15_R1.ServerPing;

import java.io.IOException;

public class PacketStatusOutServerInfoBase extends PacketStatusOutServerInfo {

    public PacketStatusOutServerInfo base = null;

    public PacketStatusOutServerInfoBase() {
    }

    public PacketStatusOutServerInfoBase(ServerPing var0) {
        super(var0);
    }

    @Override
    public void a(PacketDataSerializer var0) throws IOException {
        base.a(var0);
    }

    @Override
    public void a(PacketStatusOutListener var0) {
        base.a(var0);
    }

    @Override
    public void b(PacketDataSerializer var0) throws IOException {
        base.b(var0);
    }
}
