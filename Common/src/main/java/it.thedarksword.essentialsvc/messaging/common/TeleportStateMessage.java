package it.thedarksword.essentialsvc.messaging.common;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import it.thedarksword.essentialsvc.messaging.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TeleportStateMessage implements Message {

    private UUID player;
    private State state;

    @Override
    public void read(ByteArrayDataInput in) {
        player = UUID.fromString(in.readUTF());
        state = State.values()[in.readInt()];
    }

    @Override
    public void write(ByteArrayDataOutput out) {
        out.writeUTF(player.toString());
        out.writeInt(state.ordinal());
    }

    public enum State {
        TELEPORTING,
        CANCEL
    }
}
