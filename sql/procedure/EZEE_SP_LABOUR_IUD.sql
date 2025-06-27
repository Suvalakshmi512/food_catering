DROP PROCEDURE IF EXISTS EZEE_SP_LABOUR_IUD;
DELIMITER $$
CREATE PROCEDURE EZEE_SP_LABOUR_IUD(IN pcrCode VARCHAR(15), IN pcrName VARCHAR(45), IN pcrRoleName VARCHAR(60), IN pdlHourslySalary DECIMAL(10,2), IN pcrSpecialist VARCHAR(40),IN pitActiveFlag TINYINT, IN pitUpdatedBy VARCHAR(12), OUT pitRowCount INT)
/*
* Procedure Name       : EZEE_SP_LABOUR_IUD
* 
* Purpose              : Insert /Update /Delete labour Table
*
* Input                : None
*
* Output               : Affected Rows
*
* Returns              : None
*
* Dependencies
*
*     Tables           : labour
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
*/  DECLARE litLabourId INT DEFAULT 0;
/*
*-----------------------------------------------------------------------------------------------------
*  Variable Initialized
*------------------------------------------------------------------------------------------------------
*/
	SET pitRowCount  = 0;
	SELECT id INTO litLabourId FROM labour WHERE CODE = pcrCode;
	
/*
*-----------------------------------------------------------------------------------------------------
*  Increment Last Sequence Number
*------------------------------------------------------------------------------------------------------
*/	
	IF ( pitActiveFlag = 1 AND litLabourId != 0 AND EZEE_FN_ISNOTNULL(pcrCode)) THEN
		UPDATE labour SET NAME = pcrName, role_name = pcrRoleName, hoursly_salary = pdlHourslySalary, specialization = pcrSpecialist, active_flag = pitActiveFlag, updated_by = pitUpdatedBy , updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
		
	ELSEIF ( pitActiveFlag = 1 AND litLabourId = 0 ) THEN
		INSERT INTO labour ( CODE, NAME, role_name, hoursly_salary, specialization, active_flag, updated_by, updated_at) VALUES ( pcrCode, pcrName, pcrRoleName, pdlHourslySalary, pcrSpecialist, pitActiveFlag, pitUpdatedBy, NOW());
		SELECT ROW_COUNT() INTO pitRowCount;
	ELSEIF ( pitActiveFlag = 1 AND EZEE_FN_ISNOTNULL(pcrCode) ) THEN
		UPDATE labour SET active_flag = pitActiveFlag, updated_by = pitUpdatedBy , updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
	END IF;
END$$

DELIMITER ;