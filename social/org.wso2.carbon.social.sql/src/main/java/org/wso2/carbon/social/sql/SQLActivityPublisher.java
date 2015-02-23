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
import org.wso2.carbon.social.sql.Constants;
import org.wso2.carbon.social.sql.SocialUtil;
import org.wso2.carbon.social.core.Activity;
import org.wso2.carbon.social.core.ActivityPublisher;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SQLActivityPublisher extends ActivityPublisher {

	private static Log log = LogFactory.getLog(SQLActivityPublisher.class);

	/*
	 * public static final String INSERT_SQL = "INSERT INTO " +
	 * Constants.SOCIAL_COMMENTS_TABLE_NAME + "(" + Constants.ID_COLUMN + "," +
	 * Constants.CONTEXT_ID_COLUMN + "," + Constants.BODY_COLUMN + ", " +
	 * Constants.TENANT_DOMAIN_COLUMN + ", " + Constants.TIMESTAMP +
	 * ") VALUES(?, ?, ?, ?, ?)";
	 * 
	 * public static final String INSERT_RATING_SQL = "INSERT INTO " +
	 * Constants.SOCIAL_RATING_TABLE_NAME + "(" + Constants.ID_COLUMN + "," +
	 * Constants.CONTEXT_ID_COLUMN + "," + Constants.BODY_COLUMN + ", " +
	 * Constants.TENANT_DOMAIN_COLUMN + ", " + Constants.RATING_COLUMN + ", " +
	 * Constants.TIMESTAMP + ") VALUES(?, ?, ?, ?, ?, ?)";
	 * 
	 * public static final String SELECT_CACHE_SQL = "SELECT * FROM " +
	 * Constants.SOCIAL_RATING_CACHE_TABLE_NAME + " WHERE " +
	 * Constants.CONTEXT_ID_COLUMN + "=?"; public static final String
	 * UPDATE_CACHE_SQL = "UPDATE " + Constants.SOCIAL_RATING_CACHE_TABLE_NAME +
	 * " SET " + Constants.RATING_TOTAL + "=?, " + Constants.RATING_COUNT +
	 * "=? WHERE " + Constants.CONTEXT_ID_COLUMN + "=?"; public static final
	 * String INSERT_CACHE_SQL = "INSERT INTO " +
	 * Constants.SOCIAL_RATING_CACHE_TABLE_NAME + " (" +
	 * Constants.CONTEXT_ID_COLUMN + ", " + Constants.RATING_TOTAL + ", " +
	 * Constants.RATING_COUNT + ") VALUES(?, ?, ?) ";
	 * 
	 * public static final String ACTIVITY_SELECT_SQL = "SELECT * FROM " +
	 * Constants.SOCIAL_COMMENTS_TABLE_NAME + " WHERE " + Constants.ID_COLUMN +
	 * " =?"; public static final String ACTIVITY_UPDATE_SQL = "UPDATE " +
	 * Constants.SOCIAL_COMMENTS_TABLE_NAME + " SET " + Constants.BODY_COLUMN +
	 * "=? WHERE " + Constants.ID_COLUMN + "=?";
	 * 
	 * public static final String INSERT_LIKE_SQL = "INSERT INTO " +
	 * Constants.SOCIAL_LIKES_TABLE_NAME + "(" + Constants.ID_COLUMN + "," +
	 * Constants.CONTEXT_ID_COLUMN + "," + Constants.BODY_COLUMN + ", " +
	 * Constants.TENANT_DOMAIN_COLUMN + ", " + Constants.LIKES_COLUMN + "," +
	 * Constants.UNLIKES_COLUMN + "," + Constants.TIMESTAMP +
	 * ") VALUES(?, ?, ?, ?, ?, ?, ?)";
	 * 
	 * public static final String LIKES_UPDATE_SQL = "UPDATE " +
	 * Constants.SOCIAL_LIKES_TABLE_NAME + " SET " + Constants.BODY_COLUMN +
	 * " =?, " + Constants.LIKES_COLUMN + " =?, " + Constants.UNLIKES_COLUMN +
	 * " =? WHERE " + Constants.ID_COLUMN + " =?"; // This will be used to
	 * update body content when there are like/unlike // activities on the
	 * comment target public static final String UPDATE_RATING_SQL = "UPDATE " +
	 * Constants.SOCIAL_RATING_TABLE_NAME + " SET " + Constants.BODY_COLUMN +
	 * " =? where " + Constants.ID_COLUMN + " =?";
	 * 
	 * public static final String DELETE_COMMENT_SQL = "DELETE FROM " +
	 * Constants.SOCIAL_COMMENTS_TABLE_NAME + " WHERE " + Constants.ID_COLUMN +
	 * " =?"; public static final String DELETE_RATING_SQL = "DELETE FROM " +
	 * Constants.SOCIAL_RATING_TABLE_NAME + " WHERE " + Constants.ID_COLUMN +
	 * " =?"; public static final String DELETE_LIKES_SQL = "DELETE FROM " +
	 * Constants.SOCIAL_LIKES_TABLE_NAME + " WHERE " + Constants.ID_COLUMN +
	 * " =?";
	 */
	public static final String ErrorStr = "Failed to publish the social event.";

	private static final String INSERT_COMMENT_SQL = "INSERT INTO "
			+ Constants.SOCIAL_COMMENTS_TABLE_NAME + "(" + Constants.ID_COLUMN
			+ "," + Constants.BODY_COLUMN + "," + Constants.CONTEXT_ID_COLUMN
			+ "," + Constants.USER_COLUMN + ","
			+ Constants.TENANT_DOMAIN_COLUMN + ", " + Constants.LIKES_COLUMN
			+ ", " + Constants.UNLIKES_COLUMN + ", " + Constants.TIMESTAMP
			+ ") VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String INSERT_RATING_SQL = "INSERT INTO "
			+ Constants.SOCIAL_RATING_TABLE_NAME + "(" + Constants.ID_COLUMN
			+ "," + Constants.CONTEXT_ID_COLUMN + "," + Constants.USER_COLUMN
			+ ", " + Constants.TENANT_DOMAIN_COLUMN + ", "
			+ Constants.RATING_COLUMN + ", " + Constants.TIMESTAMP
			+ ") VALUES(?, ?, ?, ?, ?, ?)";

	private static final String INSERT_LIKE_SQL = "INSERT INTO "
			+ Constants.SOCIAL_LIKES_TABLE_NAME + "(" + Constants.ID_COLUMN
			+ "," + Constants.CONTEXT_ID_COLUMN + "," + Constants.USER_COLUMN
			+ ", " + Constants.TENANT_DOMAIN_COLUMN + ", "
			+ Constants.LIKE_VALUE_COLUMN + "," + Constants.TIMESTAMP
			+ ") VALUES(?, ?, ?, ?, ?, ?)";

	private static final String DELETE_LIKE_ACTIVITY = "DELETE FROM "
			+ Constants.SOCIAL_LIKES_TABLE_NAME + " WHERE "
			+ Constants.CONTEXT_ID_COLUMN + " =? AND " + Constants.USER_COLUMN
			+ " =?";

	private static final String COMMENT_ACTIVITY_SELECT_FOR_UPDATE_SQL = "SELECT "
			+ Constants.BODY_COLUMN
			+ " FROM "
			+ Constants.SOCIAL_COMMENTS_TABLE_NAME
			+ " WHERE "
			+ Constants.ID_COLUMN + " =?";

	public static final String COMMENT_ACTIVITY_SELECT_SQL = "SELECT "
			+ Constants.BODY_COLUMN + " FROM "
			+ Constants.SOCIAL_COMMENTS_TABLE_NAME + " WHERE "
			+ Constants.ID_COLUMN + " =?";

	private static final String COMMENT_ACTIVITY_UPDATE_SQL = "UPDATE "
			+ Constants.SOCIAL_COMMENTS_TABLE_NAME + " SET "
			+ Constants.BODY_COLUMN + "=?, " + Constants.LIKES_COLUMN + "=?, "
			+ Constants.UNLIKES_COLUMN + "=? WHERE " + Constants.ID_COLUMN
			+ " =?";

	public static final String SELECT_CACHE_SQL = "SELECT "
			+ Constants.RATING_TOTAL + "," + Constants.RATING_COUNT + " FROM "
			+ Constants.SOCIAL_RATING_CACHE_TABLE_NAME + " WHERE "
			+ Constants.CONTEXT_ID_COLUMN + "=?";

	public static final String UPDATE_CACHE_SQL = "UPDATE "
			+ Constants.SOCIAL_RATING_CACHE_TABLE_NAME + " SET "
			+ Constants.RATING_TOTAL + "=?, " + Constants.RATING_COUNT
			+ "=? WHERE " + Constants.CONTEXT_ID_COLUMN + "=?";

	public static final String INSERT_CACHE_SQL = "INSERT INTO "
			+ Constants.SOCIAL_RATING_CACHE_TABLE_NAME + " ("
			+ Constants.CONTEXT_ID_COLUMN + ", " + Constants.RATING_TOTAL
			+ ", " + Constants.RATING_COUNT + ") VALUES(?, ?, ?)";

	public static final String DELETE_COMMENT_SQL = "DELETE FROM "
			+ Constants.SOCIAL_COMMENTS_TABLE_NAME + " WHERE "
			+ Constants.ID_COLUMN + " =?";

	public static final String DELETE_RATING_SQL = "DELETE FROM "
			+ Constants.SOCIAL_RATING_TABLE_NAME + " WHERE "
			+ Constants.ID_COLUMN + " =?";

	public static final String DELETE_LIKES_SQL = "DELETE FROM "
			+ Constants.SOCIAL_LIKES_TABLE_NAME + " WHERE "
			+ Constants.CONTEXT_ID_COLUMN + " =?";

	private JsonParser parser = new JsonParser();

	@Override
	protected String publishActivity(JsonObject jsonObject) {
		SQLActivity activity = new SQLActivity(jsonObject);

		// TODO use review as the verb insted of post
		if ("post".equals(activity.getVerb())) {

			PreparedStatement commentStatement = null;
			PreparedStatement ratingStatement = null;

			String json = null;

			json = jsonObject.toString();
			String targetId = activity.getTargetId();
			String id = activity.getId();
			String userId = activity.getActorId();
			int timeStamp = activity.getTimestamp();
			String tenantDomain = SocialUtil.getTenantDomain();

			DSConnection con = new DSConnection();
			Connection connection = con.getConnection();
			int commentRet = 0;

			if (connection == null) {
				if (log.isDebugEnabled()) {
					log.debug(Constants.CONNECTION_ERROR);
				}
				return null;
			}

			int totalLikes = activity.getLikeCount();
			int totalUnlikes = activity.getDislikeCount();
			int rating = activity.getRating();

			try {
				connection.setAutoCommit(false);
				// INSERT_SQL INSERT INTO SOCIAL_COMMENTS
				// (id,payload_context_id,body,user_id,tenant_domain,likes,unlikes,
				// timestamp) VALUES ();

				commentStatement = connection
						.prepareStatement(INSERT_COMMENT_SQL);
				commentStatement.setString(1, id);
				commentStatement.setString(2, json);
				commentStatement.setString(3, targetId);
				commentStatement.setString(4, userId);
				commentStatement.setString(5, tenantDomain);
				commentStatement.setInt(6, totalLikes);
				commentStatement.setInt(7, totalUnlikes);
				commentStatement.setInt(8, timeStamp);
				commentRet = commentStatement.executeUpdate();

				// handle rating activity which comes inside the review
				if (rating > 0) {
					// INSERT INTO SOCIAL_RATING
					// (id,target_id,user_id,tenant_domain,rating_value,timestamp)
					ratingStatement = connection
							.prepareStatement(INSERT_RATING_SQL);
					ratingStatement.setString(1, id);
					ratingStatement.setString(2, targetId);
					ratingStatement.setString(3, userId);
					ratingStatement.setString(4, tenantDomain);
					ratingStatement.setInt(5, rating);
					ratingStatement.setInt(6, timeStamp);
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
			} catch (SQLException e) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					log.error(Constants.ROLLBACK_ERROR + " " + e1);
				}
				log.error(ErrorStr + e);
			} finally {
				try {
					commentStatement.close();
					ratingStatement.close();
				} catch (SQLException e) {
					log.error("Error while closing statement/resultset. " + e);
				}
				if (con != null) {
					con.closeConnection(connection);
				}
			}
		} else {
			// Handle like,dislike,unlike,undislike verbs
			publishLikes(activity);
		}

		return null;
	}

	/**
	 * publish like/dislike activity
	 * 
	 * @param activity
	 * @return
	 */
	private void publishLikes(SQLActivity activity) {
		DSConnection con = new DSConnection();
		Connection connection = con.getConnection();

		if (connection != null) {
			PreparedStatement selectActivityStatement = null;
			PreparedStatement updateActivityStatement = null;
			ResultSet resultSet = null;

			try {
				connection.setAutoCommit(false);
				String verb = activity.getVerb();
				int likeValue;
				// target of a like activity is a comment
				String commentID = activity.getTargetId();
				// ResultSet commentResultSet = getCommentResultSet(commentID);

				selectActivityStatement = connection
						.prepareStatement(COMMENT_ACTIVITY_SELECT_FOR_UPDATE_SQL);
				selectActivityStatement.setString(1, commentID);
				resultSet = selectActivityStatement.executeQuery();

				if (resultSet.next()) {

					JsonObject currentBody = (JsonObject) parser
							.parse(resultSet.getString(Constants.BODY_COLUMN));
					Activity currentActivity = new SQLActivity(currentBody);

					int likeCount = currentActivity.getLikeCount();
					int dislikeCount = currentActivity.getDislikeCount();

					switch (Constants.VERB.valueOf(verb)) {
					case like:
						likeCount += 1;
						likeValue = 1;
						insertLikeActivity(activity, likeValue, connection);
						break;
					case dislike:
						dislikeCount += 1;
						likeValue = 0;
						insertLikeActivity(activity, likeValue, connection);
						break;
					case unlike:
						likeCount -= 1;
						removeLikeActivity(activity, connection);
						break;
					case undislike:
						dislikeCount -= 1;
						removeLikeActivity(activity, connection);
						break;
					default:
						log.warn("Provided verb: " + verb
								+ " not supported by the social framework.");
						break;
					}

					currentActivity.setLikeCount(likeCount);
					currentActivity.setDislikeCount(dislikeCount);

					JsonObject json = currentActivity.getBody();
					// UPDATE SOCIAL_COMMENTS SET body=?, likes=?, dislikes=?
					// WHERE
					// id=?;
					updateActivityStatement = connection
							.prepareStatement(COMMENT_ACTIVITY_UPDATE_SQL);

					updateActivityStatement.setString(1, json.toString());
					updateActivityStatement.setInt(2, likeCount);
					updateActivityStatement.setInt(3, dislikeCount);
					updateActivityStatement.setString(4, commentID);
					updateActivityStatement.executeUpdate();
					connection.commit();
				}
			} catch (SQLException e) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					log.error(Constants.ROLLBACK_ERROR + " " + e1);
				}
				log.error(ErrorStr + e);
			} finally {
				try {
					selectActivityStatement.close();
					resultSet.close();
					updateActivityStatement.close();
				} catch (SQLException e) {
					log.error("Error while closing statement/resultset. " + e);
				}
				if (con != null) {
					con.closeConnection(connection);
				}
			}
		}

	}

	private void removeLikeActivity(SQLActivity activity, Connection connection) {
		PreparedStatement deleteActivityStatement = null;
		String targetId = activity.getTargetId();
		String actor = activity.getActorId();

		try {
			// DELETE FROM SOCIAL_LIKES WHERE payload_context_id=? AND
			// user_id=?;
			deleteActivityStatement = connection
					.prepareStatement(DELETE_LIKE_ACTIVITY);
			deleteActivityStatement.setString(1, targetId);
			deleteActivityStatement.setString(2, actor);
			deleteActivityStatement.executeUpdate();

		} catch (SQLException e) {
			log.error("Error while removing like activity from the table: "
					+ Constants.SOCIAL_LIKES_TABLE_NAME + e);
		} finally {
			try {
				deleteActivityStatement.close();
			} catch (SQLException e) {
				log.error("Error while closing deleteActivityStatement. " + e);
			}
		}
	}

	private void insertLikeActivity(SQLActivity activity, int likeValue,
			Connection connection) {
		PreparedStatement insertActivityStatement = null;
		String targetId = activity.getTargetId();
		String actor = activity.getActorId();
		int timestamp = activity.getTimestamp();
		String id = activity.getId();
		String tenantDomain = SocialUtil.getTenantDomain();

		try {
			// INSERT LIKE activity to LIKES TABLE
			insertActivityStatement = connection
					.prepareStatement(INSERT_LIKE_SQL);
			insertActivityStatement.setString(1, id);
			insertActivityStatement.setString(2, targetId);
			insertActivityStatement.setString(3, actor);
			insertActivityStatement.setString(4, tenantDomain);
			insertActivityStatement.setInt(5, likeValue);
			insertActivityStatement.setInt(6, timestamp);
			insertActivityStatement.executeUpdate();
		} catch (SQLException e) {
			log.error("Error while adding like activity to the table: "
					+ Constants.SOCIAL_LIKES_TABLE_NAME + e);
		} finally {
			try {
				insertActivityStatement.close();
			} catch (SQLException e) {
				log.error("Error while closing insertActivityStatement. " + e);
			}
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
		ResultSet resultSet = null;
		PreparedStatement selectCacheStatement = null;
		PreparedStatement updateCacheStatement = null;
		PreparedStatement insertCacheStatement = null;

		try {
			selectCacheStatement = connection
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
			try {
				resultSet.close();
				selectCacheStatement.close();
				updateCacheStatement.close();
				insertCacheStatement.close();
			} catch (SQLException e) {
				log.error("Error while closing resultSet and select/update/insert Statements. "
						+ e);
			}
		}
	}

	@Override
	public boolean remove(String activityId) {
		DSConnection con = new DSConnection();
		Connection connection = con.getConnection();

		if (connection == null) {
			if (log.isDebugEnabled()) {
				log.debug(Constants.CONNECTION_ERROR);
			}
			return false;
		}

		PreparedStatement deleteComment = null;
		PreparedStatement deleteRating = null;
		PreparedStatement deletelike = null;
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
			try {
				connection.rollback();
			} catch (SQLException e1) {
				log.error(Constants.ROLLBACK_ERROR + " " + e1);
			}
			log.error("Error while removing the activity. Activity ID: "
					+ activityId + ". " + e);
		} finally {
			try {
				deleteComment.close();
				deleteRating.close();
				deletelike.close();
			} catch (SQLException e) {
				log.error("Error while closing  deleteComment/deleteRating/deletelike Statements. "
						+ e);
			}
			if (con != null) {
				con.closeConnection(connection);
			}
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
		PreparedStatement selectStatement = null;
		PreparedStatement getCacheStatement = null;
		PreparedStatement updateCacheStatement = null;

		try {
			selectStatement = connection
					.prepareStatement(COMMENT_ACTIVITY_SELECT_SQL);
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
		}finally{
			try {
				selectStatement.close();
				getCacheStatement.close();
				updateCacheStatement.close();			
			} catch (SQLException e) {
				log.error("Error while closing resultSet and select/getCache/updateCache Statements. "
						+ e);
			}
		}

	}

}
