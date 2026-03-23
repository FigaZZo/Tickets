package com.example.reservation;

import ru.tinkoff.kora.application.graph.KoraApplication;
import ru.tinkoff.kora.common.KoraApp;
import ru.tinkoff.kora.config.yaml.YamlConfigModule;
import ru.tinkoff.kora.database.flyway.FlywayJdbcDatabaseModule;
import ru.tinkoff.kora.database.jdbc.JdbcDatabaseModule;
import ru.tinkoff.kora.http.server.common.HttpServerModule;
import ru.tinkoff.kora.http.server.undertow.UndertowHttpServerModule;
import ru.tinkoff.kora.json.module.JsonModule;
import ru.tinkoff.kora.kafka.common.KafkaModule;
import ru.tinkoff.kora.micrometer.module.MetricsModule;
import ru.tinkoff.kora.openapi.management.OpenApiManagementModule;
import ru.tinkoff.kora.resilient.ResilientModule;
import ru.tinkoff.kora.scheduling.jdk.SchedulingJdkModule;
import ru.tinkoff.kora.validation.module.ValidationModule;

@KoraApp
public interface Application extends
    YamlConfigModule,
    HttpServerModule,
    UndertowHttpServerModule,
    JsonModule,
    JdbcDatabaseModule,
    FlywayJdbcDatabaseModule,
    KafkaModule,
    ValidationModule,
    ResilientModule,
    MetricsModule,
    SchedulingJdkModule,
    OpenApiManagementModule {

    static void main(String[] args) {
        KoraApplication.run(() -> ApplicationGraph.graph());
    }
}
