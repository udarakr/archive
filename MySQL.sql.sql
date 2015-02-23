CREATE TABLE IF NOT EXISTS SOCIAL_COMMENTS ( id BIGINT NOT NULL AUTO_INCREMENT,body BLOB, payload_context_id VARCHAR(250),user_id VARCHAR(100), tenant_domain VARCHAR(100), likes TINYINT, unlikes TINYINT, timestamp VARCHAR(100), PRIMARY KEY (id));


CREATE TABLE IF NOT EXISTS SOCIAL_RATING ( id BIGINT NOT NULL AUTO_INCREMENT, comment_id BIGINT NOT NULL, payload_context_id VARCHAR(250),user_id VARCHAR(100), tenant_domain VARCHAR(100), rating TINYINT,timestamp VARCHAR(100), PRIMARY KEY (id), FOREIGN KEY (comment_id) REFERENCES SOCIAL_COMMENTS(id));


CREATE TABLE IF NOT EXISTS SOCIAL_RATING_CACHE ( payload_context_id VARCHAR(250), rating_total INT,rating_count INT, PRIMARY KEY (payload_context_id));

CREATE TABLE IF NOT EXISTS SOCIAL_LIKES ( id BIGINT NOT NULL AUTO_INCREMENT, payload_context_id BIGINT NOT NULL, user_id VARCHAR(100), tenant_domain VARCHAR(100), like_value TINYINT, timestamp VARCHAR(100), PRIMARY KEY (id), FOREIGN KEY (payload_context_id) REFERENCES SOCIAL_COMMENTS(id));


GRANT ALL PRIVILEGES ON social.* TO 'root'@'%' WITH GRANT OPTION;


