package com.jostb.bedwars;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.meta.PotionMeta;
import java.util.ArrayList;
import java.util.List;

public class Shop implements Listener {
    private static final List<List<ShopItem>> itemsByRow = List.of(
        List.of(
            new ShopItem(Material.WHITE_WOOL, 16, Material.IRON_INGOT, 4),
            new ShopItem(Material.WHITE_CONCRETE, 16, Material.IRON_INGOT, 12),
            new ShopItem(Material.END_STONE, 12, Material.IRON_INGOT, 24),
            new ShopItem(Material.ACACIA_PLANKS, 16, Material.GOLD_INGOT, 4),
                new ShopItem(Material.LADDER, 8, Material.IRON_INGOT, 4),
            new ShopItem(Material.OBSIDIAN, 4, Material.EMERALD, 4)
        ),
        List.of(
            new ShopItem(Material.STONE_SWORD, 1, Material.IRON_INGOT, 10),
            new ShopItem(Material.IRON_SWORD, 1, Material.GOLD_INGOT, 7),
            new ShopItem(Material.DIAMOND_SWORD, 1, Material.EMERALD, 4),
            new ShopItem(new ItemStack(Material.STICK) {{
                ItemMeta meta = getItemMeta();
                if (meta != null) {
                    meta.addEnchant(org.bukkit.enchantments.Enchantment.KNOCKBACK, 1, true);
                    setItemMeta(meta);
                }
            }}, Material.GOLD_INGOT, 5),
            new ShopItem(Material.BOW, 1, Material.GOLD_INGOT, 12),
            new ShopItem(new ItemStack(Material.BOW) {{
                ItemMeta meta = getItemMeta();
                if (meta != null) {
                    meta.addEnchant(Enchantment.POWER, 1, true);
                    setItemMeta(meta);
                }
            }}, Material.GOLD_INGOT, 20),
            new ShopItem(new ItemStack(Material.BOW) {{
                ItemMeta meta = getItemMeta();
                if (meta != null) {
                    meta.addEnchant(Enchantment.POWER, 1, true);
                    meta.addEnchant(Enchantment.PUNCH, 1, true);
                    setItemMeta(meta);
                }
            }}, Material.EMERALD, 6),
                new ShopItem(Material.ARROW, 6, Material.GOLD_INGOT, 2)
        ),
        List.of(
                new ShopItem(Material.CHAINMAIL_BOOTS, 1, Material.IRON_INGOT, 24),
                new ShopItem(Material.IRON_BOOTS, 1, Material.GOLD_INGOT, 12),
                new ShopItem(Material.DIAMOND_BOOTS, 1, Material.EMERALD, 6),
            new ShopItem(Material.WOODEN_PICKAXE, 1, Material.IRON_INGOT, 10),
            new ShopItem(Material.STONE_PICKAXE, 1, Material.IRON_INGOT, 20),
            new ShopItem(Material.IRON_PICKAXE, 1, Material.GOLD_INGOT, 8),
            new ShopItem(Material.DIAMOND_PICKAXE, 1, Material.GOLD_INGOT, 12),
            new ShopItem(Material.SHEARS, 1, Material.IRON_INGOT, 20)
        ),
        List.of(
                new ShopItem(Material.WOODEN_AXE, 1, Material.IRON_INGOT, 10),
                new ShopItem(Material.STONE_AXE, 1, Material.IRON_INGOT, 20),
                new ShopItem(Material.IRON_AXE, 1, Material.GOLD_INGOT, 8),
                new ShopItem(Material.DIAMOND_AXE, 1, Material.GOLD_INGOT, 12)
        ),
        List.of(
            new ShopItem(Material.GOLDEN_APPLE, 1, Material.GOLD_INGOT, 3),
            new ShopItem(Material.ENDER_PEARL, 1, Material.EMERALD, 4),
            new ShopItem(Material.WATER_BUCKET, 1, Material.GOLD_INGOT, 2),
            new ShopItem(Material.FIRE_CHARGE, 1, Material.IRON_INGOT, 40),
            new ShopItem(Material.TNT, 1, Material.GOLD_INGOT, 4),
                new ShopItem(Material.SNOWBALL, 1, Material.IRON_INGOT, 24),
            new ShopItem(Material.SPONGE, 4, Material.GOLD_INGOT, 4)
        ),
        List.of(
            new ShopItem(new ItemStack(Material.POTION) {{
                PotionMeta meta = (PotionMeta) getItemMeta();
                if (meta != null) {
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 45, 0), true);
                    meta.customName(Component.text("SPEED").color(NamedTextColor.AQUA).decoration(TextDecoration.BOLD, true));
                    setItemMeta(meta);
                }
            }}, Material.EMERALD, 1),
            new ShopItem(new ItemStack(Material.POTION) {{
                PotionMeta meta = (PotionMeta) getItemMeta();
                if (meta != null) {
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 20 * 45, 0), true);
                    meta.customName(Component.text("JUMP BOOST").color(NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true));
                    setItemMeta(meta);
                }
            }}, Material.EMERALD, 1),
            new ShopItem(new ItemStack(Material.POTION) {{
                PotionMeta meta = (PotionMeta) getItemMeta();
                if (meta != null) {
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 30, 0), true);
                    meta.customName(Component.text("INVISIBILITY").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, true));
                    setItemMeta(meta);
                }
            }}, Material.EMERALD, 2)
        )
    );

    public Shop(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static class ShopItem {
        public ItemStack itemStack;
        public Material currency;
        public int cost;

        public ShopItem(Material item, int amount, Material currency, int cost) {
            this.itemStack = new ItemStack(item, amount);
            this.currency = currency;
            this.cost = cost;
        }

        public ShopItem(ItemStack itemStack, Material currency, int cost) {
            this.itemStack = itemStack;
            this.currency = currency;
            this.cost = cost;
        }
    }

    public static void openShopGUI(Player player) {
        int rowCount = itemsByRow.size();
        int size = Math.min(6, rowCount) * 9; // Maximal 6 Reihen = 54 Slots
        Inventory gui = Bukkit.createInventory(null, size, "Shop");

        for (int row = 0; row < itemsByRow.size(); row++) {
            List<ShopItem> rowItems = itemsByRow.get(row);
            for (int col = 0; col < rowItems.size(); col++) {
                int slot = row * 9 + col;
                if (slot >= gui.getSize()) break;

                ShopItem shopItem = rowItems.get(col);
                ItemStack itemStack = shopItem.itemStack.clone();
                ItemMeta meta = itemStack.getItemMeta();
                if (meta != null) {
                    NamedTextColor currencyColor;
                    String currencyName;
                    switch (shopItem.currency) {
                        case IRON_INGOT -> {
                            currencyColor = NamedTextColor.DARK_GRAY;
                            currencyName = "Iron";
                        }
                        case GOLD_INGOT -> {
                            currencyColor = NamedTextColor.GOLD;
                            currencyName = "Gold";
                        }
                        case DIAMOND -> {
                            currencyColor = NamedTextColor.AQUA;
                            currencyName = "Diamond(s)";
                        }
                        case EMERALD -> {
                            currencyColor = NamedTextColor.GREEN;
                            currencyName = "Emerald(s)";
                        }
                        default -> {
                            currencyColor = NamedTextColor.YELLOW;
                            currencyName = shopItem.currency.name();
                        }
                    }

                    List<Component> lore = List.of(
                            Component.text("Buy for ", currencyColor)
                                    .append(Component.text(shopItem.cost + " ", currencyColor))
                                    .append(Component.text(currencyName, currencyColor))
                    );
                    meta.lore(lore);
                }
                itemStack.setItemMeta(meta);
                gui.setItem(slot, itemStack);
            }
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onVillagerInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Villager villager) {
            event.setCancelled(true);
            Shop.openShopGUI(event.getPlayer());
        }
    }

    @EventHandler
    public void onVillagerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Villager villager) {
            if (event instanceof EntityDamageByEntityEvent damageByEntity) {
                if (damageByEntity.getDamager() instanceof Player player && player.getGameMode() == org.bukkit.GameMode.CREATIVE) {
                    return;
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVillagerMove(EntityTeleportEvent event) {
        if (event.getEntity() instanceof Villager) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Shop")) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        int slot = event.getRawSlot();
        int row = slot / 9;
        int col = slot % 9;
        if (row >= itemsByRow.size() || col >= itemsByRow.get(row).size()) return;
        ShopItem item = itemsByRow.get(row).get(col);

        if (!hasEnoughCurrency(player, item)) {
            player.sendMessage(Component.text("You can't afford this item!", NamedTextColor.RED));
            return;
        }
        ItemStack resultItem = item.itemStack.clone();
        Material type = resultItem.getType();
        if (type == Material.CHAINMAIL_BOOTS || type == Material.IRON_BOOTS || type == Material.DIAMOND_BOOTS) {
            Material[] armorPriority = {Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS};
            int newPriority = java.util.Arrays.asList(armorPriority).indexOf(type);
            Material currentBoots = player.getInventory().getBoots() != null ? player.getInventory().getBoots().getType() : null;
            int currentPriority = currentBoots != null ? java.util.Arrays.asList(armorPriority).indexOf(currentBoots) : -1;
            if (currentPriority >= newPriority) {
                player.sendMessage(Component.text("You have already bought this item", NamedTextColor.RED));
                return;
            }
            deductCurrency(player, item);
            player.getInventory().setBoots(new ItemStack(type));
            Material leggings = switch (type) {
                case CHAINMAIL_BOOTS -> Material.CHAINMAIL_LEGGINGS;
                case IRON_BOOTS -> Material.IRON_LEGGINGS;
                case DIAMOND_BOOTS -> Material.DIAMOND_LEGGINGS;
                default -> null;
            };
            if (leggings != null) {
                player.getInventory().setLeggings(new ItemStack(leggings));
            }
            player.sendMessage(Component.text("Thanks for your purchase!", NamedTextColor.GREEN));
        } else {
            deductCurrency(player, item);
            player.getInventory().addItem(resultItem);
            player.sendMessage(Component.text("Thanks for your purchase!", NamedTextColor.GREEN));
        }
    }

    private ShopItem findShopItem(Material type) {
        for (List<ShopItem> row : itemsByRow) {
            for (ShopItem item : row) {
                if (item.itemStack.getType() == type) return item;
            }
        }
        return null;
    }

    private boolean hasEnoughCurrency(Player player, ShopItem item) {
        int count = 0;
        for (ItemStack content : player.getInventory().getContents()) {
            if (content != null && content.getType() == item.currency) {
                count += content.getAmount();
            }
        }
        return count >= item.cost;
    }

    private void deductCurrency(Player player, ShopItem item) {
        int remaining = item.cost;
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack content = player.getInventory().getItem(i);
            if (content != null && content.getType() == item.currency) {
                int amt = content.getAmount();
                if (amt <= remaining) {
                    player.getInventory().setItem(i, null);
                    remaining -= amt;
                } else {
                    content.setAmount(amt - remaining);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onVillagerSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Villager villager) {
            if (villager.getEntitySpawnReason() == org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) {
                villager.setAI(false);
                villager.setCollidable(false);
                villager.setInvulnerable(true);
            }
        }
    }
}
