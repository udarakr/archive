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

package org.wso2.carbon.social.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.social.sql.SQLActivityException;
import org.wso2.carbon.social.sql.SQLActivityPublisher;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

//import java.util.UUID;

public abstract class ActivityPublisher {
	
	private static final Log log = LogFactory
			.getLog(SQLActivityPublisher.class);
	
	private JsonParser parser = new JsonParser();
	
	public long publish(String activity) throws JsonSyntaxException {
		JsonObject jsonObject;
		String unixTimestamp = Long
				.toString(System.currentTimeMillis() / 1000L);
		try {
			jsonObject = (JsonObject) parser.parse(activity);
			jsonObject.add(Constants.PUBLISHED,
					(JsonElement) parser.parse(unixTimestamp));
		} catch (JsonSyntaxException e) {
			log.error("Malformed JSON element found: " + e.getMessage(), e);
			throw e;
		}
		// TODO keep UUID and expose UUID to outside
		// String id = UUID.randomUUID().toString();
		// JsonObject object = (JsonObject) jsonObject.get("object");
		// object.add(Constants.ID, (JsonElement)parser.parse(id));


		return publishActivity(jsonObject);
	}

	protected abstract long publishActivity(JsonObject activity);

	public abstract boolean remove(String activityId, String userId) throws SQLActivityException;
}