package cc.mrbird.febs.common.configure;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;


//拦截器顺序，Executor (Mapper.xml)-->StatementHandler(sql)
@Component
@Lazy
@Intercepts({@Signature(type = StatementHandler.class, method = "update", args = {Statement.class})})
public class UniqueCodeInterceptor implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        if (target instanceof RoutingStatementHandler) {
            RoutingStatementHandler routingStatementHandler = (RoutingStatementHandler) target;
            //绑定sql //INSERT INTO t_user (USERNAME, PASSWORD, DEPT_ID, EMAIL, MOBILE, STATUS, CREATE_TIME, SSEX, AVATAR, THEME, IS_TAB, DESCRIPTION) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            BoundSql boundSql = routingStatementHandler.getBoundSql();
            String sql = boundSql.getSql();
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            //参数
            Object parameterObject = boundSql.getParameterObject();


            ParameterHandler parameterHandler = routingStatementHandler.getParameterHandler();
            Object parameterObject1 = parameterHandler.getParameterObject();
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    public static void main(String[] args) {
        AtomicInteger i = new AtomicInteger(0);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(i.incrementAndGet());
            }
        }, 1000, 1000);
    }
}
