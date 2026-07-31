package net.erskrayy.eritemclear.item;

import net.erskrayy.eritemclear.config.Settings;
import net.erskrayy.eritemclear.service.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Item;
import org.bukkit.entity.TextDisplay;

public class Hologram {

    private final Settings settings;

    public Hologram(Settings settings) {
        this.settings = settings;
    }

    public TextDisplay create(Item item, int time, int amount) {
        String text = format(time, amount);

        return item.getWorld().spawn(item.getLocation(), TextDisplay.class, d -> {
            d.setBillboard(Display.Billboard.CENTER);
            d.setDefaultBackground(false);
            d.setShadowed(true);

            org.bukkit.util.Transformation transform = d.getTransformation();
            transform.getTranslation().add(0f, 0.6f, 0f);

            d.setTransformation(transform);
            d.text(Color.parse(text));
            d.setPersistent(false);
            d.setInvulnerable(true);
        });
    }

    public void updateText(TextDisplay display, int timeLeft, int amount) {
        display.text(Color.parse(format(timeLeft, amount)));
    }

    public void remove(TextDisplay display) {
        if (display != null && !display.isDead()) {
            display.remove();
        }
    }

    private String format(int time, int amount) {
        return settings.getHologramFormat()
                .replace("{time}", String.valueOf(time))
                .replace("{count}", String.valueOf(amount));
    }
}
