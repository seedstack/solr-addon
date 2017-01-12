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

import org.aopalliance.intercept.MethodInvocation;
import org.seedstack.seed.transaction.spi.TransactionMetadata;
import org.seedstack.seed.transaction.spi.TransactionMetadataResolver;
import org.seedstack.solr.Solr;
import org.seedstack.solr.SolrExceptionHandler;

import java.util.Optional;

class SolrTransactionMetadataResolver implements TransactionMetadataResolver {
    static String defaultSolrClient;

    @Override
    public TransactionMetadata resolve(MethodInvocation methodInvocation, TransactionMetadata defaults) {
        Optional<Solr> solrClient = SolrResolver.INSTANCE.apply(methodInvocation.getMethod());

        if (solrClient.isPresent() || SolrTransactionHandler.class.equals(defaults.getHandler())) {
            TransactionMetadata result = new TransactionMetadata();
            result.setHandler(SolrTransactionHandler.class);
            result.setExceptionHandler(SolrExceptionHandler.class);
            result.setResource(solrClient.isPresent() ? solrClient.get().value() : defaultSolrClient);
            return result;
        }

        return null;
    }
}
