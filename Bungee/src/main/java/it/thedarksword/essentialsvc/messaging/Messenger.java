package it.thedarksword.essentialsvc.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import it.thedarksword.essentialsvc.EssentialsVC;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class Messenger extends AbstractMessenger implements Listener {

    private final EssentialsVC essentialsVC;
    public Messenger(EssentialsVC essentialsVC) {
        super(true, new BungeeMessageListener(essentialsVC));
        this.essentialsVC = essentialsVC;
    }

    @EventHandler
    public void onMessage(PluginMessageEvent event) {
        if (!event.getTag().equals(CHANNEL) || !(event.getReceiver() instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());

        int id = in.readInt();
        if(id == -1) {
            essentialsVC.getLogger().severe("Invalid packet id");
            return;
        }

        Message message = createIncomingMessage(id);
        message.read(in);

        invokeHandler(message, player.getServer().getInfo().getName());
    }

    public void sendMessage(Message message, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeInt(getOutgoingId(message));
        message.write(out);

        essentialsVC.getProxy().getServerInfo(server).sendData(CHANNEL, out.toByteArray());
        //player.getServer().getInfo().sendData(CHANNEL, out.toByteArray());
    }

    public void sendMessage(Message message, String server, long delay) {
        essentialsVC.getProxy().getScheduler().schedule(essentialsVC, () -> {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();

            out.writeInt(getOutgoingId(message));
            message.write(out);


            essentialsVC.getProxy().getServerInfo(server).sendData(CHANNEL, out.toByteArray());
            //player.getServer().getInfo().sendData(CHANNEL, out.toByteArray());
        }, delay, TimeUnit.MILLISECONDS);
    }
}
