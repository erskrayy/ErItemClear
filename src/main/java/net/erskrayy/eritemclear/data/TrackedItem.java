package net.erskrayy.eritemclear.data;

import org.bukkit.Chunk;
import org.bukkit.entity.Item;
import org.bukkit.entity.TextDisplay;

public class TrackedItem {

    private final TextDisplay display;
    private final Item item;
    private int timeLeft;
    private final Chunk chunk;

    public TrackedItem(TextDisplay display, Item item, int timeLeft, Chunk chunk) {
        this.display = display;
        this.item = item;
        this.timeLeft = timeLeft;
        this.chunk = chunk;
    }

    public TextDisplay getDisplay() {
        return display;
    }

    public Item getItem() {
        return item;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void decrementTime() {
        this.timeLeft--;
    }
}
