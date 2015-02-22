CREATE TABLE IF NOT EXISTS SOCIAL_COMMENTS ( id VARCHAR(250),body BLOB, payload_context_id VARCHAR(250),user_id VARCHAR(100), tenant_domain VARCHAR(100), likes TINYINT, unlikes TINYINT, timestamp VARCHAR(100), PRIMARY KEY (id));


CREATE TABLE IF NOT EXISTS SOCIAL_RATING ( id VARCHAR(250), payload_context_id VARCHAR(250),user_id VARCHAR(100), tenant_domain VARCHAR(100), rating TINYINT,timestamp VARCHAR(100), PRIMARY KEY (id));


CREATE TABLE IF NOT EXISTS SOCIAL_RATING_CACHE ( payload_context_id VARCHAR(250), rating_total INT,rating_count INT, PRIMARY KEY (payload_context_id));

CREATE TABLE IF NOT EXISTS SOCIAL_LIKES ( id VARCHAR(250), payload_context_id VARCHAR(250), user_id VARCHAR(100), tenant_domain VARCHAR(100), like_value TINYINT, timestamp VARCHAR(100), PRIMARY KEY (id));


