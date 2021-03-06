/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.carewebframework.vista.mbroker;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import org.carewebframework.common.StrUtil;

/**
 * Class representing all connection parameters that influence a broker connection.
 */
public class ConnectionParams {
    
    private String server; // IP address or resolvable name of the server
    
    private int port = 9200; // Port number of the listener on the server
    
    private String namespace; // Login namespace (defaults to listener's namespace)
    
    private String username; // Username of authenticating user (optional)
    
    private String password; // Password of authenticating user (optional)
    
    private String appid; // Identifier of application requesting connection
    
    private int timeout; // Default connection timeout
    
    /**
     * Create with all default values
     */
    public ConnectionParams() {
        this("");
    }
    
    /**
     * Create from a connection parameter string.
     *
     * @param value Connection parameter string.
     */
    public ConnectionParams(String value) {
        this(value, null, 10000);
    }
    
    /**
     * Create specifying all connection parameters.
     *
     * @param value The connection parameter string.
     * @param appid The id of the application requesting the connection.
     * @param timeout Default timeout value, in milliseconds.
     */
    public ConnectionParams(String value, String appid, int timeout) {
        String[] pcs = value.split("\\@", 2);
        
        if (pcs.length == 1) {
            server = pcs[0];
            username = "";
            password = "";
        } else {
            server = pcs[1];
            pcs = StrUtil.split(pcs[0], ":", 2, true);
            username = pcs[0];
            password = pcs[1];
        }
        
        pcs = StrUtil.split(server, ":", 3, true);
        server = pcs[0];
        port = NumberUtils.toInt(pcs[1], port);
        namespace = pcs[2];
        this.timeout = timeout;
        this.appid = StringUtils.isEmpty(appid) ? Constants.DEFAULT_APP_ID : appid;
    }
    
    /**
     * Copy constructor.
     *
     * @param source Source from which to copy.
     */
    public ConnectionParams(ConnectionParams source) {
        appid = source.appid;
        namespace = source.namespace;
        password = source.password;
        port = source.port;
        server = source.server;
        timeout = source.timeout;
        username = source.username;
    }
    
    /**
     * Returns the default timeout, in milliseconds.
     *
     * @return Default timeout.
     */
    public int getTimeout() {
        return timeout;
    }
    
    /**
     * Sets the default timeout, in milliseconds.
     *
     * @param timeout Timeout in milliseconds.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    /**
     * Displays the connection string reflecting the current connection parameters.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        addPiece(username, "", sb);
        addPiece(password, ":", sb);
        addPiece(server, "@", sb);
        addPiece(Integer.toString(port), ":", sb);
        addPiece(namespace, ":", sb);
        return sb.toString();
    }
    
    /**
     * Used to build a connection string for display.
     *
     * @param pc A connection string field.
     * @param prefix The prefix to include if the field is not empty.
     * @param sb String builder instance.
     */
    private void addPiece(String pc, String prefix, StringBuilder sb) {
        if (!pc.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(prefix);
            }
            
            sb.append(pc);
        }
    }
    
    /**
     * Returns the server name.
     *
     * @return Server name.
     */
    public String getServer() {
        return server;
    }
    
    /**
     * Sets the server name.
     *
     * @param server Server name.
     */
    public void setServer(String server) {
        this.server = server;
    }
    
    /**
     * Returns the broker port #.
     *
     * @return Broker port #.
     */
    public int getPort() {
        return port;
    }
    
    /**
     * Sets the broker port #.
     *
     * @param port Broker port #.
     */
    public void setPort(int port) {
        this.port = port;
    }
    
    /**
     * Returns the target M namespace.
     *
     * @return M namespace.
     */
    public String getNamespace() {
        return namespace;
    }
    
    /**
     * Sets the target M namespace.
     *
     * @param namespace M namespace.
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
    
    /**
     * Returns the user name for authentication.
     *
     * @return User name.
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the user name for authentication.
     *
     * @param username User name.
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Returns the password for authentication.
     *
     * @return Password.
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Sets the password for authentication.
     *
     * @param password Password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Returns the application id.
     *
     * @return Application id.
     */
    public String getAppid() {
        return appid;
    }
    
    /**
     * Sets the application id.
     *
     * @param appid Application id.
     */
    public void setAppid(String appid) {
        this.appid = appid;
    }
}
