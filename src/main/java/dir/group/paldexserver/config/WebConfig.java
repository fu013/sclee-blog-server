package dir.group.paldexserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("file:${server.config.uploadPath}") // 임시 이미지 경로
    private String uploadPath;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // CORS Setting
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:2416", "http://175.121.139.222:2416")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("Authorization", "Content-Type")
                .exposedHeaders("Custom-Header")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.error(uploadPath);
        registry
                .addResourceHandler("/**")
                .addResourceLocations(uploadPath);
    }
}
