package org.dromara.hodor.client.annotation;

import org.dromara.hodor.client.HodorApiClient;
import org.dromara.hodor.client.HodorClientInit;
import org.dromara.hodor.client.JobRegistrar;
import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.client.executor.RequestEventPublisher;
import org.dromara.hodor.common.extension.ExtensionLoader;
import org.dromara.hodor.common.storage.db.DBOperator;
import org.dromara.hodor.common.storage.db.DataSourceConfig;
import org.dromara.hodor.common.storage.db.AbstractHodorDataSource;
import org.dromara.hodor.common.storage.db.HodorDataSource;
import org.dromara.hodor.remoting.api.RemotingMessageSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * hodor scheduler configuration
 *
 * @author tomgs
 * @since 2020/12/30
 */
@Configuration
@EnableConfigurationProperties(HodorProperties.class)
public class HodorSchedulerConfiguration {

    private final HodorProperties properties;

    public HodorSchedulerConfiguration(final HodorProperties properties, final ApplicationContext applicationContext) {
        this.properties = properties;
        ServiceProvider.getInstance().setApplicationContext(applicationContext);
    }

    @Bean
    public HodorSchedulerAnnotationBeanPostProcessor scheduledAnnotationProcessor() {
        return new HodorSchedulerAnnotationBeanPostProcessor(jobRegistrar());
    }

    @Bean
    public HodorApiClient hodorApiClient() {
        return new HodorApiClient(properties);
    }

    @Bean
    public JobRegistrar jobRegistrar() {
        return new JobRegistrar();
    }

    @Bean
    public HodorClientInit hodorClientInit() {
        return new HodorClientInit();
    }

    @Bean
    public RequestEventPublisher requestEventPublisher() {
        return new RequestEventPublisher();
    }

    @Bean
    public RemotingMessageSerializer remotingMessageSerializer() {
        return ExtensionLoader.getExtensionLoader(RemotingMessageSerializer.class).getDefaultJoin();
    }

    @Bean
    public DBOperator dbOperator() {
        DataSourceConfig dataSourceConfig = properties.getDataSourceConfig();
        HodorDataSource datasource = ExtensionLoader.getExtensionLoader(HodorDataSource.class, DataSourceConfig.class)
            .getProtoJoin("datasource", dataSourceConfig);
        return new DBOperator(datasource.getDataSource());
    }

}
