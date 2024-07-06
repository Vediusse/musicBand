package com.example;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {"Entities.Events", "Entities.Bands"})
@EnableJpaRepositories(basePackages = {"com.example.Repository"})
public class JpaConfig {
}
