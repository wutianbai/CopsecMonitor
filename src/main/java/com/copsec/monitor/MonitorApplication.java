package com.copsec.monitor;


import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class MonitorApplication extends SpringBootServletInitializer {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.of(20, DataUnit.MEGABYTES));
        factory.setMaxRequestSize(DataSize.of(30, DataUnit.MEGABYTES));
        return factory.createMultipartConfig();
    }

    /**
     * 配置线程池执行scheduler
     *
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler taskScheduler() {

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(32);
        taskScheduler.setWaitForTasksToCompleteOnShutdown(false);
        taskScheduler.setAwaitTerminationSeconds(30);
        taskScheduler.setThreadNamePrefix("monitor-");
        return taskScheduler;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.bannerMode(Mode.OFF);
        return application.sources(MonitorApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MonitorApplication.class);
        application.setBannerMode(Mode.OFF);
        application.run(args);
    }
}
