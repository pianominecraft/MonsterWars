package com.pianominecraft.main

import com.pianominecraft.main.Constant.plugin
import com.pianominecraft.main.Constant.team
import com.pianominecraft.main.Constant.teamChat
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import java.util.*
import org.bukkit.entity.Player
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.inventory.ClickType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.lang.Exception
import kotlin.math.pow

@Suppress("DEPRECATION")
class EventManager : Listener {

    private var menuClick = HashMap<Player, Boolean>()
    private var invincibility = HashMap<Player, Boolean>()

    // 몬스터가 죽는 이벤트
    @EventHandler
    fun onMonsterDeath(e: EntityDeathEvent) {
        if (e.entity !is Player) {
            if (team.containsKey(e.entity)) {
                when (team[e.entity]) {
                    1 -> Constant.redMonster--
                    2 -> Constant.blueMonster--
                    3 -> Constant.yellowMonster--
                    4 -> Constant.greenMonster--
                }
            }
        }
    }

    // 데미지 이벤트
    @EventHandler
    fun onDamage(e: EntityDamageByEntityEvent) {
        if (!Constant.started) {
            e.isCancelled = true
            return
        }
        if (e.damager !is Player) {
            if (team.containsKey(e.damager)) {
                when {
                    team[e.damager] == 1 -> e.damage *= Constant.BUFF_MONSTER_DAMAGE.red.toDouble().pow(0.7) + 1
                    team[e.damager] == 2 -> e.damage *= Constant.BUFF_MONSTER_DAMAGE.blue.toDouble().pow(0.7) + 1
                    team[e.damager] == 3 -> e.damage *= Constant.BUFF_MONSTER_DAMAGE.yellow.toDouble().pow(0.7) + 1
                    team[e.damager] == 4 -> e.damage *= Constant.BUFF_MONSTER_DAMAGE.green.toDouble().pow(0.7) + 1
                }
            }
        } else if (e.entity !is Player) {
            if (team.containsKey(e.damager)) {
                when {
                    team[e.entity] == 1 -> e.damage /= 1 + Constant.BUFF_MONSTER_HEALTH.red.toDouble().pow(0.9)
                    team[e.entity] == 2 -> e.damage /= 1 + Constant.BUFF_MONSTER_HEALTH.blue.toDouble().pow(0.9)
                    team[e.entity] == 3 -> e.damage /= 1 + Constant.BUFF_MONSTER_HEALTH.yellow.toDouble().pow(0.9)
                    team[e.entity] == 4 -> e.damage /= 1 + Constant.BUFF_MONSTER_HEALTH.green.toDouble().pow(0.9)
                }
            }
        }
        // 같은 팀 때리기 방지
        if (team.containsKey(e.entity)) {
            if (team.containsKey(e.damager)) {
                if (team[e.entity] == team[e.damager]) {
                    e.isCancelled = true
                    return
                }
            }
        }
        // 스플래시 데미지
        if (e.damager is Player) {
            if (((e.damager as Player).itemInHand.type == Material.WOODEN_AXE
                        || (e.damager as Player).itemInHand.type == Material.STONE_AXE)
            ) {
                for (entity: Entity? in e.entity.getNearbyEntities(3.0, 3.0, 3.0)) {
                    if (entity is LivingEntity) {
                        if (team[e.damager] != team[entity]) {
                            entity.damage(e.finalDamage / 5)
                        }
                    }
                }
            }
            if (((e.damager as Player).itemInHand.type == Material.WOODEN_SWORD
                        ) || ((e.damager as Player).itemInHand.type == Material.STONE_SWORD
                        ) || ((e.damager as Player).itemInHand.type == Material.IRON_SWORD
                        ) || ((e.damager as Player).itemInHand.type == Material.DIAMOND_SWORD
                        ) || ((e.damager as Player).itemInHand.type == Material.GOLDEN_SWORD
                        ) || ((e.damager as Player).itemInHand.type == Material.NETHERITE_SWORD)
            ) {
                for (entity: Entity? in e.entity.getNearbyEntities(4.0, 4.0, 4.0)) {
                    if (entity is LivingEntity) {
                        if (team[e.damager] != team[entity]) {
                            entity.damage(e.finalDamage / 2)
                        }
                    }
                }
                if ((e.damager as Player).itemInHand.type == Material.GOLDEN_SWORD) {
                    e.damage *= 1.8
                } else {
                    e.damage *= 1.4
                }
            }
        }
        if (e.entity is Player) {
            if ((e.entity as Player).gameMode == GameMode.SPECTATOR) {
                e.isCancelled = true
                return
            }
            if (e.entity is Player) {
                if (invincibility.containsKey(e.entity as Player)) {
                    if (invincibility[e.entity as Player]!!) {
                        e.isCancelled = true
                        return
                    }
                }
            }
            if ((e.entity as Player).health <= e.finalDamage) {
                e.isCancelled = true
                if (Constant.attacking.containsKey(e.entity as Player) && Constant.attacking[e.entity as Player]!!) {
                    (e.entity as Player).health = 20.0
                    when {
                        team[e.entity] == 1 -> e.entity.teleport(Constant.redSpawn!!)
                        team[e.entity] == 2 -> e.entity.teleport(Constant.blueSpawn!!)
                        team[e.entity] == 3 -> e.entity.teleport(Constant.yellowSpawn!!)
                        team[e.entity] == 4 -> e.entity.teleport(Constant.greenSpawn!!)
                    }
                    Constant.attacking[e.entity as Player] = false
                } else {
                    (e.entity as Player).health = (e.entity as Player).maxHealth
                    (e.entity as Player).gameMode = GameMode.SPECTATOR
                    if (team[e.entity] == 1) {
                        Constant.redHeart--
                        Bukkit.broadcastMessage(
                            ("${ChatColor.GREEN}레드 팀의 플레이어가 사망했습니다! (레드 팀 남은 하트 ${Constant.redHeart}${ChatColor.RED}♥${ChatColor.GREEN})")
                        )
                        if (Constant.redHeart == 0) {
                            teamOut(1)
                            return
                        }
                    } else if (team[e.entity] == 2) {
                        Constant.blueHeart--
                        Bukkit.broadcastMessage(
                            ("${ChatColor.GREEN}블루 팀의 플레이어가 사망했습니다! (블루 팀 남은 하트 " + Constant.blueHeart
                                    + "${ChatColor.RED}♥${ChatColor.GREEN})")
                        )
                        if (Constant.blueHeart == 0) {
                            teamOut(2)
                            return
                        }
                    } else if (team[e.entity] == 3) {
                        Constant.yellowHeart--
                        Bukkit.broadcastMessage(
                            ("${ChatColor.GREEN}옐로 팀의 플레이어가 사망했습니다! (옐로 팀 남은 하트 " + Constant.yellowHeart
                                    + "${ChatColor.RED}♥${ChatColor.GREEN})")
                        )
                        if (Constant.yellowHeart == 0) {
                            teamOut(3)
                            return
                        }
                    } else if (team[e.entity] == 4) {
                        Constant.greenHeart--
                        Bukkit.broadcastMessage(
                            ("${ChatColor.GREEN}그린 팀의 플레이어가 사망했습니다! (그린 팀 남은 하트 " + Constant.greenHeart
                                    + "${ChatColor.RED}♥${ChatColor.GREEN})")
                        )
                        if (Constant.greenHeart == 0) {
                            teamOut(4)
                            return
                        }
                    }
                    (e.entity as Player).sendTitle("${ChatColor.BLUE}5", "", 0, 20, 0)
                    plugin.delayTask(20) {
                        (e.entity as Player).sendTitle("${ChatColor.GREEN}4", "", 0, 20, 0)
                    }
                    plugin.delayTask(40) {
                        (e.entity as Player).sendTitle("${ChatColor.YELLOW}3", "", 0, 20, 0)
                    }
                    plugin.delayTask(60) {
                        (e.entity as Player).sendTitle("${ChatColor.GOLD}2", "", 0, 20, 0)
                    }
                    plugin.delayTask(80) {
                        (e.entity as Player).sendTitle("${ChatColor.RED}1", "", 0, 20, 0)
                    }
                    plugin.delayTask(100) {
                        when {
                            team[e.entity] == 1 -> {
                                e.entity.teleport(Constant.redSpawn!!)
                            }
                            team[e.entity] == 2 -> {
                                e.entity.teleport(Constant.blueSpawn!!)
                            }
                            team[e.entity] == 3 -> {
                                e.entity.teleport(Constant.yellowSpawn!!)
                            }
                            team[e.entity] == 4 -> {
                                e.entity.teleport(Constant.greenSpawn!!)
                            }
                        }
                        (e.entity as Player).gameMode = GameMode.SURVIVAL
                        (e.entity as Player).sendTitle("${ChatColor.DARK_RED}리스폰!", "${ChatColor.GRAY}무적 시간입니다", 0, 60, 0)
                        invincibility[e.entity as Player] = true
                    }

                    plugin.delayTask(160) {
                        if (invincibility.containsKey(e.entity as Player)) {
                            invincibility.remove(e.entity as Player)
                        }
                    }

                }
            }
        }
    }

    // 채팅하는 이벤트
    @EventHandler
    fun onChat(e: PlayerChatEvent) {
        if (Constant.started) {
            if (teamChat[e.player]!!) {
                e.isCancelled = true
                println("[Team Chat] <${e.player.name}> ${e.message}")
                for (p: Player in Bukkit.getOnlinePlayers()) {
                    if (team[e.player] == team[p]) {
                        when (team[e.player]) {
                            0 -> p.sendMessage(
                                ("[${ChatColor.GRAY}SPECTATOR${ChatColor.RESET}] <"
                                        + e.player.name + "> " + e.message)
                            )
                            1 -> p.sendMessage(
                                ("[${ChatColor.RED}RED${ChatColor.RESET}] <" + e.player.name
                                        + "> " + e.message)
                            )
                            2 -> p.sendMessage(
                                "[${ChatColor.BLUE}BLUE${ChatColor.RESET}] <${e.player.name}> ${e.message}"
                            )
                            3 -> p.sendMessage(
                                "[${ChatColor.YELLOW}YELLOW${ChatColor.RESET}] <${e.player.name}> ${e.message}"
                            )
                            4 -> p.sendMessage(
                                "[${ChatColor.GREEN}GREEN${ChatColor.RESET}] <${e.player.name}> ${e.message}"
                            )
                        }
                    }
                }
            }
        }
    }

    // 좀벌레가 돌에 들어가는 이벤트
    @EventHandler
    fun onInfest(e: EntityChangeBlockEvent) {
        if (e.entityType == EntityType.SILVERFISH) {
            e.isCancelled = true
        }
    }

    // 플레이어 접속 이벤트
    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        Constant.money[e.player] = 0
        team[e.player] = 0
        Constant.attacking[e.player] = false
        teamChat[e.player] = false
        e.player.gameMode = GameMode.ADVENTURE
        e.player.teleport(Constant.spawn!!)
        if (Constant.started) {
            e.player.gameMode = GameMode.SPECTATOR
            e.player.sendMessage("${ChatColor.RED}게임 중에 접속하였습니다. 자동으로 관전 모드가 됩니다.")
            Bukkit.getScoreboardManager()!!.mainScoreboard.getEntryTeam(e.player.name)!!.removeEntry(e.player.name)
        }
    }

    // 플레이어 접속 종료 이벤트
    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        when {
            team[e.player] == 1 -> {
                for (p: Player in Bukkit.getOnlinePlayers()) {
                    if (team[p] == 1) {
                        return
                    }
                }
                teamOut(1)
            }
            team[e.player] == 2 -> {
                for (p: Player in Bukkit.getOnlinePlayers()) {
                    if (team[p] == 2) {
                        return
                    }
                }
                teamOut(2)
            }
            team[e.player] == 3 -> {
                for (p: Player in Bukkit.getOnlinePlayers()) {
                    if (team[p] == 3) {
                        return
                    }
                }
                teamOut(3)
            }
            team[e.player] == 4 -> {
                for (p: Player in Bukkit.getOnlinePlayers()) {
                    if (team[p] == 4) {
                        return
                    }
                }
                teamOut(4)
            }
        }
    }

    // 폭팔 이벤트
    @EventHandler
    fun onEntityExplode(e: EntityExplodeEvent) {
        e.blockList().clear()
    }

    // 블럭 파괴 이벤트
    @EventHandler
    fun onBreak(e: BlockBreakEvent) {
        if (Constant.started) {
            if (Constant.attacking.containsKey(e.player) && Constant.attacking[e.player]!!) {
                e.isCancelled = true
                e.player.sendMessage("${ChatColor.RED}적진에서 자원을 얻을 수 없습니다")
                return
            }
            e.isCancelled = true
            val count = when (team[e.player]!!) {
                1 -> if (Constant.BUFF_FORTUNE.red * 0.15 > Math.random()) if (Math.random() < 0.5) if (Math.random() < 0.5) if (Math.random() < 0.5) 5 else 4 else 3 else 2 else 1
                2 -> if (Constant.BUFF_FORTUNE.blue * 0.15 > Math.random()) if (Math.random() < 0.5) if (Math.random() < 0.5) if (Math.random() < 0.5) 5 else 4 else 3 else 2 else 1
                3 -> if (Constant.BUFF_FORTUNE.yellow * 0.15 > Math.random()) if (Math.random() < 0.5) if (Math.random() < 0.5) if (Math.random() < 0.5) 5 else 4 else 3 else 2 else 1
                4 -> if (Constant.BUFF_FORTUNE.green * 0.15 > Math.random()) if (Math.random() < 0.5) if (Math.random() < 0.5) if (Math.random() < 0.5) 5 else 4 else 3 else 2 else 1
                else -> 0
            }
            if (e.block.type == Material.OAK_LOG) {
                if (e.player.inventory.firstEmpty() != -1) {
                    e.player.inventory.addItem(Constant.getItemStack(
                        Material.OAK_LOG, count * 10 - 9, "${ChatColor.GREEN}${ChatColor.BOLD}나무",
                        listOf("${ChatColor.GREEN}맨 처음에 제일 중요한 재료 아이템.")
                    ))
                    if (e.player.inventory.itemInMainHand.type == Material.GOLDEN_AXE) {
                        if (Math.random() < 0.2) {
                            e.player.inventory.addItem(
                                Constant.getItemStack(
                                    Material.GOLD_INGOT, count, "${ChatColor.GREEN}${ChatColor.BOLD}금괴",
                                    listOf("${ChatColor.GREEN}비싸게 거래되며 회복 아이템을 얻기 위한 재료 아이템.")
                                )
                            )
                        }
                    }
                } else {
                    e.player.world.dropItem(
                        e.player.location,
                        Constant.getItemStack(
                            Material.OAK_LOG, count * 10 - 9, "${ChatColor.GREEN}${ChatColor.BOLD}나무",
                            listOf("${ChatColor.GREEN}맨 처음에 제일 중요한 재료 아이템.")
                        )
                    )
                    if (e.player.inventory.itemInMainHand.type == Material.GOLDEN_AXE) {
                        if (Math.random() < 0.2) {
                            e.player.world.dropItem(
                                e.player.location,
                                Constant.getItemStack(
                                    Material.GOLD_INGOT, count, "${ChatColor.GREEN}${ChatColor.BOLD}금괴",
                                    listOf("${ChatColor.GREEN}비싸게 거래되며 회복 아이템을 얻기 위한 재료 아이템.")
                                )
                            )
                        }
                    }
                }
            } else if (e.block.type == Material.STONE) {
                if ((e.player.itemInHand.type == Material.WOODEN_PICKAXE
                            ) || (e.player.itemInHand.type == Material.STONE_PICKAXE
                            ) || (e.player.itemInHand.type == Material.IRON_PICKAXE
                            ) || (e.player.itemInHand.type == Material.DIAMOND_PICKAXE)
                ) {
                    placeOreBlock(e.block)
                    if (e.player.inventory.firstEmpty() != -1) {
                        e.player.inventory.addItem(Constant.getItemStack(
                            Material.COBBLESTONE, 1, "${ChatColor.GREEN}${ChatColor.BOLD}조약돌",
                            listOf("${ChatColor.GREEN}흔하지만 귀중한 재료 아이템.")
                        ))
                    } else {
                        e.player.world.dropItem(
                            e.player.location,
                            Constant.getItemStack(
                                Material.COBBLESTONE, 1, "${ChatColor.GREEN}${ChatColor.BOLD}조약돌",
                                listOf("${ChatColor.GREEN}흔하지만 귀중한 재료 아이템.")
                            )
                        )
                    }
                } else {
                    e.player.sendMessage("${ChatColor.RED}곡괭이가 너무 약한 것 같다")
                }
            } else if (e.block.type == Material.IRON_ORE) {
                if ((e.player.itemInHand.type == Material.STONE_PICKAXE
                            ) || (e.player.itemInHand.type == Material.IRON_PICKAXE
                            ) || (e.player.itemInHand.type == Material.DIAMOND_PICKAXE)
                ) {
                    placeOreBlock(e.block)
                    if (e.player.inventory.firstEmpty() != -1) {
                        e.player.inventory.addItem(Constant.getItemStack(
                            Material.IRON_INGOT, count, "${ChatColor.GREEN}${ChatColor.BOLD}철괴",
                            listOf("${ChatColor.GREEN}초반 장비를 빠르게 얻기 위해 반드시 필요한 재료 아이템.")
                        ))
                    } else {
                        e.player.world.dropItem(
                            e.player.location,
                            Constant.getItemStack(
                                Material.IRON_INGOT, count, "${ChatColor.GREEN}${ChatColor.BOLD}철괴",
                                listOf("${ChatColor.GREEN}초반 장비를 빠르게 얻기 위해 반드시 필요한 재료 아이템.")
                            )
                        )
                    }
                } else {
                    e.player.sendMessage("${ChatColor.RED}곡괭이가 너무 약한 것 같다")
                }
            } else if (e.block.type == Material.GOLD_ORE) {
                if ((e.player.itemInHand.type == Material.IRON_PICKAXE
                            || e.player.itemInHand.type == Material.DIAMOND_PICKAXE)
                ) {
                    placeOreBlock(e.block)
                    if (team.containsKey(e.player)) {
                        if (e.player.inventory.firstEmpty() != -1) {
                            e.player.inventory.addItem(
                                Constant.getItemStack(
                                    Material.GOLD_INGOT, count, "${ChatColor.GREEN}${ChatColor.BOLD}금괴",
                                    listOf("${ChatColor.GREEN}비싸게 거래되며 회복 아이템을 얻기 위한 재료 아이템.")
                                )
                            )
                        } else {
                            e.player.world.dropItem(
                                e.player.location,
                                Constant.getItemStack(
                                    Material.GOLD_INGOT, count, "${ChatColor.GREEN}${ChatColor.BOLD}금괴",
                                    listOf("${ChatColor.GREEN}비싸게 거래되며 회복 아이템을 얻기 위한 재료 아이템.")
                                )
                            )
                        }
                    }
                } else {
                    e.player.sendMessage("${ChatColor.RED}곡괭이가 너무 약한 것 같다")
                }
            } else if (e.block.type == Material.DIAMOND_ORE) {
                if ((e.player.itemInHand.type == Material.IRON_PICKAXE
                            || e.player.itemInHand.type == Material.DIAMOND_PICKAXE)
                ) {
                    placeOreBlock(e.block)
                    if (team.containsKey(e.player)) {
                        if (e.player.inventory.firstEmpty() != -1) {
                            e.player.inventory.addItem(
                                Constant.getItemStack(
                                    Material.DIAMOND, count, "${ChatColor.GREEN}${ChatColor.BOLD}다이아몬드",
                                    listOf("${ChatColor.GREEN}후반 필수 재료 아이템.")
                                )
                            )
                        } else {
                            e.player.world.dropItem(
                                e.player.location,
                                Constant.getItemStack(
                                    Material.DIAMOND, count, "${ChatColor.GREEN}${ChatColor.BOLD}다이아몬드",
                                    listOf("${ChatColor.GREEN}후반 필수 재료 아이템.")
                                )
                            )
                        }
                    }
                } else {
                    e.player.sendMessage("${ChatColor.RED}곡괭이가 너무 약한 것 같다")
                }
            } else if (e.block.type == Material.ANCIENT_DEBRIS) {
                if (e.player.itemInHand.type == Material.DIAMOND_PICKAXE) {
                    placeOreBlock(e.block)
                    if (e.player.inventory.firstEmpty() != -1) {
                        e.player.inventory.addItem(Constant.getItemStack(
                            Material.NETHERITE_INGOT, count,
                            "${ChatColor.GREEN}${ChatColor.BOLD}네더라이트",
                            listOf("${ChatColor.GREEN}전설에 도달한 재료 아이템.")
                        ))
                    } else {
                        e.player.world.dropItem(
                            e.player.location,
                            Constant.getItemStack(
                                Material.NETHERITE_INGOT, count,
                                "${ChatColor.GREEN}${ChatColor.BOLD}네더라이트",
                                listOf("${ChatColor.GREEN}전설에 도달한 재료 아이템.")
                            )
                        )
                    }
                } else {
                    e.player.sendMessage("${ChatColor.RED}곡괭이가 너무 약한 것 같다")
                }
            }
        }
    }

    // 블럭 설치 이벤트
    @EventHandler
    fun onPlace(e: BlockPlaceEvent) {
        if (Constant.started) {
            e.isCancelled = true
        }
    }

    // 랜덤 광물 생성 함수
    private fun placeOreBlock(block: Block) {
        val random = (Math.random() * 100).toInt()
        when {
            random < 64 -> block.type = Material.STONE
            random < 87 -> block.type = Material.IRON_ORE
            random < 93 -> block.type = Material.GOLD_ORE
            random < 97 -> block.type = Material.DIAMOND_ORE
            else -> block.type = Material.ANCIENT_DEBRIS
        }
    }

    // 타겟 이벤트 (팀원 타겟 방지)
    @EventHandler
    fun onTarget(e: EntityTargetEvent) {
        try {
            if (team.containsKey(e.entity)) {
                if (team.containsKey(e.target!!)) {
                    if (team[e.entity] == team[e.target!!]) {
                        e.isCancelled = true
                    }
                }
            } else if (e.target !is Player) {
                e.isCancelled = true
            }
        } catch (e: Exception) {

        }
    }

    // 제작 이벤트
    @EventHandler
    fun onCraft(e: CraftItemEvent) {
        e.isCancelled = true
    }

    // 인벤토리 클릭 이벤트 (상점)
    @EventHandler
    fun onClickOnInventory(e: InventoryClickEvent) {
        if (Constant.started && e.whoClicked.gameMode == GameMode.SPECTATOR) {
            e.isCancelled = true
            return
        }
        if (e.view.title == "${ChatColor.GREEN}재료 상점") {

            // 재료 상점
            // 재료 상점
            // 재료 상점

            e.isCancelled = true
            if (e.isLeftClick) {
                if (!e.isShiftClick) {
                    // 좌클
                    materialShopLeftClick(e.rawSlot, e.whoClicked)
                } else {
                    // 쉬프트 좌클
                    materialShopShiftLeftClick(e.rawSlot, e.whoClicked)
                }
            } else if (e.isRightClick) {
                if (!e.isShiftClick) {
                    // 우클
                    materialShopRightClick(e.rawSlot, e.whoClicked)
                } else {
                    // 쉬프트 우클
                    materialShopShiftRightClick(e.rawSlot, e.whoClicked)
                }
            } else if (e.click == ClickType.MIDDLE) {
                println(e.rawSlot)
                val msi: MaterialShopItem = when (e.rawSlot) {
                    10 -> MaterialShopItem.OAK_LOG
                    11 -> MaterialShopItem.STICK
                    12 -> MaterialShopItem.STONE
                    13 -> MaterialShopItem.IRON
                    14 -> MaterialShopItem.GOLD
                    15 -> MaterialShopItem.DIAMOND
                    16 -> MaterialShopItem.NETHERITE
                    else -> return
                }
                if (msi.autoSell.contains(e.whoClicked)) {
                    msi.autoSell.remove(e.whoClicked)
                    e.whoClicked.sendMessage("${ChatColor.RED}자동 판매 중지")
                } else {
                    msi.autoSell.add(e.whoClicked)
                    e.whoClicked.sendMessage("${ChatColor.GREEN}자동 판매 시작")
                }
            }
        } else if ((e.view.title == "${ChatColor.GREEN}몬스터 상점")) {

            // 몬스터 상점
            // 몬스터 상점
            // 몬스터 상점

            e.isCancelled = true
            if (e.rawSlot == 10) {
                summon(e.whoClicked as Player, MonsterItem.SILVERFISH)
            } else if (e.rawSlot == 11) {
                summon(e.whoClicked as Player, MonsterItem.ZOMBIE)
            } else if (e.rawSlot == 12) {
                summon(e.whoClicked as Player, MonsterItem.SKELETON)
            } else if (e.rawSlot == 13) {
                summon(e.whoClicked as Player, MonsterItem.SPIDER)
            } else if (e.rawSlot == 14) {
                summon(e.whoClicked as Player, MonsterItem.CREEPER)
            } else if (e.rawSlot == 15) {
                summon(e.whoClicked as Player, MonsterItem.PHANTOM)
            } else if (e.rawSlot == 16) {
                summon(e.whoClicked as Player, MonsterItem.WITHER_SKELETON)
            } else if (e.rawSlot == 19) {
                summon(e.whoClicked as Player, MonsterItem.IRON_GOLEM)
            } else if (e.rawSlot == 20) {
                summon(e.whoClicked as Player, MonsterItem.RAVAGER)
            } else if (e.rawSlot == 21) {
                if (Constant.money.containsKey(e.whoClicked as Player)) {
                    if (Constant.money[e.whoClicked as Player]!! >= 100000) {
                        for (p: Player in Bukkit.getOnlinePlayers()) {
                            if (Constant.attacking.containsKey(p)) {
                                if (Constant.attacking[p]!!) {
                                    if (team[p] != team[e.whoClicked]) {
                                        e.whoClicked.sendMessage("${ChatColor.RED}이미 누군가가 먼저 이 팀에 공격을 시도했습니다!")
                                        return
                                    }
                                }
                            }
                        }
                        if (Constant.attacking.containsKey(e.whoClicked as Player) && !Constant.attacking[e.whoClicked as Player]!!) {
                            if (!Constant.coolTime.containsKey(e.whoClicked)) {
                                Constant.money[e.whoClicked as Player] = Constant.money[e.whoClicked as Player]!! - 100000
                                Constant.attacking[e.whoClicked as Player] = true
                                if (Constant.multi) {
                                    val inventory = Bukkit.createInventory(null, 27, "${ChatColor.GREEN}공격 할 팀 선택")
                                    when (team[e.whoClicked]) {
                                        1 -> {
                                            if (Constant.blueLive) inventory.setItem(
                                                12,
                                                Constant.getItemStack(
                                                    Material.BLUE_CONCRETE,
                                                    1,
                                                    "${ChatColor.GREEN}블루 팀으로 공격",
                                                    listOf()
                                                )
                                            ) else inventory.setItem(
                                                12,
                                                Constant.getItemStack(
                                                    Material.BLACK_CONCRETE,
                                                    1,
                                                    "${ChatColor.RED}탈락한 팀",
                                                    listOf()
                                                )
                                            )
                                            if (Constant.yellowLive) inventory.setItem(
                                                13,
                                                Constant.getItemStack(
                                                    Material.YELLOW_CONCRETE,
                                                    1,
                                                    "${ChatColor.GREEN}옐로 팀으로 공격",
                                                    listOf()
                                                )
                                            ) else inventory.setItem(
                                                13,
                                                Constant.getItemStack(
                                                    Material.BLACK_CONCRETE,
                                                    1,
                                                    "${ChatColor.RED}탈락한 팀",
                                                    listOf()
                                                )
                                            )
                                            if (Constant.greenLive) inventory.setItem(
                                                14,
                                                Constant.getItemStack(
                                                    Material.GREEN_CONCRETE,
                                                    1,
                                                    "${ChatColor.GREEN}그린 팀으로 공격",
                                                    listOf()
                                                )
                                            ) else inventory.setItem(
                                                14,
                                                Constant.getItemStack(
                                                    Material.BLACK_CONCRETE,
                                                    1,
                                                    "${ChatColor.RED}탈락한 팀",
                                                    listOf()
                                                )
                                            )
                                        }
                                        2 -> {
                                            if (Constant.redLive) inventory.setItem(
                                                12,
                                                Constant.getItemStack(
                                                    Material.RED_CONCRETE,
                                                    1,
                                                    "${ChatColor.GREEN}레드 팀으로 공격",
                                                    listOf()
                                                )
                                            ) else inventory.setItem(
                                                12,
                                                Constant.getItemStack(
                                                    Material.BLACK_CONCRETE,
                                                    1,
                                                    "${ChatColor.RED}탈락한 팀",
                                                    listOf()
                                                )
                                            )
                                            if (Constant.yellowLive) inventory.setItem(
                                                13,
                                                Constant.getItemStack(
                                                    Material.YELLOW_CONCRETE,
                                                    1,
                                                    "${ChatColor.GREEN}옐로 팀으로 공격",
                                                    listOf()
                                                )
                                            ) else inventory.setItem(
                                                13,
                                                Constant.getItemStack(
                                                    Material.BLACK_CONCRETE,
                                                    1,
                                                    "${ChatColor.RED}탈락한 팀",
                                                    listOf()
                                                )
                                            )
                                            if (Constant.greenLive) inventory.setItem(
                                                14,
                                                Constant.getItemStack(
                                                    Material.GREEN_CONCRETE,
                                                    1,
                                                    "${ChatColor.GREEN}그린 팀으로 공격",
                                                    listOf()
                                                )
                                            ) else inventory.setItem(
                                                14,
                                                Constant.getItemStack(
                                                    Material.BLACK_CONCRETE,
                                                    1,
                                                    "${ChatColor.RED}탈락한 팀",
                                                    listOf()
                                                )
                                            )
                                        }
                                        3 -> {
                                            if (Constant.redLive) inventory.setItem(
                                                12,
                                                Constant.getItemStack(
                                                    Material.RED_CONCRETE,
                                                    1,
                                                    "${ChatColor.GREEN}레드 팀으로 공격",
                                                    listOf()
                                                )
                                            ) else inventory.setItem(
                                                12,
                                                Constant.getItemStack(
                                                    Material.BLACK_CONCRETE,
                                                    1,
                                                    "${ChatColor.RED}탈락한 팀",
                                                    listOf()
                                                )
                                            )
                                            if (Constant.blueLive) inventory.setItem(
                                                13,
                                                Constant.getItemStack(
                                                    Material.BLUE_CONCRETE,
                                                    1,
                                                    "${ChatColor.GREEN}블루 팀으로 공격",
                                                    listOf()
                                                )
                                            ) else inventory.setItem(
                                                13,
                                                Constant.getItemStack(
                                                    Material.BLACK_CONCRETE,
                                                    1,
                                                    "${ChatColor.RED}탈락한 팀",
                                                    listOf()
                                                )
                                            )
                                            if (Constant.greenLive) inventory.setItem(
                                                14,
                                                Constant.getItemStack(
                                                    Material.GREEN_CONCRETE,
                                                    1,
                                                    "${ChatColor.GREEN}그린 팀으로 공격",
                                                    listOf()
                                                )
                                            ) else inventory.setItem(
                                                14,
                                                Constant.getItemStack(
                                                    Material.BLACK_CONCRETE,
                                                    1,
                                                    "${ChatColor.RED}탈락한 팀",
                                                    listOf()
                                                )
                                            )
                                        }
                                        4 -> {
                                            if (Constant.redLive) inventory.setItem(
                                                12,
                                                Constant.getItemStack(
                                                    Material.RED_CONCRETE,
                                                    1,
                                                    "${ChatColor.GREEN}레드 팀으로 공격",
                                                    listOf()
                                                )
                                            ) else inventory.setItem(
                                                12,
                                                Constant.getItemStack(
                                                    Material.BLACK_CONCRETE,
                                                    1,
                                                    "${ChatColor.RED}탈락한 팀",
                                                    listOf()
                                                )
                                            )
                                            if (Constant.blueLive) inventory.setItem(
                                                13,
                                                Constant.getItemStack(
                                                    Material.BLUE_CONCRETE,
                                                    1,
                                                    "${ChatColor.GREEN}블루 팀으로 공격",
                                                    listOf()
                                                )
                                            ) else inventory.setItem(
                                                13,
                                                Constant.getItemStack(
                                                    Material.BLACK_CONCRETE,
                                                    1,
                                                    "${ChatColor.RED}탈락한 팀",
                                                    listOf()
                                                )
                                            )
                                            if (Constant.yellowLive) inventory.setItem(
                                                14,
                                                Constant.getItemStack(
                                                    Material.YELLOW_CONCRETE,
                                                    1,
                                                    "${ChatColor.GREEN}옐로 팀으로 공격",
                                                    listOf()
                                                )
                                            ) else inventory.setItem(
                                                14,
                                                Constant.getItemStack(
                                                    Material.BLACK_CONCRETE,
                                                    1,
                                                    "${ChatColor.RED}탈락한 팀",
                                                    listOf()
                                                )
                                            )
                                        }
                                    }
                                    e.whoClicked.openInventory(inventory)
                                } else {
                                    if (team[e.whoClicked] == 2) {
                                        e.whoClicked.teleport(Constant.redSpawn!!)
                                        Constant.coolTime[e.whoClicked as Player] = 1200
                                        team.filterValues { it == 2 }.filterKeys { it is Player }.forEach { (t,_) ->
                                            if (Constant.coolTime.containsKey(t)) {
                                                Constant.coolTime[t as Player] = Constant.coolTime[t]!! + 100
                                            }
                                        }
                                    } else if (team[e.whoClicked] == 1) {
                                        e.whoClicked.teleport(Constant.blueSpawn!!)
                                        Constant.coolTime[e.whoClicked as Player] = 1200
                                        team.filterValues { it == 1 }.filterKeys { it is Player }.forEach { (t,_) ->
                                            if (Constant.coolTime.containsKey(t)) {
                                                Constant.coolTime[t as Player] = Constant.coolTime[t]!! + 100
                                            }
                                        }
                                    }
                                    plugin.delayTask(600) {
                                        if (Constant.attacking.containsKey(e.whoClicked as Player) && Constant.attacking[e.whoClicked as Player]!!) {
                                            (e.whoClicked as Player).health = e.whoClicked.maxHealth
                                            if (team[e.whoClicked] == 2) {
                                                e.whoClicked.teleport(Constant.blueSpawn!!)
                                            } else if (team[e.whoClicked] == 1) {
                                                e.whoClicked.teleport(Constant.redSpawn!!)
                                            }
                                            Constant.attacking[e.whoClicked as Player] = false
                                        }
                                    }
                                }
                            } else {
                                e.whoClicked.sendMessage("${ChatColor.RED}플레이어 소환까지 남은 쿨타임 : ${Constant.coolTime[e.whoClicked]}틱")
                            }
                        }
                    } else {
                        e.whoClicked.sendMessage("${ChatColor.RED}돈이 부족합니다")
                    }
                }
            }
        } else if ((e.view.title == "${ChatColor.GREEN}버프 상점")) {

            // 버프 상점
            // 버프 상점
            // 버프 상점

            e.isCancelled = true
            var clickedItem: BuffItem? = null
            when (e.rawSlot) {
                10 -> clickedItem = Constant.BUFF_EFFICIENCY
                11 -> clickedItem = Constant.BUFF_SHARPNESS
                12 -> clickedItem = Constant.BUFF_PROTECTION
                13 -> clickedItem = Constant.BUFF_MONSTER_HEALTH
                14 -> clickedItem = Constant.BUFF_MONSTER_DAMAGE
                15 -> clickedItem = Constant.BUFF_FORTUNE
            }
            val team: Int = team[e.whoClicked as Player]!!
            if (clickedItem != null) {
                if (team == 1) {
                    if (clickedItem.red < 7) {
                        if (e.whoClicked.inventory.contains(
                                Material.DIAMOND,
                                clickedItem.price[clickedItem.red]
                            )
                        ) {
                            val hm = HashMap<Material, Int>()
                            hm[Material.DIAMOND] = clickedItem.price[clickedItem.red]
                            removeItems(e.whoClicked as Player, hm)
                            clickedItem.red++
                            for (p: Player in Bukkit.getOnlinePlayers()) {
                                if (Constant.team[p] == Constant.team[e.whoClicked]) {
                                    p.sendMessage("${ChatColor.GREEN}${e.whoClicked.name}님이 팀 버프${clickedItem}을 강화했습니다 (${clickedItem.red})")
                                }
                            }
                            if (clickedItem.red < 7) {
                                e.clickedInventory!!.setItem(
                                    e.rawSlot, Constant.getItemStack(
                                        clickedItem.material,
                                        1, clickedItem.name,
                                        listOf(
                                            "${ChatColor.GRAY}강화(${toRomaNum(clickedItem.red)} > ${toRomaNum(clickedItem.red + 1)}) - 다이아 ${clickedItem.price[clickedItem.red]}x", clickedItem.lore
                                        )
                                    )
                                )
                            } else {
                                e.clickedInventory!!.setItem(
                                    e.rawSlot,
                                    Constant.getItemStack(
                                        clickedItem.material, 1, clickedItem.name,
                                        listOf("${ChatColor.GRAY}강화(최대 레벨)", clickedItem.lore)
                                    )
                                )
                            }
                        } else e.whoClicked.sendMessage("${ChatColor.RED}재료가 부족합니다")
                    } else e.whoClicked.sendMessage("${ChatColor.RED}이미 최대 레벨입니다")
                } else if (team == 2) {
                    if (clickedItem.blue < 7) {
                        if (e.whoClicked.inventory.contains(
                                Material.DIAMOND,
                                clickedItem.price[clickedItem.blue]
                            )
                        ) {
                            val hm = HashMap<Material, Int>()
                            hm[Material.DIAMOND] = clickedItem.price[clickedItem.blue]
                            removeItems(e.whoClicked as Player, hm)
                            clickedItem.blue++
                            for (p: Player in Bukkit.getOnlinePlayers()) {
                                if (Constant.team[p] == Constant.team[e.whoClicked]) {
                                    p.sendMessage("${ChatColor.GREEN}${e.whoClicked.name}님이 팀 버프${clickedItem}을 강화했습니다 (${clickedItem.blue})")
                                }
                            }
                            if (clickedItem.blue < 7) {
                                e.clickedInventory!!.setItem(
                                    e.rawSlot,
                                    Constant.getItemStack(
                                        clickedItem.material, 1, clickedItem.name,
                                        listOf(
                                            "${ChatColor.GRAY}강화(${toRomaNum(clickedItem.blue)} > ${toRomaNum(clickedItem.blue + 1)}) - 다이아 ${clickedItem.price[clickedItem.blue]}x", clickedItem.lore
                                        )
                                    )
                                )
                            } else {
                                e.clickedInventory!!.setItem(
                                    e.rawSlot,
                                    Constant.getItemStack(
                                        clickedItem.material, 1, clickedItem.name,
                                        listOf("${ChatColor.GRAY}강화(최대 레벨)", clickedItem.lore)
                                    )
                                )
                            }
                        } else e.whoClicked.sendMessage("${ChatColor.RED}재료가 부족합니다")
                    } else e.whoClicked.sendMessage("${ChatColor.RED}이미 최대 레벨입니다")
                } else if (team == 3) {
                    if (clickedItem.yellow < 7) {
                        if (e.whoClicked.inventory.contains(
                                Material.DIAMOND,
                                clickedItem.price[clickedItem.yellow]
                            )
                        ) {
                            val hm = HashMap<Material, Int>()
                            hm[Material.DIAMOND] = clickedItem.price[clickedItem.yellow]
                            removeItems(e.whoClicked as Player, hm)
                            clickedItem.yellow++
                            for (p: Player in Bukkit.getOnlinePlayers()) {
                                if (Constant.team[p] == Constant.team[e.whoClicked]) {
                                    p.sendMessage("${ChatColor.GREEN}${e.whoClicked.name}님이 팀 버프${clickedItem}을 강화했습니다 (${clickedItem.yellow})")
                                }
                            }
                            if (clickedItem.yellow < 7) {
                                e.clickedInventory!!.setItem(
                                    e.rawSlot,
                                    Constant.getItemStack(
                                        clickedItem.material, 1, clickedItem.name,
                                        listOf(
                                            "${ChatColor.GRAY}강화(${toRomaNum(clickedItem.yellow)} > ${toRomaNum(clickedItem.yellow + 1)}) - 다이아 ${clickedItem.price[clickedItem.yellow]}x", clickedItem.lore
                                        )
                                    )
                                )
                            } else {
                                e.clickedInventory!!.setItem(
                                    e.rawSlot,
                                    Constant.getItemStack(
                                        clickedItem.material, 1, clickedItem.name,
                                        listOf("${ChatColor.GRAY}강화(최대 레벨)", clickedItem.lore)
                                    )
                                )
                            }
                        } else e.whoClicked.sendMessage("${ChatColor.RED}재료가 부족합니다")
                    } else e.whoClicked.sendMessage("${ChatColor.RED}이미 최대 레벨입니다")
                } else if (team == 4) {
                    if (clickedItem.green < 7) {
                        if (e.whoClicked.inventory.contains(
                                Material.DIAMOND,
                                clickedItem.price[clickedItem.green]
                            )
                        ) {
                            val hm = HashMap<Material, Int>()
                            hm[Material.DIAMOND] = clickedItem.price[clickedItem.green]
                            removeItems(e.whoClicked as Player, hm)
                            clickedItem.green++
                            for (p: Player in Bukkit.getOnlinePlayers()) {
                                if (Constant.team[p] == Constant.team[e.whoClicked]) {
                                    p.sendMessage("${ChatColor.GREEN}${e.whoClicked.name}님이 팀 버프${clickedItem}을 강화했습니다 (${clickedItem.green})")
                                }
                            }
                            if (clickedItem.green < 7) {
                                e.clickedInventory!!.setItem(
                                    e.rawSlot,
                                    Constant.getItemStack(
                                        clickedItem.material, 1, clickedItem.name,
                                        listOf(
                                            "${ChatColor.GRAY}강화(${toRomaNum(clickedItem.green)} > ${toRomaNum(clickedItem.green + 1)}) - 다이아 ${clickedItem.price[clickedItem.green]}x", clickedItem.lore
                                        )
                                    )
                                )
                            } else {
                                e.clickedInventory!!.setItem(
                                    e.rawSlot,
                                    Constant.getItemStack(
                                        clickedItem.material, 1, clickedItem.name,
                                        listOf("${ChatColor.GRAY}강화(최대 레벨)", clickedItem.lore)
                                    )
                                )
                            }
                        } else e.whoClicked.sendMessage("${ChatColor.RED}재료가 부족합니다")
                    } else e.whoClicked.sendMessage("${ChatColor.RED}이미 최대 레벨입니다")
                } else e.whoClicked.sendMessage("${ChatColor.RED}팀을 찾을 수 없습니다")
            }
        } else if ((e.view.title == "${ChatColor.GREEN}무기 대장간")) {

            // 무기 대장간
            // 무기 대장간
            // 무기 대장간

            e.isCancelled = true
            var clickedItem: WeaponItem? = null
            when (e.rawSlot) {
                10 -> clickedItem = WeaponItem.WOOD_SWORD
                11 -> clickedItem = WeaponItem.STONE_SWORD
                12 -> clickedItem = WeaponItem.IRON_SWORD
                13 -> clickedItem = WeaponItem.DIAMOND_SWORD
                14 -> clickedItem = WeaponItem.NETHERITE_SWORD
                15 -> clickedItem = WeaponItem.GOLDEN_SWORD
            }
            if (clickedItem != null) {
                if (e.whoClicked.inventory.firstEmpty() != -1) {
                    if (removeItems(e.whoClicked as Player, clickedItem.items)) {
                        if (clickedItem.unbreakable) {
                            e.whoClicked.inventory.addItem(
                                Constant.getUnbreakableItemStack(
                                    clickedItem.material,
                                    1, clickedItem.itemname, listOf(clickedItem.lore)
                                )
                            )
                        } else {
                            e.whoClicked.inventory.addItem(
                                Constant.getItemStack(
                                    clickedItem.material, 1,
                                    clickedItem.itemname, listOf(clickedItem.lore)
                                )
                            )
                        }
                    } else {
                        e.whoClicked.sendMessage("${ChatColor.RED}재료가 부족합니다")
                    }
                } else {
                    e.whoClicked.sendMessage("${ChatColor.RED}인벤토리 공간이 부족합니다")
                }
            }
        } else if ((e.view.title == "${ChatColor.GREEN}갑옷 대장간")) {

            // 갑옷 대장간
            // 갑옷 대장간
            // 갑옷 대장간

            e.isCancelled = true
            var clickedItem: ArmorItem? = null
            when (e.rawSlot) {
                10 -> clickedItem = ArmorItem.IRON_BOOTS
                11 -> clickedItem = ArmorItem.IRON_LEGGINGS
                12 -> clickedItem = ArmorItem.IRON_CHESPLATE
                13 -> clickedItem = ArmorItem.IRON_HELMET
                14 -> clickedItem = ArmorItem.DIAMOND_BOOTS
                15 -> clickedItem = ArmorItem.DIAMOND_LEGGINGS
                16 -> clickedItem = ArmorItem.DIAMOND_CHESTPLATE

                19 -> clickedItem = ArmorItem.DIAMOND_HELMET
                20 -> clickedItem = ArmorItem.NETHERITE_BOOTS
                21 -> clickedItem = ArmorItem.NETHERITE_LEGGINGS
                22 -> clickedItem = ArmorItem.NETHERITE_CHESTPLATE
                23 -> clickedItem = ArmorItem.NETHERITE_HELMET
                24 -> clickedItem = ArmorItem.GOLDEN_CHESTPLATE
            }
            if (clickedItem != null) {
                if (e.whoClicked.inventory.firstEmpty() != -1) {
                    if (removeItems(e.whoClicked as Player, clickedItem.items)) {
                        if (clickedItem.unbreakable) {
                            e.whoClicked.inventory.addItem(
                                Constant.getUnbreakableItemStack(
                                    clickedItem.material,
                                    1, clickedItem.itemname, listOf(clickedItem.lore)
                                )
                            )
                        } else {
                            e.whoClicked.inventory.addItem(
                                Constant.getItemStack(
                                    clickedItem.material, 1,
                                    clickedItem.itemname, listOf(clickedItem.lore)
                                )
                            )
                        }
                    } else {
                        e.whoClicked.sendMessage("${ChatColor.RED}재료가 부족합니다")
                    }
                } else {
                    e.whoClicked.sendMessage("${ChatColor.RED}인벤토리 공간이 부족합니다")
                }
            }
        } else if ((e.view.title == "${ChatColor.GREEN}도구 대장간")) {

            // 도구 대장간
            // 도구 대장간
            // 도구 대장간

            e.isCancelled = true
            var clickedItem: ToolItem? = null
            when (e.rawSlot) {
                10 -> clickedItem = ToolItem.WOOD_PICK
                11 -> clickedItem = ToolItem.WOOD_AXE
                12 -> clickedItem = ToolItem.STONE_PICK
                13 -> clickedItem = ToolItem.STONE_AXE
                14 -> clickedItem = ToolItem.IRON_PICK
                15 -> clickedItem = ToolItem.DIAMOND_PICK
                16 -> clickedItem = ToolItem.GOLDEN_AXE
            }
            if (clickedItem != null) {
                if (e.whoClicked.inventory.firstEmpty() != -1) {
                    if (removeItems(e.whoClicked as Player, clickedItem.items)) {
                        if (clickedItem.unbreakable) {
                            e.whoClicked.inventory.addItem(
                                Constant.getUnbreakableItemStack(
                                    clickedItem.material,
                                    1, clickedItem.itemname, listOf(clickedItem.lore)
                                )
                            )
                        } else {
                            e.whoClicked.inventory.addItem(
                                Constant.getItemStack(
                                    clickedItem.material, 1,
                                    clickedItem.itemname, listOf(clickedItem.lore)
                                )
                            )
                        }
                    } else {
                        e.whoClicked.sendMessage("${ChatColor.RED}재료가 부족합니다")
                    }
                } else {
                    e.whoClicked.sendMessage("${ChatColor.RED}인벤토리 공간이 부족합니다")
                }
            }
        } else if ((e.view.title == "${ChatColor.GREEN}기타 아이템 대장간")) {

            // 기타 아이템 대장간
            // 기타 아이템 대장간
            // 기타 아이템 대장간

            e.isCancelled = true
            var clickedItem: MisItem? = null
            when (e.rawSlot) {
                10 -> clickedItem = MisItem.SHIELD
                11 -> clickedItem = MisItem.GOLDEN_APPLE
                12 -> clickedItem = MisItem.ENC_GOLDEN_APPLE
            }
            if (clickedItem != null) {
                if (e.whoClicked.inventory.firstEmpty() != -1) {
                    if (removeItems(e.whoClicked as Player, clickedItem.items)) {
                        if (clickedItem.unbreakable) {
                            e.whoClicked.inventory.addItem(
                                Constant.getUnbreakableItemStack(
                                    clickedItem.material,
                                    1, clickedItem.itemname, listOf(clickedItem.lore)
                                )
                            )
                        } else {
                            e.whoClicked.inventory.addItem(
                                Constant.getItemStack(
                                    clickedItem.material, 1,
                                    clickedItem.itemname, listOf(clickedItem.lore)
                                )
                            )
                        }
                    } else {
                        e.whoClicked.sendMessage("${ChatColor.RED}재료가 부족합니다")
                    }
                } else {
                    e.whoClicked.sendMessage("${ChatColor.RED}인벤토리 공간이 부족합니다")
                }
            }
        } else if (e.view.title == "${ChatColor.GREEN}공격 할 팀 선택") {

            // 공격 할 팀 선택
            // 공격 할 팀 선택
            // 공격 할 팀 선택

            e.isCancelled = true
            when (team[e.whoClicked]) {
                1 -> {
                    when (e.rawSlot) {
                        12 -> {
                            if (Constant.blueLive) {
                                e.whoClicked.teleport(Constant.blueSpawn!!)
                                Constant.coolTime[e.whoClicked as Player] = 1200
                                plugin.delayTask(600) {
                                    if (Constant.attacking.containsKey(e.whoClicked as Player) && Constant.attacking[e.whoClicked as Player]!!) {
                                        (e.whoClicked as Player).health = 20.0
                                        e.whoClicked.teleport(Constant.redSpawn!!)
                                        Constant.attacking[e.whoClicked as Player] = false
                                    }
                                }
                            }
                        }
                        13 -> {
                            if (Constant.yellowLive) {
                                e.whoClicked.teleport(Constant.yellowSpawn!!)
                                Constant.coolTime[e.whoClicked as Player] = 1200
                                plugin.delayTask(600) {
                                    if (Constant.attacking.containsKey(e.whoClicked as Player) && Constant.attacking[e.whoClicked as Player]!!) {
                                        (e.whoClicked as Player).health = 20.0
                                        e.whoClicked.teleport(Constant.redSpawn!!)
                                        Constant.attacking[e.whoClicked as Player] = false
                                    }
                                }
                            }
                        }
                        14 -> {
                            if (Constant.blueLive) {
                                e.whoClicked.teleport(Constant.greenSpawn!!)
                                Constant.coolTime[e.whoClicked as Player] = 1200
                                plugin.delayTask(600) {
                                    if (Constant.attacking.containsKey(e.whoClicked as Player) && Constant.attacking[e.whoClicked as Player]!!) {
                                        (e.whoClicked as Player).health = 20.0
                                        e.whoClicked.teleport(Constant.redSpawn!!)
                                        Constant.attacking[e.whoClicked as Player] = false
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    when (e.rawSlot) {
                        12 -> {
                            if (Constant.redLive) {
                                e.whoClicked.teleport(Constant.redSpawn!!)
                                Constant.coolTime[e.whoClicked as Player] = 1200
                                plugin.delayTask(600) {
                                    if (Constant.attacking.containsKey(e.whoClicked as Player) && Constant.attacking[e.whoClicked as Player]!!) {
                                        (e.whoClicked as Player).health = 20.0
                                        e.whoClicked.teleport(Constant.blueSpawn!!)
                                        Constant.attacking[e.whoClicked as Player] = false
                                    }
                                }
                            }
                        }
                        13 -> {
                            if (Constant.yellowLive) {
                                e.whoClicked.teleport(Constant.yellowSpawn!!)
                                Constant.coolTime[e.whoClicked as Player] = 1200
                                plugin.delayTask(600) {
                                    if (Constant.attacking.containsKey(e.whoClicked as Player) && Constant.attacking[e.whoClicked as Player]!!) {
                                        (e.whoClicked as Player).health = 20.0
                                        e.whoClicked.teleport(Constant.blueSpawn!!)
                                        Constant.attacking[e.whoClicked as Player] = false
                                    }
                                }
                            }
                        }
                        14 -> {
                            if (Constant.greenLive) {
                                e.whoClicked.teleport(Constant.greenSpawn!!)
                                Constant.coolTime[e.whoClicked as Player] = 1200
                                plugin.delayTask(600) {
                                    if (Constant.attacking.containsKey(e.whoClicked as Player) && Constant.attacking[e.whoClicked as Player]!!) {
                                        (e.whoClicked as Player).health = 20.0
                                        e.whoClicked.teleport(Constant.blueSpawn!!)
                                        Constant.attacking[e.whoClicked as Player] = false
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    when (e.rawSlot) {
                        12 -> {
                            if (Constant.redLive) {
                                e.whoClicked.teleport(Constant.redSpawn!!)
                                Constant.coolTime[e.whoClicked as Player] = 1200
                                plugin.delayTask(600) {
                                    if (Constant.attacking.containsKey(e.whoClicked as Player) && Constant.attacking[e.whoClicked as Player]!!) {
                                        (e.whoClicked as Player).health = 20.0
                                        e.whoClicked.teleport(Constant.yellowSpawn!!)
                                        Constant.attacking[e.whoClicked as Player] = false
                                    }
                                }
                            }
                        }
                        13 -> {
                            if (Constant.blueLive) {
                                e.whoClicked.teleport(Constant.blueSpawn!!)
                                Constant.coolTime[e.whoClicked as Player] = 1200
                                plugin.delayTask(600) {
                                    if (Constant.attacking.containsKey(e.whoClicked as Player) && Constant.attacking[e.whoClicked as Player]!!) {
                                        (e.whoClicked as Player).health = 20.0
                                        e.whoClicked.teleport(Constant.yellowSpawn!!)
                                        Constant.attacking[e.whoClicked as Player] = false
                                    }
                                }
                            }
                        }
                        14 -> {
                            if (Constant.greenLive) {
                                e.whoClicked.teleport(Constant.greenSpawn!!)
                                Constant.coolTime[e.whoClicked as Player] = 1200
                                plugin.delayTask(600) {
                                    if (Constant.attacking.containsKey(e.whoClicked as Player) && Constant.attacking[e.whoClicked as Player]!!) {
                                        (e.whoClicked as Player).health = 20.0
                                        e.whoClicked.teleport(Constant.yellowSpawn!!)
                                        Constant.attacking[e.whoClicked as Player] = false
                                    }
                                }
                            }
                        }
                    }
                }
                4 -> {
                    when (e.rawSlot) {
                        12 -> {
                            if (Constant.redLive) {
                                e.whoClicked.teleport(Constant.redSpawn!!)
                                Constant.coolTime[e.whoClicked as Player] = 1200
                                plugin.delayTask(600) {
                                    if (Constant.attacking.containsKey(e.whoClicked as Player) && Constant.attacking[e.whoClicked as Player]!!) {
                                        (e.whoClicked as Player).health = 20.0
                                        e.whoClicked.teleport(Constant.greenSpawn!!)
                                        Constant.attacking[e.whoClicked as Player] = false
                                    }
                                }
                            }
                        }
                        13 -> {
                            if (Constant.blueLive) {
                                e.whoClicked.teleport(Constant.blueSpawn!!)
                                Constant.coolTime[e.whoClicked as Player] = 1200
                                plugin.delayTask(600) {
                                    if (Constant.attacking.containsKey(e.whoClicked as Player) && Constant.attacking[e.whoClicked as Player]!!) {
                                        (e.whoClicked as Player).health = 20.0
                                        e.whoClicked.teleport(Constant.greenSpawn!!)
                                        Constant.attacking[e.whoClicked as Player] = false
                                    }
                                }
                            }
                        }
                        14 -> {
                            if (Constant.yellowLive) {
                                e.whoClicked.teleport(Constant.yellowSpawn!!)
                                Constant.coolTime[e.whoClicked as Player] = 1200
                                plugin.delayTask(600) {
                                    if (Constant.attacking.containsKey(e.whoClicked as Player) && Constant.attacking[e.whoClicked as Player]!!) {
                                        (e.whoClicked as Player).health = 20.0
                                        e.whoClicked.teleport(Constant.greenSpawn!!)
                                        Constant.attacking[e.whoClicked as Player] = false
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 아라비아 숫자를 로마 숫자로 바꾸는 함수
    private fun toRomaNum(i: Int): String? {
        when (i) {
            0 -> return "0"
            1 -> return "I"
            2 -> return "II"
            3 -> return "III"
            4 -> return "IV"
            5 -> return "V"
            6 -> return "VI"
            7 -> return "VII"
        }
        return null
    }

    // 구매 가능 여부 확인 함수 (+돈 감소)
    private fun buy(player: Player, item: MaterialShopItem): Boolean {
        if (Constant.money.containsKey(player)) {
            return if (Constant.money[player]!! >= item.price) {
                if (player.inventory.firstEmpty() != -1) {
                    Constant.money[player] = Constant.money[player]!! - item.price
                    true
                } else {
                    player.sendMessage("${ChatColor.RED}인벤토리 공간이 부족합니다")
                    false
                }
            } else {
                player.sendMessage("${ChatColor.RED}돈이 부족합니다")
                false
            }
        }
        return false
    }

    // 1세트 구매 가능 여부 확인 함수 (+돈 감소)
    private fun buyStack(player: Player, item: MaterialShopItem): Boolean {
        if (Constant.money.containsKey(player)) {
            return if (Constant.money[player]!! >= item.price * 64) {
                if (player.inventory.firstEmpty() != -1) {
                    Constant.money[player] = Constant.money[player]!! - (item.price * 64)
                    true
                } else {
                    player.sendMessage("${ChatColor.RED}인벤토리 공간이 부족합니다")
                    false
                }
            } else {
                player.sendMessage("${ChatColor.RED}돈이 부족합니다")
                false
            }
        }
        return false
    }

    // 몬스터 소환 함수
    private fun summon(player: Player, item: MonsterItem) {
        if (team[player] == 1) {
            // 레드 팀 감지
            if (Constant.multi) {
                if (Constant.redMonster >= Constant.maxSpawn * 3) {
                    player.sendMessage("${ChatColor.RED}이미 너무 많은 몬스터가 월드에 있습니다")
                    return
                }
            } else {
                if (Constant.redMonster >= Constant.maxSpawn) {
                    player.sendMessage("${ChatColor.RED}이미 너무 많은 몬스터가 월드에 있습니다")
                    return
                }
            }
        }
        if (team[player] == 2) {
            // 블루 팀 감지
            if (Constant.multi) {
                if (Constant.blueMonster >= Constant.maxSpawn * 3) {
                    player.sendMessage("${ChatColor.RED}이미 너무 많은 몬스터가 월드에 있습니다")
                    return
                }
            } else {
                if (Constant.blueMonster >= Constant.maxSpawn) {
                    player.sendMessage("${ChatColor.RED}이미 너무 많은 몬스터가 월드에 있습니다")
                    return
                }
            }
        }
        if (team[player] == 3) {
            // 옐로 팀 감지
            if (Constant.multi) {
                if (Constant.yellowMonster >= Constant.maxSpawn * 3) {
                    player.sendMessage("${ChatColor.RED}이미 너무 많은 몬스터가 월드에 있습니다")
                    return
                }
            } else {
                if (Constant.yellowMonster >= Constant.maxSpawn) {
                    player.sendMessage("${ChatColor.RED}이미 너무 많은 몬스터가 월드에 있습니다")
                    return
                }
            }
        }
        if (team[player] == 4) {
            // 그린 팀 감지
            if (Constant.multi) {
                if (Constant.greenMonster >= Constant.maxSpawn * 3) {
                    player.sendMessage("${ChatColor.RED}이미 너무 많은 몬스터가 월드에 있습니다")
                    return
                }
            } else {
                if (Constant.greenMonster >= Constant.maxSpawn) {
                    player.sendMessage("${ChatColor.RED}이미 너무 많은 몬스터가 월드에 있습니다")
                    return
                }
            }
        }
        if (Constant.money.containsKey(player)) {
            // 실제 소환 함수
            if (Constant.money[player]!! >= item.price) {
                Constant.money[player] = Constant.money[player]!! - item.price
                Constant.summonMonsters(item.entityType, team[player]!!, item.summoned++)
            } else {
                player.sendMessage("${ChatColor.RED}돈이 부족합니다")
            }
        }
    }

    // 왼손들기 -> 메뉴 열기
    @EventHandler
    fun onMenuClick(e: PlayerSwapHandItemsEvent) {
        try {
            if (e.player.inventory.heldItemSlot == 8) {
                e.isCancelled = true
                openMenu(e.player)
            }
        } catch (e: Exception) {
        }
    }

    // 좌/우클 -> 메뉴 열기
    @EventHandler
    fun onMenuClick(e: PlayerInteractEvent) {
        try {
            if ((e.action == Action.LEFT_CLICK_AIR) || (e.action == Action.LEFT_CLICK_BLOCK
                        ) || (e.action == Action.RIGHT_CLICK_AIR) || (e.action == Action.RIGHT_CLICK_BLOCK)
            ) {
                if (e.player.inventory.heldItemSlot == 8) {
                    e.isCancelled = true
                    openMenu(e.player)
                }
            }
        } catch (e: Exception) {
        }
    }

    // 인벤토리에서 클릭 -> 메뉴 열기
    @EventHandler
    fun onMenuClick(e: InventoryClickEvent) {
        try {
            if ((e.clickedInventory != null) && (e.clickedInventory!!.getItem(e.slot) != null
                        ) && (e.clickedInventory!!.getItem(e.slot)!!.itemMeta!!.displayName
                        == "${ChatColor.GREEN}메뉴")
            ) {
                e.isCancelled = true
                openMenu(e.whoClicked as Player)
            }
        } catch (e: Exception) {
        }
    }

    // 아이템 드랍 -> 메뉴 열기
    @EventHandler
    fun onMenuClick(e: PlayerDropItemEvent) {
        try {
            if ((e.itemDrop.itemStack.itemMeta!!.displayName == "${ChatColor.GREEN}메뉴")) {
                e.isCancelled = true
                openMenu(e.player)
            }
        } catch (e: Exception) {
        }
    }

    // 메뉴를 여는 함수
    private fun openMenu(p: Player) {
        if (Constant.started) {
            // 진행 중에는 상점 메뉴 열기
            if (menuClick.containsKey(p)) {
                if (!menuClick[p]!!) {
                    p.openInventory(Constant.shop)
                }
            } else {
                p.openInventory(Constant.shop)
            }
        } else {
            // 대기 중에는 팀 설정 메뉴 열기
            val teamMenu = Bukkit.createInventory(null, 27, "${ChatColor.GREEN}팀 설정")
            for (i in 0..9) {
                teamMenu.setItem(i, Constant.ITEM_VOID)
                teamMenu.setItem(i + 17, Constant.ITEM_VOID)
            }
            teamMenu.setItem(
                16,
                Constant.getItemStack(
                    Material.GRAY_WOOL, 1,
                    "${ChatColor.GREEN}${ChatColor.BOLD}SPECTATOR TEAM",
                    listOf("${ChatColor.GRAY}CLICK")
                )
            )
            teamMenu.setItem(
                10, Constant.getItemStack(
                    Material.RED_WOOL,
                    1,
                    "${ChatColor.GREEN}${ChatColor.BOLD}RED TEAM",
                    listOf("${ChatColor.GRAY}CLICK")
                )
            )
            teamMenu.setItem(
                11, Constant.getItemStack(
                    Material.BLUE_WOOL,
                    1,
                    "${ChatColor.GREEN}${ChatColor.BOLD}BLUE TEAM",
                    listOf("${ChatColor.GRAY}CLICK")
                )
            )
            if (Constant.multi) {
                teamMenu.setItem(
                    12, Constant.getItemStack(
                        Material.YELLOW_WOOL,
                        1,
                        "${ChatColor.GREEN}${ChatColor.BOLD}YELLOW TEAM",
                        listOf("${ChatColor.GRAY}CLICK")
                    )
                )
                teamMenu.setItem(
                    13, Constant.getItemStack(
                        Material.GREEN_WOOL,
                        1,
                        "${ChatColor.GREEN}${ChatColor.BOLD}GREEN TEAM",
                        listOf("${ChatColor.GRAY}CLICK")
                    )
                )
            }
            when (team[p]) {
                0 -> teamMenu.getItem(16)!!.type = Material.GRAY_STAINED_GLASS
                1 -> teamMenu.getItem(10)!!.type = Material.RED_STAINED_GLASS
                2 -> teamMenu.getItem(11)!!.type = Material.BLUE_STAINED_GLASS
                3 -> teamMenu.getItem(12)!!.type = Material.YELLOW_STAINED_GLASS
                4 -> teamMenu.getItem(13)!!.type = Material.GREEN_STAINED_GLASS
            }
            p.openInventory(teamMenu)
        }
    }

    // 인벤토리 클릭 이벤트 (메뉴)
    @EventHandler
    fun clickOnMenu(e: InventoryClickEvent) {
        if ((e.view.title == "${ChatColor.GREEN}팀 설정")) {
            // 팀 설정일 경우
            e.isCancelled = true
            if (Constant.multi) {
                when (e.currentItem!!.type) {
                    Material.GRAY_WOOL -> {
                        team[e.whoClicked] = 0
                        Bukkit.broadcastMessage("${ChatColor.GREEN}${e.whoClicked.name} (이)가 관전 팀에 추가되었습니다")
                        openMenu(e.whoClicked as Player)
                    }
                    Material.RED_WOOL -> {
                        team[e.whoClicked] = 1
                        Bukkit.broadcastMessage("${ChatColor.GREEN}${e.whoClicked.name} (이)가 레드 팀에 추가되었습니다")
                        openMenu(e.whoClicked as Player)
                    }
                    Material.BLUE_WOOL -> {
                        team[e.whoClicked] = 2
                        Bukkit.broadcastMessage("${ChatColor.GREEN}${e.whoClicked.name} (이)가 블루 팀에 추가되었습니다")
                        openMenu(e.whoClicked as Player)
                    }
                    Material.YELLOW_WOOL -> {
                        team[e.whoClicked] = 3
                        Bukkit.broadcastMessage("${ChatColor.GREEN}${e.whoClicked.name} (이)가 옐로 팀에 추가되었습니다")
                        openMenu(e.whoClicked as Player)
                    }
                    Material.GREEN_WOOL -> {
                        team[e.whoClicked] = 4
                        Bukkit.broadcastMessage("${ChatColor.GREEN}${e.whoClicked.name} (이)가 그린 팀에 추가되었습니다")
                        openMenu(e.whoClicked as Player)
                    }
                    else -> return
                }
            } else {
                when (e.currentItem!!.type) {
                    Material.GRAY_WOOL -> {
                        team[e.whoClicked] = 0
                        Bukkit.broadcastMessage("${ChatColor.GREEN}${e.whoClicked.name} (이)가 관전 팀에 추가되었습니다")
                        openMenu(e.whoClicked as Player)
                    }
                    Material.RED_WOOL -> {
                        team[e.whoClicked] = 1
                        Bukkit.broadcastMessage("${ChatColor.GREEN}${e.whoClicked.name} (이)가 레드 팀에 추가되었습니다")
                        openMenu(e.whoClicked as Player)
                    }
                    Material.BLUE_WOOL -> {
                        team[e.whoClicked] = 2
                        Bukkit.broadcastMessage("${ChatColor.GREEN}${e.whoClicked.name} (이)가 블루 팀에 추가되었습니다")
                        openMenu(e.whoClicked as Player)
                    }
                    else -> return
                }
            }
        } else if ((e.view.title == "${ChatColor.GREEN}상점")) {
            // 상점일 경우
            e.isCancelled = true
            when (e.rawSlot) {
                10 -> {
                    menuClick[e.whoClicked as Player] = true
                    e.whoClicked.openInventory(Constant.materialShop)
                    plugin.delayTask(2) { menuClick[e.whoClicked as Player] = false }
                }
                11 -> {
                    if (Constant.peacefulTime > 0) {
                        menuClick[e.whoClicked as Player] = true
                        e.whoClicked.sendMessage("${ChatColor.RED}평화 시간입니다")
                        plugin.delayTask(2) { menuClick[e.whoClicked as Player] = false }
                    } else {
                        menuClick[e.whoClicked as Player] = true
                        e.whoClicked.openInventory(Constant.monsterShop)
                        plugin.delayTask(2) { menuClick[e.whoClicked as Player] = false }
                    }
                }
                12 -> {
                    when (team[e.whoClicked]) {
                        0 -> return
                        1 -> e.whoClicked.openInventory(Constant.redBuffShop)
                        2 -> e.whoClicked.openInventory(Constant.blueBuffShop)
                        3 -> e.whoClicked.openInventory(Constant.yellowBuffShop)
                        4 -> e.whoClicked.openInventory(Constant.greenBuffShop)
                    }
                    menuClick[e.whoClicked as Player] = true
                    plugin.delayTask(2) { menuClick[e.whoClicked as Player] = false }
                }
                13 -> {
                    menuClick[e.whoClicked as Player] = true
                    e.whoClicked.openInventory(Constant.weaponSmithy)
                    plugin.delayTask(2) { menuClick[e.whoClicked as Player] = false }
                }
                14 -> {
                    menuClick[e.whoClicked as Player] = true
                    e.whoClicked.openInventory(Constant.armorSmithy)
                    plugin.delayTask(2) { menuClick[e.whoClicked as Player] = false }
                }
                15 -> {
                    menuClick[e.whoClicked as Player] = true
                    e.whoClicked.openInventory(Constant.toolSmithy)
                    plugin.delayTask(2) { menuClick[e.whoClicked as Player] = false }
                }
                16 -> {
                    menuClick[e.whoClicked as Player] = true
                    e.whoClicked.openInventory(Constant.misSmithy)
                    plugin.delayTask(2) { menuClick[e.whoClicked as Player] = false }
                }
            }
        }
    }

    // 인벤토리에서 특정 아이템을 지우는 함수
    private fun removeItems(p: Player, items: HashMap<Material, Int>): Boolean {
        for (material: Material in items.keys) {
            if (!p.inventory.contains(material, (items[material])!!)) {
                return false
            }
        }
        for (material in items.keys) {
            var leftAmount = (items[material])!!
            var i = 0
            for (item: ItemStack? in p.inventory) {
                if (item != null) {
                    if (item.type == material) {
                        leftAmount -= item.amount
                        if (leftAmount < 0) {
                            item.amount = -leftAmount
                            break
                        } else if (leftAmount == 0) {
                            p.inventory.setItem(i, null)
                            break
                        } else {
                            p.inventory.setItem(i, null)
                        }
                    }
                }
                i++
            }
        }
        return true
    }

    @EventHandler fun onRightClick(e: PlayerInteractEvent) {
        if (e.hasItem()) {
            if (e.item!!.type == Material.ENCHANTED_GOLDEN_APPLE) {
                if (e.action == Action.RIGHT_CLICK_BLOCK || e.action == Action.RIGHT_CLICK_AIR) {
                    e.isCancelled = true
                    if (e.player.inventory.itemInMainHand.type == Material.ENCHANTED_GOLDEN_APPLE) {
                        e.player.addPotionEffect(PotionEffect(PotionEffectType.ABSORPTION, 99999, 2, false, false))
                        e.player.inventory.itemInMainHand.amount--
                    }
                }
            }
        }
    }

    // 재료 상점 함수들
    private fun materialShopLeftClick(rawSlot: Int, whoClicked: HumanEntity) {
        if (rawSlot < 27) {
            if (rawSlot == 10) {
                if (buy((whoClicked) as Player, MaterialShopItem.OAK_LOG)) {
                    whoClicked.inventory
                        .addItem(
                            Constant.getItemStack(
                                Material.OAK_LOG, 1,
                                "${ChatColor.GREEN}${ChatColor.BOLD}나무",
                                listOf("${ChatColor.GREEN}맨 처음에 제일 중요한 재료 아이템.")
                            )
                        )
                }
            } else if (rawSlot == 11) {
                if (buy((whoClicked) as Player, MaterialShopItem.STICK)) {
                    whoClicked.inventory
                        .addItem(
                            Constant.getItemStack(
                                Material.STICK, 1,
                                "${ChatColor.GREEN}${ChatColor.BOLD}막대",
                                listOf("${ChatColor.GREEN}흔하지만 귀중한 재료 아이템.")
                            )
                        )
                }
            } else if (rawSlot == 12) {
                if (buy((whoClicked) as Player, MaterialShopItem.STONE)) {
                    whoClicked.inventory
                        .addItem(
                            Constant.getItemStack(
                                Material.COBBLESTONE, 1,
                                "${ChatColor.GREEN}${ChatColor.BOLD}조약돌",
                                listOf("${ChatColor.GREEN}흔하지만 귀중한 재료 아이템.")
                            )
                        )
                }
            } else if (rawSlot == 13) {
                if (buy((whoClicked) as Player, MaterialShopItem.IRON)) {
                    whoClicked.inventory
                        .addItem(
                            Constant.getItemStack(
                                Material.IRON_INGOT, 1,
                                "${ChatColor.GREEN}${ChatColor.BOLD}철괴",
                                listOf("${ChatColor.GREEN}초반 장비를 빠르게 얻기 위해 반드시 필요한 재료 아이템.")
                            )
                        )
                }
            } else if (rawSlot == 14) {
                if (buy((whoClicked) as Player, MaterialShopItem.GOLD)) {
                    whoClicked.inventory
                        .addItem(
                            Constant.getItemStack(
                                Material.GOLD_INGOT, 1,
                                "${ChatColor.GREEN}${ChatColor.BOLD}금괴",
                                listOf("${ChatColor.GREEN}비싸게 거래되며 회복 아이템을 얻기 위한 재료 아이템.")
                            )
                        )
                }
            } else if (rawSlot == 15) {
                if (buy((whoClicked) as Player, MaterialShopItem.DIAMOND)) {
                    whoClicked.inventory
                        .addItem(
                            Constant.getItemStack(
                                Material.DIAMOND, 1,
                                "${ChatColor.GREEN}${ChatColor.BOLD}다이아몬드",
                                listOf("${ChatColor.GREEN}후반 필수 재료 아이템.")
                            )
                        )
                }
            } else if (rawSlot == 16) {
                if (buy((whoClicked) as Player, MaterialShopItem.NETHERITE)) {
                    whoClicked.inventory
                        .addItem(
                            Constant.getItemStack(
                                Material.NETHERITE_INGOT, 1,
                                "${ChatColor.GREEN}${ChatColor.BOLD}네더라이트",
                                listOf("${ChatColor.GREEN}전설에 도달한 재료 아이템.")
                            )
                        )
                }
            }
        }
    }
    private fun materialShopShiftLeftClick(rawSlot: Int, whoClicked: HumanEntity) {
        if (rawSlot < 27) {
            if (rawSlot == 10) {
                if (buyStack((whoClicked) as Player, MaterialShopItem.OAK_LOG)) {
                    whoClicked.inventory
                        .addItem(
                            Constant.getItemStack(
                                Material.OAK_LOG, 64,
                                "${ChatColor.GREEN}${ChatColor.BOLD}나무",
                                listOf("${ChatColor.GREEN}맨 처음에 제일 중요한 재료 아이템.")
                            )
                        )
                }
            } else if (rawSlot == 11) {
                if (buyStack((whoClicked) as Player, MaterialShopItem.STICK)) {
                    whoClicked.inventory
                        .addItem(
                            Constant.getItemStack(
                                Material.STICK, 64,
                                "${ChatColor.GREEN}${ChatColor.BOLD}막대",
                                listOf("${ChatColor.GREEN}흔하지만 귀중한 재료 아이템.")
                            )
                        )
                }
            } else if (rawSlot == 12) {
                if (buyStack((whoClicked) as Player, MaterialShopItem.STONE)) {
                    whoClicked.inventory
                        .addItem(
                            Constant.getItemStack(
                                Material.COBBLESTONE, 64,
                                "${ChatColor.GREEN}${ChatColor.BOLD}조약돌",
                                listOf("${ChatColor.GREEN}흔하지만 귀중한 재료 아이템.")
                            )
                        )
                }
            } else if (rawSlot == 13) {
                if (buyStack((whoClicked) as Player, MaterialShopItem.IRON)) {
                    whoClicked.inventory
                        .addItem(
                            Constant.getItemStack(
                                Material.IRON_INGOT, 64,
                                "${ChatColor.GREEN}${ChatColor.BOLD}철괴",
                                listOf("${ChatColor.GREEN}초반 장비를 빠르게 얻기 위해 반드시 필요한 재료 아이템.")
                            )
                        )
                }
            } else if (rawSlot == 14) {
                if (buyStack((whoClicked) as Player, MaterialShopItem.GOLD)) {
                    whoClicked.inventory
                        .addItem(
                            Constant.getItemStack(
                                Material.GOLD_INGOT, 64,
                                "${ChatColor.GREEN}${ChatColor.BOLD}금괴",
                                listOf("${ChatColor.GREEN}비싸게 거래되며 회복 아이템을 얻기 위한 재료 아이템.")
                            )
                        )
                }
            } else if (rawSlot == 15) {
                if (buyStack((whoClicked) as Player, MaterialShopItem.DIAMOND)) {
                    whoClicked.inventory
                        .addItem(
                            Constant.getItemStack(
                                Material.DIAMOND, 64,
                                "${ChatColor.GREEN}${ChatColor.BOLD}다이아몬드",
                                listOf("${ChatColor.GREEN}후반 필수 재료 아이템.")
                            )
                        )
                }
            } else if (rawSlot == 16) {
                if (buyStack((whoClicked) as Player, MaterialShopItem.NETHERITE)) {
                    whoClicked.inventory
                        .addItem(
                            Constant.getItemStack(
                                Material.NETHERITE_INGOT, 64,
                                "${ChatColor.GREEN}${ChatColor.BOLD}네더라이트",
                                listOf("${ChatColor.GREEN}전설에 도달한 재료 아이템.")
                            )
                        )
                }
            }
        }
    }
    private fun materialShopRightClick(rawSlot: Int, whoClicked: HumanEntity) {
        var clickedItem: MaterialShopItem? = null
        if (rawSlot < 27) {
            when (rawSlot) {
                10 -> clickedItem = MaterialShopItem.OAK_LOG
                11 -> clickedItem = MaterialShopItem.STICK
                12 -> clickedItem = MaterialShopItem.STONE
                13 -> clickedItem = MaterialShopItem.IRON
                14 -> clickedItem = MaterialShopItem.GOLD
                15 -> clickedItem = MaterialShopItem.DIAMOND
                16 -> clickedItem = MaterialShopItem.NETHERITE
            }
            if (clickedItem != null) {
                if (whoClicked.inventory.contains(clickedItem.material)) {
                    var i = 0
                    for (item: ItemStack? in whoClicked.inventory) {
                        if (item != null) {
                            if (item.type == clickedItem.material) {
                                var amount = item.amount
                                if (--amount == 0) {
                                    whoClicked.inventory.setItem(i, null)
                                } else {
                                    item.amount = amount
                                }
                                Constant.money[whoClicked as Player] = Constant.money[whoClicked]!! + (clickedItem.price / 2)
                                break
                            }
                        }
                        i++
                    }
                }
            }
        }
    }
    private fun materialShopShiftRightClick(rawSlot: Int, whoClicked: HumanEntity) {
        var clickedItem: MaterialShopItem? = null
        if (rawSlot < 27) {
            when (rawSlot) {
                10 -> clickedItem = MaterialShopItem.OAK_LOG
                11 -> clickedItem = MaterialShopItem.STICK
                12 -> clickedItem = MaterialShopItem.STONE
                13 -> clickedItem = MaterialShopItem.IRON
                14 -> clickedItem = MaterialShopItem.GOLD
                15 -> clickedItem = MaterialShopItem.DIAMOND
                16 -> clickedItem = MaterialShopItem.NETHERITE
            }
            if (clickedItem != null) {
                var amount = 0
                for (item: ItemStack? in whoClicked.inventory) {
                    if (item != null) {
                        if (item.type == clickedItem.material) {
                            amount += item.amount
                        }
                    }
                }
                Constant.money[whoClicked as Player] = Constant.money[whoClicked]!! + (clickedItem.price * amount / 2)
                whoClicked.inventory.remove(clickedItem.material)
            }
        }
    }

    // 팀 탈락 함수
    private fun teamOut(team: Int) {
        if (Constant.leftTeam != 2) {
            Constant.team.keys.forEach {
                if (Constant.team[it] == team) {
                    it.sendMessage("${ChatColor.RED}탈락했습니다! 관전 모드가 됩니다!")
                    Constant.team[it] = 0
                    Constant.leftTeam--
                } else {
                    when(team) {
                        1 -> it.sendMessage("${ChatColor.RED}레드 팀이 탈락했습니다!")
                        2 -> it.sendMessage("${ChatColor.RED}블루 팀이 탈락했습니다!")
                        3 -> it.sendMessage("${ChatColor.RED}옐로 팀이 탈락했습니다!")
                        4 -> it.sendMessage("${ChatColor.RED}그린 팀이 탈락했습니다!")
                    }
                }
            }
            when (team) {
                1 -> Constant.redLive = false
                2 -> Constant.blueLive = false
                3 -> Constant.yellowLive = false
                4 -> Constant.greenLive = false
            }
        } else {
            when {
                Constant.redLive -> {
                    for (p: Player in Bukkit.getOnlinePlayers()) {
                        p.gameMode = GameMode.ADVENTURE
                        p.teleport(Constant.spawn!!)
                        p.inventory.clear()
                        p.activePotionEffects.clear()
                        p.sendTitle("${ChatColor.GREEN}레드 팀 승리", "", 20, 60, 20)
                        p.playSound(p.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f)
                        Constant.initVariables(p)
                        Constant.inits()
                        plugin.load()
                    }
                }
                Constant.blueLive -> {
                    for (p: Player in Bukkit.getOnlinePlayers()) {
                        p.gameMode = GameMode.ADVENTURE
                        p.teleport(Constant.spawn!!)
                        p.inventory.clear()
                        p.activePotionEffects.clear()
                        p.sendTitle("${ChatColor.GREEN}블루 팀 승리", "", 20, 60, 20)
                        p.playSound(p.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f)
                        Constant.initVariables(p)
                        Constant.inits()
                        plugin.load()
                    }
                }
                Constant.yellowLive -> {
                    for (p: Player in Bukkit.getOnlinePlayers()) {
                        p.gameMode = GameMode.ADVENTURE
                        p.teleport(Constant.spawn!!)
                        p.inventory.clear()
                        p.activePotionEffects.clear()
                        p.sendTitle("${ChatColor.GREEN}옐로 팀 승리", "", 20, 60, 20)
                        p.playSound(p.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f)
                        Constant.initVariables(p)
                        Constant.inits()
                        plugin.load()
                    }
                }
                Constant.greenLive -> {
                    for (p: Player in Bukkit.getOnlinePlayers()) {
                        p.gameMode = GameMode.ADVENTURE
                        p.teleport(Constant.spawn!!)
                        p.inventory.clear()
                        p.activePotionEffects.clear()
                        p.sendTitle("${ChatColor.GREEN}그린 팀 승리", "", 20, 60, 20)
                        p.playSound(p.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f)
                        Constant.initVariables(p)
                        Constant.inits()
                        plugin.load()
                    }
                    Constant.started = false
                }
            }
        }
    }

}