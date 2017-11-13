package io.union.admin.config;


import io.union.admin.security.AuthenticationProcessingFilter;
import io.union.admin.security.SecurityInterceptor;
import io.union.admin.security.SecurityMetadataSourceImpl;
import io.union.admin.security.UserDetailsServiceImpl;
import io.union.admin.util.ApplicationConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created by Administrator on 2017/4/12.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(value = "io.union.admin")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private SecurityMetadataSourceImpl securityMetadataSource;
    @Autowired
    private AccessDecisionManager accessDecisionManager;
    //@Autowired
    //private UserDetailsAuthenticationProvider userDetailsAuthenticationProvider;

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider userDetailsAuthenticationProvider() {
        //SystemWideSaltSource saltSource = new SystemWideSaltSource();
        //saltSource.setSystemWideSalt("");
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        //provider.setSaltSource(saltSource);
        return provider;
    }

    @Bean("authenticationSuccessHandler")
    protected SimpleUrlAuthenticationSuccessHandler authenticationSuccessHandler() {
        return new SimpleUrlAuthenticationSuccessHandler(ApplicationConstant.SYSTEM_INDEX_PATH);
    }

    @Bean("authenticationFailureHandler")
    protected SimpleUrlAuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler(ApplicationConstant.LOGIN_PAGE_PATH);
    }

    @Bean(name = "authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() {
        AuthenticationManager authenticationManager = null;
        try {
            authenticationManager = super.authenticationManagerBean();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authenticationManager;
    }

    @Bean
    public AuthenticationProcessingFilter authenticationProcessingFilter() {
        AuthenticationProcessingFilter authenticationProcessingFilter = new AuthenticationProcessingFilter();
        authenticationProcessingFilter.setAuthenticationManager(authenticationManagerBean());
        authenticationProcessingFilter.setFilterProcessesUrl(ApplicationConstant.LOGIN_CHECK_PATH);
        authenticationProcessingFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        authenticationProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler());
        authenticationProcessingFilter.setPostOnly(true);
        return authenticationProcessingFilter;
    }

    @Bean
    public SecurityInterceptor securityInterceptor() {
        SecurityInterceptor securityInterceptor = new SecurityInterceptor();
        securityInterceptor.setAccessDecisionManager(accessDecisionManager);
        securityInterceptor.setAuthenticationManager(authenticationManagerBean());
        securityInterceptor.setSecurityMetadataSource(securityMetadataSource);
        return securityInterceptor;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterAt(authenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(securityInterceptor(), FilterSecurityInterceptor.class);
        http.sessionManagement().invalidSessionUrl(ApplicationConstant.LOGIN_PAGE_PATH)
                .sessionAuthenticationErrorUrl(ApplicationConstant.LOGIN_PAGE_PATH)
                .maximumSessions(1);
        http.rememberMe().disable();
        http.authorizeRequests().and().formLogin().loginPage(ApplicationConstant.LOGIN_PAGE_PATH)
                .and().logout().deleteCookies("JSESSIONID").logoutUrl(ApplicationConstant.LOGOUT_PAGE_PATH)
                .invalidateHttpSession(true).logoutSuccessUrl(ApplicationConstant.LOGIN_PAGE_PATH).permitAll();
        http.csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(userDetailsAuthenticationProvider());
    }
}
