package com.pianominecraft.main

import org.bukkit.ChatColor
import org.bukkit.Material

enum class MisItem(
    var items: HashMap<Material, Int>, var material: Material, var itemname: String, var lore: String,
    var unbreakable: Boolean
) {
    SHIELD(object : HashMap<Material, Int>() {
        init {
            put(Material.IRON_INGOT, 64)
            put(Material.OAK_LOG, 64)
        }
    }, Material.SHIELD, "${ChatColor.GREEN}방패", "${ChatColor.GREEN}후반 필수 장비.", true),
    GOLDEN_APPLE(
        object : HashMap<Material, Int>() {
            init {
                put(Material.GOLD_INGOT, 2)
            }
        },
        Material.GOLDEN_APPLE,
        "${ChatColor.GREEN}황금 사과",
        "${ChatColor.GREEN}적당히 사용하기 좋은 회복 아이템.",
        false
    ),
    ENC_GOLDEN_APPLE(
        object : HashMap<Material, Int>() {
            init {
                put(Material.GOLD_INGOT, 25)
            }
        },
        Material.ENCHANTED_GOLDEN_APPLE,
        "${ChatColor.GREEN}마법이 부여된 황금사과",
        "${ChatColor.GREEN}비싸지만 효력은 끝내주는 회복 아이템.",
        false
    );
}