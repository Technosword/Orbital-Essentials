package it.thedarksword.essentialsvc.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import it.thedarksword.essentialsvc.EssentialsVC;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Messenger extends AbstractMessenger implements PluginMessageListener {

    private final EssentialsVC essentialsVC;

    public Messenger(EssentialsVC essentialsVC) {
        super(new SpigotMessageListener(essentialsVC));
        this.essentialsVC = essentialsVC;
    }

    @Override
    public void onPluginMessageReceived(String channel, @NotNull Player messenger, byte[] bytes) {
        if(!channel.equals(CHANNEL)) return;

        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);

        int id = in.readInt();
        if(id == -1) {
            essentialsVC.getLogger().severe("Invalid packet id");
            return;
        }

        Message message = createIncomingMessage(id);
        message.read(in);

        invokeHandler(message);
    }

    public void sendMessage(Player player, Message message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeInt(getOutgoingId(message));
        message.write(out);

        player.sendPluginMessage(essentialsVC, CHANNEL, out.toByteArray());
    }

    public void sendMessage(Player player, Message message, long delay) {
        essentialsVC.getServer().getScheduler().runTaskLater(essentialsVC, () -> {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();

            out.writeInt(getOutgoingId(message));
            message.write(out);

            player.sendPluginMessage(essentialsVC, CHANNEL, out.toByteArray());
        }, delay);
    }
}
