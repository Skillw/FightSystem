const Clock = find("com.skillw.pouvoir.util.Tick").INSTANCE
const mapLocation = find("java.util.concurrent.ConcurrentHashMap")
const map = new mapLocation()

//@Mechanic(砍倒)
function kd(data, context, damageType) {

    const attacker = data.attacker
    const uuid = attacker.uniqueId
    const tick = data.handle(context.get("ticks"))
    const now = Clock.getCurrentTick()
    isCoolDown(function() {
        //代码块
    })
    function hasPlayer(block1, block2) {
        const result = map.get(uuid) != null
        if (result) {
            return block1()
        } else {
            return block2()
        }
    }
    //玩家是否在冷却状态
    function isCoolDown(block1) {
        return hasPlayer(function() {
            const ticks = map.get(uuid)
            print(now - ticks <= tick)
            if (now - ticks <= tick) {
                block1()
                map.put(uuid, now)
                return true
            }
            map.put(uuid, now)
            return false
        }, function() {
            map.put(uuid, now)
            return false
        })
    }
    print(map)
}
