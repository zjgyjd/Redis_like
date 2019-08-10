package com.zjgyjd.commands;

import com.zjgyjd.Command;
import com.zjgyjd.DataBase;
import com.zjgyjd.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class LPUSHCommand implements Command {
    private static final Logger logger = LoggerFactory.getLogger(LPUSHCommand.class);
    private List<Object> args;

    @Override
    public void setArgs(List<Object> args) {
        this.args = args;
    }

    @Override
    public void run(OutputStream os) throws IOException {
        if (args.size() != 2) {
            Protocol.writeError(os, "命令只能有两个参数");
            return;
        }
        String key = new String((byte[])args.get(0));
        String value = new String((byte[])args.get(1));
        logger.debug("运行的是 lpush 命令: {} {}", key, value);

        // 这种方式不是一个很好的线程同步的方式
        List<String> list = DataBase.getInstance().getList(key);
        list.add(0, value);

        logger.debug("插入后数据共有 {} 个", list.size());

        Protocol.writeInteger(os, list.size());
    }
}

