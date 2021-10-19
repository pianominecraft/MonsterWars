package com.pianominecraft.main

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*
import java.util.ArrayList

import org.bukkit.inventory.ItemStack
import org.bukkit.entity.EntityType

import org.bukkit.Bukkit
import org.bukkit.ChatColor

object Constant {

    val team by lazy { HashMap<Entity, Int>() }
    val money by lazy { HashMap<Player, Int>() }
    val attacking by lazy { HashMap<Player, Boolean>() }
    val teamChat by lazy { HashMap<CommandSender, Boolean>() }
    val coolTime by lazy { HashMap<Player, Int>() }

    val sharpnessItems = listOf(
        Material.WOODEN_AXE,
        Material.WOODEN_SWORD,
        Material.STONE_AXE,
        Material.STONE_SWORD,
        Material.IRON_SWORD,
        Material.DIAMOND_SWORD,
        Material.NETHERITE_SWORD,
        Material.GOLDEN_SWORD
    )

    val efficiencyItems = listOf(
        Material.WOODEN_PICKAXE,
        Material.WOODEN_AXE,
        Material.STONE_PICKAXE,
        Material.STONE_AXE,
        Material.IRON_PICKAXE,
        Material.DIAMOND_PICKAXE,
        Material.GOLDEN_AXE
    )

    val protectionItems = listOf(
        Material.IRON_BOOTS,
        Material.IRON_LEGGINGS,
        Material.IRON_CHESTPLATE,
        Material.IRON_HELMET,
        Material.DIAMOND_BOOTS,
        Material.DIAMOND_LEGGINGS,
        Material.DIAMOND_CHESTPLATE,
        Material.DIAMOND_HELMET,
        Material.NETHERITE_BOOTS,
        Material.NETHERITE_LEGGINGS,
        Material.NETHERITE_CHESTPLATE,
        Material.NETHERITE_HELMET,
        Material.GOLDEN_CHESTPLATE
    )

    var shop = Bukkit.createInventory(null, 27, "${ChatColor.GREEN}상점")
    var materialShop = Bukkit.createInventory(null, 27, "${ChatColor.GREEN}재료 상점")
    var monsterShop = Bukkit.createInventory(null, 36, "${ChatColor.GREEN}몬스터 상점")
    var blueBuffShop = Bukkit.createInventory(null, 27, "${ChatColor.GREEN}버프 상점")
    var redBuffShop = Bukkit.createInventory(null, 27, "${ChatColor.GREEN}버프 상점")
    var yellowBuffShop = Bukkit.createInventory(null, 27, "${ChatColor.GREEN}버프 상점")
    var greenBuffShop = Bukkit.createInventory(null, 27, "${ChatColor.GREEN}버프 상점")
    var weaponSmithy = Bukkit.createInventory(null, 54, "${ChatColor.GREEN}무기 대장간")
    var armorSmithy = Bukkit.createInventory(null, 54, "${ChatColor.GREEN}갑옷 대장간")
    var toolSmithy = Bukkit.createInventory(null, 54, "${ChatColor.GREEN}도구 대장간")
    var misSmithy = Bukkit.createInventory(null, 54, "${ChatColor.GREEN}기타 아이템 대장간")

    val itemNames = HashMap<Material, String>().apply {
        this[Material.OAK_LOG] = "나무"
        this[Material.STICK] = "막대기"
        this[Material.COBBLESTONE] = "돌"
        this[Material.IRON_INGOT] = "철"
        this[Material.GOLD_INGOT] = "금"
        this[Material.DIAMOND] = "다이아몬드"
        this[Material.NETHERITE_INGOT] = "네더라이트"
    }

    val BUFF_EFFICIENCY = BuffItem.EFFICIENCY
    val BUFF_SHARPNESS = BuffItem.SHARPNESS
    val BUFF_PROTECTION = BuffItem.PROTECTION
    val BUFF_MONSTER_HEALTH = BuffItem.MONSTER_HEALTH
    val BUFF_MONSTER_DAMAGE = BuffItem.MONSTER_DAMAGE
    val BUFF_FORTUNE = BuffItem.FORTUNE

    lateinit var plugin : Main

    var spawn: Location? = null
    var redSpawn: Location? = null
    var blueSpawn : Location? = null
    var yellowSpawn: Location? = null
    var greenSpawn : Location? = null
    var redHeart = 8
    var blueHeart = 8
    var yellowHeart = 8
    var greenHeart = 8
    var redLive = false
    var blueLive = false
    var yellowLive = false
    var greenLive = false
    var leftTeam = 0
    var redMonster = 0
    var blueMonster = 0
    var yellowMonster = 0
    var greenMonster = 0
    var maxSpawn = 50

    var peacefulTime = 14400

    var multi = false
    var testing = false

    val ITEM_VOID = getItemStack(
        Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1, " ",
        ArrayList<String>()
    )

    var started = false

    fun getItemStack(material: Material, amount: Int, name: String?, lore: List<String?>?): ItemStack {
        val item = ItemStack(material, amount)
        val im = item.itemMeta
        im!!.setDisplayName(name)
        im.lore = lore
        item.itemMeta = im
        return item
    }

    fun getUnbreakableItemStack(material: Material, amount: Int, name: String?, lore: List<String?>?): ItemStack {
        val item = ItemStack(material, amount)
        val im = item.itemMeta
        im!!.isUnbreakable = true
        im.setDisplayName(name)
        im.lore = lore
        item.itemMeta = im
        return item
    }

    @Suppress("DEPRECATION")
    fun summonMonsters(et: EntityType, _team: Int, summoned: Int) {
        if (multi) {
            when (_team) {
                1 -> {
                    val e = listOf(
                        blueSpawn!!.world!!.spawnEntity(blueSpawn!!, et),
                        yellowSpawn!!.world!!.spawnEntity(yellowSpawn!!, et),
                        greenSpawn!!.world!!.spawnEntity(greenSpawn!!, et)
                    )
                    redMonster += 3
                }
                2 -> {
                    val e = listOf(
                        redSpawn!!.world!!.spawnEntity(redSpawn!!, et),
                        yellowSpawn!!.world!!.spawnEntity(yellowSpawn!!, et),
                        greenSpawn!!.world!!.spawnEntity(greenSpawn!!, et)
                    )
                    blueMonster += 3
                }
                3 -> {
                    val e = listOf(
                        redSpawn!!.world!!.spawnEntity(redSpawn!!, et),
                        blueSpawn!!.world!!.spawnEntity(blueSpawn!!, et),
                        greenSpawn!!.world!!.spawnEntity(greenSpawn!!, et)
                    )
                    yellowMonster += 3
                }
                4 -> {
                    val e = listOf(
                        redSpawn!!.world!!.spawnEntity(redSpawn!!, et),
                        blueSpawn!!.world!!.spawnEntity(blueSpawn!!, et),
                        yellowSpawn!!.world!!.spawnEntity(yellowSpawn!!, et)
                    )
                    greenMonster += 3
                }
            }
        } else {
            when (_team) {
                1 -> {
                    val e = blueSpawn!!.world!!.spawnEntity(blueSpawn!!, et)
                    team[e] = _team
                    redMonster++
                }
                2 -> {
                    val e: Entity = redSpawn!!.world!!.spawnEntity(redSpawn!!, et)
                    team[e] = _team
                    blueMonster++
                }
            }
        }
    }

    fun initVariables(p: Player) {
        money[p] = 0
        team[p] = 0
        attacking[p] = false
        teamChat[p] = false
        redHeart = 8
        blueHeart = 8
        peacefulTime = 14400
        started = false
    }

    fun inits() {
        BUFF_EFFICIENCY.red = 0
        BUFF_EFFICIENCY.blue = 0
        BUFF_EFFICIENCY.yellow = 0
        BUFF_EFFICIENCY.green = 0
        BUFF_MONSTER_DAMAGE.red = 0
        BUFF_MONSTER_DAMAGE.blue = 0
        BUFF_MONSTER_DAMAGE.yellow = 0
        BUFF_MONSTER_DAMAGE.green = 0
        BUFF_PROTECTION.red = 0
        BUFF_PROTECTION.blue = 0
        BUFF_PROTECTION.yellow = 0
        BUFF_PROTECTION.green = 0
        BUFF_SHARPNESS.red = 0
        BUFF_SHARPNESS.blue = 0
        BUFF_SHARPNESS.yellow = 0
        BUFF_SHARPNESS.green = 0
        BUFF_MONSTER_HEALTH.red = 0
        BUFF_MONSTER_HEALTH.blue = 0
        BUFF_MONSTER_HEALTH.yellow = 0
        BUFF_MONSTER_HEALTH.green = 0
        BUFF_FORTUNE.red = 0
        BUFF_FORTUNE.blue = 0
        BUFF_FORTUNE.yellow = 0
        BUFF_FORTUNE.green = 0
        Bukkit.getWorlds().forEach {
            it.entities.forEach { e ->
                if (e !is Player) {
                    e.remove()
                }
            }
        }
    }

}