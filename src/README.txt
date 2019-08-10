hashes
    hget kry field
        返回bulk string 或者 null
    hset key field value
        返回0 更新           1插入

lists
    lpush key value
        返回插入后的长度    Integer
    lrange key start end
        返回数组            array

需要改进点:
    现在是单线程,不支持并发连接    => 引入多线程

    连接的处理循环没有停下来的条件  => 找到识别出对方关闭连接的情况 , 跳出循环

    错误处理不够丰富

    持久化                 => 存到磁盘中/存储到mysql中  (方式一:直接用ObjectWriter

    名称:
    关键词: redis/协议解析/Socket/TCP/多线程/哈希/反射/IO/单例
    功能描述:
        1.协议解析是重心
        2.性能测试
            1.读写效率      2.并发效率          3.存储空间

            参考了Jedis的代码