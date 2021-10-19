package com.pianominecraft.main

import org.bukkit.ChatColor
import org.bukkit.Material

enum class ToolItem(
    var items: HashMap<Material, Int>, var material: Material, var itemname: String, var lore: String,
    var unbreakable: Boolean
) {
    WOOD_PICK(
        object : HashMap<Material, Int>() {
            init {
                put(Material.OAK_LOG, 5)
                put(Material.STICK, 5)
            }
        },
        Material.WOODEN_PICKAXE,
        "${ChatColor.GREEN}나무 곡괭이",
        "${ChatColor.GREEN}돌을 캐기 위한 장비.",
        true
    ),
    WOOD_AXE(
        object : HashMap<Material, Int>() {
            init {
                put(Material.OAK_LOG, 3)
            }
        },
        Material.WOODEN_AXE,
        "${ChatColor.GREEN}나무 도끼",
        "${ChatColor.GREEN}초반에 속도를 증가시켜주는 장비.",
        true
    ),
    STONE_PICK(object : HashMap<Material, Int>() {
        init {
            put(Material.COBBLESTONE, 10)
            put(Material.STICK, 10)
        }
    }, Material.STONE_PICKAXE, "${ChatColor.GREEN}돌 곡괭이", "${ChatColor.GREEN}철을 캐기 위한 장비.", true),
    STONE_AXE(object : HashMap<Material, Int>() {
        init {
            put(Material.COBBLESTONE, 20)
            put(Material.STICK, 15)
        }
    }, Material.STONE_AXE, "${ChatColor.GREEN}돌 도끼", "${ChatColor.GREEN}이제는 퇴물이 된 사기였던 아이템.", true),
    IRON_PICK(
        object : HashMap<Material, Int>() {
            init {
                put(Material.IRON_INGOT, 30)
                put(Material.STICK, 30)
            }
        },
        Material.IRON_PICKAXE,
        "${ChatColor.GREEN}철 곡괭이",
        "${ChatColor.GREEN}금/다이아몬드를 캐기 위한 장비.",
        true
    ),
    DIAMOND_PICK(
        object : HashMap<Material, Int>() {
            init {
                put(Material.DIAMOND, 30)
                put(Material.STICK, 30)
            }
        },
        Material.DIAMOND_PICKAXE,
        "${ChatColor.GREEN}다이아몬드 곡괭이",
        "${ChatColor.GREEN}네더라이트를 캐기 위한 장비.",
        true
    ),
    GOLDEN_AXE(object : HashMap<Material, Int>() {
        init {
            put(Material.GOLD_INGOT, 50)
            put(Material.STICK, 20)
        }
    }, Material.GOLDEN_AXE, "${ChatColor.GREEN}금 도끼", "${ChatColor.GREEN}나무를 캐면 일정 확률로 금을 드랍하는 갓-도끼.", true);
}