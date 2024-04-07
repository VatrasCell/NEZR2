package de.vatrascell.nezr.application;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan("de.vatrascell.nezr")
@EntityScan("de.vatrascell.nezr")
@EnableJpaRepositories("de.vatrascell.nezr")
public class ApplicationConfiguration {

}
