/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */
package org.seedstack.solr.internal;

import com.google.common.base.Strings;
import io.nuun.kernel.api.plugin.InitState;
import io.nuun.kernel.api.plugin.context.InitContext;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.LBHttpSolrClient;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.core.internal.AbstractSeedPlugin;
import org.seedstack.solr.SolrConfig;
import org.seedstack.solr.SolrExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This plugin manages configured Solr clients.
 */
public class SolrPlugin extends AbstractSeedPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(SolrPlugin.class);
    private final Map<String, SolrClient> solrClients = new HashMap<String, SolrClient>();
    private final Map<String, Class<? extends SolrExceptionHandler>> solrExceptionHandlers = new HashMap<String, Class<? extends SolrExceptionHandler>>();

    @Override
    public String name() {
        return "solr";
    }

    @Override
    @SuppressWarnings("unchecked")
    public InitState initialize(InitContext initContext) {
        SolrConfig solrConfig = getConfiguration(SolrConfig.class);

        if (solrConfig.getClients().isEmpty()) {
            LOGGER.info("No Solr client configured, Solr support disabled");
            return InitState.INITIALIZED;
        }

        for (Map.Entry<String, SolrConfig.ClientConfig> solrClientEntry : solrConfig.getClients().entrySet()) {
            String clientName = solrClientEntry.getKey();
            SolrConfig.ClientConfig clientConfig = solrClientEntry.getValue();

            try {
                this.solrClients.put(clientName, buildSolrClient(clientConfig));
            } catch (Exception e) {
                throw SeedException.wrap(e, SolrErrorCode.UNABLE_TO_CREATE_CLIENT).put("clientName", clientName);
            }

            solrExceptionHandlers.put(clientName, clientConfig.getExceptionHandler());
        }

        if (!Strings.isNullOrEmpty(solrConfig.getDefaultClient())) {
            SolrTransactionMetadataResolver.defaultSolrClient = solrConfig.getDefaultClient();
        }

        return InitState.INITIALIZED;
    }

    @Override
    public Object nativeUnitModule() {
        return new SolrModule(solrClients, solrExceptionHandlers);
    }

    @Override
    public void stop() {
        for (Map.Entry<String, SolrClient> solrClientEntry : solrClients.entrySet()) {
            LOGGER.info("Closing Solr client {}", solrClientEntry.getKey());
            try {
                solrClientEntry.getValue().close();
            } catch (Exception e) {
                LOGGER.error(String.format("Unable to properly close Solr client %s", solrClientEntry.getKey()), e);
            }
        }
    }

    private SolrClient buildSolrClient(SolrConfig.ClientConfig clientConfig) throws MalformedURLException {
        switch (clientConfig.getType()) {
            case LOAD_BALANCED_HTTP:
                return buildLBSolrClient(clientConfig);
            case HTTP:
                return buildHttpSolrClient(clientConfig);
            case CLOUD:
                return buildCloudSolrClient(clientConfig);
            default:
                throw SeedException.createNew(SolrErrorCode.UNSUPPORTED_CLIENT_TYPE)
                        .put("clientType", clientConfig.getType().name());
        }
    }

    private SolrClient buildLBSolrClient(SolrConfig.ClientConfig clientConfig) throws MalformedURLException {
        LBHttpSolrClient lbHttpSolrClient = new LBHttpSolrClient(clientConfig.getUrls().toArray(new String[clientConfig.getUrls().size()]));
        SolrConfig.ClientConfig.LBHttpClientConfig lbHttpClientConfig = clientConfig.lbHttp();

        Optional.ofNullable(lbHttpClientConfig.getConnectionTimeout()).ifPresent(lbHttpSolrClient::setConnectionTimeout);
        Optional.ofNullable(lbHttpClientConfig.getSocketTimeout()).ifPresent(lbHttpSolrClient::setSoTimeout);
        Optional.ofNullable(lbHttpClientConfig.getQueryParams()).ifPresent(lbHttpSolrClient::setQueryParams);
        Optional.ofNullable(lbHttpClientConfig.getAliveCheckInterval()).ifPresent(lbHttpSolrClient::setAliveCheckInterval);

        return lbHttpSolrClient;
    }

    private SolrClient buildHttpSolrClient(SolrConfig.ClientConfig clientConfig) {
        HttpSolrClient httpSolrClient = new HttpSolrClient(clientConfig.getUrls().iterator().next());
        SolrConfig.ClientConfig.HttpClientConfig httpClientConfig = clientConfig.http();

        Optional.ofNullable(httpClientConfig.getConnectionTimeout()).ifPresent(httpSolrClient::setConnectionTimeout);
        Optional.ofNullable(httpClientConfig.getSocketTimeout()).ifPresent(httpSolrClient::setSoTimeout);
        Optional.ofNullable(httpClientConfig.getQueryParams()).ifPresent(httpSolrClient::setQueryParams);
        Optional.ofNullable(httpClientConfig.getAllowCompression()).ifPresent(httpSolrClient::setAllowCompression);
        Optional.ofNullable(httpClientConfig.getMaxConnectionsPerHost()).ifPresent(httpSolrClient::setDefaultMaxConnectionsPerHost);
        Optional.ofNullable(httpClientConfig.getFollowRedirects()).ifPresent(httpSolrClient::setFollowRedirects);
        Optional.ofNullable(httpClientConfig.getMaxTotalConnections()).ifPresent(httpSolrClient::setMaxTotalConnections);
        Optional.ofNullable(httpClientConfig.getUseMultiPartHost()).ifPresent(httpSolrClient::setUseMultiPartPost);

        return httpSolrClient;
    }

    private CloudSolrClient buildCloudSolrClient(SolrConfig.ClientConfig clientConfig) throws MalformedURLException {
        CloudSolrClient cloudSolrClient;
        SolrConfig.ClientConfig.CloudClientConfig cloudClientConfig = clientConfig.cloud();

        if (cloudClientConfig.getLoadBalancedUrls().isEmpty()) {
            cloudSolrClient = new CloudSolrClient(clientConfig.getUrls(), cloudClientConfig.getChroot());
        } else {
            cloudSolrClient = new CloudSolrClient(
                    clientConfig.getUrls().iterator().next(),
                    new LBHttpSolrClient(cloudClientConfig.getLoadBalancedUrls().toArray(new String[clientConfig.getUrls().size()])),
                    cloudClientConfig.isUpdateToLeaders()
            );
        }

        Optional.ofNullable(cloudClientConfig.getDefaultCollection()).ifPresent(cloudSolrClient::setDefaultCollection);
        Optional.ofNullable(cloudClientConfig.getIdField()).ifPresent(cloudSolrClient::setIdField);
        Optional.ofNullable(cloudClientConfig.getCollectionCacheTTL()).ifPresent(cloudSolrClient::setCollectionCacheTTl);
        Optional.ofNullable(cloudClientConfig.getParallelCacheRefreshes()).ifPresent(cloudSolrClient::setParallelCacheRefreshes);
        Optional.ofNullable(cloudClientConfig.getParallelUpdates()).ifPresent(cloudSolrClient::setParallelUpdates);
        Optional.ofNullable(cloudClientConfig.getZookeeperClientTimeout()).ifPresent(cloudSolrClient::setZkClientTimeout);
        Optional.ofNullable(cloudClientConfig.getZookeeperConnectTimeout()).ifPresent(cloudSolrClient::setZkConnectTimeout);

        return cloudSolrClient;
    }
}
