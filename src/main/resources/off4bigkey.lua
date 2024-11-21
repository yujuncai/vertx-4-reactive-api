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
        local type = redis.call("TYPE", key)["ok"]
        local ttl = redis.call("ttl", key)
        -- 根据键的类型检查大小
        if type == "string" then
            local size = redis.call("STRLEN", key)

            if size~=nil and size > bigKeysThreshold then
                table.insert(list,key..":->"..size..":->"..ttl..":->"..type)
            end
        elseif type == "list" then
            local size = redis.call("LLEN", key)
            if size~=nil and size > bigKeysThreshold then
                table.insert(list,key..":->"..size..":->"..ttl..":->"..type)
            end
        elseif type == "set" then
            local size = redis.call("SCARD", key)
            if size~=nil and size > bigKeysThreshold then
                table.insert(list,key..":->"..size..":->"..ttl..":->"..type)
            end
        elseif type == "hash" then
            local size = redis.call("HLEN", key)
            if size~=nil and size > bigKeysThreshold then
                table.insert(list,key..":->"..size..":->"..ttl..":->"..type)
            end
        elseif type == "zset" then
            local size = redis.call("ZCARD", key)
            if size~=nil and size > bigKeysThreshold then
                table.insert(list,key..":->"..size..":->"..ttl..":->"..type)
            end
        end
    end

return  list  -- 返回big keys的总数



