package stud.ntnu.no.backend.item.repository;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
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
@EntityScan(basePackages = {
    "stud.ntnu.no.backend.item.entity",
    "stud.ntnu.no.backend.category.entity",
    "stud.ntnu.no.backend.user.entity",
    "stud.ntnu.no.backend.location.entity",
    "stud.ntnu.no.backend.shippingoption.entity",
    "stud.ntnu.no.backend.itemimage.entity",
    "stud.ntnu.no.backend.message.entity",
    "stud.ntnu.no.backend.review.entity",
    "stud.ntnu.no.backend.transaction.entity",
    "stud.ntnu.no.backend.history.entity",
    "stud.ntnu.no.backend.favorite.entity"
})
@EnableJpaRepositories(basePackages = {
    "stud.ntnu.no.backend.item.repository"
})
@ComponentScan(
    basePackages = {
        "stud.ntnu.no.backend.item.repository"
    },
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
            stud.ntnu.no.backend.BackendApplication.class
        })
    }
)
@EnableTransactionManagement
public class ItemRepositoryTestConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .generateUniqueName(true)
                .build();
    }
} 