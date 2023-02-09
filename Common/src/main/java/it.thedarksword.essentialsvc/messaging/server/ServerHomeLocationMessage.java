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
public class ServerHomeLocationMessage implements Message {

    private String player;
    private String homeName;

    @Override
    public void read(ByteArrayDataInput in) {
        player = in.readUTF();
        homeName = in.readUTF();
    }

    @Override
    public void write(ByteArrayDataOutput out) {
        out.writeUTF(player);
        out.writeUTF(homeName);
    }
}
