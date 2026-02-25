package ferrin.dupe.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public boolean isOffCooldown(UUID uuid, int cooldownSeconds) {
        if (!cooldowns.containsKey(uuid)) return true;
        long secondsLeft = ((cooldowns.get(uuid) / 1000) + cooldownSeconds) - (System.currentTimeMillis() / 1000);
        return secondsLeft <= 0;
    }

    public void setCooldown(UUID uuid) {
        cooldowns.put(uuid, System.currentTimeMillis());
    }
}