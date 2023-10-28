package org.browsit.mbedwarsquests;

import de.marcely.bedwars.api.event.arena.RoundEndEvent;
import me.pikamug.quests.module.BukkitCustomObjective;
import me.pikamug.quests.player.Quester;
import me.pikamug.quests.quests.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class MBedwarsEndObjective extends BukkitCustomObjective implements Listener {

    public MBedwarsEndObjective() {
        setName("MBedwars End Objective");
        setAuthor("Browsit, LLC");
        setItem("EYE_OF_ENDER", (short)0);
        setShowCount(true);
        addStringPrompt("MBW End Obj", "Set a name for the objective", "Finish arena");
        addStringPrompt("MBW End Arena", "Enter arena names, separating each one by a comma", "ANY");
        setCountPrompt("Set the number of times to finish the game");
        setDisplay("%MBW End Obj% %MBW End Arena%: %count%");
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
    public void onGameEnd(final RoundEndEvent event) {
        if (MBedwarsModule.getQuests() == null) {
            return;
        }
        for (final Player winner : event.getArena().getPlayers()) {
            final Quester quester = MBedwarsModule.getQuests().getQuester(winner.getUniqueId());
            if (quester == null) {
                return;
            }
            for (final Quest q : quester.getCurrentQuests().keySet()) {
                final Player p = quester.getPlayer();
                final Map<String, Object> dataMap = getDataForPlayer(p.getUniqueId(), this, q);
                if (dataMap != null) {
                    final String arenaNames = (String)dataMap.getOrDefault("MBW End Arena", "ANY");
                    if (arenaNames == null) {
                        return;
                    }
                    final String[] spl = arenaNames.split(",");
                    for (final String str : spl) {
                        if (str.equalsIgnoreCase("ANY") || event.getArena().getName().equalsIgnoreCase(str)) {
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(MBedwarsModule
                                    .getQuests(), () -> incrementObjective(p.getUniqueId(), this, q, 1), 40L);
                            break;
                        }
                    }
                }
            }
        }
    }
}
