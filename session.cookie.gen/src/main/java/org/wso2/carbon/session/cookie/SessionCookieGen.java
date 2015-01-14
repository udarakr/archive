/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.session.cookie;

import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;

import java.rmi.RemoteException;

public class SessionCookieGen {

    private AuthenticationAdminStub authenticationAdminStub = null;

    /**
     * Initialize
     */
    public void init () {
        System.setProperty("javax.net.ssl.trustStore", "/home/wso2is-5.0.0/repository/resources/security/wso2carbon.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
    }

    /**
     * Login to the system
     *
     * @param username valid username
     * @param password valid password
     * @return
     * @throws RemoteException
     * @throws LoginAuthenticationExceptionException
     */
    public String login (String username, String password) throws RemoteException, LoginAuthenticationExceptionException {

        authenticationAdminStub = new AuthenticationAdminStub("https://localhost:9443/services/AuthenticationAdmin");

        String sessionCookie = null;

        if (authenticationAdminStub.login(username, password, "localhost")) {
            System.out.println("Login Successful");

            ServiceContext serviceContext = authenticationAdminStub.
                    _getServiceClient().getLastOperationContext().getServiceContext();
            sessionCookie = (String) serviceContext.getProperty(HTTPConstants.COOKIE_STRING);
        }

        return sessionCookie;
    }

    
    public void logout () throws RemoteException, LogoutAuthenticationExceptionException {
        authenticationAdminStub.logout();
        System.out.println("Logout successful");
    }

    public static void main (String [] args) {
        SessionCookieGen sessionCookieGen = new SessionCookieGen();

        try {
            sessionCookieGen.init();
            String sessionid = sessionCookieGen.login("admin", "admin");
            System.out.println("Session Cookie: " + sessionid);
            sessionCookieGen.logout();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
