package stud.ntnu.no.backend.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@TestConfiguration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "stud.ntnu.no.backend")
@EntityScan(basePackages = "stud.ntnu.no.backend")
@ComponentScan(
    basePackages = "stud.ntnu.no.backend",
    includeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*Repository"),
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*Application")
)
@EnableTransactionManagement
@AutoConfigureDataJpa
public class TestConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .generateUniqueName(true)
                .build();
    }
} 