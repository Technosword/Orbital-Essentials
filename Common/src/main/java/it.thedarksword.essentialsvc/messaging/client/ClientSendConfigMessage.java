package it.thedarksword.essentialsvc.messaging.client;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import it.thedarksword.essentialsvc.messaging.Message;
import it.thedarksword.essentialsvc.objets.ConfigMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
public class ClientSendConfigMessage implements Message {

    private String player;
    private ConfigMessage path;
    private Map<String, String> replaces;

    public ClientSendConfigMessage(String player, ConfigMessage path) {
        this.player = player;
        this.path = path;
        this.replaces = new HashMap<>();
    }

    public ClientSendConfigMessage(String player, ConfigMessage path, String[][] replaces) {
        this.player = player;
        this.path = path;
        this.replaces = new HashMap<>();
        for (String[] replace : replaces) {
            this.replaces.put(replace[0], replace[1]);
        }
    }

    @Override
    public void read(ByteArrayDataInput in) {
        player = in.readUTF();
        path = ConfigMessage.values()[in.readInt()];
        int length = in.readInt();
        replaces = new HashMap<>(length);
        for(int i = 0; i < length; i++) {
            replaces.put(in.readUTF(), in.readUTF());
        }
    }

    @Override
    public void write(ByteArrayDataOutput out) {
        out.writeUTF(player);
        out.writeInt(path.ordinal());
        out.writeInt(replaces.size());
        for(Map.Entry<String, String> entry : replaces.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeUTF(entry.getValue());
        }
    }
}
