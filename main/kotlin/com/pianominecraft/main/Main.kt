package com.pianominecraft.main

import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.IronGolem

import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

import org.bukkit.potion.PotionEffectType

import org.bukkit.potion.PotionEffect
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.PrintWriter
import java.util.*


@Suppress("DEPRECATION")
class Main : JavaPlugin() {

    private val cfgFile = File(dataFolder, "config.txt")

    override fun onEnable() {

        if (!dataFolder.exists()) {
            dataFolder.mkdir()
            save()
        }

        Constant.plugin = this

        Bukkit.getOnlinePlayers().forEach {
            Constant.money[it] = 0
            Constant.team[it] = 0
            Constant.attacking[it] = false
            Constant.teamChat[it] = false
        }

        // 인벤토리 초기화
        for (i in 0..9) {
            Constant.shop.setItem(i, Constant.ITEM_VOID)
            Constant.shop.setItem(i + 17, Constant.ITEM_VOID)
            Constant.materialShop.setItem(i, Constant.ITEM_VOID)
            Constant.materialShop.setItem(i + 17, Constant.ITEM_VOID)
            Constant.monsterShop.setItem(i, Constant.ITEM_VOID)
            Constant.monsterShop.setItem(i + 26, Constant.ITEM_VOID)
            Constant.blueBuffShop.setItem(i, Constant.ITEM_VOID)
            Constant.blueBuffShop.setItem(i + 17, Constant.ITEM_VOID)
            Constant.redBuffShop.setItem(i, Constant.ITEM_VOID)
            Constant.redBuffShop.setItem(i + 17, Constant.ITEM_VOID)
            Constant.yellowBuffShop.setItem(i, Constant.ITEM_VOID)
            Constant.yellowBuffShop.setItem(i + 17, Constant.ITEM_VOID)
            Constant.greenBuffShop.setItem(i, Constant.ITEM_VOID)
            Constant.greenBuffShop.setItem(i + 17, Constant.ITEM_VOID)
        }
        Constant.shop.setItem(10, Constant.getItemStack(Material.EMERALD, 1, "${ChatColor.GREEN}재료 상점", listOf("${ChatColor.GRAY}CLICK")))
        Constant.shop.setItem(11, Constant.getItemStack(Material.GOLDEN_APPLE, 1, "${ChatColor.GREEN}몬스터 상점", listOf("${ChatColor.GRAY}CLICK")))
        Constant.shop.setItem(12, Constant.getItemStack(Material.ENCHANTED_BOOK, 1, "${ChatColor.GREEN}버프 상점", listOf("${ChatColor.GRAY}CLICK")))
        Constant.shop.setItem(13, Constant.getItemStack(Material.DIAMOND_SWORD, 1, "${ChatColor.GREEN}무기 상점", listOf("${ChatColor.GRAY}CLICK")))
        Constant.shop.setItem(14, Constant.getItemStack(Material.DIAMOND_CHESTPLATE, 1, "${ChatColor.GREEN}갑옷 상점", listOf("${ChatColor.GRAY}CLICK")))
        Constant.shop.setItem(15, Constant.getItemStack(Material.DIAMOND_PICKAXE, 1, "${ChatColor.GREEN}도구 상점", listOf("${ChatColor.GRAY}CLICK")))
        Constant.shop.setItem(16, Constant.getItemStack(Material.SHIELD, 1, "${ChatColor.GREEN}기타 아이템 상점", listOf("${ChatColor.GRAY}CLICK")))
        Constant.materialShop.setItem(10, Constant.getItemStack(Material.OAK_LOG, 1, "${ChatColor.GREEN}${ChatColor.BOLD}나무", listOf(
                    "${ChatColor.GRAY}구매 - (120원)", "${ChatColor.GRAY}판매 - (60원)",
                    "${ChatColor.GREEN}맨 처음에 제일 중요한 재료 아이템."
                )))
        Constant.materialShop.setItem(11, Constant.getItemStack(Material.STICK, 1, "${ChatColor.GREEN}${ChatColor.BOLD}막대", listOf(
                    "${ChatColor.GRAY}구매 - (10원)", "${ChatColor.GRAY}판매 - 5원)",
                    "${ChatColor.GREEN}흔하지만 귀중한 재료 아이템."
                )))
        Constant.materialShop.setItem(12, Constant.getItemStack(Material.COBBLESTONE, 1, "${ChatColor.GREEN}${ChatColor.BOLD}조약돌", listOf(
                    "${ChatColor.GRAY}구매 - (50원)", "${ChatColor.GRAY}판매 - (25원)",
                    "${ChatColor.GREEN}흔하지만 귀중한 재료 아이템."
                )))
        Constant.materialShop.setItem(13, Constant.getItemStack(Material.IRON_INGOT, 1, "${ChatColor.GREEN}${ChatColor.BOLD}철괴", listOf(
                    "${ChatColor.GRAY}구매 - (200원)", "${ChatColor.GRAY}판매 - (100원)",
                    "${ChatColor.GREEN}초반 장비를 빠르게 얻기 위해 반드시 필요한 재료 아이템."
                )))
        Constant.materialShop.setItem(14, Constant.getItemStack(Material.GOLD_INGOT, 1, "${ChatColor.GREEN}${ChatColor.BOLD}금괴", listOf(
                    "${ChatColor.GRAY}구매 - (3000원)", "${ChatColor.GRAY}판매 - (1500원)",
                    "${ChatColor.GREEN}비싸게 거래되며 회복 아이템을 얻기 위한 재료 아이템."
                )))
        Constant.materialShop.setItem(15, Constant.getItemStack(Material.DIAMOND, 1, "${ChatColor.GREEN}${ChatColor.BOLD}다이아몬드", listOf(
                    "${ChatColor.GRAY}구매 - (600원)", "${ChatColor.GRAY}판매 - (300원)",
                    "${ChatColor.GREEN}후반 필수 재료 아이템."
                )))
        Constant.materialShop.setItem(16, Constant.getItemStack(Material.NETHERITE_INGOT, 1, "${ChatColor.GREEN}${ChatColor.BOLD}네더라이트", listOf(
                    "${ChatColor.GRAY}구매 - (15000원)", "${ChatColor.GRAY}판매 - (7500원)",
                    "${ChatColor.GREEN}전설에 도달한 재료 아이템."
                )))

        Constant.monsterShop.setItem(10, Constant.getItemStack(Material.SILVERFISH_SPAWN_EGG, 1, "${ChatColor.GREEN}${ChatColor.BOLD}좀벌레", listOf("${ChatColor.GRAY}소환 - (200원)", "${ChatColor.GREEN}러쉬!")))
        Constant.monsterShop.setItem(11, Constant.getItemStack(Material.ZOMBIE_SPAWN_EGG, 1, "${ChatColor.GREEN}${ChatColor.BOLD}좀비", listOf("${ChatColor.GRAY}소환 - (500원)", "${ChatColor.GREEN}러쉬!!")))
        Constant.monsterShop.setItem(12, Constant.getItemStack(Material.SKELETON_SPAWN_EGG, 1, "${ChatColor.GREEN}${ChatColor.BOLD}스켈레톤", listOf("${ChatColor.GRAY}소환 - (500원)", "${ChatColor.GREEN}훌륭한 원거리 사수.")))
        Constant.monsterShop.setItem(13, Constant.getItemStack(Material.SPIDER_SPAWN_EGG, 1, "${ChatColor.GREEN}${ChatColor.BOLD}거미", listOf("${ChatColor.GRAY}소환 - (500원)", "${ChatColor.GREEN}으악 거미다!!")))
        Constant.monsterShop.setItem(14, Constant.getItemStack(Material.CREEPER_SPAWN_EGG, 1, "${ChatColor.GREEN}${ChatColor.BOLD}크리퍼", listOf("${ChatColor.GRAY}소환 - (2000원)", "${ChatColor.GREEN}펑!")))
        Constant.monsterShop.setItem(15, Constant.getItemStack(Material.PHANTOM_SPAWN_EGG, 1, "${ChatColor.GREEN}${ChatColor.BOLD}팬텀", listOf("${ChatColor.GRAY}소환 - (4000원)", "${ChatColor.GREEN}방해 용도로 적합하다.")))
        Constant.monsterShop.setItem(16, Constant.getItemStack(Material.WITHER_SKELETON_SPAWN_EGG, 1, "${ChatColor.GREEN}${ChatColor.BOLD}위더 스켈레톤", listOf("${ChatColor.GRAY}소환 - (5000원)", "${ChatColor.GREEN}치명적인 디버프를 주는 몬스터.")))
        Constant.monsterShop.setItem(19, Constant.getItemStack(Material.WOLF_SPAWN_EGG, 1, "${ChatColor.GREEN}${ChatColor.BOLD}철 골렘", listOf("${ChatColor.GRAY}소환 - (30000원)", "${ChatColor.GREEN}파괴수보다 좋을지도.")))
        Constant.monsterShop.setItem(20, Constant.getItemStack(Material.RAVAGER_SPAWN_EGG, 1, "${ChatColor.GREEN}${ChatColor.BOLD}파괴수", listOf("${ChatColor.GRAY}소환 - (30000원)", "${ChatColor.GREEN}방패가 없으면 잡기 힘든 몬스터.")))
        Constant.monsterShop.setItem(21, Constant.getItemStack(Material.PLAYER_HEAD, 1, "${ChatColor.GREEN}${ChatColor.BOLD}플레이어", listOf("${ChatColor.GRAY}소환 - (100000원)", "${ChatColor.GREEN}본인이 적진으로 가 공격을 할 수 있다 (30초).")))

        Constant.redBuffShop.setItem(10, Constant.getItemStack(Material.DIAMOND_PICKAXE, 1, "${ChatColor.GREEN}${ChatColor.BOLD}효율", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.EFFICIENCY.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.EFFICIENCY.lore}"
                )))
        Constant.blueBuffShop.setItem(10, Constant.getItemStack(Material.DIAMOND_PICKAXE, 1, "${ChatColor.GREEN}${ChatColor.BOLD}효율", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.EFFICIENCY.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.EFFICIENCY.lore}"
                )))
        Constant.yellowBuffShop.setItem(10, Constant.getItemStack(Material.DIAMOND_PICKAXE, 1, "${ChatColor.GREEN}${ChatColor.BOLD}효율", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.EFFICIENCY.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.EFFICIENCY.lore}"
                )))
        Constant.greenBuffShop.setItem(10, Constant.getItemStack(Material.DIAMOND_PICKAXE, 1, "${ChatColor.GREEN}${ChatColor.BOLD}효율", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.EFFICIENCY.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.EFFICIENCY.lore}"
                )))
        Constant.redBuffShop.setItem(11, Constant.getItemStack(Material.DIAMOND_SWORD, 1, "${ChatColor.GREEN}${ChatColor.BOLD}날카로움", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.SHARPNESS.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.SHARPNESS.lore}"
                )))
        Constant.blueBuffShop.setItem(11, Constant.getItemStack(Material.DIAMOND_SWORD, 1, "${ChatColor.GREEN}${ChatColor.BOLD}날카로움", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.SHARPNESS.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.SHARPNESS.lore}"
                )))
        Constant.yellowBuffShop.setItem(11, Constant.getItemStack(Material.DIAMOND_SWORD, 1, "${ChatColor.GREEN}${ChatColor.BOLD}날카로움", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.SHARPNESS.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.SHARPNESS.lore}"
                )))
        Constant.greenBuffShop.setItem(11, Constant.getItemStack(Material.DIAMOND_SWORD, 1, "${ChatColor.GREEN}${ChatColor.BOLD}날카로움", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.SHARPNESS.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.SHARPNESS.lore}"
                )))
        Constant.redBuffShop.setItem(12, Constant.getItemStack(Material.DIAMOND_CHESTPLATE, 1, "${ChatColor.GREEN}${ChatColor.BOLD}보호", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.PROTECTION.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.PROTECTION.lore}"
                )))
        Constant.blueBuffShop.setItem(12, Constant.getItemStack(Material.DIAMOND_CHESTPLATE, 1, "${ChatColor.GREEN}${ChatColor.BOLD}보호", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.PROTECTION.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.PROTECTION.lore}"
                )))
        Constant.yellowBuffShop.setItem(12, Constant.getItemStack(Material.DIAMOND_CHESTPLATE, 1, "${ChatColor.GREEN}${ChatColor.BOLD}보호", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.PROTECTION.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.PROTECTION.lore}"
                )))
        Constant.greenBuffShop.setItem(12, Constant.getItemStack(Material.DIAMOND_CHESTPLATE, 1, "${ChatColor.GREEN}${ChatColor.BOLD}보호", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.PROTECTION.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.PROTECTION.lore}"
                )))
        Constant.redBuffShop.setItem(13, Constant.getItemStack(Material.NETHERITE_CHESTPLATE, 1, "${ChatColor.GREEN}${ChatColor.BOLD}몬스터 체력", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.MONSTER_HEALTH.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.MONSTER_HEALTH.lore}"
                )))
        Constant.blueBuffShop.setItem(13, Constant.getItemStack(Material.NETHERITE_CHESTPLATE, 1, "${ChatColor.GREEN}${ChatColor.BOLD}몬스터 체력", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.MONSTER_HEALTH.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.MONSTER_HEALTH.lore}"
                )))
        Constant.yellowBuffShop.setItem(13, Constant.getItemStack(Material.NETHERITE_CHESTPLATE, 1, "${ChatColor.GREEN}${ChatColor.BOLD}몬스터 체력", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.MONSTER_HEALTH.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.MONSTER_HEALTH.lore}"
                )))
        Constant.greenBuffShop.setItem(13, Constant.getItemStack(Material.NETHERITE_CHESTPLATE, 1, "${ChatColor.GREEN}${ChatColor.BOLD}몬스터 체력", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.MONSTER_HEALTH.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.MONSTER_HEALTH.lore}"
                )))
        Constant.redBuffShop.setItem(14, Constant.getItemStack(Material.NETHERITE_SWORD, 1, "${ChatColor.GREEN}${ChatColor.BOLD}몬스터 데미지", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.MONSTER_DAMAGE.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.MONSTER_DAMAGE.lore}"
                )))
        Constant.blueBuffShop.setItem(14, Constant.getItemStack(Material.NETHERITE_SWORD, 1, "${ChatColor.GREEN}${ChatColor.BOLD}몬스터 데미지", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.MONSTER_DAMAGE.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.MONSTER_DAMAGE.lore}"
                )))
        Constant.yellowBuffShop.setItem(14, Constant.getItemStack(Material.NETHERITE_SWORD, 1, "${ChatColor.GREEN}${ChatColor.BOLD}몬스터 데미지", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.MONSTER_DAMAGE.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.MONSTER_DAMAGE.lore}"
                )))
        Constant.greenBuffShop.setItem(14, Constant.getItemStack(Material.NETHERITE_SWORD, 1, "${ChatColor.GREEN}${ChatColor.BOLD}몬스터 데미지", listOf(
                    "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.MONSTER_DAMAGE.price[0]}x",
                    "${ChatColor.GREEN}${BuffItem.MONSTER_DAMAGE.lore}"
                )))
        Constant.redBuffShop.setItem(15, Constant.getItemStack(Material.DIAMOND, 1, "${ChatColor.GREEN}${ChatColor.BOLD}행운", listOf(
            "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.MONSTER_DAMAGE.price[0]}x",
            "${ChatColor.GREEN}${BuffItem.MONSTER_DAMAGE.lore}"
        )))
        Constant.blueBuffShop.setItem(15, Constant.getItemStack(Material.DIAMOND, 1, "${ChatColor.GREEN}${ChatColor.BOLD}행운", listOf(
            "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.MONSTER_DAMAGE.price[0]}x",
            "${ChatColor.GREEN}${BuffItem.MONSTER_DAMAGE.lore}"
        )))
        Constant.yellowBuffShop.setItem(15, Constant.getItemStack(Material.DIAMOND, 1, "${ChatColor.GREEN}${ChatColor.BOLD}행운", listOf(
            "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.MONSTER_DAMAGE.price[0]}x",
            "${ChatColor.GREEN}${BuffItem.MONSTER_DAMAGE.lore}"
        )))
        Constant.greenBuffShop.setItem(15, Constant.getItemStack(Material.DIAMOND, 1, "${ChatColor.GREEN}${ChatColor.BOLD}행운", listOf(
            "${ChatColor.GRAY}강화(0 > I) - 다이아 ${BuffItem.MONSTER_DAMAGE.price[0]}x",
            "${ChatColor.GREEN}${BuffItem.MONSTER_DAMAGE.lore}"
        )))


        WeaponItem.values().forEachIndexed { index, value ->
            var recipeString = ""
            value.items.keys.forEach {
                recipeString += "${Constant.itemNames[it]} ${value.items[it]}"
            }
            var slot = 0
            when {
                index < 7 -> slot = index + 10
                index < 14 -> slot = index + 12
                index < 21 -> slot = index + 14
                index < 28 -> slot = index + 16
            }
            Constant.weaponSmithy.setItem(
                slot,
                Constant.getItemStack(
                    value.material,
                    1,
                    "${ChatColor.BOLD}${value.itemname}",
                    listOf("${ChatColor.GRAY}제작 - ${recipeString}x", value.lore)
                )
            )
        }
        ArmorItem.values().forEachIndexed { index, value ->
            var recipeString = ""
            value.items.keys.forEach {
                recipeString += "${Constant.itemNames[it]} ${value.items[it]}"
            }
            var slot = 0
            when {
                index < 7 -> slot = index + 10
                index < 14 -> slot = index + 12
                index < 21 -> slot = index + 14
                index < 28 -> slot = index + 16
            }
            Constant.armorSmithy.setItem(
                slot,
                Constant.getItemStack(
                    value.material,
                    1,
                    "${ChatColor.BOLD}${value.itemname}",
                    listOf("${ChatColor.GRAY}제작 - ${recipeString}x", value.lore)
                )
            )
        }
        ToolItem.values().forEachIndexed { index, value ->
            var recipeString = ""
            value.items.keys.forEach {
                recipeString += "${Constant.itemNames[it]} ${value.items[it]}"
            }
            var slot = 0
            when {
                index < 7 -> slot = index + 10
                index < 14 -> slot = index + 12
                index < 21 -> slot = index + 14
                index < 28 -> slot = index + 16
            }
            Constant.toolSmithy.setItem(
                slot,
                Constant.getItemStack(
                    value.material,
                    1,
                    "${ChatColor.BOLD}${value.itemname}",
                    listOf("${ChatColor.GRAY}제작 - ${recipeString}x", value.lore)
                )
            )
        }
        MisItem.values().forEachIndexed { index, value ->
            var recipeString = ""
            value.items.keys.forEach {
                recipeString += "${Constant.itemNames[it]} ${value.items[it]}"
            }
            var slot = 0
            when {
                index < 7 -> slot = index + 10
                index < 14 -> slot = index + 12
                index < 21 -> slot = index + 14
                index < 28 -> slot = index + 16
            }
            Constant.misSmithy.setItem(
                slot,
                Constant.getItemStack(
                    value.material,
                    1,
                    "${ChatColor.BOLD}${value.itemname}",
                    listOf("${ChatColor.GRAY}제작 - ${recipeString}x", value.lore)
                )
            )
        }

        // 초기 스케줄러

        // 메뉴 아이템
        repeatTask(1) {
            for (p in Bukkit.getOnlinePlayers()) {
                p.inventory.setItem(
                    8, Constant.getItemStack(
                        Material.NETHER_STAR, 1, "${ChatColor.GREEN}메뉴",
                        listOf("${ChatColor.GREEN}CLICK")
                    )
                )
                if (Constant.started) {
                    p.spigot().sendMessage(
                        ChatMessageType.ACTION_BAR,
                        TextComponent("${ChatColor.GREEN}돈 : ${Constant.money[p]}") as BaseComponent
                    )
                }
            }
        }

        // 자동 판매
        repeatTask(1) {
            MaterialShopItem.values().forEach { msi ->
                msi.autoSell.forEach { autoSell ->
                    var amount = 0
                    for (item: ItemStack? in autoSell.inventory) {
                        if (item != null) {
                            if (item.type == msi.material) {
                                amount += item.amount
                            }
                        }
                    }
                    Constant.money[autoSell as Player] = Constant.money[autoSell]!! + (msi.price * amount / 2)
                    autoSell.inventory.remove(msi.material)
                }
            }
        }

        // 평화 시간
        repeatTask(1) {
            if (Constant.started) {
                if (Constant.peacefulTime > 0) {
                    Constant.peacefulTime--
                    when (Constant.peacefulTime) {
                        12000 -> {
                            server.broadcastMessage("${ChatColor.GOLD}평화 시간이 ${ChatColor.RED}10${ChatColor.GOLD}분 남았습니다")
                            server.onlinePlayers.forEach { p ->
                                p.playSound(p.eyeLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                            }
                        }
                        6000 -> {
                            server.broadcastMessage("${ChatColor.GOLD}평화 시간이 ${ChatColor.RED}5${ChatColor.GOLD}분 남았습니다")
                            server.onlinePlayers.forEach { p ->
                                p.playSound(p.eyeLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                            }
                        }
                        2400 -> {
                            server.broadcastMessage("${ChatColor.GOLD}평화 시간이 ${ChatColor.RED}2${ChatColor.GOLD}분 남았습니다")
                            server.onlinePlayers.forEach { p ->
                                p.playSound(p.eyeLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                            }
                        }
                        1200 -> {
                            server.broadcastMessage("${ChatColor.GOLD}평화 시간이 ${ChatColor.RED}1${ChatColor.GOLD}분 남았습니다")
                            server.onlinePlayers.forEach { p ->
                                p.playSound(p.eyeLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                            }
                        }
                        600 -> {
                            server.broadcastMessage("${ChatColor.GOLD}평화 시간이 ${ChatColor.RED}30${ChatColor.GOLD}초 남았습니다")
                            server.onlinePlayers.forEach { p ->
                                p.playSound(p.eyeLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                            }
                        }
                        200 -> {
                            server.broadcastMessage("${ChatColor.GOLD}평화 시간이 ${ChatColor.RED}10${ChatColor.GOLD}초 남았습니다")
                            server.onlinePlayers.forEach { p ->
                                p.playSound(p.eyeLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                            }
                        }
                        100 -> {
                            server.broadcastMessage("${ChatColor.GOLD}평화 시간이 ${ChatColor.RED}5${ChatColor.GOLD}초 남았습니다")
                            server.onlinePlayers.forEach { p ->
                                p.playSound(p.eyeLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                            }
                        }
                        80 -> {
                            server.broadcastMessage("${ChatColor.GOLD}평화 시간이 ${ChatColor.RED}4${ChatColor.GOLD}초 남았습니다")
                            server.onlinePlayers.forEach { p ->
                                p.playSound(p.eyeLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                            }
                        }
                        60 -> {
                            server.broadcastMessage("${ChatColor.GOLD}평화 시간이 ${ChatColor.RED}3${ChatColor.GOLD}초 남았습니다")
                            server.onlinePlayers.forEach { p ->
                                p.playSound(p.eyeLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                            }
                        }
                        40 -> {
                            server.broadcastMessage("${ChatColor.GOLD}평화 시간이 ${ChatColor.RED}2${ChatColor.GOLD}초 남았습니다")
                            server.onlinePlayers.forEach { p ->
                                p.playSound(p.eyeLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                            }
                        }
                        20 -> {
                            server.broadcastMessage("${ChatColor.GOLD}평화 시간이 ${ChatColor.RED}1${ChatColor.GOLD}초 남았습니다")
                            server.onlinePlayers.forEach { p ->
                                p.playSound(p.eyeLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                            }
                        }
                        0 -> {
                            server.broadcastMessage("${ChatColor.RED}이제 몬스터를 소환할 수 있습니다")
                            server.onlinePlayers.forEach { p ->
                                p.playSound(p.eyeLocation, Sound.ENTITY_WITHER_SPAWN, 1f, 1f)
                                p.sendTitle(null, "${ChatColor.RED}이제 몬스터를 소환할 수 있습니다", 20, 60, 20)
                            }
                        }
                    }
                }
            }
        }

        // 포션 효과
        repeatTask(20) {
            for (p in Bukkit.getOnlinePlayers()) {
                p.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, 100, 0))
                if (Constant.started) {
                    p.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 100, 2))
                    if (p.inventory.chestplate != null) {
                        if (p.inventory.chestplate!!.type == Material.GOLDEN_CHESTPLATE) {
                            p.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 2))
                            p.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 100, 2))
                            p.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 100, 1))
                            p.maxHealth = 30.0
                        } else {
                            p.maxHealth = 20.0
                        }
                    } else {
                        p.maxHealth = 20.0
                    }
                    if (p.inventory.itemInMainHand.type == Material.GOLDEN_SWORD) {
                        p.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 9))
                    }
                }
            }
        }

        // 철 골렘 타겟팅
        repeatTask(100) {
            server.worlds.forEach { w ->
                w.entities.filterIsInstance<IronGolem>().forEach { golem ->
                    if (golem.target == null) {
                        Constant.team[golem]?.let { team ->
                            val near =
                                server.onlinePlayers.filter { team != Constant.team[it] && golem.world == it.world }
                                    .minByOrNull { golem.location.distance(it.location) }
                            if (near != null) {
                                golem.target = near
                            }
                        }
                    }
                }
            }
        }

        // 버프 상점 인첸트 적용
        repeatTask(1) {
            try {
                for (p in Bukkit.getOnlinePlayers()) {
                    when {
                        Constant.team[p] == 1 -> {
                            for (i in p.inventory) {
                                if (i != null) {
                                    if (Constant.BUFF_EFFICIENCY.red > 0) {
                                        if (i.type == Material.WOODEN_AXE || i.type == Material.STONE_AXE || i.type == Material.WOODEN_PICKAXE || i.type == Material.STONE_PICKAXE || i.type == Material.IRON_PICKAXE || i.type == Material.DIAMOND_PICKAXE || i.type == Material.GOLDEN_AXE) {
                                            val im = i.itemMeta!!
                                            im.addEnchant(Enchantment.DIG_SPEED, Constant.BUFF_EFFICIENCY.red, true)
                                            im.addEnchant(Enchantment.LUCK, Constant.BUFF_FORTUNE.red, true)
                                            i.itemMeta = im
                                        }
                                    }
                                    if (Constant.BUFF_SHARPNESS.red > 0) {
                                        if (i.type == Material.WOODEN_AXE || i.type == Material.STONE_AXE || i.type == Material.WOODEN_SWORD || i.type == Material.STONE_SWORD || i.type == Material.IRON_SWORD || i.type == Material.DIAMOND_SWORD || i.type == Material.NETHERITE_SWORD || i.type == Material.GOLDEN_SWORD) {
                                            val im = i.itemMeta!!
                                            im.addEnchant(Enchantment.DAMAGE_ALL, Constant.BUFF_SHARPNESS.red * 4, true)
                                            i.itemMeta = im
                                        }
                                    }
                                    if (Constant.BUFF_PROTECTION.red > 0) {
                                        if (i.type == Material.LEATHER_BOOTS ||
                                            i.type == Material.IRON_BOOTS ||
                                            i.type == Material.IRON_LEGGINGS ||
                                            i.type == Material.IRON_CHESTPLATE ||
                                            i.type == Material.IRON_HELMET ||
                                            i.type == Material.DIAMOND_BOOTS ||
                                            i.type == Material.DIAMOND_LEGGINGS ||
                                            i.type == Material.DIAMOND_CHESTPLATE || i.type == Material.DIAMOND_HELMET || i.type == Material.NETHERITE_BOOTS || i.type == Material.NETHERITE_LEGGINGS || i.type == Material.NETHERITE_CHESTPLATE || i.type == Material.NETHERITE_HELMET || i.type == Material.GOLDEN_CHESTPLATE
                                        ) {
                                            val im = i.itemMeta!!
                                            im.addEnchant(
                                                Enchantment.PROTECTION_ENVIRONMENTAL,
                                                Constant.BUFF_PROTECTION.red,
                                                true
                                            )
                                            i.itemMeta = im
                                        }
                                    }
                                }
                            }
                        }
                        Constant.team[p] == 2 -> {
                            for (i in p.inventory) {
                                if (i != null) {
                                    if (Constant.BUFF_EFFICIENCY.blue > 0) {
                                        if (i.type in Constant.efficiencyItems) {
                                            val im = i.itemMeta!!
                                            im.addEnchant(Enchantment.DIG_SPEED, Constant.BUFF_EFFICIENCY.blue, true)
                                            im.addEnchant(Enchantment.LUCK, Constant.BUFF_FORTUNE.blue, true)
                                            i.itemMeta = im
                                        }
                                    }
                                    if (Constant.BUFF_SHARPNESS.blue > 0) {
                                        if (i.type in Constant.sharpnessItems) {
                                            val im = i.itemMeta!!
                                            im.addEnchant(
                                                Enchantment.DAMAGE_ALL,
                                                Constant.BUFF_SHARPNESS.blue * 4,
                                                true
                                            )
                                            i.itemMeta = im
                                        }
                                    }
                                    if (Constant.BUFF_PROTECTION.blue > 0) {
                                        if (i.type in Constant.protectionItems) {
                                            val im = i.itemMeta!!
                                            im.addEnchant(
                                                Enchantment.PROTECTION_ENVIRONMENTAL,
                                                Constant.BUFF_PROTECTION.blue,
                                                true
                                            )
                                            i.itemMeta = im
                                        }
                                    }
                                }
                            }
                        }
                        Constant.team[p] == 3 -> {
                            for (i in p.inventory) {
                                if (i != null) {
                                    if (Constant.BUFF_EFFICIENCY.yellow > 0) {
                                        if (i.type in Constant.efficiencyItems) {
                                            val im = i.itemMeta!!
                                            im.addEnchant(Enchantment.DIG_SPEED, Constant.BUFF_EFFICIENCY.yellow, true)
                                            im.addEnchant(Enchantment.LUCK, Constant.BUFF_FORTUNE.yellow, true)
                                            i.itemMeta = im
                                        }
                                    }
                                    if (Constant.BUFF_SHARPNESS.yellow > 0) {
                                        if (i.type in Constant.sharpnessItems) {
                                            val im = i.itemMeta!!
                                            im.addEnchant(
                                                Enchantment.DAMAGE_ALL,
                                                Constant.BUFF_SHARPNESS.yellow * 4,
                                                true
                                            )
                                            i.itemMeta = im
                                        }
                                    }
                                    if (Constant.BUFF_PROTECTION.yellow > 0) {
                                        if (i.type in Constant.protectionItems) {
                                            val im = i.itemMeta!!
                                            im.addEnchant(
                                                Enchantment.PROTECTION_ENVIRONMENTAL,
                                                Constant.BUFF_PROTECTION.yellow,
                                                true
                                            )
                                            i.itemMeta = im
                                        }
                                    }
                                }
                            }
                        }
                        Constant.team[p] == 4 -> {
                            for (i in p.inventory) {
                                if (i != null) {
                                    if (Constant.BUFF_EFFICIENCY.green > 0) {
                                        if (i.type in Constant.efficiencyItems) {
                                            val im = i.itemMeta!!
                                            im.addEnchant(Enchantment.DIG_SPEED, Constant.BUFF_EFFICIENCY.green, true)
                                            im.addEnchant(Enchantment.LUCK, Constant.BUFF_FORTUNE.green, true)
                                            i.itemMeta = im
                                        }
                                    }
                                    if (Constant.BUFF_SHARPNESS.green > 0) {
                                        if (i.type in Constant.sharpnessItems) {
                                            val im = i.itemMeta!!
                                            im.addEnchant(
                                                Enchantment.DAMAGE_ALL,
                                                Constant.BUFF_SHARPNESS.green * 4,
                                                true
                                            )
                                            i.itemMeta = im
                                        }
                                    }
                                    if (Constant.BUFF_PROTECTION.green > 0) {
                                        if (i.type in Constant.protectionItems) {
                                            val im = i.itemMeta!!
                                            im.addEnchant(
                                                Enchantment.PROTECTION_ENVIRONMENTAL,
                                                Constant.BUFF_PROTECTION.green,
                                                true
                                            )
                                            i.itemMeta = im
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        delayTask(10) {
            Constant.blueSpawn = Location(Bukkit.getWorld("Blue"), 0.5, 101.0, 0.5)
            Constant.redSpawn = Location(Bukkit.getWorld("Red"), 0.5, 101.0, 0.5)
            Constant.yellowSpawn = Location(Bukkit.getWorld("Yellow"), 0.5, 101.0, 0.5)
            Constant.greenSpawn = Location(Bukkit.getWorld("Green"), 0.5, 101.0, 0.5)
            Constant.spawn = Location(Bukkit.getWorld("Spawn"), 0.5, 4.0, 0.5)
        }

        repeatTask(1) {
            Bukkit.getOnlinePlayers().forEach {
                if (Constant.coolTime.containsKey(it)) {
                    if (Constant.coolTime[it] != null) {
                        Constant.coolTime[it]?.minus(1)?.let { it1 -> Constant.coolTime.put(it, it1) }
                    }
                    if (Constant.coolTime[it] == 0) {
                        Constant.coolTime.remove(it)
                    }
                }
            }
        }

        Bukkit.getPluginManager().registerEvents(EventManager(), this)
        Bukkit.getLogger().info("[MonsterWars] has been enabled!")
    }

    override fun onDisable() {
        Bukkit.getLogger().info("[MonsterWars] has been disabled!")
    }

    override fun onCommand(s: CommandSender, c: Command, l: String, a: Array<String>): Boolean {
        if (c.name == "start") {
            return if (s.isOp) {
                if (start()) {
                    true
                } else {
                    s.sendMessage("${ChatColor.RED}일부 팀에 사람이 없습니다")
                    false
                }
            } else {
                s.sendMessage("${ChatColor.RED}입구컷")
                false
            }
        } else if (c.name == "multi") {
            Constant.multi = !Constant.multi
            s.sendMessage("${ChatColor.GREEN}팀 증가 : ${Constant.multi}")
            if (!Constant.multi) {
                Bukkit.getOnlinePlayers().forEach {
                    if (Constant.team[it] == 3 || Constant.team[it] == 4) {
                        Constant.team[it] = 0
                        it.sendMessage("${ChatColor.RED}팀 증가 모드가 해제되어 자동으로 관전 모드가 되었습니다")
                    }
                }
            }
        } else if (c.name == "test") {
            Constant.testing = !Constant.testing
            s.sendMessage("${ChatColor.GREEN}테스트 모드 : ${Constant.testing}")
        } else if (c.name == "save") {
            save()
        } else if (c.name == "finish") {
            return if (s.isOp) {
                for (p: Player in Bukkit.getOnlinePlayers()) {
                    Constant.initVariables(p)
                    Constant.inits()
                    load()
                    p.gameMode = GameMode.ADVENTURE
                    p.teleport(Constant.spawn!!)
                    p.inventory.clear()
                    p.activePotionEffects.forEach { e ->
                        p.removePotionEffect(e.type)
                    }
                    p.sendTitle("${ChatColor.RED}관리자에 의해 게임이 강제 종료됨", "", 20, 60, 20)
                }
                Constant.started = false
                true
            } else {
                s.sendMessage("${ChatColor.RED}입구컷")
                false
            }
        } else if (c.name == "chat") {
            if (Constant.teamChat[s as Player]!!) {
                s.sendMessage("${ChatColor.RED}팀 채팅 비활성화")
                Constant.teamChat[s] = false
            } else {
                s.sendMessage("${ChatColor.GREEN}팀 채팅 활성화")
                Constant.teamChat[s] = true
            }
        } else if (c.name == "teamList") {
            var red = 0
            var blue = 0
            var yellow = 0
            var green = 0
            var spec = 0
            for (p: Player in Bukkit.getOnlinePlayers()) when (Constant.team[p]) {
                    0 -> {
                        spec++
                        s.sendMessage("${ChatColor.GRAY}관전 : ${ChatColor.GREEN}${p.name}")
                    }
                    1 -> {
                        red++
                        s.sendMessage("${ChatColor.RED}레드 : ${ChatColor.GREEN}${p.name}")
                    }
                    2 -> {
                        blue++
                        s.sendMessage("${ChatColor.BLUE}블루 : ${ChatColor.GREEN}${p.name}")
                    }
                    3  -> {
                        if (Constant.multi) {
                            yellow++
                            s.sendMessage("${ChatColor.YELLOW}옐로 : ${ChatColor.GREEN}${p.name}")
                        }
                    }
                    4 -> {
                        if (Constant.multi) {
                            green++
                            s.sendMessage("${ChatColor.GREEN}그린 : ${ChatColor.GREEN}${p.name}")
                        }
                    }
                }
            s.sendMessage("${ChatColor.GRAY}관전 팀 ${spec}명")
            s.sendMessage("${ChatColor.RED}레드 팀 ${red}명")
            s.sendMessage("${ChatColor.BLUE}블루 팀 ${blue}명")
            if (Constant.multi) {
                s.sendMessage("${ChatColor.YELLOW}옐로 팀 ${yellow}명")
                s.sendMessage("${ChatColor.GREEN}그린 팀 ${green}명")
            }
        } else if (c.name == "giveMoney") {
            if (a.size >= 2) {
                var amount = 0
                try {
                    amount = a[1].toInt()
                } catch (e: Exception) {
                    s.sendMessage(ChatColor.GOLD.toString() + "/giveMoney [Player] [Amount]")
                }
                if (amount > 0) {
                    if (Constant.money[s]!! >= amount) {
                        if (Bukkit.getOfflinePlayer(a[0]).isOnline) {
                            val p = Bukkit.getPlayer(a[0])!!
                            if (s != p) {
                                if (Constant.team[p]!! == Constant.team[s]) {
                                    Constant.money[p] = Constant.money[p]!! + amount
                                    Constant.money[s as Player] = Constant.money[s]!! - amount
                                    s.sendMessage(
                                        "${ChatColor.GREEN}플레이어 " + p.name + "에게 " + amount + "만큼의 돈을 보냈습니다"
                                    )
                                    p.sendMessage(
                                        "${ChatColor.GREEN}플레이어 " + s.getName() + "가 당신에게 " + amount
                                                + "만큼의 돈을 보냈습니다"
                                    )
                                } else {
                                    s.sendMessage("${ChatColor.RED}다른 팀 플레이어에게 돈을 보낼 수 없습니다")
                                }
                            } else {
                                s.sendMessage("${ChatColor.RED}자신에게 돈을 보낼 수 없습니다")
                            }
                        } else {
                            s.sendMessage("${ChatColor.RED}오프라인인 플레이어입니다")
                        }
                    } else {
                        s.sendMessage("${ChatColor.RED}돈이 부족합니다")
                    }
                } else {
                    s.sendMessage("${ChatColor.RED}금액으로 양수만 사용할 수 있습니다")
                }
            } else {
                s.sendMessage(ChatColor.GOLD.toString() + "/giveMoney [Player] [Amount]")
            }
        }
        return false
    }

    @Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
    private fun start(): Boolean {
        if (Constant.multi) {
            Constant.redLive = true
            Constant.blueLive = true
            Constant.yellowLive = true
            Constant.greenLive = true
            Constant.leftTeam = 4
            var red = 0
            var blue = 0
            var yellow = 0
            var green = 0
            Constant.BUFF_EFFICIENCY.red = 0
            Constant.BUFF_EFFICIENCY.blue = 0
            Constant.BUFF_EFFICIENCY.yellow = 0
            Constant.BUFF_EFFICIENCY.green = 0
            Constant.BUFF_MONSTER_DAMAGE.red = 0
            Constant.BUFF_MONSTER_DAMAGE.blue = 0
            Constant.BUFF_MONSTER_DAMAGE.yellow = 0
            Constant.BUFF_MONSTER_DAMAGE.green = 0
            Constant.BUFF_PROTECTION.red = 0
            Constant.BUFF_PROTECTION.blue = 0
            Constant.BUFF_PROTECTION.yellow = 0
            Constant.BUFF_PROTECTION.green = 0
            Constant.BUFF_SHARPNESS.red = 0
            Constant.BUFF_SHARPNESS.blue = 0
            Constant.BUFF_SHARPNESS.yellow = 0
            Constant.BUFF_SHARPNESS.green = 0
            Constant.BUFF_MONSTER_HEALTH.red = 0
            Constant.BUFF_MONSTER_HEALTH.blue = 0
            Constant.BUFF_MONSTER_HEALTH.yellow = 0
            Constant.BUFF_MONSTER_HEALTH.green = 0
            val sb = server.scoreboardManager!!.mainScoreboard
            for (p: Player in Bukkit.getOnlinePlayers()) {
                Constant.money[p] = 0
                Constant.attacking[p] = false
                Constant.teamChat[p] = false
                when {
                    Constant.team[p] == 1 -> red++
                    Constant.team[p] == 2 -> blue++
                    Constant.team[p] == 3 -> yellow++
                    Constant.team[p] == 4 -> green++
                }
            }
            if ((red > 0 && blue > 0 && yellow > 0 && green > 0) || Constant.testing) {
                Constant.started = true
                for (p: Player in Bukkit.getOnlinePlayers()) when {
                    Constant.team[p]!! == 0 -> p.gameMode = GameMode.SPECTATOR
                    Constant.team[p]!! == 1 -> {
                        p.gameMode = GameMode.SURVIVAL
                        p.teleport(Constant.redSpawn!!)
                        p.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 99999, 0, false, false))
                        sb.getTeam("Red")!!.addEntry(p.name)
                    }
                    Constant.team[p]!! == 2 -> {
                        p.gameMode = GameMode.SURVIVAL
                        p.teleport(Constant.blueSpawn!!)
                        p.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 99999, 0, false, false))
                        sb.getTeam("Blue")!!.addEntry(p.name)
                    }
                    Constant.team[p]!! == 3 -> {
                        p.gameMode = GameMode.SURVIVAL
                        p.teleport(Constant.yellowSpawn!!)
                        p.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 99999, 0, false, false))
                        sb.getTeam("Yellow")!!.addEntry(p.name)
                    }
                    Constant.team[p]!! == 4 -> {
                        p.gameMode = GameMode.SURVIVAL
                        p.teleport(Constant.greenSpawn!!)
                        p.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 99999, 0, false, false))
                        sb.getTeam("Green")!!.addEntry(p.name)
                    }
                }
                return true
            } else {
                return false
            }
        } else {
            Constant.redLive = true
            Constant.blueLive = true
            Constant.leftTeam = 2
            var red = 0
            var blue = 0
            Constant.BUFF_EFFICIENCY.red = 0
            Constant.BUFF_EFFICIENCY.blue = 0
            Constant.BUFF_MONSTER_DAMAGE.red = 0
            Constant.BUFF_MONSTER_DAMAGE.blue = 0
            Constant.BUFF_PROTECTION.red = 0
            Constant.BUFF_PROTECTION.blue = 0
            Constant.BUFF_SHARPNESS.red = 0
            Constant.BUFF_SHARPNESS.blue = 0
            Constant.BUFF_MONSTER_HEALTH.red = 0
            Constant.BUFF_MONSTER_HEALTH.blue = 0
            val sb = server.scoreboardManager!!.mainScoreboard
            for (p: Player in Bukkit.getOnlinePlayers()) {
                Constant.money[p] = 0
                Constant.attacking[p] = false
                Constant.teamChat[p] = false
                when {
                    Constant.team[p] == 1 -> red++
                    Constant.team[p] == 2 -> blue++
                }
            }
            if ((red > 0 && blue > 0) || Constant.testing) {
                Constant.started = true
                for (p: Player in Bukkit.getOnlinePlayers()) when {
                    Constant.team[p]!! == 0 -> p.gameMode = GameMode.SPECTATOR
                    Constant.team[p]!! == 1 -> {
                        p.gameMode = GameMode.SURVIVAL
                        p.teleport(Constant.redSpawn!!)
                        p.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 99999, 0, false, false))
                        sb.getTeam("Red")!!.addEntry(p.name)
                    }
                    Constant.team[p]!! == 2 -> {
                        p.gameMode = GameMode.SURVIVAL
                        p.teleport(Constant.blueSpawn!!)
                        p.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 99999, 0, false, false))
                        sb.getTeam("Blue")!!.addEntry(p.name)
                    }
                }
                return true
            } else {
                return false
            }
        }
    }

    fun delayTask(delay: Long, task: () -> Unit) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, task, delay)
    }

    fun repeatTask(delay: Long, startDelay: Long = 0, task: () -> Unit) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, task, startDelay, delay)
    }

    fun load() {
        try {
            val br = BufferedReader(FileReader(cfgFile))
            var lines = br.lines()
            lines.forEach {
                val l = it?.split(" ")
                val x = l?.get(0)?.toInt()
                val y = l?.get(1)?.toInt()
                val z = l?.get(2)?.toInt()
                if (x != null && y != null && z != null) {
                    Bukkit.getWorld("Red")?.getBlockAt(x, y, z)?.type = Material.STONE
                    Bukkit.getWorld("Blue")?.getBlockAt(x, y, z)?.type = Material.STONE
                    Bukkit.getWorld("Yellow")?.getBlockAt(x, y, z)?.type = Material.STONE
                    Bukkit.getWorld("Green")?.getBlockAt(x, y, z)?.type = Material.STONE
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun save() {
        val pw = PrintWriter(cfgFile)
        for (x in -30..30) {
            for (y in 100..120) {
                for (z in -30..30) {
                    if (Bukkit.getWorld("Red")?.getBlockAt(x, y, z)?.type == Material.STONE) pw.println("$x $y $z")
                }
            }
        }
        pw.close()
    }

}