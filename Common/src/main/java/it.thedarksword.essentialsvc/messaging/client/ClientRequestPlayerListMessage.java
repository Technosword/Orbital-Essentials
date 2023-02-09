package it.thedarksword.essentialsvc.messaging.client;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import it.thedarksword.essentialsvc.messaging.Message;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
public class ClientRequestPlayerListMessage implements Message {

    @Override
    public void read(ByteArrayDataInput in) {

    }

    @Override
    public void write(ByteArrayDataOutput out) {

    }
}
