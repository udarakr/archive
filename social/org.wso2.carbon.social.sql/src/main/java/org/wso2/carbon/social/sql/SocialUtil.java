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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.social.core.SocialActivityException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
	
	public static String getSelectSQL(Connection connection, String order) throws SocialActivityException {

		//StringBuffer sql = new StringBuffer();
		//BufferedReader reader = null;
		//String delimiter = ";";

		try {
			String databaseType = SocialDBInitilizer
					.getDatabaseType(connection);
			if (log.isDebugEnabled()) {
				log.debug("Loading select script for " + databaseType
						+ "- order " + order);
			}

			/*boolean keepFormat = false;
			if ("oracle".equals(databaseType)) {
				delimiter = "/";
			} else if ("db2".equals(databaseType)) {
				delimiter = "/";
			} else if ("openedge".equals(databaseType)) {
				delimiter = "/";
				keepFormat = true;
			}*/

			String carbonHome = System.getProperty("carbon.home");
			/*String dbScriptLocation = carbonHome + "/dbscripts/social/"
					+ databaseType + "/select-" + order + ".sql";*/
			String dbJsonLocation = carbonHome +"/dbscripts/social/select.json";
			
			Object obj = parser.parse(new FileReader(dbJsonLocation));
			JsonObject jsonObject = (JsonObject) obj;
			JsonObject dbTypeObject = (JsonObject)jsonObject.get(databaseType);
            String sql = dbTypeObject.get(order).getAsString();
            return sql;
 
			/*InputStream is = new FileInputStream(dbJsonLocation);
			reader = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (!keepFormat) {
					if (line.startsWith("//")) {
						continue;
					}
					if (line.startsWith("--")) {
						continue;
					}
					StringTokenizer st = new StringTokenizer(line);
					if (st.hasMoreTokens()) {
						String token = st.nextToken();
						if ("REM".equalsIgnoreCase(token)) {
							continue;
						}
					}
				}
				sql.append(keepFormat ? "\n" : " ").append(line);

				// SQL defines "--" as a comment to EOL
				// and in Oracle it may contain a hint
				// so we cannot just remove it, instead we must end it
				if (!keepFormat && line.contains("--")) {
					sql.append("\n");
				}
				if ((SocialDBInitilizer.checkStringBufferEndsWith(sql, delimiter))) {
					if (log.isDebugEnabled()) {
						log.debug("SELECT SQL: " + sql.toString());
					}
					//TODO remove info log
					log.info("SELECT SQL: " + sql.toString());
					return sql.toString();
				}
			}*/

		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
			throw new SocialActivityException(e.getMessage(), e);
		} catch(JsonIOException e){
			log.error(e.getMessage());
			throw new SocialActivityException(e.getMessage(), e);
		} catch(Exception e){
			log.error(e.getMessage());
			throw new SocialActivityException(e.getMessage(), e);
		}finally{
			/*if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					log.error(e.getMessage());
					throw new SocialActivityException(e.getMessage(), e);
				}
			}*/
		}
	}

}
