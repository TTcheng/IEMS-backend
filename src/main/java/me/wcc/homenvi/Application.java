package me.wcc.homenvi;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * @author chuncheng.wang@hand-china.com 19-3-6 上午9:22
 */
@EnableScheduling
@EnableSwagger2Doc
@EnableAuthorizationServer // 提供/oauth/authorize,/oauth/token,/oauth/check_token,/oauth/confirm_access,/oauth/error
@SpringBootApplication(scanBasePackages = {"io.choerodon.mybatis", "me.wcc.auth", "me.wcc.base", "me.wcc.homenvi"})
public class Application {
    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }
}
