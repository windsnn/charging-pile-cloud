-- KEYS[1]: 库存 key  (coupon:stock:优惠券code)
-- KEYS[2]: 用户领取集合 key  (coupon:user:优惠券code)
-- ARGV[1]: 用户 id

-- 1. 判断是否已领取
if redis.call("SISMEMBER", KEYS[2], ARGV[1]) == 1 then
    return 0
end

-- 2. 获取库存
local stock = redis.call("GET", KEYS[1])

-- 2.1 无限库存
if not stock then
    redis.call("SADD", KEYS[2], ARGV[1])
    return 1
end

-- 2.2 有限库存
stock = tonumber(stock)
if stock <= 0 then
    return -1
end

-- 3. 扣减库存（先尝试扣减，再判断是否越界）
local newStock = redis.call("DECRBY", KEYS[1], 1)
if newStock < 0 then
    -- 回滚
    redis.call("INCR", KEYS[1])
    return -1
end

-- 4. 记录用户
redis.call("SADD", KEYS[2], ARGV[1])

return 1
