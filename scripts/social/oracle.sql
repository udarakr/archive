CREATE PROCEDURE activity_pagin (IN i_payload_context_id VARCHAR2(50), IN i_tenant_domain VARCHAR2(100),IN i_order_val VARCHAR2(50),IN i_lim_val NUMBER(19, 0),IN i_lim_offset NUMBER(19, 0), OUT SYS_REFCURSOR)
IS
BEGIN
SELECT a.*
  FROM (SELECT b.*,
               rownum b_rownum
          FROM (SELECT c.*
                  FROM SOCIAL_COMMENTS c
                 ORDER BY id DESC) b
         WHERE rownum <= 10) a
WHERE b_rownum >= 0 || '%';
END
