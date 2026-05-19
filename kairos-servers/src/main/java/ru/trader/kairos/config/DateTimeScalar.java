package ru.trader.kairos.config;

import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class DateTimeScalar {

    @Bean
    public GraphQLScalarType graphQLDateTimeScalar() {
        return GraphQLScalarType.newScalar()
                .name("DateTime")
                .description("ISO-8601 date time with offset")
                .coercing(new DateTimeCoercing())
                .build();
    }

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(GraphQLScalarType graphQLDateTimeScalar) {
        return wiring -> wiring.scalar(graphQLDateTimeScalar);
    }
}
