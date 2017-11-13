package io.union.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;


/**
 * Spring boot 使用的是tomcat的默认文件大小限制，即 1MB
 * 这里修改默认限制
 * <p>
 * 在配置文件中找到相对应的配置项，先使用配置项
 */

@Configuration
public class MultipartConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement(@Value("${spring.http.multipart.max-file-size}") String maxFileSize) {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(maxFileSize);
        factory.setMaxRequestSize(maxFileSize);
        return factory.createMultipartConfig();
    }
}
