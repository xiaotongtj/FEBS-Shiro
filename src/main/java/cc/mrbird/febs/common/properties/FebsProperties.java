package cc.mrbird.febs.common.properties;

import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

/**
 * @author MrBird
 */
@Data
@SpringBootConfiguration
@PropertySource(value = {"classpath:febs.properties"})
@ConfigurationProperties(prefix = "febs") //松散绑定（松散语法）
public class FebsProperties {

    private ShiroProperties shiro = new ShiroProperties();
    private boolean openAopLog = true;
}
