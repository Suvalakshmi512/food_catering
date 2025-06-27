DROP PROCEDURE IF EXISTS EZEE_SP_DISH_IUD;
DELIMITER $$

CREATE PROCEDURE EZEE_SP_DISH_IUD ( IN  pcrCode VARCHAR(15), IN  pcrName VARCHAR(25), IN  pcrDescription TEXT, IN  pinTimeToMake SMALLINT, IN  pcrVegType ENUM('VEG', 'NON_VEG'), IN  pinMinQty INT, IN  pdblMarginProfit  DECIMAL(5,2), IN  pcrTaxCode VARCHAR(15), IN  pitActiveFlag TINYINT,IN  pcrUpdatedBy VARCHAR(20),OUT pitRowCount INT )
/*
* Procedure Name       : EZEE_SP_DISH_IUD
* 
* Purpose              : Insert /Update /Delete dish Table
*
* Input                : None
*
* Output               : Affected Rows
*
* Returns              : None
*
* Dependencies
*
*     Tables           : dish
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
  /* ------------------------------------------------------------
   * Local variables
   * ---------------------------------------------------------- */
    DECLARE litDishId INT DEFAULT NULL;
    DECLARE litTaxId  INT DEFAULT NULL;
    
    
 /* ------------------------------------------------------------
   * Variable Initialized
   * ---------------------------------------------------------- */
    
    SET pitRowCount = 0;

    
    SELECT id INTO litDishId
     FROM dish
    WHERE CODE = pcrCode
    LIMIT 1;
    
    SELECT id INTO litTaxId
     FROM tax
    WHERE CODE = pcrTaxCode
    LIMIT 1;

  /*
*-----------------------------------------------------------------------------------------------------
*  Increment Last Sequence Number
*------------------------------------------------------------------------------------------------------
*/	
	IF ( pitActiveFlag = 1 AND litDishId != 0 AND EZEE_FN_ISNOTNULL(pcrCode)) THEN
                UPDATE dish SET NAME = pcrName, DESCRIPTION = pcrDescription, time_to_make_min = pinTimeToMake, veg_type = pcrVegType, min_available_qty = pinMinQty, margin_profit = pdblMarginProfit, tax_id = litTaxId, active_flag = pitActiveFlag , updated_by = pcrUpdatedBy, updated_at = NOW() WHERE CODE = pcrCode;
	        SELECT ROW_COUNT() INTO pitRowCount;
		
	ELSEIF ( pitActiveFlag = 1 AND litDishId = 0 ) THEN
		INSERT INTO dish (CODE, NAME, DESCRIPTION, time_to_make_min, veg_type, min_available_qty, margin_profit, tax_id, active_flag, updated_by, updated_at) VALUES (pcrCode, pcrName, pcrDescription, pinTimeToMake, pcrVegType, pinMinQty, pdblMarginProfit, litTaxId, pitActiveFlag, pcrUpdatedBy, NOW());
                SELECT ROW_COUNT() INTO pitRowCount;
		
	ELSEIF ( pitActiveFlag = 1 AND EZEE_FN_ISNOTNULL(pcrCode) ) THEN
		UPDATE dish SET active_flag = pitActiveFlag, updated_by = pitUpdatedBy , updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
	END IF;
END$$

DELIMITER ; 

 

   

        