package org.makarfp.griefnotify.data;

import org.bukkit.Material;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MaterialData {

    private static final Set<Material> trackedMaterials = new HashSet<>();

    public static void init() {
        Collections.addAll(trackedMaterials,
                Material.TNT,
                Material.DISPENSER,
                Material.SOUL_SAND,
                Material.PISTON,
                Material.STICKY_PISTON,
                Material.LEVER,
                Material.REDSTONE_TORCH,
                Material.REDSTONE,
                Material.WITHER_SKELETON_SKULL,

                //buttons
                Material.CHERRY_BUTTON,
                Material.PALE_OAK_BUTTON,
                Material.SPRUCE_BUTTON,
                Material.BIRCH_BUTTON,
                Material.POLISHED_BLACKSTONE_BUTTON,
                Material.OAK_BUTTON,
                Material.BAMBOO_BUTTON,
                Material.MANGROVE_BUTTON,
                Material.JUNGLE_BUTTON,
                Material.STONE_BUTTON,
                Material.WARPED_BUTTON,
                Material.CRIMSON_BUTTON,
                Material.DARK_OAK_BUTTON,
                Material.ACACIA_BUTTON,

                //pressure plates
                Material.BAMBOO_PRESSURE_PLATE,
                Material.CRIMSON_PRESSURE_PLATE,
                Material.WARPED_PRESSURE_PLATE,
                Material.STONE_PRESSURE_PLATE,
                Material.POLISHED_BLACKSTONE_PRESSURE_PLATE,
                Material.OAK_PRESSURE_PLATE,
                Material.SPRUCE_PRESSURE_PLATE,
                Material.BIRCH_PRESSURE_PLATE,
                Material.JUNGLE_PRESSURE_PLATE,
                Material.ACACIA_PRESSURE_PLATE,
                Material.DARK_OAK_PRESSURE_PLATE,
                Material.MANGROVE_PRESSURE_PLATE,
                Material.CHERRY_PRESSURE_PLATE,
                Material.PALE_OAK_PRESSURE_PLATE,
                Material.LIGHT_WEIGHTED_PRESSURE_PLATE,
                Material.HEAVY_WEIGHTED_PRESSURE_PLATE,

                //other redstone mechanism
                Material.REDSTONE_BLOCK,
                Material.TNT_MINECART,
                Material.TRAPPED_CHEST,
                Material.TARGET,
                Material.SCULK_SENSOR,
                Material.CALIBRATED_SCULK_SENSOR,
                Material.DAYLIGHT_DETECTOR,
                Material.OBSERVER,
                Material.REPEATER,
                Material.COMPARATOR,
                Material.TRIPWIRE_HOOK,

                //Physical blocks
                Material.WHITE_CONCRETE_POWDER,
                Material.ORANGE_CONCRETE_POWDER,
                Material.MAGENTA_CONCRETE_POWDER,
                Material.LIGHT_BLUE_CONCRETE_POWDER,
                Material.YELLOW_CONCRETE_POWDER,
                Material.LIME_CONCRETE_POWDER,
                Material.PINK_CONCRETE_POWDER,
                Material.GRAY_CONCRETE_POWDER,
                Material.LIGHT_GRAY_CONCRETE_POWDER,
                Material.CYAN_CONCRETE_POWDER,
                Material.PURPLE_CONCRETE_POWDER,
                Material.BLUE_CONCRETE_POWDER,
                Material.BROWN_CONCRETE_POWDER,
                Material.GREEN_CONCRETE_POWDER,
                Material.RED_CONCRETE_POWDER,
                Material.BLACK_CONCRETE_POWDER,
                Material.SAND,
                Material.RED_SAND,
                Material.GRAVEL,
                Material.ANVIL,
                Material.CHIPPED_ANVIL,
                Material.DAMAGED_ANVIL,

                Material.WATER,
                Material.LAVA
        );
    }

    public static boolean isTracked(Material material) {
        return trackedMaterials.contains(material);
    }

    public static Set<Material> getTrackedMaterials() {
        return Collections.unmodifiableSet(trackedMaterials);
    }
}
