package cc.mrbird.febs.common.properties;

import lombok.Data;

/**
 * @author MrBird
 */
@Data
public class ShiroProperties {

//    # 是否开启 AOP日志，默认开启
//    febs.openAopLog=true
//            # session 超时时间，单位为秒
//    febs.shiro.session_timeout=3600
//            # rememberMe cookie有效时长，单位为秒
//    febs.shiro.cookie_timeout=86400
//            # 免认证的路径配置，如静态资源等
//    febs.shiro.anon_url=/test/**,/febs/**,/img/**,/layui/**,/json/**,/images/captcha,/regist
// # 登录 url
// febs.shiro.login_url=/login
// # 首页 url
// febs.shiro.success_url=/index
// # 登出 url
// febs.shiro.logout_url=/logout
// # 未授权跳转 url
// febs.shiro.unauthorized_url=/unauthorized
// # Excel单次导入最大数据量，如 300个数据一次commit
// febs.max.batch.insert.num=300

    private long sessionTimeout;
    private int cookieTimeout;
    private String anonUrl;
    private String loginUrl;
    private String successUrl;
    private String logoutUrl;
    private String unauthorizedUrl;
}
