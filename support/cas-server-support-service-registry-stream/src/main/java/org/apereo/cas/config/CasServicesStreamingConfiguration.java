package org.apereo.cas.config;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.services.CasServicesRegistryStreamingEventListener;
import org.apereo.cas.services.DefaultCasServicesRegistryStreamingEventListener;
import org.apereo.cas.services.RegisteredService;
import org.apereo.cas.services.publisher.CasRegisteredServiceStreamPublisher;
import org.apereo.cas.services.publisher.DefaultCasRegisteredServiceStreamPublisher;
import org.apereo.cas.services.replication.DefaultRegisteredServiceReplicationStrategy;
import org.apereo.cas.services.replication.RegisteredServiceReplicationStrategy;
import org.apereo.cas.util.PublisherIdentifier;
import org.apereo.cas.util.cache.DistributedCacheManager;
import org.apereo.cas.util.cache.DistributedCacheObject;

import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopedProxyMode;

/**
 * This is {@link CasServicesStreamingConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@EnableConfigurationProperties(CasConfigurationProperties.class)
@ConditionalOnProperty(prefix = "cas.service-registry.stream.core", name = "enabled", havingValue = "true", matchIfMissing = true)
@Configuration(value = "CasServicesStreamingConfiguration", proxyBeanMethods = false)
public class CasServicesStreamingConfiguration {

    @Bean
    public CasServicesRegistryStreamingEventListener casServicesRegistryStreamingEventListener(
        @Qualifier("casRegisteredServiceStreamPublisher")
        final CasRegisteredServiceStreamPublisher casRegisteredServiceStreamPublisher,
        @Qualifier("casRegisteredServiceStreamPublisherIdentifier")
        final PublisherIdentifier casRegisteredServiceStreamPublisherIdentifier) {
        return new DefaultCasServicesRegistryStreamingEventListener(casRegisteredServiceStreamPublisher,
            casRegisteredServiceStreamPublisherIdentifier);
    }

    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    @Bean
    public RegisteredServiceReplicationStrategy registeredServiceReplicationStrategy(
        final CasConfigurationProperties casProperties,
        @Qualifier("registeredServiceDistributedCacheManager")
        final DistributedCacheManager<RegisteredService, DistributedCacheObject<RegisteredService>, PublisherIdentifier> registeredServiceDistributedCacheManager,
        @Qualifier("casRegisteredServiceStreamPublisherIdentifier")
        final PublisherIdentifier casRegisteredServiceStreamPublisherIdentifier) {
        val stream = casProperties.getServiceRegistry().getStream();
        return new DefaultRegisteredServiceReplicationStrategy(registeredServiceDistributedCacheManager,
            stream, casRegisteredServiceStreamPublisherIdentifier);
    }

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public CasRegisteredServiceStreamPublisher casRegisteredServiceStreamPublisher(
        @Qualifier("registeredServiceDistributedCacheManager")
        final DistributedCacheManager<RegisteredService, DistributedCacheObject<RegisteredService>, PublisherIdentifier> registeredServiceDistributedCacheManager) {
        return new DefaultCasRegisteredServiceStreamPublisher(registeredServiceDistributedCacheManager);
    }

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    @ConditionalOnMissingBean(name = "registeredServiceDistributedCacheManager")
    public DistributedCacheManager<RegisteredService, DistributedCacheObject<RegisteredService>, PublisherIdentifier> registeredServiceDistributedCacheManager() {
        return DistributedCacheManager.noOp();
    }

    @ConditionalOnMissingBean(name = "casRegisteredServiceStreamPublisherIdentifier")
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public PublisherIdentifier casRegisteredServiceStreamPublisherIdentifier() {
        return new PublisherIdentifier();
    }
}
