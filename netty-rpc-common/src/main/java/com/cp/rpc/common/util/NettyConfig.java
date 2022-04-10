package com.cp.rpc.common.util;

import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

public class NettyConfig {

    private static Map<String,Object> keyMap = new HashMap(32);

    /** TCP_NODELAY option */
    public static final String TCP_NODELAY                           = "cp.tcp.nodelay";
    public static final String TCP_NODELAY_DEFAULT                   = "true";

    /** TCP SO_REUSEADDR option */
    public static final String TCP_SO_REUSEADDR                      = "cp.tcp.so.reuseaddr";
    public static final String TCP_SO_REUSEADDR_DEFAULT              = "true";

    /** TCP SO_BACKLOG option */
    public static final String TCP_SO_BACKLOG                        = "cp.tcp.so.backlog";
    public static final String TCP_SO_BACKLOG_DEFAULT                = "1024";

    /** TCP SO_KEEPALIVE option */
    public static final String TCP_SO_KEEPALIVE                      = "cp.tcp.so.keepalive";
    public static final String TCP_SO_KEEPALIVE_DEFAULT              = "true";

    /** Netty ioRatio option*/
    public static final String NETTY_IO_RATIO                        = "cp.netty.io.ratio";
    public static final String NETTY_IO_RATIO_DEFAULT                = "70";

    /** Netty buffer allocator, enabled as default */
    public static final String NETTY_BUFFER_POOLED                   = "cp.netty.buffer.pooled";
    public static final String NETTY_BUFFER_POOLED_DEFAULT           = "true";

    /** Netty buffer high watermark */
    public static final String NETTY_BUFFER_HIGH_WATERMARK           = "cp.netty.buffer.high.watermark";
    public static final Integer NETTY_BUFFER_HIGH_WATERMARK_DEFAULT   = 64 * 1024;

    /** Netty buffer low watermark */
    public static final String NETTY_BUFFER_LOW_WATERMARK            = "cp.netty.buffer.low.watermark";
    public static final Integer NETTY_BUFFER_LOW_WATERMARK_DEFAULT    = 32 * 1024;

    static {
        keyMap.put(TCP_NODELAY, TCP_NODELAY_DEFAULT);
        keyMap.put(TCP_SO_REUSEADDR, TCP_SO_REUSEADDR_DEFAULT);
        keyMap.put(TCP_SO_BACKLOG, TCP_SO_BACKLOG_DEFAULT);
        keyMap.put(TCP_SO_KEEPALIVE, TCP_SO_KEEPALIVE_DEFAULT);
        keyMap.put(NETTY_IO_RATIO, NETTY_IO_RATIO_DEFAULT);
        keyMap.put(NETTY_BUFFER_POOLED, NETTY_BUFFER_POOLED_DEFAULT);
        keyMap.put(NETTY_BUFFER_HIGH_WATERMARK, NETTY_BUFFER_HIGH_WATERMARK_DEFAULT);
        keyMap.put(NETTY_BUFFER_LOW_WATERMARK, NETTY_BUFFER_LOW_WATERMARK_DEFAULT);
    }

    public static <T> T getValue(Environment environment,String config){
        if(environment == null){
            return (T)keyMap.get(config);
        }
        return (T)environment.getProperty(config);

    }

}
