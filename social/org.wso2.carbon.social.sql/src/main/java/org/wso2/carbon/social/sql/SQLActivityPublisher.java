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

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.mozilla.javascript.NativeObject;
import org.wso2.carbon.social.sql.Constants;
import org.wso2.carbon.social.sql.SocialUtil;
import org.wso2.carbon.social.core.Activity;
import org.wso2.carbon.social.core.ActivityPublisher;
import org.wso2.carbon.social.core.JSONUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SQLActivityPublisher extends ActivityPublisher {

	private static Log log = LogFactory.getLog(SQLActivityPublisher.class);

	public static final String INSERT_SQL = "INSERT INTO "
			+ Constants.SOCIAL_TABLE_NAME + "(" + Constants.ID_COLUMN + ","
			+ Constants.CONTEXT_ID_COLUMN + "," + Constants.BODY_COLUMN + ", "
			+ Constants.TENANT_DOMAIN_COLUMN + ", " + Constants.TIMESTAMP
			+ ") VALUES(?, ?, ?, ?, ?)";

	public static final String INSERT_RATING_SQL = "INSERT INTO "
			+ Constants.SOCIAL_RATING_TABLE_NAME + "(" + Constants.ID_COLUMN
			+ "," + Constants.CONTEXT_ID_COLUMN + "," + Constants.BODY_COLUMN
			+ ", " + Constants.TENANT_DOMAIN_COLUMN + ", "
			+ Constants.RATING_COLUMN + ", " + Constants.TIMESTAMP
			+ ") VALUES(?, ?, ?, ?, ?, ?)";

	public static final String SELECT_CACHE_SQL = "SELECT * FROM "
			+ Constants.SOCIAL_RATING_CACHE_TABLE_NAME + " WHERE "
			+ Constants.CONTEXT_ID_COLUMN + "=?";
	public static final String UPDATE_CACHE_SQL = "UPDATE "
			+ Constants.SOCIAL_RATING_CACHE_TABLE_NAME + " SET "
			+ Constants.RATING_TOTAL + "=?, " + Constants.RATING_COUNT
			+ "=? WHERE " + Constants.CONTEXT_ID_COLUMN + "=?";
	public static final String INSERT_CACHE_SQL = "INSERT INTO "
			+ Constants.SOCIAL_RATING_CACHE_TABLE_NAME + " ("
			+ Constants.CONTEXT_ID_COLUMN + ", " + Constants.RATING_TOTAL
			+ ", " + Constants.RATING_COUNT + ") VALUES(?, ?, ?) ";

	public static final String ACTIVITY_SELECT_SQL = "SELECT * FROM "
			+ Constants.SOCIAL_TABLE_NAME + " WHERE " + Constants.ID_COLUMN
			+ " =?";
	public static final String ACTIVITY_UPDATE_SQL = "UPDATE "
			+ Constants.SOCIAL_TABLE_NAME + " SET " + Constants.BODY_COLUMN
			+ "=? WHERE " + Constants.ID_COLUMN + "=?";

	public static final String INSERT_LIKE_SQL = "INSERT INTO "
			+ Constants.SOCIAL_LIKES_TABLE_NAME + "(" + Constants.ID_COLUMN
			+ "," + Constants.CONTEXT_ID_COLUMN + "," + Constants.BODY_COLUMN
			+ ", " + Constants.TENANT_DOMAIN_COLUMN + ", "
			+ Constants.LIKES_COLUMN + "," + Constants.UNLIKES_COLUMN + ","
			+ Constants.TIMESTAMP + ") VALUES(?, ?, ?, ?, ?, ?, ?)";

	public static final String LIKES_UPDATE_SQL = "UPDATE "
			+ Constants.SOCIAL_LIKES_TABLE_NAME + " SET "
			+ Constants.BODY_COLUMN + " =?, " + Constants.LIKES_COLUMN
			+ " =?, " + Constants.UNLIKES_COLUMN + " =? WHERE "
			+ Constants.ID_COLUMN + " =?";
	// This will be used to update body content when there are like/unlike
	// activities on the comment target
	public static final String UPDATE_RATING_SQL = "UPDATE "
			+ Constants.SOCIAL_RATING_TABLE_NAME + " SET "
			+ Constants.BODY_COLUMN + " =? where " + Constants.ID_COLUMN
			+ " =?";

	public static final String DELETE_COMMENT_SQL = "DELETE FROM "
			+ Constants.SOCIAL_TABLE_NAME + " WHERE " + Constants.ID_COLUMN
			+ " =?";
	public static final String DELETE_RATING_SQL = "DELETE FROM "
			+ Constants.SOCIAL_RATING_TABLE_NAME + " WHERE "
			+ Constants.ID_COLUMN + " =?";
	public static final String DELETE_LIKES_SQL = "DELETE FROM "
			+ Constants.SOCIAL_LIKES_TABLE_NAME + " WHERE "
			+ Constants.ID_COLUMN + " =?";

	public static final String ErrorStr = "Failed to publish the social event.";

	private JsonParser parser = new JsonParser();

	@Override
	protected String publish(String id, NativeObject activity) {
		DSConnection con = new DSConnection();
		Connection connection = con.getConnection();
		int commentRet = 0;

		if (connection == null) {
			return null;
		}

		PreparedStatement commentStatement;
		PreparedStatement ratingStatement;

		String json = null;
		try {
			json = JSONUtil.SimpleNativeObjectToJson(activity);

			String targetId = JSONUtil.getNullableProperty(activity,
					Constants.CONTEXT_JSON_PROP, Constants.ID_JSON_PROP);
			if (targetId == null) {
				targetId = JSONUtil.getProperty(activity,
						Constants.TARGET_JSON_PROP, Constants.ID_JSON_PROP);
			}

			String timeStamp = JSONUtil.getProperty(activity,
					Constants.TIMESTAMP);
			String tenantDomain = SocialUtil.getTenantDomain();

			if ("post".equals(SocialUtil.getVerb(activity))) {
				connection.setAutoCommit(false);
				commentStatement = connection.prepareStatement(INSERT_SQL);
				commentStatement.setString(1, id);
				commentStatement.setString(2, targetId);
				commentStatement.setString(3, json);
				commentStatement.setString(4, tenantDomain);
				commentStatement.setString(5, timeStamp);
				commentRet = commentStatement.executeUpdate();

				insertToLikes(id, targetId, json, tenantDomain, timeStamp,
						connection);

				if (SocialUtil.isValidRating(activity)) {
					int rating = Integer.parseInt(JSONUtil.getProperty(
							activity, Constants.OBJECT_JSON_PROP,
							Constants.RATING));

					ratingStatement = connection
							.prepareStatement(INSERT_RATING_SQL);
					ratingStatement.setString(1, id);
					ratingStatement.setString(2, targetId);
					ratingStatement.setString(3, json);
					ratingStatement.setString(4, tenantDomain);
					ratingStatement.setInt(5, rating);
					ratingStatement.setString(6, timeStamp);
					ratingStatement.executeUpdate();

					updateRatingCache(connection, targetId, rating);
				}

				connection.commit();

				if (commentRet > 0) {
					return id;
				}

				if (log.isDebugEnabled()) {
					if (commentRet > 0) {
						log.debug("Activity published successfully. "
								+ " Activity ID: " + id + " TargetID: "
								+ targetId + " JSON: " + json);
					} else {
						log.debug(ErrorStr + " Activity ID: " + id
								+ " TargetID: " + targetId + " JSON: " + json);
					}
				}
			} else {
				// Handle like,dislike,unlike,undislike verbs
				publishLikes(connection, activity);
			}

		} catch (SQLException e) {
			log.error(ErrorStr + e);
		} catch (JSONException e) {
			log.error("Unable to retrieve the JSON activity. " + e);
		} finally {
			if (con != null) {
				con.closeConnection(connection);
			}
		}

		return null;
	}

	/**
	 * Add init like activity when there is a comment activity occurred.
	 * 
	 * @param id
	 * @param targetId
	 * @param json
	 * @param tenantDomain
	 * @param timeStamp
	 * @param connection
	 */
	private void insertToLikes(String id, String targetId, String json,
			String tenantDomain, String timeStamp, Connection connection) {
		PreparedStatement insertStatement;

		try {
			insertStatement = connection.prepareStatement(INSERT_LIKE_SQL);
			insertStatement.setString(1, id);
			insertStatement.setString(2, targetId);
			insertStatement.setString(3, json);
			insertStatement.setString(4, tenantDomain);
			insertStatement.setInt(5, 0);
			insertStatement.setInt(6, 0);
			insertStatement.setString(7, timeStamp);
			insertStatement.executeUpdate();
		} catch (SQLException e) {
			log.error("Failed to add activity in to the table: "
					+ Constants.SOCIAL_LIKES_TABLE_NAME + ". " + e);
		}

	}

	/**
	 * publish like/dislike activity and update all tables accordingly. We need
	 * to update like/unlike count within body object resides in all tables.
	 * 
	 * @param connection
	 * @param activity
	 */
	private void publishLikes(Connection connection, NativeObject activity) {
		DSConnection con = new DSConnection();
		Connection selectConnection = con.getConnection();

		ResultSet resultSet;

		String targetId = JSONUtil.getProperty(activity,
				Constants.TARGET_JSON_PROP, Constants.ID_JSON_PROP);
		String actor = JSONUtil.getProperty(activity,
				Constants.ACTOR_JSON_PROP, Constants.ID_JSON_PROP);

		PreparedStatement selectActivityStatement;
		PreparedStatement updateActivityStatement;

		try {
			selectActivityStatement = selectConnection
					.prepareStatement(ACTIVITY_SELECT_SQL);
			selectActivityStatement.setString(1, targetId);
			resultSet = selectActivityStatement.executeQuery();

			if (resultSet.next()) {

				JsonObject currentBody = (JsonObject) parser.parse(resultSet
						.getString(Constants.BODY_COLUMN));

				JsonElement actorElement = (JsonElement) parser.parse(actor);

				Activity currentActivity = new SQLActivity(currentBody);
				int likeCount = currentActivity.getLikeCount();
				int dislikeCount = currentActivity.getDislikeCount();
				String id = currentActivity.getId();

				String verb = SocialUtil.getVerb(activity);

				switch (Constants.VERB.valueOf(verb)) {
				case like:
					likeCount += 1;
					JsonObject likesObj = (JsonObject) currentBody.get("likes");
					JsonArray likes = (JsonArray) likesObj.get("items")
							.getAsJsonArray();
					likes.add(actorElement);
					break;
				case unlike:
					likeCount -= 1;
					JsonObject uLikesObj = (JsonObject) currentBody
							.get("likes");
					JsonArray uLikes = (JsonArray) uLikesObj.get("items")
							.getAsJsonArray();
					// uLikes.remove(actorElement);
					break;
				case dislike:
					dislikeCount += 1;
					JsonObject disLikesObj = (JsonObject) currentBody
							.get("dislikes");
					JsonArray disLikes = (JsonArray) disLikesObj.get("items")
							.getAsJsonArray();
					disLikes.add(actorElement);
					break;
				case undislike:
					dislikeCount -= 1;
					JsonObject unDisLikesObj = (JsonObject) currentBody
							.get("dislikes");
					JsonArray unDisLikes = (JsonArray) unDisLikesObj.get(
							"items").getAsJsonArray();
					// unDisLikes.remove(actorElement);
					break;
				default:
					// TODO Add a proper debug log
					break;
				}
				currentActivity.setLikeCount(likeCount);
				currentActivity.setDislikeCount(dislikeCount);

				JsonObject json = currentActivity.getBody();

				connection.setAutoCommit(false);
				updateActivityStatement = connection
						.prepareStatement(ACTIVITY_UPDATE_SQL);

				updateActivityStatement.setString(1, json.toString());
				updateActivityStatement.setString(2, targetId);
				updateActivityStatement.executeUpdate();

				updateLikes(targetId, likeCount, dislikeCount, json, connection);
				updateRating(id, json, connection);

				connection.commit();
			}
		} catch (SQLException e) {
			log.error("Error while publishing like/unlike activity. " + e);
		}
	}

	/**
	 * Update like/unlike count within the body object of the ratings table.
	 * 
	 * @param parentId
	 * @param json
	 * @param connection
	 */
	private void updateRating(String id, JsonObject json, Connection connection) {
		PreparedStatement updateRatingStatement;

		try {
			updateRatingStatement = connection
					.prepareStatement(UPDATE_RATING_SQL);
			updateRatingStatement.setString(1, json.toString());
			updateRatingStatement.setString(2, id);
			updateRatingStatement.executeUpdate();
		} catch (SQLException e) {
			log.error("Error while updating like/unlike activity in table: "
					+ Constants.SOCIAL_RATING_TABLE_NAME + ". " + e);
		}

	}

	/**
	 * Update like/unlike count within body object and update likes/dislikes
	 * columns accordingly.
	 * 
	 * @param targetId
	 * @param likeCount
	 * @param dislikeCount
	 * @param json
	 * @param connection
	 */
	private void updateLikes(String targetId, int likeCount, int dislikeCount,
			JsonObject json, Connection connection) {
		PreparedStatement updateActivityStatement;
		try {
			updateActivityStatement = connection
					.prepareStatement(LIKES_UPDATE_SQL);
			updateActivityStatement.setString(1, json.toString());
			updateActivityStatement.setInt(2, likeCount);
			updateActivityStatement.setInt(3, dislikeCount);
			updateActivityStatement.setString(4, targetId);
			updateActivityStatement.executeUpdate();
		} catch (SQLException e) {
			log.error("Unable to update the table: "
					+ Constants.SOCIAL_LIKES_TABLE_NAME + ". " + e);
		}

	}

	/**
	 * rating cache consists of pre-populated rating-average values for a
	 * particular target. We update rating cache when there is a new rating
	 * activity.Rating activity always occurs with a comment activity.
	 * 
	 * @param connection
	 * @param targetId
	 * @param rating
	 */
	private void updateRatingCache(Connection connection, String targetId,
			int rating) {
		DSConnection con = new DSConnection();
		Connection selectConnection = con.getConnection();
		ResultSet resultSet = null;

		PreparedStatement selectCacheStatement;
		PreparedStatement updateCacheStatement;
		PreparedStatement insertCacheStatement;

		try {
			selectCacheStatement = selectConnection
					.prepareStatement(SELECT_CACHE_SQL);
			selectCacheStatement.setString(1, targetId);
			resultSet = selectCacheStatement.executeQuery();

			if (!resultSet.next()) {
				insertCacheStatement = connection
						.prepareStatement(INSERT_CACHE_SQL);
				insertCacheStatement.setString(1, targetId);
				insertCacheStatement.setInt(2, rating);
				insertCacheStatement.setInt(3, 1);
				insertCacheStatement.executeUpdate();
			} else {
				int total, count;
				total = Integer.parseInt(resultSet
						.getString(Constants.RATING_TOTAL));
				count = Integer.parseInt(resultSet
						.getString(Constants.RATING_COUNT));

				updateCacheStatement = connection
						.prepareStatement(UPDATE_CACHE_SQL);

				updateCacheStatement.setInt(1, total + rating);
				updateCacheStatement.setInt(2, count + 1);
				updateCacheStatement.setString(3, targetId);
				updateCacheStatement.executeUpdate();
			}
		} catch (SQLException e) {
			log.error("Unable to update the cache. " + e);
		} finally {
			con.closeConnection(selectConnection);
		}
	}

	@Override
	public boolean remove(String activityId) {
		DSConnection con = new DSConnection();
		Connection connection = con.getConnection();
		if (connection == null) {
			return false;
		}

		PreparedStatement deleteComment;
		PreparedStatement deleteRating;
		PreparedStatement deletelike;
		try {
			connection.setAutoCommit(false);
			removeRating(activityId, connection);

			deleteComment = connection.prepareStatement(DELETE_COMMENT_SQL);
			deleteComment.setString(1, activityId);
			deleteComment.executeUpdate();

			deleteRating = connection.prepareStatement(DELETE_RATING_SQL);
			deleteRating.setString(1, activityId);
			deleteRating.executeUpdate();

			deletelike = connection.prepareStatement(DELETE_LIKES_SQL);
			deletelike.setString(1, activityId);
			deletelike.executeUpdate();

			connection.commit();
		} catch (SQLException e) {
			log.error("Error while removing the activity. Activity ID: "
					+ activityId + ". " + e);
		} finally {
			con.closeConnection(connection);
		}
		return false;
	}

	/**
	 * Use this method to update rating-cache if we remove the attached comment
	 * activity
	 * 
	 * @param activityId
	 * @return
	 */
	private void removeRating(String activityId, Connection connection) {
		ResultSet resultSet = null;
		PreparedStatement selectStatement;

		try {
			selectStatement = connection.prepareStatement(ACTIVITY_SELECT_SQL);
			selectStatement.setString(1, activityId);
			resultSet = selectStatement.executeQuery();

			if (!resultSet.next()) {
				JsonObject body = (JsonObject) parser.parse(resultSet
						.getString(Constants.BODY_COLUMN));
				Activity activity = new SQLActivity(body);

				int rating = activity.getRating();
				if (rating > 0) {
					// reduce this rating value from target
					String targetId = activity.getTargetId();
					PreparedStatement getCacheStatement;
					PreparedStatement updateCacheStatement;
					getCacheStatement = connection
							.prepareStatement(SELECT_CACHE_SQL);
					getCacheStatement.setString(1, targetId);
					resultSet = getCacheStatement.executeQuery();

					int total, count;
					total = Integer.parseInt(resultSet
							.getString(Constants.RATING_TOTAL));
					count = Integer.parseInt(resultSet
							.getString(Constants.RATING_COUNT));

					updateCacheStatement = connection
							.prepareStatement(UPDATE_CACHE_SQL);

					updateCacheStatement.setInt(1, total - rating);
					updateCacheStatement.setInt(2, count - 1);
					updateCacheStatement.setString(3, targetId);
					updateCacheStatement.executeUpdate();
				}
			}
		} catch (SQLException e) {
			log.error("Unable to update the rating cache. " + e);
		}

	}

}
