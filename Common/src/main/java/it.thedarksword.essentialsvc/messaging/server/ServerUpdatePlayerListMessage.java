package it.thedarksword.essentialsvc.messaging.server;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import it.thedarksword.essentialsvc.messaging.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ServerUpdatePlayerListMessage implements Message {

    private String player;
    private Action action;

    @Override
    public void read(ByteArrayDataInput in) {
        player = in.readUTF();
        action = Action.values()[in.readInt()];
    }

    @Override
    public void write(ByteArrayDataOutput out) {
        out.writeUTF(player.toLowerCase());
        out.writeInt(action.ordinal());
    }

    public enum Action {
        ADD,
        REMOVE
    }
}
