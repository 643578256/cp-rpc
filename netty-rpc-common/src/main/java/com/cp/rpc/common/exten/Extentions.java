package com.cp.rpc.common.exten;

import com.cp.rpc.common.annotaion.SPI;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class Extentions {
    private static final   ConcurrentHashMap<Class, ConcurrentHashMap<String,Object>>  EXTENTATION_MAP = new ConcurrentHashMap<>();

    public static <T>  T loadExtation(Class<T> zlass,String name){
        if(EXTENTATION_MAP.get(zlass) != null){
            ConcurrentHashMap<String, Object> map = EXTENTATION_MAP.get(zlass);
            return (T)map.get(name);
        }
        ServiceLoader<T> load = ServiceLoader.load(zlass, Extentions.class.getClassLoader());
        ConcurrentHashMap<String,Object> map = new ConcurrentHashMap<>();
        Iterator<T> iterator = load.iterator();
        while (iterator.hasNext()){
            T next = iterator.next();
            SPI spi = next.getClass().getAnnotation(SPI.class);
            map.put(spi.name(),next);
        }
        synchronized (EXTENTATION_MAP){
           EXTENTATION_MAP.put(zlass, map);
            return (T)map.get(name);
        }
    }
}
