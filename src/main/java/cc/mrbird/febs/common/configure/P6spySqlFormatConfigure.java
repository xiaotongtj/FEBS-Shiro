package cc.mrbird.febs.common.configure;

import cc.mrbird.febs.common.utils.DateUtil;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

/**
 * 自定义 p6spy sql输出格式
 *
 * @author MrBird
 */
public class P6spySqlFormatConfigure implements MessageFormattingStrategy {

    /**
     * 过滤掉定时任务的 SQL
     *example:
     * 2019-06-25 11:13:50.378 febs [main] INFO  p6spy -
     * 2019-06-25 11:13:50 | 耗时 3 ms | SQL 语句：
     * SELECT job_id jobId, bean_name beanName, method_name methodName, params, cron_expression cronExpression, status, remark, create_time createTime FROM t_job ORDER BY job_id;
     *
     * 2019-06-25 11:37:55 | 耗时 0 ms | SQL 语句：
     * SELECT date_format(l.login_time, '%m-%d') days, count(1) count FROM (SELECT * FROM t_login_log WHERE date_sub(curdate(), INTERVAL 10 day) <= date(login_time)) AS l WHERE 1 = 1 AND l.username = 'MrBird' GROUP BY days;
     */
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        return StringUtils.isNotBlank(sql) ? DateUtil.formatFullTime(LocalDateTime.now(), DateUtil.FULL_TIME_SPLIT_PATTERN)
                + " | 耗时 " + elapsed + " ms | SQL 语句：" + StringUtils.LF
                + sql.replaceAll("[\\s]+", StringUtils.SPACE) + ";" : StringUtils.EMPTY;
    }
}
