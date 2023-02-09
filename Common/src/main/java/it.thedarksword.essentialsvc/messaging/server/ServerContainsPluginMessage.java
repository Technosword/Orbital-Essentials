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
public class ServerContainsPluginMessage implements Message {

    private boolean containsPlugin;

    @Override
    public void read(ByteArrayDataInput in) {
        containsPlugin = in.readBoolean();
    }

    @Override
    public void write(ByteArrayDataOutput out) {
        out.writeBoolean(containsPlugin);
    }

}
