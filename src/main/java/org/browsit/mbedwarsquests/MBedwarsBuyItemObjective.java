package org.browsit.mbedwarsquests;

import de.marcely.bedwars.api.event.player.PlayerBuyInShopEvent;
import me.pikamug.quests.module.BukkitCustomObjective;
import me.pikamug.quests.player.Quester;
import me.pikamug.quests.quests.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class MBedwarsBuyItemObjective extends BukkitCustomObjective implements Listener {

    public MBedwarsBuyItemObjective() {
        setName("MBedwars Buy Item Objective");
        setAuthor("Browsit, LLC");
        setItem("BREAD", (short)0);
        setShowCount(true);
        addStringPrompt("MBW Item Obj", "Set a name for the objective", "Buy item");
        addStringPrompt("MBW Item Arena", "Enter arena names, separating each one by a comma", "ANY");
        setCountPrompt("Set the number of times to buy an item");
        setDisplay("%MBW Item Obj% in arena %MBW Item Arena%: %count%");
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
    public void onBuyItem(final PlayerBuyInShopEvent event) {
        if (MBedwarsModule.getQuests() == null) {
            return;
        }
        final Player customer = event.getPlayer();
        final Quester quester = MBedwarsModule.getQuests().getQuester(customer.getUniqueId());
        if (quester == null) {
            return;
        }
        for (final Quest q : quester.getCurrentQuests().keySet()) {
            final Player p = quester.getPlayer();
            final Map<String, Object> dataMap = getDataForPlayer(p.getUniqueId(), this, q);
            if (dataMap != null) {
                final String arenaNames = (String)dataMap.getOrDefault("MBW Item Arena", "ANY");
                if (arenaNames == null) {
                    return;
                }
                final String[] spl = arenaNames.split(",");
                for (final String str : spl) {
                    if (event.getArena() == null) {
                        continue;
                    }
                    if (str.equalsIgnoreCase("ANY") || event.getArena().getName().equalsIgnoreCase(str)) {
                        incrementObjective(p.getUniqueId(), this, q, 1);
                        break;
                    }
                }
            }
        }
    }
}
