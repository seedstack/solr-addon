/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.solr.internal;

import org.seedstack.shed.reflect.StandardAnnotationResolver;
import org.seedstack.solr.Solr;

import java.lang.reflect.Method;

class SolrResolver extends StandardAnnotationResolver<Method, Solr> {
    static SolrResolver INSTANCE = new SolrResolver();

    private SolrResolver() {
        // no external instantiation allowed
    }
}
