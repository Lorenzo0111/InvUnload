package de.jeff_media.InvUnload;

import de.jeff_media.InvUnload.Hooks.ExecutableItemsWrapper;
import de.jeff_media.InvUnload.Hooks.ItemsAdderWrapper;
import de.jeff_media.InvUnload.utils.EnumUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class BlockUtils {

    private static final EnumSet<Material> CONTAINER_TYPES;
    private static final List<String> CONTAINER_NAMES = Arrays.asList("(.*)BARREL$", "(.*)CHEST$", "^SHULKER_BOX$", "^(.*)_SHULKER_BOX$");

    static {
        CONTAINER_TYPES = EnumUtils.getEnumsFromRegexList(Material.class, CONTAINER_NAMES);
    }

    final Main main;

    BlockUtils(Main main) {
        this.main = main;
    }

    static List<Block> findBlocksInRadius(Location loc, int radius) {
        BoundingBox box = BoundingBox.of(loc, radius, radius, radius);
        //List<BlockVector> blocks = de.jeff_media.jefflib.BlockUtils.getBlocks(loc.getWorld(), box, true, blockData -> isChestLikeBlock(blockData.getMaterial()));
        List<Chunk> chunks = getChunks(loc.getWorld(), box, true);
        List<Block> blocks = new ArrayList<>();
        for (Chunk chunk : chunks) {
            for (BlockState state : chunk.getTileEntities()) {
                if (state instanceof Container && isChestLikeBlock(state.getType())) {
                    if (state.getLocation().distanceSquared(loc) <= radius * radius) {

                        // Only chests that can be opened
                        if (Main.getInstance().getConfig().getBoolean("ignore-blocked-chests", false)) {
                            Block above = state.getBlock().getRelative(BlockFace.UP);
                            if (state.getType() == Material.CHEST && above.getType().isSolid() && above.getType().isOccluding()) {
                                continue;
                            }
                        }

                        blocks.add(state.getBlock());
                    }
                }
            }
        }
        return blocks;
    }

    static List<Block> findChestsInRadius(Location loc, int radius) {
        // Todo
        return findBlocksInRadius(loc, radius);
    }

    public static boolean isChestLikeBlock(Material material) {
        return CONTAINER_TYPES.contains(material);
    }

    static boolean doesChestContain(Inventory inv, ItemStack item) {
        Main main = Main.getInstance();
        ItemsAdderWrapper itemsAdder = main.getItemsAdderWrapper();
        ExecutableItemsWrapper executableItems = main.getExecutableItemsWrapper();
        boolean useItemsAdder = main.getConfig().getBoolean("use-itemsadder");
        boolean useExecutableItems = main.getConfig().getBoolean("use-executable-items");
        for (ItemStack otherItem : inv.getContents()) {
            if (otherItem == null) continue;
            if (otherItem.getType() == item.getType()) {

                if (!main.getEnchantmentUtils().hasMatchingEnchantments(item, otherItem)) continue;

                if (useItemsAdder) {

                    // Item ist NOT ItemsAdder item
                    if (!itemsAdder.isItemsAdderItem(item)) {

                        // Only return true if otherItem also is NOT ItemsAdder item
                        if (itemsAdder.isItemsAdderItem(otherItem)) {
                            continue;
                        } else {
                            return true;
                        }
                    }

                    // Item IS ItemsAdder item
                    else {
                        // But other Item is not
                        if (!itemsAdder.isItemsAdderItem(otherItem)) {
                            continue;
                        }
                        // Both are ItemsAdder items
                        else {
                            if (itemsAdder.getItemsAdderName(item).equals(itemsAdder.getItemsAdderName(otherItem))) {
                                return true;
                            } else {
                                continue;
                            }
                        }
                    }
                } else if (useExecutableItems) {
                    // Item ist NOT ItemsAdder item
                    if (!executableItems.isExecutableItem(item)) {

                        // Only return true if otherItem also is NOT ItemsAdder item
                        if (executableItems.isExecutableItem(otherItem)) {
                            continue;
                        } else {
                            return true;
                        }
                    }

                    // Item IS ItemsAdder item
                    else {
                        // But other Item is not
                        if (!executableItems.isExecutableItem(otherItem)) {
                            continue;
                        }
                        // Both are ItemsAdder items
                        else {
                            if (executableItems.getExecutableItemsName(item).equals(itemsAdder.getItemsAdderName(otherItem))) {
                                return true;
                            } else {
                                continue;
                            }
                        }
                    }
                }

                return true;
            }
        }
        return false;
    }

    static void sortBlockListByDistance(List<Block> blocks, Location loc) {
        blocks.sort((b1, b2) -> {
            if (b1.getLocation().distance(loc) > b2.getLocation().distance(loc)) {
                return 1;
            }
            return -1;
        });
    }

    static Location getCenterOfBlock(Block block) {
        Location loc = block.getLocation();
        if (block.getState() instanceof Chest
                && ((Chest) block.getState()).getInventory().getHolder() instanceof DoubleChest) {
            DoubleChest doubleChest = (DoubleChest) ((Chest) block.getState()).getInventory().getHolder();
            DoubleChestInventory doubleChestInv = (DoubleChestInventory) doubleChest.getInventory();
            loc = doubleChestInv.getLeftSide().getLocation().add(doubleChestInv.getRightSide().getLocation()).multiply(0.5);
        }
        loc.add(new Vector(0.5, 1, 0.5));
        return loc;
    }

    static int doesChestContainCount(Inventory inv, Material mat) {
        int count = 0;
        for (ItemStack item : inv.getContents()) {
            if (item == null) continue;
            if (item.getType() == mat) {
                count += item.getAmount();
            }
        }
        return count;
    }

    /**
     * Gets all {@link Chunk}s that are inside or intersect the given {@link BoundingBox}
     *
     * @param world            World to check for
     * @param box              BoundingBox to check for
     * @param onlyLoadedChunks When true, only returns already loaded chunks. When false, this will force load chunks and return those too
     * @return List of all chunks that are inside or intersect the given BoundingBox
     *
     * @author <a href="https://github.com/mfnalex/JeffLib/blob/master/core/src/main/java/com/jeff_media/jefflib/BlockUtils.java">mfnalex</a>
     */
    public static List<Chunk> getChunks(final World world, final BoundingBox box, final boolean onlyLoadedChunks) {
        final int minX = (int) box.getMinX() >> 4;
        final int maxX = (int) box.getMaxX() >> 4;
        final int minZ = (int) box.getMinZ() >> 4;
        final int maxZ = (int) box.getMaxZ() >> 4;

        final List<Chunk> chunks = new ArrayList<>();
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (!onlyLoadedChunks || world.isChunkLoaded(x, z)) {
                    final Chunk chunk = world.getChunkAt(x, z);
                    chunks.add(chunk);
                }
            }
        }
        return chunks;
    }
}
