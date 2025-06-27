DROP FUNCTION IF EXISTS EZEE_FN_ISNOTNULL;

CREATE FUNCTION EZEE_FN_ISNOTNULL(pcrString TEXT) RETURNS BOOLEAN
DETERMINISTIC          
NO SQL
/*
* Procedure Name       : EZEE_FN_ISNOTNULL
* 
* Purpose              : Compress the Numberic Char To String. 
*
* Input                : Nemeric String
*
* Output               : Status
* 						 Sequence Code
*
* Returns              : 0 - Successful
*                        1 - Unsuccessful
*
* Dependencies
*
*     Tables           : none
*
*     Functions        : None
*
*     Procedures       : none
*
* Revision History:
*
*     1.0 - 2025/06/19	      Ezee Info 
*     EzeeTech                 Original Code
*
*/

	RETURN	IF(ISNULL(pcrString) OR TRIM(pcrString) = '' OR TRIM(pcrString) = 'null'  OR TRIM(pcrString) = 'NULL'  OR TRIM(pcrString) = 'NA' ,FALSE,TRUE);