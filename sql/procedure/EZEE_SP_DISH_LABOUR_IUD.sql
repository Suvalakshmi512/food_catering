DROP PROCEDURE IF EXISTS EZEE_SP_DISH_LABOUR_IUD;
DELIMITER $$

CREATE PROCEDURE EZEE_SP_DISH_LABOUR_IUD ( IN  pcrCode VARCHAR(15), IN  pcrDishCode VARCHAR(15), IN  pcrLabourCode VARCHAR(15), IN  pdblHoursRequired  DECIMAL(5,2), IN  pitActiveFlag TINYINT, IN  pcrUpdatedBy VARCHAR(20), OUT pitRowCount INT )
/*
* Procedure Name       : EZEE_SP_DISH_LABOUR_IUD
* 
* Purpose              : Insert /Update /Delete dish_labour Table
*
* Input                : None
*
* Output               : Affected Rows
*
* Returns              : None
*
* Dependencies
*
*     Tables           : dish_labour
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
    /*------------------------------------------------------------------
      Local variables
    ------------------------------------------------------------------*/
    DECLARE litDishLabourId INT DEFAULT 0;
    DECLARE litDishId       INT DEFAULT 0;
    DECLARE litLabourId     INT DEFAULT 0;

  /* ------------------------------------------------------------
   * Variable Initialized
   * ---------------------------------------------------------- */
    SET pitRowCount = 0;
    
    SELECT id INTO litDishId
    FROM dish
    WHERE CODE = pcrDishCode
    LIMIT 1;

    SELECT id INTO litLabourId
    FROM labour
    WHERE CODE = pcrLabourCode
    LIMIT 1;

    
    SELECT id INTO litDishLabourId
    FROM dish_labour
    WHERE CODE = pcrCode
    LIMIT 1;
 /*
*-----------------------------------------------------------------------------------------------------
*  Increment Last Sequence Number
*------------------------------------------------------------------------------------------------------
*/	
	IF ( pitActiveFlag = 1 AND litDishLabourId != 0 AND EZEE_FN_ISNOTNULL(pcrCode)) THEN
		UPDATE dish_labour SET labour_id = litLabourId, dish_id = litDishId, hours_required = pdblHoursRequired, active_flag = pitActiveFlag, updated_by = pcrUpdatedBy, updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
		
	ELSEIF ( pitActiveFlag = 1 AND litDishLabourId = 0 ) THEN
		INSERT INTO dish_labour (CODE, labour_id, dish_id, hours_required, active_flag, updated_by, updated_at) VALUES (pcrCode, litLabourId, litDishId, pdblHoursRequired, pitActiveFlag, pcrUpdatedBy, NOW());
		SELECT ROW_COUNT() INTO pitRowCount;
		
	ELSEIF ( pitActiveFlag != 1 AND EZEE_FN_ISNOTNULL(pcrCode) ) THEN
		UPDATE dish_labour SET active_flag = pitActiveFlag, updated_by = pcrUpdatedBy , updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
	END IF;
END$$

DELIMITER ; 

  
    