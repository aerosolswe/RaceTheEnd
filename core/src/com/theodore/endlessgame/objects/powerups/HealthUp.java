package com.theodore.endlessgame.objects.powerups;

import com.theodore.endlessgame.objects.Player;

public class HealthUp extends PowerUp {
    public HealthUp(float x, float y) {
        super("health", x, y);
    }

    @Override
    public void activate(Player player) {
        player.damage(300);
    }
}
