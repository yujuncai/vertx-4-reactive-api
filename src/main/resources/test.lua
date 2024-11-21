local mytype=redis.call("TYPE", "a")


    return  mytype["ok"]=='string'


