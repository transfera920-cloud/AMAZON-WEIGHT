package com.example.data.model

data class PresetTemplate(
    val title: String,
    val subtitle: String,
    val description: String,
    val recommendedBaseWeightKg: String,
    val recommendedMaxLoadKg: String,
    val items: List<PresetItem>
)

data class PresetItem(
    val name: String,
    val category: GearCategory,
    val weightGrams: Int,
    val quantity: Int = 1,
    val notes: String = ""
)

object PresetTemplates {
    val ALL_PRESETS = listOf(
        PresetTemplate(
            title = "單日郊山",
            subtitle = "中低海拔 / 郊山步道日歸",
            description = "適合陽明山、大坑、大坑步道、谷關七雄單日輕鬆行",
            recommendedBaseWeightKg = "2.5 - 4.0 kg",
            recommendedMaxLoadKg = "5.0 kg 以下",
            items = listOf(
                PresetItem("輕量雙肩後背包 20L", GearCategory.BACKPACK, 450, 1, "透氣網背"),
                PresetItem("輕量雙向雨衣", GearCategory.CLOTHING, 280, 1, "防風防水"),
                PresetItem("保暖刷毛中層外套", GearCategory.CLOTHING, 320, 1, "山頂防風保暖"),
                PresetItem("10000mAh 行動電源", GearCategory.ELECTRONICS, 210, 1, "附充電線"),
                PresetItem("智慧型手機 (離線地圖)", GearCategory.ELECTRONICS, 200, 1, "安裝 Hikingbook/離線地圖"),
                PresetItem("高分貝救命哨子", GearCategory.SAFETY, 25, 1, "掛背包胸扣"),
                PresetItem("簡易個人急救包", GearCategory.SAFETY, 150, 1, "OK繃、消毒棉片、個人藥"),
                PresetItem("防曬乳與防蚊液", GearCategory.PERSONAL, 120, 1, "小瓶裝"),
                PresetItem("輕量鋁合金登山杖", GearCategory.PERSONAL, 220, 2, "一對"),
                PresetItem("飲用水 (1500ml)", GearCategory.CONSUMABLES, 1500, 1, "視沿途水源補給"),
                PresetItem("午餐與行動糧", GearCategory.CONSUMABLES, 600, 1, "飯糰、堅果、巧克力")
            )
        ),
        PresetTemplate(
            title = "百岳單攻",
            subtitle = "合歡群峰 / 畢羊單攻 / 玉山主峰單攻",
            description = "針對高海拔一日來回，高寒風大、強烈輻射、高山症預防之精準輕量配置",
            recommendedBaseWeightKg = "3.5 - 5.5 kg",
            recommendedMaxLoadKg = "7.5 kg 以下",
            items = listOf(
                PresetItem("攻頂單攻背包 28L", GearCategory.BACKPACK, 780, 1, "含防雨罩"),
                PresetItem("Gore-Tex 高山兩截式雨衣褲", GearCategory.CLOTHING, 480, 1, "防水透氣10,000mm+"),
                PresetItem("輕量化連帽羽絨外套", GearCategory.CLOTHING, 290, 1, "800FP 高蓬鬆度"),
                PresetItem("防風保暖毛帽與毛手套", GearCategory.CLOTHING, 110, 1, "高海拔頭部防風"),
                PresetItem("高明亮 LED 頭燈", GearCategory.ELECTRONICS, 90, 1, "凌晨摸黑起步"),
                PresetItem("10000mAh 快充行動電源", GearCategory.ELECTRONICS, 220, 1, "耐寒電池"),
                PresetItem("高山緊急求生毯", GearCategory.SAFETY, 55, 1, "防溫差過大失溫"),
                PresetItem("個人急救與丹木斯藥包", GearCategory.SAFETY, 180, 1, "高山症預防藥"),
                PresetItem("300ml 輕量保溫水瓶", GearCategory.COOKING, 210, 1, "盛裝熱水防高山寒氣"),
                PresetItem("高山電解質防抽筋飲", GearCategory.CONSUMABLES, 100, 1, "鹽錠與寶礦力粉"),
                PresetItem("高熱量行動糧與能量膠", GearCategory.CONSUMABLES, 800, 1, "羊羹、能量棒、肉乾"),
                PresetItem("飲用水與能量飲 (2000ml)", GearCategory.CONSUMABLES, 2000, 1, "水袋或寶特瓶")
            )
        ),
        PresetTemplate(
            title = "三天兩夜 (自備帳篷)",
            subtitle = "嘉明湖 / 奇萊南華 / 大霸群峰",
            description = "台灣經典高山百岳路線，營地炊煮與完整睡眠系統配置",
            recommendedBaseWeightKg = "7.0 - 9.0 kg",
            recommendedMaxLoadKg = "13.5 kg 以下",
            items = listOf(
                PresetItem("登山重裝背包 55L", GearCategory.BACKPACK, 1650, 1, "強效背負系統"),
                PresetItem("全防水海寶袋/打包袋", GearCategory.BACKPACK, 160, 1, "主艙完全防水"),
                PresetItem("超輕量雙人三季帳篷", GearCategory.SHELTER, 1850, 1, "營釘營繩營柱"),
                PresetItem("高山黑冰羽絨睡袋 (FP700)", GearCategory.SHELTER, 820, 1, "舒適溫度-5°C"),
                PresetItem("高山蛋殼閉孔睡墊", GearCategory.SHELTER, 410, 1, "防地氣寒冷"),
                PresetItem("充氣輕量頭枕", GearCategory.SHELTER, 85, 1, "睡眠品質提升"),
                PresetItem("輕量鈦金屬分體爐", GearCategory.COOKING, 120, 1, "抗風高熱效"),
                PresetItem("鋁合金/鈦合金雙鍋組", GearCategory.COOKING, 240, 1, "附湯匙叉子"),
                PresetItem("擠壓式微米濾水器", GearCategory.COOKING, 85, 1, "過濾高山溪水/水塘"),
                PresetItem("高山羽絨風雨三層衣組", GearCategory.CLOTHING, 1150, 1, "底層保暖中層風雨"),
                PresetItem("營地替換備用衣物", GearCategory.CLOTHING, 420, 1, "吸濕排汗備用衣襪"),
                PresetItem("20000mAh 高容量行動電源", GearCategory.ELECTRONICS, 380, 1, "多日相機手機供電"),
                PresetItem("高明亮頭燈與備用電池", GearCategory.ELECTRONICS, 110, 1, "夜間營地與早起"),
                PresetItem("完整野外高山急救包", GearCategory.SAFETY, 260, 1, "含彈性繃帶與腸胃藥"),
                PresetItem("高山級高氣壓瓦斯罐 230g", GearCategory.CONSUMABLES, 380, 1, "異丁烷混合氣"),
                PresetItem("三天兩夜主食乾燥飯與乾糧", GearCategory.CONSUMABLES, 1800, 1, "輕量化乾燥飯"),
                PresetItem("起登與備用飲用水 (2000ml)", GearCategory.CONSUMABLES, 2000, 1, "第一天飲用")
            )
        ),
        PresetTemplate(
            title = "高山縱走 (5-6天)",
            subtitle = "能高安東軍 / 北一段 / 馬博橫斷",
            description = "長程高山縱走，考量高海拔營地、天候變幻與長天數糧食計畫",
            recommendedBaseWeightKg = "8.0 - 10.5 kg",
            recommendedMaxLoadKg = "16.0 kg 以下",
            items = listOf(
                PresetItem("大型登山縱走背包 65L-75L", GearCategory.BACKPACK, 1950, 1, "高承重鋼架背負"),
                PresetItem("背包防雨罩 + 內部防潮打包", GearCategory.BACKPACK, 280, 1, "雙重防水保護"),
                PresetItem("自立式雙人高山抗風帳", GearCategory.SHELTER, 1950, 1, "抗強陣風"),
                PresetItem("900FP 極限抗寒羽絨睡袋", GearCategory.SHELTER, 980, 1, "舒適 -10°C"),
                PresetItem("高 R 值充氣睡墊 + 蛋殼薄墊", GearCategory.SHELTER, 580, 1, "雙層隔熱"),
                PresetItem("高山攻頂/營地輕便包", GearCategory.BACKPACK, 120, 1, "營地短程輕便"),
                PresetItem("高效率蜘蛛爐具組", GearCategory.COOKING, 210, 1, "穩固耐重"),
                PresetItem("鈦金屬個人深鍋與餐碗", GearCategory.COOKING, 210, 1, "高傳熱輕量"),
                PresetItem("Sawyer/Katadyn 高效濾水器", GearCategory.COOKING, 110, 1, "長程水質處理"),
                PresetItem("極高山防寒羽絨外套 (重量級)", GearCategory.CLOTHING, 480, 1, "營地保暖"),
                PresetItem("專業暴雨等級風雨衣褲", GearCategory.CLOTHING, 550, 1, "耐磨防暴雨"),
                PresetItem("縱走備用衣襪 (排汗衣/羊毛襪)", GearCategory.CLOTHING, 600, 1, "密封袋分裝"),
                PresetItem("手持衛星通訊/GPS 裝置 (InReach)", GearCategory.ELECTRONICS, 210, 1, "緊急 SOS 救援"),
                PresetItem("20000mAh 雙行動電源", GearCategory.ELECTRONICS, 720, 1, "長天數電力補給"),
                PresetItem("高山頭燈 (含多組備用電池)", GearCategory.ELECTRONICS, 150, 1, "夜行防斷電"),
                PresetItem("縱走等級醫藥急救箱 + 生存毯", GearCategory.SAFETY, 380, 1, "含傷口止血與消炎"),
                PresetItem("個人衛生與可降解濕紙巾", GearCategory.PERSONAL, 200, 1, "無痕山林 LNT"),
                PresetItem("高山瓦斯罐 230g", GearCategory.CONSUMABLES, 760, 2, "兩罐長天數"),
                PresetItem("5天極高熱量輕量乾燥飯與行動糧", GearCategory.CONSUMABLES, 3800, 1, "每天約600-700g糧食"),
                PresetItem("起登預備飲用水 (2500ml)", GearCategory.CONSUMABLES, 2500, 1, "視水況調配")
            )
        ),
        PresetTemplate(
            title = "冬季雪季 (雪山聖稜/南湖大山)",
            subtitle = "含雪階硬雪冰斧冰爪裝備",
            description = "因應雪季零下低溫、積雪硬雪與嚴苛極端天候之安全極限配置",
            recommendedBaseWeightKg = "10.0 - 12.5 kg",
            recommendedMaxLoadKg = "18.0 kg 以下",
            items = listOf(
                PresetItem("重裝雪季登山背包 70L", GearCategory.BACKPACK, 2100, 1, "帶外掛冰斧環"),
                PresetItem("四季四季極高山雪帳", GearCategory.SHELTER, 2400, 1, "雪裙抗重雪"),
                PresetItem("極寒極地羽絨睡袋 (-15°C)", GearCategory.SHELTER, 1350, 1, "填充 1000g 高蓬羽絨"),
                PresetItem("高 R 值抗寒充氣睡墊 (R-value 4.5+)", GearCategory.SHELTER, 620, 1, "地面冰雪隔熱"),
                PresetItem("12 齒鋼製專業綁帶式冰爪", GearCategory.SAFETY, 880, 1, "硬雪冰階踏步"),
                PresetItem("技術冰斧/登山冰斧", GearCategory.SAFETY, 420, 1, "滑墜制動必備"),
                PresetItem("高山雪鏡/抗UV極佳太陽眼鏡", GearCategory.SAFETY, 130, 1, "防止雪盲症"),
                PresetItem("防雪綁腿 (Gore-Tex)", GearCategory.CLOTHING, 260, 1, "防止冰雪掉入登山靴"),
                PresetItem("厚實防水保暖雪手套 + 薄內層手套", GearCategory.CLOTHING, 220, 1, "防凍傷"),
                PresetItem("高厚度厚羊毛保暖帽與面罩", GearCategory.CLOTHING, 140, 1, "臉部凍傷防護"),
                PresetItem("雪季強烈防寒重羽絨衣", GearCategory.CLOTHING, 650, 1, "營地與零下防寒"),
                PresetItem("強效預熱式高山液態瓦斯爐", GearCategory.COOKING, 320, 1, "低溫雪地融雪點火"),
                PresetItem("融雪大金屬鍋具與極致保溫瓶", GearCategory.COOKING, 580, 1, "煮雪水飲用"),
                PresetItem("防凍耐寒電池與 20000mAh 電源", GearCategory.ELECTRONICS, 450, 1, "低溫掉電快備用"),
                PresetItem("凍傷急救膏與加厚求生毯", GearCategory.SAFETY, 200, 1, "低溫醫療防護"),
                PresetItem("高山高純度異丁烷瓦斯 230g", GearCategory.CONSUMABLES, 760, 2, "低溫融雪消耗瓦斯較多"),
                PresetItem("高熱量雪季伙食與高脂肪行動糧", GearCategory.CONSUMABLES, 2400, 1, "高熱量禦寒")
            )
        ),
        PresetTemplate(
            title = "南三段 / 丹大東郡長程縱走",
            subtitle = "台灣中央山脈心臟 8-10 天無補給縱走",
            description = "全台最遠最深深山縱走，極端考量每一公克重與極致體能調配",
            recommendedBaseWeightKg = "8.5 - 10.0 kg",
            recommendedMaxLoadKg = "17.0 kg 以下 (第一天)",
            items = listOf(
                PresetItem("UL 輕量化高強度 65L 背包", GearCategory.BACKPACK, 1100, 1, "Dyneema / Ultra 織布"),
                PresetItem("輕量化非自立雙人雙杖帳篷", GearCategory.SHELTER, 950, 1, "用登山杖架設省重量"),
                PresetItem("850FP 輕量羽絨睡袋 (舒適-3°C)", GearCategory.SHELTER, 680, 1, "極致壓合體積"),
                PresetItem("輕量化短版充氣睡墊", GearCategory.SHELTER, 310, 1, "腳下墊背包省重量"),
                PresetItem("極簡直噴高效鈦爐頭", GearCategory.COOKING, 48, 1, "極致輕量"),
                PresetItem("個人 750ml 鈦深鍋", GearCategory.COOKING, 95, 1, "煮水兼做碗"),
                PresetItem("Katadyn BeFree 0.6L 濾水軟水壺", GearCategory.COOKING, 65, 1, "沿途水塘過濾"),
                PresetItem("高蓬鬆度 UL 保暖羽絨外套", GearCategory.CLOTHING, 240, 1, "超輕量防寒"),
                PresetItem("超輕量風雨衣褲組 (15D)", GearCategory.CLOTHING, 320, 1, "透氣防暴雨"),
                PresetItem("羊毛底層替換衣物與備用襪", GearCategory.CLOTHING, 380, 1, "抑菌防臭"),
                PresetItem("Garmin inReach Mini 2 衛星通訊器", GearCategory.ELECTRONICS, 100, 1, "長程縱走回報定位"),
                PresetItem("快充型 20000mAh 行動電源 x2", GearCategory.ELECTRONICS, 720, 1, "10天手機與GPS發信"),
                PresetItem("輕量超高亮頭燈", GearCategory.ELECTRONICS, 75, 1, "USB 充", ),
                PresetItem("高山特製輕量急救藥包", GearCategory.SAFETY, 180, 1, "針劑、止痛、水泡貼"),
                PresetItem("南三段 8天糧食 (高密度脫水乾燥飯)", GearCategory.CONSUMABLES, 4800, 1, "每天約600g極致高熱量糧"),
                PresetItem("高山瓦斯罐 230g", GearCategory.CONSUMABLES, 760, 2, "精算沸水時間量"),
                PresetItem("出發水 (1500ml)", GearCategory.CONSUMABLES, 1500, 1, "第一天溪谷前飲用")
            )
        )
    )
}
