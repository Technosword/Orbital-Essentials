package it.thedarksword.essentialsvc.messaging.client;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import it.thedarksword.essentialsvc.messaging.Message;
import it.thedarksword.essentialsvc.objets.LocationObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter

public class ClientTeleportToPlayerMessage implements Message {

    private String player;
    private String target;
    private LocationObject location;
    private Cause cause;

    @Override
    public void read(ByteArrayDataInput in) {
        player = in.readUTF();
        target = in.readUTF();
        location = new LocationObject(in.readUTF(), in.readDouble(), in.readDouble(), in.readDouble(), in.readFloat(), in.readFloat());
        cause = Cause.values()[in.readInt()];
    }

    @Override
    public void write(ByteArrayDataOutput out) {
        out.writeUTF(player);
        out.writeUTF(target);
        out.writeUTF(location.getWorld());
        out.writeDouble(location.getX());
        out.writeDouble(location.getY());
        out.writeDouble(location.getZ());
        out.writeFloat(location.getYaw());
        out.writeFloat(location.getPitch());
        out.writeInt(cause.ordinal());
    }

    public enum Cause {
        TP,
        TPA,
        TPACCEPT
    }
}
