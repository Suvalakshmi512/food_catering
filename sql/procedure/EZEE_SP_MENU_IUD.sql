DROP PROCEDURE IF EXISTS EZEE_SP_MENU_IUD;
DELIMITER $$
CREATE PROCEDURE EZEE_SP_MENU_IUD(IN pcrCode VARCHAR(15), IN pcrName VARCHAR(45), IN pitActiveFlag TINYINT, IN pitUpdatedBy VARCHAR(12), OUT pitRowCount INT, OUT pitId INT)
/*
* Procedure Name       : EZEE_SP_MENU_IUD
* 
* Purpose              : Insert /Update /Delete menu Table
*
* Input                : None
*
* Output               : Affected Rows
*
* Returns              : None
*
* Dependencies
*
*     Tables           : menu
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
*/  DECLARE litMenuId INT DEFAULT 0;
/*
*-----------------------------------------------------------------------------------------------------
*  Variable Initialized
*------------------------------------------------------------------------------------------------------
*/
	SET pitRowCount  = 0;
	SELECT id INTO litMenuId FROM menu WHERE CODE = pcrCode;
	
/*
*-----------------------------------------------------------------------------------------------------
*  Increment Last Sequence Number
*------------------------------------------------------------------------------------------------------
*/	
	IF ( pitActiveFlag = 1 AND litMenuId != 0 AND EZEE_FN_ISNOTNULL(pcrCode)) THEN
		UPDATE menu SET NAME = pcrName, updated_by = pitUpdatedBy , updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
		SET pitId = litMenuId;
		
	ELSEIF ( pitActiveFlag = 1 AND litMenuId = 0) THEN
		INSERT INTO menu ( CODE, NAME, active_flag, updated_by, updated_at) VALUES ( pcrCode, pcrName, pitActiveFlag, pitUpdatedBy, NOW());
		SELECT ROW_COUNT() INTO pitRowCount;
		SET pitId = LAST_INSERT_ID();
	ELSEIF ( pitActiveFlag = 1 AND EZEE_FN_ISNOTNULL(pcrCode) ) THEN
		UPDATE menu SET active_flag = pitActiveFlag, updated_by = pitUpdatedBy , updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
		SET pitId = litMenuId;
	END IF;
END$$

DELIMITER ;