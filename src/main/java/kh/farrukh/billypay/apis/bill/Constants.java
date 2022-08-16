package kh.farrukh.billypay.apis.bill;

import static kh.farrukh.billypay.apis.user.Constants.ENDPOINT_USER;

public class Constants {

    public static final String ENDPOINT_BILL = "/api/v1/users/{userId}/bills";
    public static final String SECURITY_ENDPOINT_BILL = ENDPOINT_USER + "/**/bills";
    public static final String SEQUENCE_NAME_BILL_ID = "bill_id_seq";
    public static final String TABLE_NAME_BILL = "bill";

}
