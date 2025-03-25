package org.financer.transactionservice.config

import org.slf4j.LoggerFactory
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CachingConfigurerSupport
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.cache.interceptor.CacheInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
class CacheConfig : CachingConfigurerSupport() {
    companion object {
        private val logger = LoggerFactory.getLogger(CacheConfig::class.java)
    }

    @Bean
    override fun cacheManager(): CacheManager {
        return LoggingCacheManager()
    }

    private inner class LoggingCacheManager : ConcurrentMapCacheManager() {
        override fun getCache(name: String) = super.getCache(name)?.let { cache ->
            LoggingCache(cache, name)
        }
    }

    private inner class LoggingCache(private val cache: org.springframework.cache.Cache, private val name: String) : 
            org.springframework.cache.Cache by cache {
        
        override fun get(key: Any): org.springframework.cache.Cache.ValueWrapper? {
            val value = cache.get(key)
            if (value != null) {
                logger.info("Cache HIT for cache: {}, key: {}", name, key)
            } else {
                logger.info("Cache MISS for cache: {}, key: {}", name, key)
            }
            return value
        }

        override fun put(key: Any, value: Any?) {
            logger.debug("Caching value for cache: {}, key: {}", name, key)
            cache.put(key, value)
        }

        override fun evict(key: Any) {
            logger.debug("Evicting cache entry for cache: {}, key: {}", name, key)
            cache.evict(key)
        }

        override fun clear() {
            logger.debug("Clearing all entries for cache: {}", name)
            cache.clear()
        }
    }
} 