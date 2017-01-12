/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.solr;

import org.hibernate.validator.constraints.NotEmpty;
import org.seedstack.coffig.Config;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Config("solr")
public class SolrConfig {
    private Map<String, ClientConfig> clients = new HashMap<>();
    private String defaultClient;

    public Map<String, ClientConfig> getClients() {
        return Collections.unmodifiableMap(clients);
    }

    public SolrConfig addClient(String name, ClientConfig config) {
        this.clients.put(name, config);
        return this;
    }

    public String getDefaultClient() {
        return defaultClient;
    }

    public SolrConfig setDefaultClient(String defaultClient) {
        this.defaultClient = defaultClient;
        return this;
    }

    public static class ClientConfig {
        // all
        @NotNull
        private SolrClientType type = SolrClientType.HTTP;
        @NotEmpty
        private Set<String> urls = new HashSet<>();
        private HttpClientConfig http = new HttpClientConfig();
        private LBHttpClientConfig lbHttp = new LBHttpClientConfig();
        private CloudClientConfig cloud = new CloudClientConfig();
        private Class<? extends SolrExceptionHandler> exceptionHandler;

        public SolrClientType getType() {
            return type;
        }

        public ClientConfig setType(SolrClientType type) {
            this.type = type;
            return this;
        }

        public Set<String> getUrls() {
            return urls;
        }

        public ClientConfig addUrl(String url) {
            this.urls.add(url);
            return this;
        }

        public Class<? extends SolrExceptionHandler> getExceptionHandler() {
            return exceptionHandler;
        }

        public ClientConfig setExceptionHandler(Class<? extends SolrExceptionHandler> exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return this;
        }

        public HttpClientConfig http() {
            return http;
        }

        public LBHttpClientConfig lbHttp() {
            return lbHttp;
        }

        public CloudClientConfig cloud() {
            return cloud;
        }

        public static class LBHttpClientConfig {
            private Integer connectionTimeout;
            private Integer socketTimeout;
            private Set<String> queryParams;
            private Integer aliveCheckInterval;

            public Integer getConnectionTimeout() {
                return connectionTimeout;
            }

            public LBHttpClientConfig setConnectionTimeout(Integer connectionTimeout) {
                this.connectionTimeout = connectionTimeout;
                return this;
            }

            public Integer getSocketTimeout() {
                return socketTimeout;
            }

            public LBHttpClientConfig setSocketTimeout(Integer socketTimeout) {
                this.socketTimeout = socketTimeout;
                return this;
            }

            public Set<String> getQueryParams() {
                return queryParams;
            }

            public LBHttpClientConfig setQueryParams(Set<String> queryParams) {
                this.queryParams = queryParams;
                return this;
            }

            public Integer getAliveCheckInterval() {
                return aliveCheckInterval;
            }

            public LBHttpClientConfig setAliveCheckInterval(Integer aliveCheckInterval) {
                this.aliveCheckInterval = aliveCheckInterval;
                return this;
            }
        }

        public static class HttpClientConfig {
            private Integer connectionTimeout;
            private Integer socketTimeout;
            private Set<String> queryParams;
            private Boolean allowCompression;
            private Integer maxConnectionsPerHost;
            private Boolean followRedirects;
            private Integer maxTotalConnections;
            private Boolean useMultiPartHost;
            private Integer aliveCheckInterval;

            public Integer getConnectionTimeout() {
                return connectionTimeout;
            }

            public HttpClientConfig setConnectionTimeout(Integer connectionTimeout) {
                this.connectionTimeout = connectionTimeout;
                return this;
            }

            public Integer getSocketTimeout() {
                return socketTimeout;
            }

            public HttpClientConfig setSocketTimeout(Integer socketTimeout) {
                this.socketTimeout = socketTimeout;
                return this;
            }

            public Set<String> getQueryParams() {
                return queryParams;
            }

            public HttpClientConfig setQueryParams(Set<String> queryParams) {
                this.queryParams = queryParams;
                return this;
            }

            public Boolean getAllowCompression() {
                return allowCompression;
            }

            public HttpClientConfig setAllowCompression(Boolean allowCompression) {
                this.allowCompression = allowCompression;
                return this;
            }

            public Integer getMaxConnectionsPerHost() {
                return maxConnectionsPerHost;
            }

            public HttpClientConfig setMaxConnectionsPerHost(Integer maxConnectionsPerHost) {
                this.maxConnectionsPerHost = maxConnectionsPerHost;
                return this;
            }

            public Boolean getFollowRedirects() {
                return followRedirects;
            }

            public HttpClientConfig setFollowRedirects(Boolean followRedirects) {
                this.followRedirects = followRedirects;
                return this;
            }

            public Integer getMaxTotalConnections() {
                return maxTotalConnections;
            }

            public HttpClientConfig setMaxTotalConnections(Integer maxTotalConnections) {
                this.maxTotalConnections = maxTotalConnections;
                return this;
            }

            public Boolean getUseMultiPartHost() {
                return useMultiPartHost;
            }

            public HttpClientConfig setUseMultiPartHost(Boolean useMultiPartHost) {
                this.useMultiPartHost = useMultiPartHost;
                return this;
            }

            public Integer getAliveCheckInterval() {
                return aliveCheckInterval;
            }

            public HttpClientConfig setAliveCheckInterval(Integer aliveCheckInterval) {
                this.aliveCheckInterval = aliveCheckInterval;
                return this;
            }
        }

        public static class CloudClientConfig {
            private Set<String> loadBalancedUrls = new HashSet<>();
            private boolean updateToLeaders = true;
            private String chroot;
            private String defaultCollection;
            private String idField;
            private Integer collectionCacheTTL;
            private Integer parallelCacheRefreshes;
            private Boolean parallelUpdates;
            private Integer zookeeperClientTimeout;
            private Integer zookeeperConnectTimeout;

            public Set<String> getLoadBalancedUrls() {
                return loadBalancedUrls;
            }

            public CloudClientConfig addLoadBalancedUrl(String loadBalancedUrl) {
                this.loadBalancedUrls.add(loadBalancedUrl);
                return this;
            }

            public boolean isUpdateToLeaders() {
                return updateToLeaders;
            }

            public CloudClientConfig setUpdateToLeaders(boolean updateToLeaders) {
                this.updateToLeaders = updateToLeaders;
                return this;
            }

            public String getChroot() {
                return chroot;
            }

            public CloudClientConfig setChroot(String chroot) {
                this.chroot = chroot;
                return this;
            }

            public String getDefaultCollection() {
                return defaultCollection;
            }

            public CloudClientConfig setDefaultCollection(String defaultCollection) {
                this.defaultCollection = defaultCollection;
                return this;
            }

            public String getIdField() {
                return idField;
            }

            public CloudClientConfig setIdField(String idField) {
                this.idField = idField;
                return this;
            }

            public Integer getCollectionCacheTTL() {
                return collectionCacheTTL;
            }

            public CloudClientConfig setCollectionCacheTTL(Integer collectionCacheTTL) {
                this.collectionCacheTTL = collectionCacheTTL;
                return this;
            }

            public Integer getParallelCacheRefreshes() {
                return parallelCacheRefreshes;
            }

            public CloudClientConfig setParallelCacheRefreshes(Integer parallelCacheRefreshes) {
                this.parallelCacheRefreshes = parallelCacheRefreshes;
                return this;
            }

            public Boolean getParallelUpdates() {
                return parallelUpdates;
            }

            public CloudClientConfig setParallelUpdates(Boolean parallelUpdates) {
                this.parallelUpdates = parallelUpdates;
                return this;
            }

            public Integer getZookeeperClientTimeout() {
                return zookeeperClientTimeout;
            }

            public CloudClientConfig setZookeeperClientTimeout(Integer zookeeperClientTimeout) {
                this.zookeeperClientTimeout = zookeeperClientTimeout;
                return this;
            }

            public Integer getZookeeperConnectTimeout() {
                return zookeeperConnectTimeout;
            }

            public CloudClientConfig setZookeeperConnectTimeout(Integer zookeeperConnectTimeout) {
                this.zookeeperConnectTimeout = zookeeperConnectTimeout;
                return this;
            }
        }

        public enum SolrClientType {
            HTTP,
            LOAD_BALANCED_HTTP,
            CLOUD
        }
    }
}
