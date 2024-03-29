package org.browsit.mbedwarsquests;

import de.marcely.bedwars.api.event.arena.ArenaBedBreakEvent;
import me.pikamug.quests.enums.ObjectiveType;
import me.pikamug.quests.module.BukkitCustomObjective;
import me.pikamug.quests.player.Quester;
import me.pikamug.quests.quests.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class MBedwarsDestroyBedObjective extends BukkitCustomObjective implements Listener {

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
        for (final Quest quest : quester.getCurrentQuests().keySet()) {
            final Player p = quester.getPlayer();
            final Map<String, Object> dataMap = getDataForPlayer(p.getUniqueId(), this, quest);
            if (dataMap != null) {
                final String arenaNames = (String) dataMap.getOrDefault("MBW Destroy Arena", "ANY");
                if (arenaNames == null) {
                    return;
                }
                final String[] spl = arenaNames.split(",");
                for (final String str : spl) {
                    if (str.equalsIgnoreCase("ANY") || event.getArena().getName().equalsIgnoreCase(str)) {
                        incrementObjective(p.getUniqueId(), this, quest, 1);

                        quester.dispatchMultiplayerEverything(quest, ObjectiveType.CUSTOM,
                                (final Quester q, final Quest cq) -> {
                                    incrementObjective(q.getUUID(), this, quest, 1);
                                    return null;
                                });
                        break;
                    }
                }
            }
        }
    }
}
