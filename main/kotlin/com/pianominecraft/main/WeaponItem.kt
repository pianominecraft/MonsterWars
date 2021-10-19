package com.pianominecraft.main

import org.bukkit.ChatColor
import org.bukkit.Material
import java.util.*

enum class WeaponItem (
    var items: HashMap<Material, Int>, var material: Material, var itemname: String, var lore: String,
    var unbreakable: Boolean) {
    WOOD_SWORD(
        object : HashMap<Material, Int>() {
            init {
                put(Material.OAK_LOG, 1)
            }
        },
        Material.WOODEN_SWORD,
        "${ChatColor.GREEN}나무 검",
        "${ChatColor.GREEN}빨리 만들어 몬스터들을 잡아야 하는 장비.",
        true
    ),
    STONE_SWORD(
        object : HashMap<Material, Int>() {
            init {
                put(Material.COBBLESTONE, 10)
                put(Material.STICK, 5)
            }
        },
        Material.STONE_SWORD,
        "${ChatColor.GREEN}돌 검",
        "${ChatColor.GREEN}초반에 빠르게 만들 수 있는 장비.",
        true
    ),
    IRON_SWORD(
        object : HashMap<Material, Int>() {
            init {
                put(Material.IRON_INGOT, 10)
                put(Material.STICK, 5)
            }
        },
        Material.IRON_SWORD,
        "${ChatColor.GREEN}철 검",
        "${ChatColor.GREEN}몬스터를 빨리 죽일 수 있는 장비.",
        true
    ),
    DIAMOND_SWORD(
        object : HashMap<Material, Int>() {
            init {
                put(Material.DIAMOND, 8)
                put(Material.STICK, 5)
            }
        },
        Material.DIAMOND_SWORD,
        "${ChatColor.GREEN}다이아몬드 검",
        "${ChatColor.GREEN}매우 강력한 공격력을 자랑하는 고급 장비.",
        true
    ),
    NETHERITE_SWORD(
        object : HashMap<Material, Int>() {
            init {
                put(Material.NETHERITE_INGOT, 6)
                put(Material.STICK, 5)
            }
        },
        Material.NETHERITE_SWORD,
        "${ChatColor.GREEN}네더라이트 검",
        "${ChatColor.GREEN}몬스터를 박멸할 계획으로 초강화된 장비.",
        true
    ),
    GOLDEN_SWORD(
    object : HashMap<Material, Int>() {
        init {
            put(Material.GOLD_INGOT, 200)
            put(Material.STICK, 10)
        }
    },
    Material.GOLDEN_SWORD,
    "${ChatColor.GREEN}금 검",
    "${ChatColor.GREEN}그냥 설명이 필요 없는 장비.",
    true
    )
}
