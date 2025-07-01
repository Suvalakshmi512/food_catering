DROP PROCEDURE IF EXISTS EZEE_SP_DISH_LIST_IUD;
DELIMITER $$
CREATE PROCEDURE EZEE_SP_DISH_LIST_IUD ( IN  pcrCode VARCHAR(15), pcrMenuCode VARCHAR(15), IN  pcrDishCode VARCHAR(15), IN pitActiveFlag INT, IN pcrUpdatedBy VARCHAR(20), OUT pitRowCount INT )
/*
* Procedure Name       : EZEE_SP_DISH_LABOUR_IUD
* 
* Purpose              : Insert /Update /Delete dish_list Table
*
* Input                : None
*
* Output               : Affected Rows
*
* Returns              : None
*
* Dependencies
*
*     Tables           : dish_list
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
    DECLARE ldlprice DECIMAL(10,2);
    DECLARE litDishListId INT DEFAULT 0;
    DECLARE litDishId INT DEFAULT 0;
    DECLARE litMenuId INT DEFAULT 0;
    
    /* ------------------------------------------------------------
   * Variable Initialized
   * ---------------------------------------------------------- */
    SET pitRowCount = 0;
    SELECT id INTO litDishId
    FROM dish
    WHERE CODE = pcrDishCode;
    
    SELECT id INTO litMenuId
    FROM menu
    WHERE CODE = pcrMenuCode;
    
    SELECT id INTO litDishListId
    FROM dish_list
    WHERE CODE = pcrCode;
    
    SELECT price INTO ldlprice
    FROM dish 
    WHERE id = litDishId;
    
/*
*-----------------------------------------------------------------------------------------------------
*  Increment Last Sequence Number
*------------------------------------------------------------------------------------------------------
*/

        IF ( pitActiveFlag = 1 AND litDishListId != 0 AND EZEE_FN_ISNOTNULL(pcrCode)) THEN
		UPDATE dish_list SET menu_id = litMenuId, dish_id = litDishId, unit_price = ldlprice, active_flag = pitActiveFlag, updated_by = pcrUpdatedBy, updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
		
	ELSEIF ( pitActiveFlag = 1 AND litDishListId = 0) THEN
		INSERT INTO dish_list (CODE, menu_id, dish_id, unit_price, active_flag, updated_by, updated_at) VALUES (pcrCode, litMenuId, litDishId, ldlprice, pitActiveFlag, pcrUpdatedBy, NOW());
		SELECT ROW_COUNT() INTO pitRowCount;
		
	ELSEIF ( pitActiveFlag != 1 AND EZEE_FN_ISNOTNULL(pcrCode) ) THEN
		UPDATE dish_list SET active_flag = pitActiveFlag, updated_by = pcrUpdatedBy , updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
	END IF;
END$$

DELIMITER ; 
