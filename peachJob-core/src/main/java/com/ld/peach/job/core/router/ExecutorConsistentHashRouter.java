package com.ld.peach.job.core.router;

import cn.hutool.core.collection.CollectionUtil;
import com.ld.peach.job.core.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @ClassName ExecutorConsistentHashRouter
 * @Description 一致性hash路由
 * @Author lidong
 * @Date 2020/11/18
 * @Version 1.0
 */
@Slf4j
public class ExecutorConsistentHashRouter implements IExecutorRouter {

    @Override
    public String route(String app, List<String> addressList) {
        if (StringUtil.isBlank(app) || CollectionUtil.isEmpty(addressList)) {
            return null;
        }

        int nodeCount = addressList.size();
        if (addressList.size() == 1) {
            return addressList.get(0);
        } else {
            nodeCount = nodeCount * 10;
        }

        // 设置虚拟节点为真实节点数的 10 倍
        ConsistentHash<String> consistentHash = new ConsistentHash(nodeCount);
        consistentHash.add(addressList);
        String address = consistentHash.getNode(app + ThreadLocalRandom.current().nextInt(nodeCount));
        log.info("{} Consistent Hash Address [ {} ]", app, address);
        return address;
    }
}
