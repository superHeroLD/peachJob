package com.ld.peach.job.core.executor;

import com.ld.peach.job.core.anno.PeachTask;
import com.ld.peach.job.core.exception.PeachJobConfigException;
import com.ld.peach.job.core.handler.TaskHandler;
import com.ld.peach.job.core.generic.TaskResponse;
import com.ld.peach.job.core.util.MapUtil;
import com.ld.peach.job.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName TaskSpringExecutor
 * @Description Spring Executor
 * 内嵌到Spring中执行
 * @Author lidong
 * @Date 2020/9/8
 * @Version 1.0
 */
public class TaskSpringExecutor extends AbstractTaskExecutor implements ApplicationContextAware, SmartInitializingSingleton, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskSpringExecutor.class);

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        TaskSpringExecutor.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void destroy() throws Exception {
        super.destory();
    }

    @Override
    public void afterSingletonsInstantiated() {
        injectTaskHandler(applicationContext);
    }

    /**
     * inject task handler to spring
     *
     * @param applicationContext spring上下文
     */
    private void injectTaskHandler(ApplicationContext applicationContext) {
        if (Objects.isNull(applicationContext)) {
            return;
        }

        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);

            Map<Method, PeachTask> annotatedMethodMap = null;

            try {
                annotatedMethodMap = MethodIntrospector.selectMethods(bean.getClass(),
                        (MethodIntrospector.MetadataLookup<PeachTask>) method -> AnnotatedElementUtils.findMergedAnnotation(method, PeachTask.class));
            } catch (Exception ex) {
                LOGGER.error("[TaskSpringExecutor] resolve error for bean[" + beanDefinitionName + "].", ex);
            }

            if (MapUtil.isEmpty(annotatedMethodMap)) {
                return;
            }

            for (Map.Entry<Method, PeachTask> entry : annotatedMethodMap.entrySet()) {
                Method method = entry.getKey();
                PeachTask peachTask = entry.getValue();

                if (Objects.isNull(peachTask)) {
                    continue;
                }

                String handlerName = peachTask.value();
                if (StringUtil.isBlank(handlerName)) {
                    throw new PeachJobConfigException("[TaskSpringExecutor] task handler name invalid, for[" + bean.getClass() + "#" + method.getName() + "] .");
                }

                if (getTaskHandler(handlerName) != null) {
                    throw new PeachJobConfigException("[TaskSpringExecutor] task handler name : [" + handlerName + "] naming conflicts.");
                }

                if (!(method.getParameterTypes().length == 1 && method.getParameterTypes()[0].isAssignableFrom(String.class))) {
                    throw new RuntimeException("[TaskSpringExecutor] task handler param-classtype invalid, for[" + bean.getClass() + "#" + method.getName() + "] , " +
                            "The correct method format like \" public TaskResponse execute(String param) \" .");
                }

                if (!method.getReturnType().isAssignableFrom(TaskResponse.class)) {
                    throw new RuntimeException("[TaskSpringExecutor] task handler return-classtype invalid, for[" + bean.getClass() + "#" + method.getName() + "] , " +
                            "The correct method format like \" public TaskResponse execute(String param) \" .");
                }

                method.setAccessible(true);


                registerTaskHandler(handlerName, new TaskHandler(bean, method));
                LOGGER.info("[TaskSpringExecutor] register bean [{}] method name[{}] for TaskHandler", bean.getClass().getName(), method.getName());
            }
        }
    }
}
