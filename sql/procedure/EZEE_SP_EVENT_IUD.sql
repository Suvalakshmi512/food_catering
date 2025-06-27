DROP PROCEDURE IF EXISTS EZEE_SP_EVENT_IUD;
DELIMITER $$
CREATE PROCEDURE EZEE_SP_EVENT_IUD(IN pcrCode VARCHAR(15), IN pcrName VARCHAR(45), IN pcrCustomerCode VARCHAR(15), IN pdtEventDate DATE, IN ptTime TIME, IN pcrVenue VARCHAR(200), IN pitGuestCount INT,IN pitActiveFlag TINYINT, IN pitUpdatedBy VARCHAR(12), OUT pitRowCount INT)
/*
* Procedure Name       : EZEE_SP_EVENT_IUD
* 
* Purpose              : Insert /Update /Delete event Table
*
* Input                : None
*
* Output               : Affected Rows
*
* Returns              : None
*
* Dependencies
*
*     Tables           : event
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
*/  DECLARE litEventId INT DEFAULT 0;
    DECLARE litCustomerIt INT DEFAULT 0;
/*
*-----------------------------------------------------------------------------------------------------
*  Variable Initialized
*------------------------------------------------------------------------------------------------------
*/
	SET pitRowCount  = 0;
	SELECT id INTO litEventId FROM EVENT WHERE CODE = pcrCode;
	SELECT id INTO litCustomerIt FROM user_customer WHERE CODE = pcrCustomerCode;
/*
*-----------------------------------------------------------------------------------------------------
*  Increment Last Sequence Number
*------------------------------------------------------------------------------------------------------
*/	
	IF ( pitActiveFlag = 1 AND litEventId != 0 AND EZEE_FN_ISNOTNULL(pcrCode)) THEN
		UPDATE EVENT SET NAME = pcrName, customer_id = litCustomerIt, event_date = pdtEventDate, event_time = ptTime, venue = pcrVenue, guest_count =pitGuestCount, active_flag = pitActiveFlag, updated_by = pitUpdatedBy , updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
		
	ELSEIF ( pitActiveFlag = 1 AND litEventId = 0) THEN
		INSERT INTO EVENT ( CODE, NAME, customer_id, event_date, event_time, venue, guest_count, active_flag, updated_by, updated_at) VALUES ( pcrCode, pcrName, litCustomerIt, pdtEventDate, ptTime, pcrVenue, pitGuestCount, pitActiveFlag, pitUpdatedBy, NOW());
		SELECT ROW_COUNT() INTO pitRowCount;
		
	ELSEIF ( pitActiveFlag = 1 AND EZEE_FN_ISNOTNULL(pcrCode) ) THEN
		UPDATE EVENT SET active_flag = pitActiveFlag, updated_by = pitUpdatedBy , updated_at = NOW() WHERE CODE = pcrCode;
		SELECT ROW_COUNT() INTO pitRowCount;
	END IF;
END$$

DELIMITER ;