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

package org.wso2.carbon.social.sql.service;

import org.mozilla.javascript.NativeObject;
import org.wso2.carbon.social.sql.SQLActivityBrowser;
import org.wso2.carbon.social.sql.SQLActivityPublisher;
import org.wso2.carbon.social.core.ActivityBrowser;
import org.wso2.carbon.social.core.ActivityPublisher;
import org.wso2.carbon.social.core.service.SocialActivityService;

public class SQLActivityService extends SocialActivityService {
	
	private ActivityPublisher activityPublisher = new SQLActivityPublisher();
	private ActivityBrowser activityBrowser = new SQLActivityBrowser();

    @Override
    public ActivityBrowser getActivityBrowser() {
        return activityBrowser;
    }

    @Override
    public ActivityPublisher getActivityPublisher() {
        return activityPublisher; 
    }

    @Override
    public void configPublisher(NativeObject configObject) {
    }
}
