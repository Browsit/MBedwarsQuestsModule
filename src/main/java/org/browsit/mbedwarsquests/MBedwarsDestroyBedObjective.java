package org.browsit.mbedwarsquests;

import de.marcely.bedwars.api.event.arena.ArenaBedBreakEvent;
import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class MBedwarsDestroyBedObjective extends CustomObjective implements Listener {

    public MBedwarsDestroyBedObjective() {
        setName("MBedwars Destroy Bed Objective");
        setAuthor("Browsit, LLC");
        setItem("BED", (short) 0);
        setShowCount(true);
        addStringPrompt("MBW Destroy Obj", "Set a name for the objective", "Destroy bed");
        addStringPrompt("MBW Destroy Arena", "Enter arena names, separating each one by a comma", "ANY");
        setCountPrompt("Set the number of times to destroy a bed");
        setDisplay("%MBW Destroy Obj% in arena %MBW Destroy Arena%: %count%");
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
    public void onDestroyBed(final ArenaBedBreakEvent event) {
        if (MBedwarsModule.getQuests() == null) {
            return;
        }
        final Player destroyer = event.getPlayer();
        if (destroyer == null) {
            return;
        }
        final Quester quester = MBedwarsModule.getQuests().getQuester(destroyer.getUniqueId());
        if (quester == null) {
            return;
        }
        for (final Quest q : quester.getCurrentQuests().keySet()) {
            final Player p = quester.getPlayer();
            final Map<String, Object> dataMap = getDataForPlayer(p, this, q);
            if (dataMap != null) {
                final String arenaNames = (String) dataMap.getOrDefault("MBW Destroy Arena", "ANY");
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
