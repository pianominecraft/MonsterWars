package com.pianominecraft.main

import org.bukkit.Material
import org.bukkit.entity.HumanEntity

enum class MaterialShopItem(var price: Int, var material: Material, var autoSell: ArrayList<HumanEntity>) {
    OAK_LOG(120, Material.OAK_LOG, arrayListOf()),
    STICK(10, Material.STICK, arrayListOf()),
    STONE(50, Material.COBBLESTONE, arrayListOf()),
    IRON(200, Material.IRON_INGOT, arrayListOf()),
    GOLD(3000, Material.GOLD_INGOT, arrayListOf()),
    DIAMOND(600, Material.DIAMOND, arrayListOf()),
    NETHERITE(15000, Material.NETHERITE_INGOT, arrayListOf());
}