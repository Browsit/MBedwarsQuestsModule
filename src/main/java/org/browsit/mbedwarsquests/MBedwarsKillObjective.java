package org.browsit.mbedwarsquests;

import de.marcely.bedwars.api.event.player.PlayerKillPlayerEvent;
import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class MBedwarsKillObjective extends CustomObjective implements Listener {

    public MBedwarsKillObjective() {
        setName("MBedwars Kill Objective");
        setAuthor("Browsit, LLC");
        setItem("STONE_SWORD", (short)0);
        setShowCount(true);
        addStringPrompt("MBW Kill Obj", "Set a name for the objective", "Kill player");
        addStringPrompt("MBW Kill Arena", "Enter arena names, separating each one by a comma", "ANY");
        setCountPrompt("Set the number of players to kill");
        setDisplay("%MBW Kill Obj% in arena %MBW Kill Arena%: %count%");
    }

    @Override
    public String getModuleName() {
        return MBedwarsModule.getModuleName();
    }

    @Override
    public Map.Entry<String, Short> getModuleItem() {
        return MBedwarsModule.getModuleItem();
    }

    @EventHandler
    public void onPlayerKill(final PlayerKillPlayerEvent event) {
        if (MBedwarsModule.getQuests() == null) {
            return;
        }
        final Player killer = event.getKiller();
        if (killer == null) {
            return;
        }
        final Quester quester = MBedwarsModule.getQuests().getQuester(killer.getUniqueId());
        if (quester == null) {
            return;
        }
        for (final Quest q : quester.getCurrentQuests().keySet()) {
            final Player p = quester.getPlayer();
            final Map<String, Object> dataMap = getDataForPlayer(p, this, q);
            if (dataMap != null) {
                final String arenaNames = (String)dataMap.getOrDefault("MBW Kill Arena", "ANY");
                if (arenaNames == null) {
                    return;
                }
                final String[] spl = arenaNames.split(",");
                for (final String str : spl) {
                    if (str.equalsIgnoreCase("ANY") || event.getArena().getName().equalsIgnoreCase(str)) {
                        incrementObjective(p, this, 1, q);
                        break;
                    }
                }
            }
        }
    }
}
