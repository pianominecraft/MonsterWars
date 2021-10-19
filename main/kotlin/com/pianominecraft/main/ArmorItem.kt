package com.pianominecraft.main

import org.bukkit.ChatColor
import org.bukkit.Material
import java.util.*

enum class ArmorItem(
    var items: HashMap<Material, Int>, var material: Material, var itemname: String, var lore: String,
    var unbreakable: Boolean
) {
    IRON_BOOTS(
        object : HashMap<Material, Int>() {
            init {
                put(Material.IRON_INGOT, 20)
            }
        },
        Material.IRON_BOOTS,
        "${ChatColor.GREEN}철 신발",
        "${ChatColor.GREEN}초반에 부담 없이 만들 수 있는 장비.",
        true
    ),
    IRON_LEGGINGS(
        object : HashMap<Material, Int>() {
            init {
                put(Material.IRON_INGOT, 35)
            }
        },
        Material.IRON_LEGGINGS,
        "${ChatColor.GREEN}철 바지",
        "${ChatColor.GREEN}초반에 부담 없이 만들 수 있는 장비.",
        true
    ),
    IRON_CHESPLATE(
        object : HashMap<Material, Int>() {
            init {
                put(Material.IRON_INGOT, 40)
            }
        },
        Material.IRON_CHESTPLATE,
        "${ChatColor.GREEN}철 갑옷",
        "${ChatColor.GREEN}초반에 부담 없이 만들 수 있는 장비.",
        true
    ),
    IRON_HELMET(
        object : HashMap<Material, Int>() {
            init {
                put(Material.IRON_INGOT, 25)
            }
        },
        Material.IRON_HELMET,
        "${ChatColor.GREEN}철 모자",
        "${ChatColor.GREEN}초반에 부담 없이 만들 수 있는 장비.",
        true
    ),
    DIAMOND_BOOTS(
        object : HashMap<Material, Int>() {
            init {
                put(Material.DIAMOND, 15)
            }
        },
        Material.DIAMOND_BOOTS,
        "${ChatColor.GREEN}다이아몬드 신발",
        "${ChatColor.GREEN}왠만해선 죽지 않도록 강화된 장비.",
        true
    ),
    DIAMOND_LEGGINGS(
        object : HashMap<Material, Int>() {
            init {
                put(Material.DIAMOND, 30)
            }
        },
        Material.DIAMOND_LEGGINGS,
        "${ChatColor.GREEN}다이아몬드 바지",
        "${ChatColor.GREEN}왠만해선 죽지 않도록 강화된 장비.",
        true
    ),
    DIAMOND_CHESTPLATE(
        object : HashMap<Material, Int>() {
            init {
                put(Material.DIAMOND, 35)
            }
        },
        Material.DIAMOND_CHESTPLATE,
        "${ChatColor.GREEN}다이아몬드 갑옷",
        "${ChatColor.GREEN}왠만해선 죽지 않도록 강화된 장비.",
        true
    ),
    DIAMOND_HELMET(
        object : HashMap<Material, Int>() {
            init {
                put(Material.DIAMOND, 20)
            }
        },
        Material.DIAMOND_HELMET,
        "${ChatColor.GREEN}다이아몬드 모자",
        "${ChatColor.GREEN}왠만해선 죽지 않도록 강화된 장비.",
        true
    ),
    NETHERITE_BOOTS(
        object : HashMap<Material, Int>() {
            init {
                put(Material.NETHERITE_INGOT, 10)
            }
        },
        Material.NETHERITE_BOOTS,
        "${ChatColor.GREEN}네더라이트 신발",
        "${ChatColor.GREEN}어떠한 상황에서도 죽지 않도록 초강화된 장비.",
        true
    ),
    NETHERITE_LEGGINGS(
        object : HashMap<Material, Int>() {
            init {
                put(Material.NETHERITE_INGOT, 17)
            }
        },
        Material.NETHERITE_LEGGINGS,
        "${ChatColor.GREEN}네더라이트 바지",
        "${ChatColor.GREEN}어떠한 상황에서도 죽지 않도록 초강화된 장비.",
        true
    ),
    NETHERITE_CHESTPLATE(
        object : HashMap<Material, Int>() {
            init {
                put(Material.NETHERITE_INGOT, 20)
            }
        },
        Material.NETHERITE_CHESTPLATE,
        "${ChatColor.GREEN}네더라이트 갑옷",
        "${ChatColor.GREEN}어떠한 상황에서도 죽지 않도록 초강화된 장비.",
        true
    ),
    NETHERITE_HELMET(
        object : HashMap<Material, Int>() {
            init {
                put(Material.NETHERITE_INGOT, 12)
            }
        },
        Material.NETHERITE_HELMET,
        "${ChatColor.GREEN}네더라이트 모자",
        "${ChatColor.GREEN}어떠한 상황에서도 죽지 않도록 초강화된 장비.",
        true
    ),
    GOLDEN_CHESTPLATE(
        object : HashMap<Material, Int>() {
            init {
                put(Material.GOLD_INGOT, 300)
            }
        },
        Material.GOLDEN_CHESTPLATE,
        "${ChatColor.GREEN}금 갑옷",
        "${ChatColor.GREEN}입으면 방어력과 공격력이 대폭 증가하는 장비.",
        true
    );
}