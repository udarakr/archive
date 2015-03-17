CREATE PROCEDURE remove_emp (i_payload_context_id IN VARCHAR2, i_tenant_domain IN VARCHAR2, i_order_val IN VARCHAR2, i_lim_val IN NUMBER, i_lim_offset IN NUMBER, activity_cursor OUT SYS_REFCURSOR) AS
   BEGIN
	SELECT a.*
  FROM (SELECT b.*,
               rownum b_rownum
          FROM (SELECT c.*
                  FROM SOCIAL_COMMENTS c
                 ORDER BY id DESC) b
         WHERE rownum <= 10) a
WHERE b_rownum >= 0 || '%';
   END;
/
