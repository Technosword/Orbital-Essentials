package it.thedarksword.essentialsvc.commands;

import it.thedarksword.essentialsvc.EssentialsVC;
import it.thedarksword.essentialsvc.messaging.client.ClientSendConfigMessage;
import it.thedarksword.essentialsvc.objets.ConfigMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RepairCommand extends AbstractCommand {

    public RepairCommand(EssentialsVC essentialsVC) {
        super(essentialsVC, "repair");
    }

    @Override
    public void execute(Player player, String[] args) {
        for(ItemStack item : player.getInventory().getContents()) {
            Material type;

            if(item == null || (type = item.getType()) == Material.AIR ||
                    type.isBlock() || type.isEdible() ||
                    type.getMaxDurability() <= 0 || item.getDurability() == 0) continue;

            item.setDurability((short)0);
        }
        for(ItemStack item : player.getInventory().getArmorContents()) {
            Material type;

            if(item == null || (type = item.getType()) == Material.AIR ||
            type.isBlock() || type.isEdible() ||
            type.getMaxDurability() <= 0 || item.getDurability() == 0) continue;

            item.setDurability((short)0);
        }
        player.updateInventory();
        essentialsVC.getMessenger().sendMessage(player, new ClientSendConfigMessage(player.getName(), ConfigMessage.REPAIR));
    }
}
