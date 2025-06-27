`dish_labour`DROP PROCEDURE IF EXISTS EZEE_SP_ESTIMATE_IUD;
DELIMITER $$

CREATE PROCEDURE EZEE_SP_ESTIMATE_IUD ( IN pcrCode VARCHAR(15), IN pcrEventCode VARCHAR(15), IN pcrMenuCode VARCHAR(15), IN pdlDiscountPct  DECIMAL(5,2), IN pitActiveFlag INT, IN pcrUpdatedBy VARCHAR(20), OUT pitRowCount INT)
/*
* Procedure Name       : EZEE_SP_ESTIMATE_IUD
* 
* Purpose              : Insert /Update /Delete estimate Table
*
* Input                : None
*
* Output               : Affected Rows
*
* Returns              : None
*
* Dependencies
*
*     Tables           : estimate
*
*     Functions        : None
*
*     Procedures       :
*
* Revision History:
*
*     1.0 - 2024/06/19      Ezee Info 
*     Guruprasath             Original Code
*
*/
BEGIN
/*------------------------------------------------------------------
      Local variables
------------------------------------------------------------------*/
    DECLARE ldlSubTotal     DECIMAL(12,2) DEFAULT 0;
    DECLARE ldlDiscountAmt DECIMAL(12,2) DEFAULT 0;
    DECLARE ldlGrandTotal  DECIMAL(12,2) DEFAULT 0;
    DECLARE ldlMenuId  INT DEFAULT 0;
    DECLARE ldlEventId INT DEFAULT 0;
    DECLARE ldlEstimateId  INT DEFAULT 0;
    DECLARE litGuestCount INT DEFAULT 0;

/* ------------------------------------------------------------
*     Variable Initialized
* ---------------------------------------------------------- */

    SET pitRowCount = 0;
    SELECT id INTO ldlMenuID FROM menu WHERE CODE = pcrMenuCode;
    SELECT id INTO ldlEventId FROM EVENT WHERE CODE = pcrEventCode;
    SELECT id INTO ldlEstimateId FROM estimate WHERE CODE = pcrCode;
    
    SELECT price INTO ldlSubTotal FROM menu WHERE id = ldlMenuId AND active_flag = 1;
    SELECT guest_count INTO litGuestCount FROM EVENT WHERE id = ldlEventId AND active_flag = 1;
    
    SET ldlSubTotal = litGuestCount * ldlSubTotal;

    SET ldlDiscountAmt = ROUND(ldlSubTotal * pdlDiscountPct / 100, 2);
    SET ldlGrandTotal  = ROUND(ldlSubTotal - ldlDiscountAmt, 2);

/*
*-----------------------------------------------------------------------------------------------------
*  Increment Last Sequence Number
*------------------------------------------------------------------------------------------------------
*/	
	IF ( pitActiveFlag = 1 AND ldlEstimateId != 0 AND EZEE_FN_ISNOTNULL(pcrCode)) THEN
		UPDATE estimate SET menu_id = ldlMenuId, event_id = ldlEventId, subtotal = ldlSubTotal, discount = pdlDiscountPct, grand_total = ldlGrandTotal, active_flag = pitActiveFlag, updated_by = pcrUpdatedBy, updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
		
	ELSEIF ( pitActiveFlag = 1 AND ldlEstimateId = 0) THEN
		INSERT INTO estimate ( CODE, event_id, menu_id, subtotal, discount, grand_total, active_flag, updated_by, updated_at ) VALUES (pcrCode, ldlEventId, ldlMenuId, ldlSubTotal, pdlDiscountPct, ldlGrandTotal, pitActiveFlag, pcrUpdatedBy, NOW());
		SELECT ROW_COUNT() INTO pitRowCount;
		
	ELSEIF ( pitActiveFlag = 1 AND EZEE_FN_ISNOTNULL(pcrCode) ) THEN
		UPDATE estimate SET active_flag = pitActiveFlag, updated_by = pitUpdatedBy , updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
	END IF;
END$$

DELIMITER ; 
 
 
`dish_labour`DROP PROCEDURE IF EXISTS EZEE_SP_ESTIMATE_IUD;
DELIMITER $$

CREATE PROCEDURE EZEE_SP_ESTIMATE_IUD ( IN pcrCode VARCHAR(15), IN pcrEventCode VARCHAR(15), IN pcrMenuCode VARCHAR(15), IN pdlDiscountPct  DECIMAL(5,2), IN pitActiveFlag INT, IN pcrUpdatedBy VARCHAR(20), OUT pitRowCount INT)
/*
* Procedure Name       : EZEE_SP_ESTIMATE_IUD
* 
* Purpose              : Insert /Update /Delete estimate Table
*
* Input                : None
*
* Output               : Affected Rows
*
* Returns              : None
*
* Dependencies
*
*     Tables           : estimate
*
*     Functions        : None
*
*     Procedures       :
*
* Revision History:
*
*     1.0 - 2024/06/19      Ezee Info 
*     Guruprasath             Original Code
*
*/
BEGIN
/*------------------------------------------------------------------
      Local variables
------------------------------------------------------------------*/
    DECLARE ldlSubTotal     DECIMAL(12,2) DEFAULT 0;
    DECLARE ldlDiscountAmt DECIMAL(12,2) DEFAULT 0;
    DECLARE ldlGrandTotal  DECIMAL(12,2) DEFAULT 0;
    DECLARE ldlMenuId  INT DEFAULT 0;
    DECLARE ldlEventId INT DEFAULT 0;
    DECLARE ldlEstimateId  INT DEFAULT 0;
    DECLARE litGuestCount INT DEFAULT 0;

/* ------------------------------------------------------------
*     Variable Initialized
* ---------------------------------------------------------- */

    SET pitRowCount = 0;
    SELECT id INTO ldlMenuID FROM menu WHERE CODE = pcrMenuCode;
    SELECT id INTO ldlEventId FROM EVENT WHERE CODE = pcrEventCode;
    SELECT id INTO ldlEstimateId FROM estimate WHERE CODE = pcrCode;
    
    SELECT price INTO ldlSubTotal FROM menu WHERE id = ldlMenuId AND active_flag = 1;
    SELECT guest_count INTO litGuestCount FROM EVENT WHERE id = ldlEventId AND active_flag = 1;
    
    SET ldlSubTotal = litGuestCount * ldlSubTotal;

    SET ldlDiscountAmt = ROUND(ldlSubTotal * pdlDiscountPct / 100, 2);
    SET ldlGrandTotal  = ROUND(ldlSubTotal - ldlDiscountAmt, 2);

/*
*-----------------------------------------------------------------------------------------------------
*  Increment Last Sequence Number
*------------------------------------------------------------------------------------------------------
*/	
	IF ( pitActiveFlag = 1 AND ldlEstimateId != 0 AND EZEE_FN_ISNOTNULL(pcrCode)) THEN
		UPDATE estimate SET menu_id = ldlMenuId, event_id = ldlEventId, subtotal = ldlSubTotal, discount = pdlDiscountPct, grand_total = ldlGrandTotal, active_flag = pitActiveFlag, updated_by = pcrUpdatedBy, updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
		
	ELSEIF ( pitActiveFlag = 1 AND ldlEstimateId = 0) THEN
		INSERT INTO estimate ( CODE, event_id, menu_id, subtotal, discount, grand_total, active_flag, updated_by, updated_at ) VALUES (pcrCode, ldlEventId, ldlMenuId, ldlSubTotal, pdlDiscountPct, ldlGrandTotal, pitActiveFlag, pcrUpdatedBy, NOW());
		SELECT ROW_COUNT() INTO pitRowCount;
		
	ELSEIF ( pitActiveFlag = 1 AND EZEE_FN_ISNOTNULL(pcrCode) ) THEN
		UPDATE estimate SET active_flag = pitActiveFlag, updated_by = pitUpdatedBy , updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
	END IF;
END$$

DELIMITER ; 
 
 
