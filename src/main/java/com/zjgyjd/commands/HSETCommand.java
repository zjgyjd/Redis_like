package com.zjgyjd.commands;

import com.zjgyjd.Command;
import com.zjgyjd.DataBase;
import com.zjgyjd.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class HSETCommand implements Command {
    private List<Object> args;
    private static final Logger logger = LoggerFactory.getLogger(HSETCommand.class);

    @Override
    public void setArgs(List<Object> args) {
        this.args = args;
    }

    @Override
    public void run(OutputStream os) throws IOException {
        if (args.size() != 3) {
            Protocol.writeError(os, "命令只能有三个参数");
            return;
        }
        String key = new String((byte[]) args.get(0));
        String field = new String((byte[]) args.get(1));
        String value = new String((byte[]) args.get(2));
        logger.debug("运行的是 HSET 命令:{} {} {}", key, field, value);
        DataBase db = DataBase.getInstance(); // set
        Map<String , String> map = db.getHashMap(key);
        map.put(field, value);
        logger.debug("插入后数据共有 {} 个HashMap, 在{}下有{}个元素", db.getHashes().size() , key ,map.size());
        String response = "has "+ db.getHashes().size() +" HashMap" + " in "+key+" has "+map.size()+" elements";
        Protocol.writeBulkString(os, response);
    }
}
