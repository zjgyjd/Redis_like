package com.zjgyjd;

import com.zjgyjd.exception.RedisException;
import com.zjgyjd.exception.RemoteException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Protocol {


    public static Command readCommand(InputStream is) throws Exception {
        Object o = read(is);
        if(!(o instanceof List)){
            throw new RedisException("命令必须是Array类型");
        }

        List<Object> list = (List<Object>) o;
        if(list.size() < 1){
            throw new RedisException("命令元素必须大于1");
        }

        Object o2 = list.get(0);
        if(!(o2 instanceof byte[])){
            throw new RemoteException("错误的命令类型");
        }
        byte[] array = (byte[]) o2;
        String commandName = new String(array).toUpperCase();
        if(commandName.equals("COMMAND")){
            return null;
        }
        if(commandName.equals("Q")){
            return null;
        }
        String className = String.format("com.zjgyjd.commands.%sCommand",commandName);
        Class<?> command = Class.forName(className);
        //判断是不是子类
        if (!Command.class.isAssignableFrom(command)) {
            throw new Exception("错误的命令");
        }
        list.remove(0);
        Command v = (Command)command.newInstance();
        v.setArgs(list);
        return v;
    }

    private static String processSimpleString(ProtocolInputStream is) throws IOException {
       return is.readLine();
    }

    private static Long processInteger(ProtocolInputStream is) throws IOException {
        return is.readInteger();
    }

    private static String processError(ProtocolInputStream is) throws IOException {
        return is.readLine();
    }

    private static byte[] processBulkString(ProtocolInputStream is) throws IOException {
        int len = (int) is.readInteger();
        if (len == -1) {
            // "$-1\r\n"    ==> null
            return null;
        }
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            int b = is.read();
            result[i] = (byte)b;
        }
        is.read();//去掉\r
        is.read();//去掉\n
        return result;
    }

    private static List<Object> processArray(ProtocolInputStream is) throws IOException {
        int len = (int) is.readInteger();
        if (len == -1) {
            // "*-1\r\n"        ==> null
            return null;
        }
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            try {
                list.add(process(is));
            } catch (RemoteException e) {
                list.add(e);
            }
        }
        return list;
    }

    public static Object read(InputStream is) throws IOException, RemoteException{
        return process(new ProtocolInputStream(is));
    }

    public static Object process(ProtocolInputStream is) throws IOException, RemoteException {
        int b = is.read();//将类型读出来
        if (b == -1) {
            throw new RuntimeException("读到结尾了");
        }
        switch (b) {
            case '+':
                return processSimpleString(is);
            case '-':
                throw new RemoteException(processError(is));
            case ':':
                return processInteger(is);
            case '$':
                return processBulkString(is);
            case '*':
                return processArray(is);
            default:
            throw new RuntimeException("输入错误 " + (char)b);
        }
    }
    public static void writeError(OutputStream os, String message) throws IOException {
        os.write('-');
        os.write(message.getBytes("GBK"));
        os.write("\r\n".getBytes("GBK"));
    }

    public static void writeInteger(OutputStream os, long v) throws IOException {
        // v = 10
        //:10\r\n

        // v = -1
        //:-1\r\n

        os.write(':');
        os.write(String.valueOf(v).getBytes());
        os.write("\r\n".getBytes());
    }

    public static void writeArray(OutputStream os, List<String> result) throws IOException {
        os.write('*');
        os.write(String.valueOf(result.size()).getBytes());
        os.write("\r\n".getBytes());
        for (Object o : result) {
            if (o instanceof String) {
                writeBulkString(os, (String)o);
            } else if (o instanceof Integer) {
                writeInteger(os, (Integer)o);
            } else if (o instanceof Long) {
                writeInteger(os, (Long)o);
            } else {
                writeError(os,"类型错误");
            }
        }
    }
    public static void writeBulkString(OutputStream os, String s) throws IOException {
        byte[] buf = s.getBytes();
        os.write('$');
        os.write(String.valueOf(buf.length).getBytes());
        os.write("\r\n".getBytes());
        os.write(buf);
        os.write("\r\n".getBytes());
    }

    public static void writeNull(OutputStream os) throws IOException {
        os.write('$');
        os.write('-');
        os.write('1');
        os.write('\r');
        os.write('\n');
    }
}
