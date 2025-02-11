package org.financer.transactionservice.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cache.CacheManager
import org.springframework.cache.support.NoOpCacheManager
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestConfig {
    @Bean
    fun cacheManager(): CacheManager {
        return NoOpCacheManager()
    }
}