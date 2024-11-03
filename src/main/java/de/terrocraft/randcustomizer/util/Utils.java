package de.terrocraft.randcustomizer.util;

import de.terrocraft.randcustomizer.RandCustomizer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Utils {



    public static void openEditInventory(Player player, int page) {
        List<ItemStack> materials = (List<ItemStack>) RandCustomizer.materials.getList("materials", new ArrayList<ItemStack>());

        int startIndex = (page - 1) * 54;
        int endIndex = Math.min(startIndex + 54, materials.size());

        if (startIndex >= materials.size()) {
            player.sendMessage("§cNo more items to display on this page.");
            return;
        }

        Inventory editInv = Bukkit.createInventory(null, 54, "§aEdit§7-§eInventory");

        int removeditems = 0;

        for (int i = startIndex; i < endIndex; i++) {
            if (RandCustomizer.BlockPermissions.getString("Blocks." + materials.get(i)) != null){
                if (!player.hasPermission(Objects.requireNonNull(RandCustomizer.BlockPermissions.getString("Blocks." + materials.get(i))))){
                    removeditems++;
                    continue;
                }
            }
            editInv.setItem(i - removeditems - startIndex, materials.get(i));
        }

        addNavigationButtons(player);

        player.openInventory(editInv);
    }

    public static void openSearchInventory(Player player, List<ItemStack> searchResults, Plugin plugin) {
        Inventory searchInventory = Bukkit.createInventory(null, 54, "Search Results");

        int index = 0;
        for (ItemStack item : searchResults) {
            if (index >= 54) break;
            searchInventory.setItem(index, item);
            index++;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            player.openInventory(searchInventory);
        });


    }

    public static ItemStack createNavigationButton(Material material, String displayName, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static void addNavigationButtons(Player player) {
        ItemStack backButton = createNavigationButton(Material.ARROW, "§7Back", "§7Gehe zur vorherigen Seite");
        ItemStack forwardButton = createNavigationButton(Material.ARROW, "§7Forward", "§7Gehe zur nächsten Seite");
        player.getInventory().setItem(9, backButton);
        player.getInventory().setItem(17, forwardButton);
    }

    public static void removeNavigationButtons(Player player) {
        player.getInventory().setItem(9, new ItemStack(Material.AIR));
        player.getInventory().setItem(17, new ItemStack(Material.AIR));
    }


    public static boolean isSearchItem(ItemStack item) {
        if (item == null || item.getType() != Material.PAPER) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && "§aSearch".equals(meta.getDisplayName());
    }

    public static boolean isMaterialItem(ItemStack item) {
        if (item == null || item.getType() != Material.BARREL) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta != null && "§aMaterials".equals(meta.getDisplayName());
    }

    public static boolean isContentOfSetMaterials(ItemStack item) {
        for (ItemStack material : RandCustomizer.materials.getList("materials", new ArrayList<ItemStack>()).toArray(new ItemStack[0])) {
            if (material == null) {
                continue;
            }

            if (material.getType() == item.getType()) {
                return true;
            }

        }
        return false;
    }


    public static List<ItemStack> findMatchingItems(String searchTerm) {
        List<ItemStack> results = new ArrayList<>();
        searchTerm = searchTerm.toLowerCase();

        for (ItemStack material : RandCustomizer.materials.getList("materials", new ArrayList<ItemStack>()).toArray(new ItemStack[0])) {
            if (material == null) {
                continue;
            }

            if (material.toString().toLowerCase().contains(searchTerm)) {
                results.add(material);
            }
        }

        return results;
    }

}
