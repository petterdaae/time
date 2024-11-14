package dev.daae.time.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer
import org.springframework.security.web.SecurityFilterChain
import java.lang.Exception

@Configuration
@EnableWebSecurity
open class SecurityConfig {
    @Bean
    @Throws(Exception::class)
    open fun securityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        http
            .authorizeHttpRequests {
                it.anyRequest().authenticated()
            }
            .csrf(Customizer { obj: CsrfConfigurer<HttpSecurity?>? -> obj!!.disable() })
            .httpBasic(Customizer.withDefaults<HttpBasicConfigurer<HttpSecurity?>?>())
        return http.build()
    }
}
