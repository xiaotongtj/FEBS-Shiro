package cc.mrbird.febs.common.runner;

import cc.mrbird.febs.common.properties.FebsProperties;
import cc.mrbird.febs.monitor.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * @author MrBird
 */
@Slf4j
@Component
public class FebsStartedUpRunner implements ApplicationRunner {

    @Autowired
    private ConfigurableApplicationContext context;
    @Autowired
    private FebsProperties febsProperties;
    @Autowired
    private IRedisService redisService;

    @Value("${server.port:8080}")
    private String port;
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            // 测试 Redis连接是否正常
            redisService.exists("febs_test");
        } catch (Exception e) {
            log.error(" ____   __    _   _ ");
            log.error("| |_   / /\\  | | | |");
            log.error("|_|   /_/--\\ |_| |_|__");
            log.error("                        ");
            log.error("FEBS启动失败，{}", e.getMessage());
            log.error("Redis连接异常，请检查Redis连接配置并确保Redis服务已启动");
            // 关闭 FEBS
            context.close();
        }
        //IoC容器已经激活了，获取本地ip
        if (context.isActive()) {
            InetAddress address = InetAddress.getLocalHost();
            String url = String.format("http://%s:%s", address.getHostAddress(), port);
            //loginUrl = /login
            String loginUrl = febsProperties.getShiro().getLoginUrl();
            if (StringUtils.isNotBlank(contextPath))
                url += contextPath;
            if (StringUtils.isNotBlank(loginUrl))
                url += loginUrl;
            log.info(" __    ___   _      ___   _     ____ _____  ____ ");
            log.info("/ /`  / / \\ | |\\/| | |_) | |   | |_   | |  | |_  ");
            log.info("\\_\\_, \\_\\_/ |_|  | |_|   |_|__ |_|__  |_|  |_|__ ");
            log.info("                                                      ");
            log.info("FEBS 权限系统启动完毕，地址：{}", url);
        }
    }
}
