package com.pixvoxgames.ld36;

public class Player {
    public static final int MAX_INVENTORY_ITEMS = 10;

    private Entity[] inventory = new Entity[MAX_INVENTORY_ITEMS];

    public Player() {

    }

    private int findFreeSlot() {
        for (int i = 0; i < 10; i++) {
            if (inventory[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public boolean pickupItem(Entity entity) {
        if (!entity.isPickeable()) {
            return false;
        }
        int slot = findFreeSlot();
        if (slot < 0) {
            return false;
        }
        if (entity.onPickup()) {
            storeItem(slot, entity);
        }
        return true;
    }

    public String lookAt(Entity entity) {
        entity.onLookAt();
        return entity.getHint();
    }

    public void moveTo(int endpoint) {

    }

    private void storeItem(int slot, Entity entity) {
        inventory[slot] = entity;
    }
}
