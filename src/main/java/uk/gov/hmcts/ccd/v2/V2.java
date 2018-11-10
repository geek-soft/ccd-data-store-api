package uk.gov.hmcts.ccd.v2;

public final class V2 {

    public static final String EXPERIMENTAL_HEADER = "experimental";
    public static final String EXPERIMENTAL_WARNING = "Experimental! Subject to change or removal, do not use in production!";

    public final class MediaType {
        // External API
        public static final String CASE = "application/vnd.uk.gov.hmcts.ccd-data-store-api.case.v2+json;charset=UTF-8";

        // Internal API
        public static final String UI_CASE_VIEW = "application/vnd.uk.gov.hmcts.ccd-data-store-api.ui-case-view.v2+json;charset=UTF-8";
    }

    public final class Error {
        public static final String CASE_NOT_FOUND = "Case not found";
        public static final String CASE_ID_INVALID = "Case ID is not valid";
        public static final String CASE_ROLE_REQUIRED = "Case role missing";
        public static final String CASE_ROLE_INVALID = "Case role does not exist";
    }
}
