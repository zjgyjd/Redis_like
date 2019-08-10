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
        args = this.args;
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
        Map<String, String> map = DataBase.getInstance().getHashMap(key); // set
        map.put(key, value);
        logger.debug("插入后数据共有 {} 个", map.size());

        Protocol.writeInteger(os, map.size());
    }
}
