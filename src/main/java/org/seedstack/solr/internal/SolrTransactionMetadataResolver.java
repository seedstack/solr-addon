/**
 * Copyright (c) 2013-2015, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 *
 */
package org.seedstack.solr.internal;

import org.aopalliance.intercept.MethodInvocation;
import org.seedstack.seed.core.utils.SeedReflectionUtils;
import org.seedstack.solr.Solr;
import org.seedstack.solr.SolrExceptionHandler;
import org.seedstack.seed.transaction.spi.TransactionMetadata;
import org.seedstack.seed.transaction.spi.TransactionMetadataResolver;

class SolrTransactionMetadataResolver implements TransactionMetadataResolver {
    static String defaultSolrClient;

    @Override
    public TransactionMetadata resolve(MethodInvocation methodInvocation, TransactionMetadata defaults) {
        Solr solrClient = SeedReflectionUtils.getMetaAnnotationFromAncestors(methodInvocation.getMethod(), Solr.class);

        if (solrClient != null || SolrTransactionHandler.class.equals(defaults.getHandler())) {
            TransactionMetadata result = new TransactionMetadata();
            result.setHandler(SolrTransactionHandler.class);
            result.setExceptionHandler(SolrExceptionHandler.class);
            result.setResource(solrClient == null ? defaultSolrClient : solrClient.value());
            return result;
        }

        return null;
    }
}
