package org.browsit.mbedwarsquests;

import de.marcely.bedwars.api.event.player.PlayerKillPlayerEvent;
import me.pikamug.quests.enums.ObjectiveType;
import me.pikamug.quests.module.BukkitCustomObjective;
import me.pikamug.quests.player.Quester;
import me.pikamug.quests.quests.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class MBedwarsKillObjective extends BukkitCustomObjective implements Listener {

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
        for (final Quest quest : quester.getCurrentQuests().keySet()) {
            final Player p = quester.getPlayer();
            final Map<String, Object> dataMap = getDataForPlayer(p.getUniqueId(), this, quest);
            if (dataMap != null) {
                final String arenaNames = (String)dataMap.getOrDefault("MBW Kill Arena", "ANY");
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
