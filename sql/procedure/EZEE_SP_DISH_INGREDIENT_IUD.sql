DROP PROCEDURE IF EXISTS EZEE_SP_DISH_INGREDIENT_IUD;
DELIMITER $$
CREATE PROCEDURE EZEE_SP_DISH_INGREDIENT_IUD (IN  pcrCode VARCHAR(15),IN  pcrDishCode VARCHAR(15),IN  pcrIngredientCode VARCHAR(15), IN pdcQtyUsed DECIMAL(10,3), IN  pdcWastagePct DECIMAL(5,2), IN  pitActiveFlag TINYINT, IN  pcrUpdatedBy VARCHAR(20), OUT pitRowCount INT )
/*
* Procedure Name       : EZEE_SP_DISH_INGREDIENT_IUD
* 
* Purpose              : Insert /Update /Delete dish_ingredient Table
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
  /* ------------------------------------------------------------
   * Local variables
   * ---------------------------------------------------------- */
  DECLARE litdishIngeridentId INT DEFAULT 0;         
  DECLARE litDishId         INT DEFAULT 0;
  DECLARE litIngredientId   INT DEFAULT 0;
  DECLARE ldcUnitCost       DECIMAL(10,3) DEFAULT 0;
  DECLARE ldcTaxPct         DECIMAL(5,2)  DEFAULT 0;
  DECLARE ldcPurchaseQty    DECIMAL(10,3);
  DECLARE ldcBaseCost       DECIMAL(10,3);
  DECLARE ldcTaxAmount      DECIMAL(10,3);
  DECLARE litTaxId          INT;
  DECLARE ldcFinalPrice     DECIMAL(10,2);
  
  /* ------------------------------------------------------------
   * Variable Initialized
   * ---------------------------------------------------------- */
   
   SET pitRowCount = 0;
  SELECT id INTO litDishId
    FROM dish
   WHERE CODE = pcrDishCode
   LIMIT 1;
   
   SELECT id, unit_cost, tax_id
    INTO litIngredientId, ldcUnitCost, litTaxId
    FROM ingredient
   WHERE CODE = pcrIngredientCode
   LIMIT 1;
   
   SELECT rate_pct INTO ldcTaxPct
   FROM tax WHERE id = litTaxId;

  SELECT id INTO litdishIngeridentId
    FROM dish_ingredient
   WHERE CODE = pcrCode
   LIMIT 1;
   
   /* ------------------------------------------------------------
   * Cost calculation (same for insert & update)
   * ---------------------------------------------------------- */
  SET ldcPurchaseQty =   pdcQtyUsed * 100.0
                       / (100.0 - IFNULL(pdcWastagePct,0));
  SET ldcBaseCost    = ldcPurchaseQty * ldcUnitCost;
  SET ldcTaxAmount   = ldcBaseCost * ldcTaxPct / 100.0;
  SET ldcFinalPrice   = ldcBaseCost + ldcTaxAmount;  
  /*
*-----------------------------------------------------------------------------------------------------
*  Increment Last Sequence Number
*------------------------------------------------------------------------------------------------------
*/	
	IF ( pitActiveFlag = 1 AND litdishIngeridentId != 0 AND EZEE_FN_ISNOTNULL(pcrCode)) THEN
	UPDATE dish_ingredient SET dish_id = litDishId, ingredient_id  = litIngredientId, quantity_used  = pdcQtyUsed, wastage = pdcWastagePct, price = ldcFinalPrice, active_flag = pitActiveFlag , updated_by = pcrUpdatedBy, updated_at = NOW() WHERE CODE = pcrCode;
	SELECT ROW_COUNT() INTO pitRowCount;
		
	ELSEIF ( pitActiveFlag = 1 AND litdishIngeridentId = 0) THEN
		INSERT INTO dish_ingredient (CODE, dish_id, ingredient_id, quantity_used, wastage, price, active_flag, updated_by, updated_at) VALUES (pcrCode, litDishId, litIngredientId, pdcQtyUsed, pdcWastagePct, ldcFinalPrice, 1, pcrUpdatedBy, NOW());
		SELECT ROW_COUNT() INTO pitRowCount;
		
	ELSEIF ( pitActiveFlag != 1 AND EZEE_FN_ISNOTNULL(pcrCode) ) THEN
		UPDATE ingredient SET active_flag = pitActiveFlag, updated_by = pitUpdatedBy , updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
	END IF;
END$$

DELIMITER ; 
