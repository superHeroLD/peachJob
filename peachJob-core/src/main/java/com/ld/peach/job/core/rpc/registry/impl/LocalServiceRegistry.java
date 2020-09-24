package com.ld.peach.job.core.rpc.registry.impl;

import com.ld.peach.job.core.rpc.registry.IServiceRegistry;
import com.ld.peach.job.core.util.StringUtil;

import java.util.*;

/**
 * @ClassName LocalServiceRegistry
 * @Description 服务注册 存在本地
 * @Author lidong
 * @Date 2020/9/24
 * @Version 1.0
 */
public class LocalServiceRegistry implements IServiceRegistry {

    private Map<String, TreeSet<String>> registryData;

    @Override
    public void start(Map<String, String> param) {
        registryData = new HashMap<>();
    }

    @Override
    public void stop() {
        registryData.clear();
    }

    @Override
    public boolean registry(Set<String> keys, String value) {
        if (Objects.isNull(keys) || keys.isEmpty() || StringUtil.isBlank(value)) {
            return false;
        }

        keys.forEach(key -> {
            TreeSet<String> values = registryData.computeIfAbsent(key, k -> new TreeSet<>());
            values.add(value);
        });

        return true;
    }

    @Override
    public boolean remove(Set<String> keys, String value) {
        if (Objects.isNull(keys) || keys.isEmpty() || StringUtil.isBlank(value)) {
            return false;
        }

        keys.forEach(key -> {
            TreeSet<String> values = registryData.get(key);
            if (Objects.nonNull(values)) {
                values.remove(value);
            }
        });

        return true;
    }

    @Override
    public Map<String, TreeSet<String>> discovery(Set<String> keys) {
        if (Objects.isNull(keys) || keys.isEmpty()) {
            return null;
        }

        Map<String, TreeSet<String>> registryDataTmp = new HashMap<>(keys.size());

        keys.forEach(key -> {
            TreeSet<String> valueSetTmp = discovery(key);
            if (Objects.nonNull(valueSetTmp)) {
                registryDataTmp.put(key, valueSetTmp);
            }
        });

        return registryDataTmp;
    }

    @Override
    public TreeSet<String> discovery(String key) {
        return registryData.get(key);
    }
}
