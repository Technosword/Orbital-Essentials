package it.thedarksword.essentialsvc.commands;

import it.thedarksword.essentialsvc.EssentialsVC;
import org.bukkit.entity.Player;

public class WorkbenchCommand extends AbstractCommand {

    public WorkbenchCommand(EssentialsVC essentialsVC) {
        super(essentialsVC, "workbench");
    }

    @Override
    public void execute(Player player, String[] args) {
        player.openWorkbench(null, true);
    }
}
