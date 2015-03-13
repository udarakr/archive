SELECT body, id FROM SOCIAL_COMMENTS WHERE payload_context_id=? AND tenant_domain=? ORDER BY id ASC LIMIT ?,?;
