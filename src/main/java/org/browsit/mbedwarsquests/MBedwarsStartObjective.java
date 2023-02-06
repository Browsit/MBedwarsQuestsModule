package org.browsit.mbedwarsquests;

import de.marcely.bedwars.api.event.arena.RoundStartEvent;
import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class MBedwarsStartObjective extends CustomObjective implements Listener {

    public MBedwarsStartObjective() {
        setName("MBedwars Start Objective");
        setAuthor("Browsit, LLC");
        setItem("FISHING_ROD", (short)0);
        setShowCount(true);
        addStringPrompt("MBW Start Obj", "Set a name for the objective", "Start arena");
        addStringPrompt("MBW Start Arena", "Enter arena names, separating each one by a comma", "ANY");
        setCountPrompt("Set the number of times to start the game");
        setDisplay("%MBW Start Obj% %MBW Start Arena%: %count%");
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
    public void onGameStart(final RoundStartEvent event) {
        if (MBedwarsModule.getQuests() == null) {
            return;
        }
        for (final Player player : event.getArena().getPlayers()) {
            final Quester quester = MBedwarsModule.getQuests().getQuester(player.getUniqueId());
            if (quester == null) {
                return;
            }
            for (final Quest q : quester.getCurrentQuests().keySet()) {
                final Player p = quester.getPlayer();
                final Map<String, Object> dataMap = getDataForPlayer(p, this, q);
                if (dataMap != null) {
                    final String arenaNames = (String)dataMap.getOrDefault("SBW Start Arena", "ANY");
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
}
