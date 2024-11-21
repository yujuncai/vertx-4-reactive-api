local bigKeysThreshold = tonumber(ARGV[1])
local cursor  = tonumber(ARGV[2])

local list = {}
-- 开始 SCAN 操作
    local scanResult = redis.call("SCAN", cursor, "MATCH", "*", "COUNT", 5000)
    cursor = scanResult[1]
    local keys = scanResult[2]
    table.insert(list,"cursor:->"..cursor)
    -- 遍历扫描到的键
    for i, key in ipairs(keys) do
        local size =tonumber( redis.call("MEMORY", "USAGE", key))
        local type = redis.call("TYPE", key)["ok"]
        if size~=nil and size > bigKeysThreshold then
            local ttl = redis.call("ttl", key)
            table.insert(list,key..":->"..size..":->"..ttl..":->"..type)
        end
    end

return  list  -- 返回big keys



