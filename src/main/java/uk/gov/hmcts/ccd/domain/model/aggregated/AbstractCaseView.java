package uk.gov.hmcts.ccd.domain.model.aggregated;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public abstract class AbstractCaseView {

    @JsonProperty("case_id")
    private String caseId;
    @JsonProperty("case_type")
    private CaseViewType caseType;
    private CaseViewTab[] tabs;

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public CaseViewType getCaseType() {
        return caseType;
    }

    public void setCaseType(CaseViewType caseType) {
        this.caseType = caseType;
    }

    public CaseViewTab[] getTabs() {
        return tabs;
    }

    public void setTabs(CaseViewTab[] tabs) {
        this.tabs = tabs;
    }

    @JsonIgnore
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
