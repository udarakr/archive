CREATE TABLE IF NOT EXISTS SOCIAL_COMMENTS ( id VARCHAR(250), payload_context_id VARCHAR(250),body CLOB, user_id VARCHAR(100), tenant_domain VARCHAR(100), timestamp DATETIME, PRIMARY KEY (id));

CREATE INDEX social_index ON SOCIAL_COMMENTS (payload_context_id,tenant_domain); 

CREATE TABLE IF NOT EXISTS SOCIAL_RATING ( id VARCHAR(250), payload_context_id VARCHAR(250),user_id VARCHAR(100), tenant_domain VARCHAR(100), rating TINYINT,timestamp DATETIME, PRIMARY KEY (id));

CREATE INDEX social_rating_index ON SOCIAL_RATING (payload_context_id,tenant_domain);

CREATE TABLE IF NOT EXISTS SOCIAL_RATING_CACHE ( payload_context_id VARCHAR(250), rating_total INT,rating_count INT, PRIMARY KEY (payload_context_id));

CREATE INDEX social_rating_cache_index1 ON SOCIAL_RATING_CACHE (rating_total);
CREATE INDEX social_rating_cache_index2 ON SOCIAL_RATING_CACHE (rating_count);

CREATE TABLE IF NOT EXISTS SOCIAL_LIKES ( id VARCHAR(250), payload_context_id VARCHAR(250), user_id VARCHAR(100), tenant_domain VARCHAR(100), like_value TINYINT, timestamp DATETIME, PRIMARY KEY (id));

CREATE INDEX social_like_index ON SOCIAL_LIKES (payload_context_id,tenant_domain);

