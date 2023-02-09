package it.thedarksword.essentialsvc.messaging.client;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import it.thedarksword.essentialsvc.messaging.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ClientHomeListMessage implements Message {

    private String player;
    private String who;

    @Override
    public void read(ByteArrayDataInput in) {
        player = in.readUTF();
        who = in.readUTF();
    }

    @Override
    public void write(ByteArrayDataOutput out) {
        out.writeUTF(player);
        out.writeUTF(who);
    }
}
