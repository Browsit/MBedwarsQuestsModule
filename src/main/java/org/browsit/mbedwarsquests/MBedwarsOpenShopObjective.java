package org.browsit.mbedwarsquests;

import de.marcely.bedwars.api.event.player.PlayerOpenShopEvent;
import me.pikamug.quests.module.BukkitCustomObjective;
import me.pikamug.quests.player.Quester;
import me.pikamug.quests.quests.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class MBedwarsOpenShopObjective extends BukkitCustomObjective implements Listener {

    public MBedwarsOpenShopObjective() {
        setName("MBedwars Open Shop Objective");
        setAuthor("Browsit, LLC");
        setItem("CHEST", (short)0);
        setShowCount(true);
        addStringPrompt("MBW Open Obj", "Set a name for the objective", "Open shop");
        addStringPrompt("MBW Open Arena", "Enter arena names, separating each one by a comma", "ANY");
        setCountPrompt("Set the number of times to open shop");
        setDisplay("%MBW Open Obj% in arena %MBW Open Arena%: %count%");
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
    public void onOpenShop(final PlayerOpenShopEvent event) {
        if (MBedwarsModule.getQuests() == null) {
            return;
        }
        final Player opener = event.getPlayer();
        final Quester quester = MBedwarsModule.getQuests().getQuester(opener.getUniqueId());
        if (quester == null) {
            return;
        }
        for (final Quest q : quester.getCurrentQuests().keySet()) {
            final Player p = quester.getPlayer();
            final Map<String, Object> dataMap = getDataForPlayer(p.getUniqueId(), this, q);
            if (dataMap != null) {
                final String arenaNames = (String)dataMap.getOrDefault("MBW Open Arena", "ANY");
                if (arenaNames == null) {
                    return;
                }
                final String[] spl = arenaNames.split(",");
                for (final String str : spl) {
                    if (str.equalsIgnoreCase("ANY") || event.getArena().getName().equalsIgnoreCase(str)) {
                        incrementObjective(p.getUniqueId(), this, q, 1);
                        break;
                    }
                }
            }
        }
    }
}
