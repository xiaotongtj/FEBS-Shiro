package cc.mrbird.febs.monitor.controller;

import cc.mrbird.febs.common.entity.FebsConstant;
import cc.mrbird.febs.common.exception.RedisConnectException;
import cc.mrbird.febs.common.utils.FebsUtil;
import cc.mrbird.febs.monitor.entity.JvmInfo;
import cc.mrbird.febs.monitor.entity.RedisInfo;
import cc.mrbird.febs.monitor.entity.ServerInfo;
import cc.mrbird.febs.monitor.entity.TomcatInfo;
import cc.mrbird.febs.monitor.helper.FebsActuatorHelper;
import cc.mrbird.febs.monitor.service.IRedisService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static cc.mrbird.febs.monitor.endpoint.FebsMetricsEndpoint.FebsMetricResponse;

/**
 * @author MrBird
 */
@Controller("monitorView")
@RequestMapping(FebsConstant.VIEW_PREFIX + "monitor")
public class ViewController {

    @Autowired
    private FebsActuatorHelper actuatorHelper;
    @Autowired
    private IRedisService redisService;

    @GetMapping("online")
    @RequiresPermissions("online:view")
    public String online() {
        return FebsUtil.view("monitor/online");
    }

    @GetMapping("log")
    @RequiresPermissions("log:view")
    public String log() {
        return FebsUtil.view("monitor/log");
    }

    @GetMapping("loginlog")
    @RequiresPermissions("loginlog:view")
    public String loginLog() {
        return FebsUtil.view("monitor/loginLog");
    }

    @GetMapping("redis/info")
    @RequiresPermissions("redis:view")
    public String getRedisInfo(Model model) throws RedisConnectException {
        List<RedisInfo> infoList = this.redisService.getRedisInfo();
        model.addAttribute("infoList", infoList);
        return FebsUtil.view("monitor/redisInfo");
    }

    //redis终端
    @GetMapping("redis/terminal")
    @RequiresPermissions("redis:terminal:view")
    public String redisTerminal(Model model) {
        String osName = System.getProperty("os.name");
        model.addAttribute("osName", osName);
        return FebsUtil.view("monitor/redisTerminal");
    }

    @GetMapping("httptrace")
    @RequiresPermissions("httptrace:view")
    public String httptrace() {
        return FebsUtil.view("monitor/httpTrace");
    }

    @GetMapping("jvm")
    @RequiresPermissions("jvm:view")
    public String jvmInfo(Model model) {
        List<FebsMetricResponse> jvm = actuatorHelper.getMetricResponseByType("jvm");
        JvmInfo jvmInfo = actuatorHelper.getJvmInfoFromMetricData(jvm);
        model.addAttribute("jvm", jvmInfo);
        return FebsUtil.view("monitor/jvmInfo");
    }

    @GetMapping("tomcat")
    @RequiresPermissions("tomcat:view")
    public String tomcatInfo(Model model) {
        List<FebsMetricResponse> tomcat = actuatorHelper.getMetricResponseByType("tomcat");
        TomcatInfo tomcatInfo = actuatorHelper.getTomcatInfoFromMetricData(tomcat);
        model.addAttribute("tomcat", tomcatInfo);
        return FebsUtil.view("monitor/tomcatInfo");
    }

    @GetMapping("server")
    @RequiresPermissions("server:view")
    public String serverInfo(Model model) {
        List<FebsMetricResponse> jdbcInfo = actuatorHelper.getMetricResponseByType("jdbc");
        List<FebsMetricResponse> systemInfo = actuatorHelper.getMetricResponseByType("system");
        List<FebsMetricResponse> processInfo = actuatorHelper.getMetricResponseByType("process");

        ServerInfo serverInfo = actuatorHelper.getServerInfoFromMetricData(jdbcInfo, systemInfo, processInfo);
        model.addAttribute("server", serverInfo);
        return FebsUtil.view("monitor/serverInfo");
    }

}
