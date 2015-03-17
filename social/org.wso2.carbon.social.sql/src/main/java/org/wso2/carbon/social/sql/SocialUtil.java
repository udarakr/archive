/*
 * Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.social.sql;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.social.core.SocialActivityException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class SocialUtil {
	private static final Log log = LogFactory.getLog(SocialUtil.class);
	private static JsonParser parser = new JsonParser();

	public static String getTenantDomain() {
		String tenantDomainName = CarbonContext.getThreadLocalCarbonContext()
				.getTenantDomain();
		return tenantDomainName;

	}

	public static int getActivityLimit(int limit) {

		if (limit > Constants.MAXIMUM_ACTIVITY_SELECT_COUNT || limit == 0) {
			if (log.isDebugEnabled()) {
				log.debug("Provided limit: " + limit
						+ " exceeds default max limit: "
						+ Constants.MAXIMUM_ACTIVITY_SELECT_COUNT);
			}
			return Constants.MAXIMUM_ACTIVITY_SELECT_COUNT;
		} else {
			return limit;
		}
	}

	public static String getPreviousActivityID(String PreviousActivityID) {
		if (PreviousActivityID.isEmpty()) {
			return PreviousActivityID;
		} else {
			return null;
		}
	}
	
	public static String getSelectSQL(Connection connection, String key, String queryType)
 throws SocialActivityException {

		try {

			JsonObject jsonObject = readJson(connection);
			JsonObject selectSQLObject = (JsonObject) jsonObject.get(queryType);
			String sql = selectSQLObject.get(key).getAsString();

			if (sql != null) {
				return sql;
			} else {
				throw new SocialActivityException(
						"Unable to locate the query related to, type: "
								+ queryType + " key: " + key);
			}

		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
			throw new SocialActivityException(e.getMessage(), e);
		} catch (JsonIOException e) {
			log.error(e.getMessage());
			throw new SocialActivityException(e.getMessage(), e);
		} catch (JsonSyntaxException e) {
			log.error(e.getMessage());
			throw new SocialActivityException(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new SocialActivityException(e.getMessage(), e);
		}
	}
	
	private static JsonObject readJson(Connection connection)
			throws SocialActivityException, JsonIOException,
			JsonSyntaxException, FileNotFoundException {
		String databaseType;
		try {
			databaseType = SocialDBInitilizer.getDatabaseType(connection);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new SocialActivityException(e.getMessage(), e);
		}
		if (log.isDebugEnabled()) {
			log.debug("Loading select query for " + databaseType);
		}

		String carbonHome = System.getProperty("carbon.home");
		String dbJsonLocation = carbonHome
				+ "/dbscripts/social/sql-scripts.json";
		Object obj = parser.parse(new FileReader(dbJsonLocation));
		JsonObject jsonObject = (JsonObject) obj;
		JsonObject dbTypeObject = (JsonObject) jsonObject.get(databaseType);
		
		return dbTypeObject;

	}

}
