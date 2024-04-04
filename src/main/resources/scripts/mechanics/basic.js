Plus = operation("Plus")
Scalar = operation("Scalar")
//@Mechanic(damage)
function damage(data, context, damageType) {
    const enable = data.handle(context.get("enable"));
    if (enable.toString() != "true") {
        data.hasResult = false;
        return 0.0;
    }
    const value = toDouble(data.handle(context.get("value")));
    data.damageSources.put("damage", Plus.element(value));
    //返回值会以 damage 为id 存到FightData里
    return value;
}

//@Mechanic(crit)
function crit(data, context, damageType) {
    const enable = data.handle(context.get("enable"));
    if (enable.toString() != "true") {
        return 0.0;
    }
    const multiplier = toDouble(data.handle(context.get("multiplier")));
    data.damageSources.put("crit", Scalar.element(multiplier));
    //返回值会以 damage 为id 存到FightData里
    return multiplier;
}

//@Mechanic(vampire)
function vampire(data, context, damageType) {
    const enable = data.handle(context.get("enable"));
    if (enable.toString() != "true") {
        return 0.0;
    }
    const attacker = data.attacker;
    if (attacker == null) return null;
    let healthRegain = toDouble(data.handle(context.get("value")));
    const maxHealth = attacker.maxHealth;
    const healthNow = attacker.health;
    const healthValue = healthNow + healthRegain;
    if (healthValue >= maxHealth) {
        attacker.health = maxHealth;
        healthRegain = maxHealth - healthNow;
    } else {
        attacker.health = healthValue;
    }
    return healthRegain;
}
function toDouble(obj) {
    if (obj === null || typeof obj === "undefined") {
        return 0.0;
    }
    if (typeof obj === "number" || obj instanceof Number) {
        return Number(obj);
    }
    var parsed = parseFloat(obj);
    if (!isNaN(parsed)) {
        return parsed;
    }
    return 0.0;
}