DROP FUNCTION IF EXISTS EZEE_FN_PRICE_FOR_DISH;
DELIMITER $$
CREATE FUNCTION EZEE_FN_PRICE_FOR_DISH (pitDish_id INT) RETURNS DECIMAL(10,2)
DETERMINISTIC
READS SQL DATA
/*
* Procedure Name       : EZEE_FN_PRICE_FOR_DISH
* 
* Purpose              : Calculate the dish price. 
*
* Input                : Entity Type
*
* Output               : None
*
* Returns              : price
*
* Dependencies
*
*     Tables           : None
*
*     Functions        : 
*
*     Procedures       : None
*
* Revision History:
*
*     1.0 - 2025/06/19	      Ezee Info 
*     EzeeTech                 Original Code
*
*/
BEGIN

/*
*----------------------------------------------------------------------------------------------------
* Variable Declare
*-----------------------------------------------------------------------------------------------------
*/
    DECLARE ldlIngCost DECIMAL(10,2) DEFAULT 0;
    DECLARE ldlLabourCost DECIMAL(10,2) DEFAULT 0;
    DECLARE ldlMarginCost   DECIMAL(5,2);
    DECLARE ldlTaxCost      DECIMAL(5,2);
    DECLARE ldlCost     DECIMAL(10,2);
    DECLARE litMinQuantity INT;
    DECLARE litServingSize INT;
 
/*
*-----------------------------------------------------------------------------------------------------
*  Variable Initialized
*------------------------------------------------------------------------------------------------------
*/

    /* ingredient cost (price is already stored) */
    SELECT IFNULL(SUM(price),0)
      INTO ldlIngCost
      FROM dish_ingredient
     WHERE dish_id = pitDish_id
       AND active_flag = '1';

    /* labour cost */
    SELECT IFNULL(SUM(dl.hours_required * l.hoursly_salary),0)
      INTO ldlLabourCost
      FROM dish_labour dl
      JOIN labour l ON l.id = dl.labour_id
     WHERE dl.dish_id = pitDish_id
       AND dl.active_flag = '1';

    /* margin and tax */
    SELECT d.margin_profit , t.rate_pct
      INTO ldlMarginCost   , ldlTaxCost
      FROM dish d
      JOIN tax  t ON t.id = d.tax_id
     WHERE d.id = pitDish_id;
     
     
     SELECT min_available_qty , serving_size INTO litMinQuantity , litServingSize
     FROM dish 
     WHERE id = pitDish_id;

    SET ldlCost = ldlIngCost + ldlLabourCost;
    SET ldlCost = ldlCost + (ldlCost * ldlMarginCost / 100);
    SET ldlCost = ldlCost + (ldlCost * ldlTaxCost    / 100);
    SET ldlCost = (ldlCost / litMinQuantity) * litServingSize;
   

    RETURN ROUND(ldlCost,2);
END$$

DELIMITER ;

