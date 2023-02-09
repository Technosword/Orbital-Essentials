package it.thedarksword.essentialsvc.messaging.client;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import it.thedarksword.essentialsvc.messaging.Message;
import it.thedarksword.essentialsvc.objets.LocationObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ClientUpdateBackMessage implements Message {

    private UUID player;
    private LocationObject location;

    @Override
    public void read(ByteArrayDataInput in) {
        player = UUID.fromString(in.readUTF());
        location = new LocationObject(in.readUTF(), in.readDouble(), in.readDouble(), in.readDouble(), in.readFloat(), in.readFloat());
    }

    @Override
    public void write(ByteArrayDataOutput out) {
        out.writeUTF(player.toString());
        out.writeUTF(location.getWorld());
        out.writeDouble(location.getX());
        out.writeDouble(location.getY());
        out.writeDouble(location.getZ());
        out.writeFloat(location.getYaw());
        out.writeFloat(location.getPitch());
    }
}
