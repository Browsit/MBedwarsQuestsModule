package org.browsit.mbedwarsquests;

import de.marcely.bedwars.api.event.player.PlayerBuyUpgradeEvent;
import me.pikamug.quests.enums.ObjectiveType;
import me.pikamug.quests.module.BukkitCustomObjective;
import me.pikamug.quests.player.Quester;
import me.pikamug.quests.quests.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class MBedwarsBuyUpgradeObjective extends BukkitCustomObjective implements Listener {

    public MBedwarsBuyUpgradeObjective() {
        setName("MBedWars Buy Upgrade Objective");
        setAuthor("Browsit, LLC");
        setItem("DIAMOND_BLOCK", (short)0);
        setShowCount(true);
        addStringPrompt("MBW Upgrade Obj", "Set a name for the objective", "Buy upgrade");
        addStringPrompt("MBW Upgrade Arena", "Enter arena names, separating each one by a comma", "ANY");
        setCountPrompt("Set the number of times to buy an upgrade");
        setDisplay("%MBW Upgrade Obj% in arena %MBW Upgrade Arena%: %count%");
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
    public void onBuyItem(final PlayerBuyUpgradeEvent event) {
        if (MBedwarsModule.getQuests() == null) {
            return;
        }
        final Player customer = event.getPlayer();
        final Quester quester = MBedwarsModule.getQuests().getQuester(customer.getUniqueId());
        if (quester == null) {
            return;
        }
        for (final Quest quest : quester.getCurrentQuests().keySet()) {
            final Player p = quester.getPlayer();
            final Map<String, Object> dataMap = getDataForPlayer(p.getUniqueId(), this, quest);
            if (dataMap != null) {
                final String arenaNames = (String) dataMap.getOrDefault("MBW Upgrade Arena", "ANY");
                if (arenaNames == null) {
                    return;
                }
                final String[] spl = arenaNames.split(",");
                for (final String str : spl) {
                    if (event.getArena() == null) {
                        continue;
                    }
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
