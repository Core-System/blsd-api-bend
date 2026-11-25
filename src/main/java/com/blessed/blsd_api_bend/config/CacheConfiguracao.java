package com.blessed.blsd_api_bend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguracao implements CachingConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheConfiguracao.class);

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                logCacheFailure("consultar", cache, key, exception);
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                logCacheFailure("gravar", cache, key, exception);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                logCacheFailure("invalidar", cache, key, exception);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                LOGGER.warn(
                        "Falha ao limpar o cache Redis '{}'; os dados podem permanecer ate o TTL.",
                        cache.getName(),
                        exception
                );
            }
        };
    }

    private void logCacheFailure(String operation, Cache cache, Object key, RuntimeException exception) {
        LOGGER.warn(
                "Falha ao {} o cache Redis '{}' para a chave '{}'; a requisicao continuara sem cache.",
                operation,
                cache.getName(),
                key,
                exception
        );
    }
}
