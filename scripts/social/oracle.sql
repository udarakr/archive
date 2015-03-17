CREATE OR REPLACE PACKAGE types
AS
    TYPE ref_cursor IS REF CURSOR;
END;


CREATE OR REPLACE FUNCTION activity_pagin(i_payload_context_id IN VARCHAR2, i_tenant_domain IN VARCHAR2, i_order_val IN VARCHAR2, i_lim_val IN NUMBER, i_lim_offset IN NUMBER) 
    RETURN types.ref_cursor
AS
activity_cursor types.ref_cursor;
BEGIN
    OPEN activity_cursor FOR
		SELECT a.*
  FROM (SELECT b.*,
               rownum b_rownum
          FROM (SELECT c.*
                  FROM SOCIAL_COMMENTS c WHERE payload_context_id= i_payload_context_id AND tenant_domain= i_tenant_domain
                 ORDER BY CASE WHEN i_order_val='NEWEST' THEN id END DESC, CASE WHEN i_order_val='OLDEST' THEN id END ASC,CASE WHEN i_order_val='POPULAR' THEN likes END DESC) b
         WHERE rownum <= i_lim_val) a
WHERE b_rownum >= i_lim_offset;
    RETURN activity_cursor;
END;
/
