package me.bow.treecapitatorultimate.Utils.Packet;

public enum PacketType {

    PlayOut("PlayOut"), PlayIn("PlayIn");

    public String prefix;

    PacketType(String prefix){
        this.prefix = prefix;
    }

}