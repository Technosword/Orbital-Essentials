package it.thedarksword.essentialsvc.messaging.server;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import it.thedarksword.essentialsvc.messaging.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.HashSet;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ServerUpdatePlayersMessage implements Message {

    private Collection<String> players;

    @Override
    public void read(ByteArrayDataInput in) {
        int length = in.readInt();
        players = new HashSet<>();
        for(int i = 0; i < length; i++) {
            players.add(in.readUTF());
        }
    }

    @Override
    public void write(ByteArrayDataOutput out) {
        out.writeInt(players.size());
        for(String player : players) out.writeUTF(player.toLowerCase());
    }
}
