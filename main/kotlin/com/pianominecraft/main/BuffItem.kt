package com.pianominecraft.main

import org.bukkit.ChatColor
import org.bukkit.Material

enum class BuffItem(var price: IntArray, var material: Material, var lore: String) {
    EFFICIENCY(
        intArrayOf(20, 40, 60, 80, 100, 120, 150),
        Material.DIAMOND_PICKAXE,
        ChatColor.GREEN.toString() + "돈이 생명! 즉 파밍이 생명!"
    ),
    SHARPNESS(
        intArrayOf(50, 100, 150, 200, 250, 320, 400),
        Material.DIAMOND_SWORD,
        ChatColor.GREEN.toString() + "체력이 점점 많아지는 몬스터들을 위한 선물!"
    ),
    PROTECTION(
        intArrayOf(50, 100, 150, 200, 250, 320, 400),
        Material.DIAMOND_CHESTPLATE,
        ChatColor.GREEN.toString() + "오래 버티는 것이 하트를 아끼는 비법!"
    ),
    MONSTER_HEALTH(
        intArrayOf(50, 70, 90, 110, 130, 200, 300),
        Material.NETHERITE_CHESTPLATE,
        ChatColor.GREEN.toString() + "몬스터들이 죽지 않는다면 적은 무슨 기분일까?"
    ),
    MONSTER_DAMAGE(
        intArrayOf(50, 80, 110, 140, 170, 250, 400),
        Material.NETHERITE_SWORD,
        ChatColor.GREEN.toString() + "넌 강해졌다! 돌격해!"
    ),
    FORTUNE(
        intArrayOf(30, 60, 90, 120, 150, 180, 210),
        Material.DIAMOND,
        "${ChatColor.GREEN}다이아를 모아서 부자가 될거야!"
    );

    var blue = 0
    var red = 0
    var yellow = 0
    var green = 0
}