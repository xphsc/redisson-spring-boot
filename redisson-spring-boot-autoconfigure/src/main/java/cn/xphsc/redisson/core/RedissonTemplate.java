/*
 * Copyright (c) 2021 huipei.x
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.xphsc.redisson.core;


import org.redisson.api.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * {@link }
 * @author <a href="xiongpeih@163.com">huipei.x</a>
 * @description:
 * @since 1.0.0
 */
public class RedissonTemplate {

    private final RedissonClient redissonClient;

    public RedissonTemplate(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 获取对象值
     */
    public <T> T value(String name) {
        RBucket<T> bucket = redissonClient.getBucket(name);
        return bucket.get();
    }

    /**
     * 获取对象空间
     */
    public <T> RBucket<T> getBucket(String name) {
        return redissonClient.getBucket(name);
    }



    /**
     * 设置对象的值
     * @param name  键
     * @param value 值
     * @param time  缓存时间 单位毫秒 -1 永久缓存
     */
    public <T> void setValue(String name, T value, Long time) {
        RBucket<Object> bucket = redissonClient.getBucket(name);
        if(time==-1){
            bucket.set(value);
        }else {
            bucket.set(value, time, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 如果值已经存在则则不设置
     * @param name  键
     * @param value 值
     * @param time  缓存时间 单位毫秒
     */
    public <T> boolean trySetValue(String name, T value, Long time) {
        RBucket<Object> bucket = redissonClient.getBucket(name);
        boolean b;
        if(time==-1){
            b = bucket.trySet(value);
        }else {
            b = bucket.trySet(value, time, TimeUnit.MILLISECONDS);
        }
        return b;
    }

    /**
     * 删除对象
     * @param name 键
     */
    public boolean delete(String name) {
        return redissonClient.getBucket(name).delete();
    }


    /**
     * 获取map集合
     * @param name
     * @param <K>
     * @param <V>
     */
    public <K,V> RMap<K, V> getMap(String name){
        return redissonClient.getMap(name);
    }

    /**
     * 设置map集合
     * @param name
     * @param data
     * @param time 缓存时间,单位毫秒 -1永久缓存
     */
    public void setMapValues(String name, Map data, Long time){
        RMap map = redissonClient.getMap(name);
            map.expire(time, TimeUnit.MILLISECONDS);
            map.putAll(data);
    }


    /**
     * 获取List集合
     * @param name
     */
    public <T> RList<T> getList(String name){
        return redissonClient.getList(name);
    }

    /**
     * 设置List集合
     * @param name
     * @param data
     * @param time 缓存时间,单位毫秒 -1永久缓存
     */
    public void setListValues(String name, List data, Long time) {
        RList list = redissonClient.getList(name);
            list.expire(time, TimeUnit.MILLISECONDS);
            list.addAll(data);
    }
    /**
     * 获取set集合
     * @param name
     */

        public <T> RSet<T> getSet(String name){
            return redissonClient.getSet(name);
        }


    /**
     * 设置set集合
     * @param name
     * @param data
     * @param time 缓存时间,单位毫秒 -1永久缓存
     */
    public void setSetValues(String name, Set data, Long time){
        RSet set = redissonClient.getSet(name);
        if(time!=-1){
            set.expire(time, TimeUnit.MILLISECONDS);
        }
        set.addAll(data);
    }
    /**
     * 获取输出流
     * @param name
     */
    public OutputStream getOutputStream(String name) {
        RListMultimap<Object, Object> listMultimap = redissonClient.getListMultimap("");
        RBinaryStream binaryStream = redissonClient.getBinaryStream(name);
        return binaryStream.getOutputStream();
    }
    /**
     * 获取输入流
     * @param name
     * @return
     */
    public InputStream getInputStream(String name) {
        RBinaryStream binaryStream = redissonClient.getBinaryStream(name);
        return binaryStream.getInputStream();
    }
    /**
     * 获取输入流
     * @param name
     */
    public InputStream getValue(String name,OutputStream stream) {
        try {
            RBinaryStream binaryStream = redissonClient.getBinaryStream(name);
            InputStream inputStream = binaryStream.getInputStream();
            byte[] buff=new byte[1024];
            int len;
            while ((len=inputStream.read(buff))!=-1){
                stream.write(buff,0,len);
            }
            return binaryStream.getInputStream();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取对象空间
     * @param name
     */
    public RBinaryStream getBinaryStream(String name) {
        return redissonClient.getBinaryStream(name);
    }

    /**
     * 设置对象的值
     * @param name  键
     * @param value 值
     */
    public void setValue(String name, InputStream value) {
        try {
            RBinaryStream binaryStream = redissonClient.getBinaryStream(name);
            binaryStream.delete();
            OutputStream outputStream = binaryStream.getOutputStream();
            byte[] buff = new byte[1024];
            int len;
            while ((len=value.read(buff))!=-1){
                outputStream.write(buff,0,len);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除对象
     * @param name 键
     * @return true 删除成功,false 不成功
     */
    public boolean streamDelete(String name) {
        RBinaryStream binaryStream = redissonClient.getBinaryStream(name);
        return binaryStream.delete();
    }


}
