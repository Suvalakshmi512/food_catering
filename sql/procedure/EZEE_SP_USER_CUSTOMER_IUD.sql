DROP PROCEDURE IF EXISTS EZEE_SP_USER_CUSTOMER_IUD;
DELIMITER $$
CREATE PROCEDURE EZEE_SP_USER_CUSTOMER_IUD(IN pcrCode VARCHAR(15), IN pcrName VARCHAR(45), IN pcrEmail VARCHAR(60), IN pcrMobile VARCHAR(12),IN	pitActiveFlag TINYINT, IN pitUpdatedBy VARCHAR(12), OUT pitRowCount INT)
/*
* Procedure Name       : EZEE_SP_USER_CUSTOMER_IUD
* 
* Purpose              : Insert /Update /Delete user_customer Table
*
* Input                : None
*
* Output               : Affected Rows
*
* Returns              : None
*
* Dependencies
*
*     Tables           : user_customer
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
*/  DECLARE litCustomerId INT DEFAULT 0;
/*
*-----------------------------------------------------------------------------------------------------
*  Variable Initialized
*------------------------------------------------------------------------------------------------------
*/
	SET pitRowCount  = 0;
	select id into litCustomerId from user_customer where code = pcrCode;
	
/*
*-----------------------------------------------------------------------------------------------------
*  Increment Last Sequence Number
*------------------------------------------------------------------------------------------------------
*/	
	if ( pitActiveFlag = 1 and litCustomerId != 0 and EZEE_FN_ISNOTNULL(pcrCode)) then
		update user_customer set name = pcrName, mobile = pcrMobile, email = pcrEmail, active_flag = pitActiveFlag, updated_by = pitUpdatedBy , updated_at = now() where code = pcrCode;
		select Row_Count() into pitRowCount;
		
	elseif ( pitActiveFlag = 1 AND litCustomerId = 0) then
		insert into user_customer ( code, name, mobile, email, active_flag, updated_by, updated_at) values ( pcrCode, pcrName, pcrMobile, pcrEmail, pitActiveFlag, pitUpdatedBy, now());
		SELECT ROW_COUNT() INTO pitRowCount;
	elseif ( pitActiveFlag = 1 and EZEE_FN_ISNOTNULL(pcrCode) ) then
		update user_customer set active_flag = pitActiveFlag, updated_by = pitUpdatedBy , updated_at = NOW() where code = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
	END IF;
END$$

DELIMITER ;