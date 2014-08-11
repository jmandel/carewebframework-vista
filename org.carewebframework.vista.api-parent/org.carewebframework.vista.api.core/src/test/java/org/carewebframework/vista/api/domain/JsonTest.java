/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.api.domain;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;

import org.carewebframework.api.domain.DomainFactoryRegistry;
import org.carewebframework.api.test.CommonTest;
import org.carewebframework.vista.api.property.PropertyDefinition;

import org.junit.Test;

public class JsonTest extends CommonTest {
    
    @Test
    public void test() throws URISyntaxException {
        PropertyDefinition def = DomainFactoryRegistry.fetchObject(PropertyDefinition.class, "1");
        assertEquals("1", def.getLogicalId());
    }
    
}
