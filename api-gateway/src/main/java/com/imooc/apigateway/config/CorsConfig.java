package com.imooc.apigateway.config;

import org.apache.catalina.filters.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 跨域配置
 *
 *C - Cross 0 - Origin R - Resource S - Sharing
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter(){
        final UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config=new CorsConfiguration();
        //是否支持Cookie跨域
        config.setAllowCredentials(true);
        //这个是要放哪些原始域,所有的话就放一个*号其实自己可以放原始域，自己定义的域名，比如 http:www.a.com
        config.setAllowedOrigins(Arrays.asList("*"));
        //允许的头信息
        config.setAllowedHeaders(Arrays.asList("*"));
        //允许的请求方式,有get,post *号就是全都允许
        config.setAllowedMethods(Arrays.asList("*"));
        //缓存时间,也就是说再这个时间段里，对于相同的跨域请求，进行一个检查了
        config.setMaxAge(300l);
        //跨域的配置注册到source上去  path路径呢就是对哪些域名下面进行配置config  就是跨域的真正的各种配置了
        source.registerCorsConfiguration("/**",config);
        return new CorsFilter();
    }
}
