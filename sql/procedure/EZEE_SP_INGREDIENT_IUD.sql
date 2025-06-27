DROP PROCEDURE IF EXISTS EZEE_SP_INGREDIENT_IUD;
DELIMITER $$
CREATE PROCEDURE EZEE_SP_INGREDIENT_IUD(IN pcrCode VARCHAR(15), IN pcrName VARCHAR(45),IN pdlUnitQty DECIMAL(5,2), IN pcrUnit VARCHAR(60), IN pdlUnitCost DECIMAL(10,2), IN pitTaxCode VARCHAR(15),IN pitActiveFlag TINYINT, IN pitUpdatedBy VARCHAR(12), OUT pitRowCount INT)
/*
* Procedure Name       : EZEE_SP_INGREDIENT_IUD
* 
* Purpose              : Insert /Update /Delete incredient Table
*
* Input                : None
*
* Output               : Affected Rows
*
* Returns              : None
*
* Dependencies
*
*     Tables           : incredient
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
*/  DECLARE litIngredientId INT DEFAULT 0;
    DECLARE litTaxId INT DEFAULT 0;
/*   
*-----------------------------------------------------------------------------------------------------
*  Variable Initialized
*------------------------------------------------------------------------------------------------------
*/
	SET pitRowCount  = 0;
	SELECT id INTO litIngredientId FROM ingredient WHERE CODE = pcrCode;
	SELECT id INTO litTaxId FROM tax WHERE CODE = pitTaxCode;
	
/*
*-----------------------------------------------------------------------------------------------------
*  Increment Last Sequence Number
*------------------------------------------------------------------------------------------------------
*/	
	IF ( pitActiveFlag = 1 AND litIngredientId != 0 AND EZEE_FN_ISNOTNULL(pcrCode)) THEN
		UPDATE ingredient SET NAME = pcrName, unit_qty = pdlUnitQty, unit = pcrUnit, unit_cost = pdlUnitCost, tax_id = litTaxId, active_flag = pitActiveFlag, updated_by = pitUpdatedBy , updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
		
	ELSEIF ( pitActiveFlag = 1 AND litIngredientId = 0) THEN
		INSERT INTO ingredient ( CODE, NAME, unit_qty, unit, unit_cost, tax_id, active_flag, updated_by, updated_at) VALUES ( pcrCode, pcrName, pdlUnitQty, pcrUnit, pdlUnitCost, litTaxId, pitActiveFlag, pitUpdatedBy, NOW());
		SELECT ROW_COUNT() INTO pitRowCount;
		
	ELSEIF ( pitActiveFlag = 1 AND EZEE_FN_ISNOTNULL(pcrCode) ) THEN
		UPDATE ingredient SET active_flag = pitActiveFlag, updated_by = pitUpdatedBy , updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
	END IF;
END$$

DELIMITER ;