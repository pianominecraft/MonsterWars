package com.pianominecraft.main

import org.bukkit.entity.EntityType

enum class MonsterItem(val price: Int, val entityType: EntityType, var summoned: Int) {
    ZOMBIE(500, EntityType.ZOMBIE, 0),
    SILVERFISH(200, EntityType.SILVERFISH, 0),
    SKELETON(500, EntityType.SKELETON, 0),
    SPIDER(500, EntityType.SPIDER, 0),
    CREEPER(2000, EntityType.CREEPER, 0),
    PHANTOM(4000, EntityType.PHANTOM, 0),
    WITHER_SKELETON(5000, EntityType.WITHER_SKELETON, 0),
    IRON_GOLEM(30000, EntityType.IRON_GOLEM, 0),
    RAVAGER(30000, EntityType.RAVAGER, 0);
}