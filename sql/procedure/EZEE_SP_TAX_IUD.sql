DROP PROCEDURE IF EXISTS EZEE_SP_TAX_IUD;
DELIMITER $$
CREATE PROCEDURE EZEE_SP_TAX_IUD(IN pcrCode VARCHAR(15), IN pcrDescription VARCHAR(150), IN pdlRatePct DECIMAL(10,2), IN pitActiveFlag TINYINT, IN pitUpdatedBy VARCHAR(12), OUT pitRowCount INT)
/* 
* Procedure Name       : EZEE_SP_TAX_IUD
* 
* Purpose              : Insert /Update /Delete tax Table
*
* Input                : None
*
* Output               : Affected Rows
*
* Returns              : None
*
* Dependencies
*
*     Tables           : tax
*
*     Functions        : None
*
*     Procedures       :
*
* Revision History:
*
*     1.0 - 2024/06/18      Ezee Info 
*     Guruprasath             Original Code
*
*/

BEGIN

/*
*----------------------------------------------------------------------------------------------------
* Variable Declare
*-----------------------------------------------------------------------------------------------------
*/  DECLARE litTaxId INT DEFAULT 0;
/*
*-----------------------------------------------------------------------------------------------------
*  Variable Initialized
*------------------------------------------------------------------------------------------------------
*/
	SET pitRowCount  = 0;
	SELECT id INTO litTaxId FROM tax WHERE CODE = pcrCode;
	
/*
*-----------------------------------------------------------------------------------------------------
*  Increment Last Sequence Number
*------------------------------------------------------------------------------------------------------
*/	
	IF ( pitActiveFlag = 1 AND litTaxId != 0 AND EZEE_FN_ISNOTNULL(pcrCode)) THEN
		UPDATE tax SET DESCRIPTION = pcrDescription, rate_pct = pdlRatePct, active_flag = pitActiveFlag, updated_by = pitUpdatedBy , updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
		
	ELSEIF ( pitActiveFlag = 1 AND litTaxId = 0 ) THEN
		INSERT INTO tax ( CODE, DESCRIPTION, rate_pct, active_flag, updated_by, updated_at) VALUES ( pcrCode, pcrDescription, pdlRatePct, pitActiveFlag, pitUpdatedBy, NOW());
		SELECT ROW_COUNT() INTO pitRowCount;
	ELSEIF ( pitActiveFlag != 1 AND EZEE_FN_ISNOTNULL(pcrCode) ) THEN
		UPDATE tax SET active_flag = pitActiveFlag, updated_by = pitUpdatedBy , updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
	END IF;
END$$

DELIMITER ;

