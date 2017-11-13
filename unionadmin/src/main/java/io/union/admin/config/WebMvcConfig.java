package io.union.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Administrator on 2017/4/13.<br/>
 * 因为使用了@RestController，所以如果不返回ModelAndView的话，那就是JSON数据<br/>
 * 而且同时使用了application.properties，所以这个文件也是多余的<br/>
 * 如果以后不想使用application.properties，那可以启用这个文件<br/>
 * update by Administrator on 2017/04/18
 */
/*@Configuration
@EnableWebMvc
@ComponentScan(value = "io.union.admin")*/
@Deprecated
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean(name = "templateResolver")
    public ITemplateResolver servletContextTemplateResolver() {
        ServletContextTemplateResolver contextTemplateResolver = new ServletContextTemplateResolver();
        contextTemplateResolver.setCacheable(false);
        contextTemplateResolver.setOrder(0);
        contextTemplateResolver.setPrefix("classpath:/templates/");
        contextTemplateResolver.setSuffix(".html");
        contextTemplateResolver.setCharacterEncoding("UTF8");
        contextTemplateResolver.setTemplateMode("HTML5");
        return contextTemplateResolver;
    }

    @Bean(name = "templateEngine")
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(servletContextTemplateResolver());
        return templateEngine;
    }

    @Bean(name = "thymeleafViewResolver")
    public ThymeleafViewResolver thymeleafViewResolver() {
        ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
        thymeleafViewResolver.setCharacterEncoding("UTF8");
        thymeleafViewResolver.setContentType("text/html; charset=UTF-8");
        thymeleafViewResolver.setTemplateEngine(templateEngine());
        thymeleafViewResolver.setExcludedViewNames(new String[]{"*.xml"});
        return thymeleafViewResolver;
    }

    @Bean(name = "internalResourceViewResolver")
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setOrder(1);
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    @Bean(name = "contentNegotiationManagerFactoryBean")
    @Primary
    public ContentNegotiationManagerFactoryBean contentNegotiationManagerFactoryBean() {
        //Media Types
        Properties types = new Properties();
        types.setProperty("html", MediaType.ALL_VALUE);
        //types.setProperty("html", MediaType.TEXT_HTML_VALUE);
        //types.setProperty("xml", MediaType.APPLICATION_XML_VALUE);
        //types.setProperty("atom", MediaType.APPLICATION_ATOM_XML_VALUE);


        ContentNegotiationManagerFactoryBean managerFactoryBean = new ContentNegotiationManagerFactoryBean();
        managerFactoryBean.setFavorParameter(true);
        managerFactoryBean.setFavorPathExtension(true);
        //managerFactoryBean.setIgnoreAcceptHeader(true);
        managerFactoryBean.setDefaultContentType(MediaType.TEXT_HTML);
        managerFactoryBean.setMediaTypes(types);
        return managerFactoryBean;
    }

    @Bean
    public ContentNegotiatingViewResolver contentNegotiatingViewResolver() {
        List<ViewResolver> resolvers = new ArrayList<ViewResolver>();
        resolvers.add(thymeleafViewResolver());
        //resolvers.add(internalResourceViewResolver());
        ContentNegotiatingViewResolver contentNegotiatingViewResolver = new ContentNegotiatingViewResolver();
        contentNegotiatingViewResolver.setContentNegotiationManager(contentNegotiationManagerFactoryBean().getObject());
        contentNegotiatingViewResolver.setViewResolvers(resolvers);
        return contentNegotiatingViewResolver;
    }
}
