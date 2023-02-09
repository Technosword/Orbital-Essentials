package it.thedarksword.essentialsvc.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public interface Message {

    void read(ByteArrayDataInput in);

    void write(ByteArrayDataOutput out);
}
