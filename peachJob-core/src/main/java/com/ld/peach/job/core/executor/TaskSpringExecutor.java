package com.ld.peach.job.core.executor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @ClassName TaskSpringExecutor
 * @Description Spring Executor
 * 内嵌到Spring中执行
 * @Author lidong
 * @Date 2020/9/8
 * @Version 1.0
 */
public class TaskSpringExecutor implements ApplicationContextAware, SmartInitializingSingleton, DisposableBean {

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterSingletonsInstantiated() {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
