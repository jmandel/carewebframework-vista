/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.api.mbroker;

import org.carewebframework.api.alias.AliasType;
import org.carewebframework.api.alias.AliasTypeRegistry;
import org.carewebframework.fhir.client.FhirClient;
import org.carewebframework.vista.mbroker.BrokerSession;
import org.carewebframework.vista.mbroker.RPCParameters;

/**
 * This subclass exists to allow transparent use of RPC aliases in broker calls and registers a
 * broker request factory to allow making RESTful calls via the broker.
 */
public class BrokerSessionEx extends BrokerSession {
    
    static {
        FhirClient.getInstance().registerClientHttpRequestFactory("broker://*", new BrokerRequestFactory());
    }
    
    private final AliasType rpcAliasType = AliasTypeRegistry.getType("RPC");
    
    @Override
    public String callRPC(String name, boolean async, int timeout, RPCParameters params) {
        String alias = rpcAliasType.get(name);
        return super.callRPC(alias == null ? name : alias, async, timeout, params);
    }
    
}
