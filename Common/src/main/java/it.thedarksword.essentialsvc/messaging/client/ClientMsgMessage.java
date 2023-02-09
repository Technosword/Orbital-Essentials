package it.thedarksword.essentialsvc.messaging.client;

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
public class ClientMsgMessage implements Message {

    private UUID player;
    private String target;
    private String message;

    @Override
    public void read(ByteArrayDataInput in) {
        player = UUID.fromString(in.readUTF());
        target = in.readUTF();
        message = in.readUTF();
    }

    @Override
    public void write(ByteArrayDataOutput out) {
        out.writeUTF(player.toString());
        out.writeUTF(target);
        out.writeUTF(message);
    }
}
