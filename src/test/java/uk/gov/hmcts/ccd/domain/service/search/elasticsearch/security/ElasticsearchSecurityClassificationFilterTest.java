package uk.gov.hmcts.ccd.domain.service.search.elasticsearch.security;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ccd.data.casedetails.CaseDetailsEntity.SECURITY_CLASSIFICATION_FIELD_COL;
import static uk.gov.hmcts.ccd.data.casedetails.SecurityClassification.PRIVATE;
import static uk.gov.hmcts.ccd.data.casedetails.SecurityClassification.PUBLIC;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.ccd.data.user.UserRepository;
import uk.gov.hmcts.ccd.domain.model.definition.CaseType;
import uk.gov.hmcts.ccd.domain.model.definition.Jurisdiction;
import uk.gov.hmcts.ccd.domain.service.common.CaseTypeService;

class ElasticsearchSecurityClassificationFilterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CaseTypeService caseTypeService;

    @InjectMocks
    private ElasticsearchSecurityClassificationFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void shouldCreateTermsQueryBuilder() {
        String caseTypeId = "caseType";
        String jurisdictionId = "jurisdiction";
        when(userRepository.getHighestUserClassification(jurisdictionId)).thenReturn(PRIVATE);
        CaseType caseType = new CaseType();
        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setId(jurisdictionId);
        caseType.setJurisdiction(jurisdiction);
        when(caseTypeService.getCaseType(caseTypeId)).thenReturn(caseType);

        Optional<QueryBuilder> optQueryBuilder = filter.getFilter(caseTypeId);

        assertThat(optQueryBuilder.isPresent(), is(true));
        assertThat(optQueryBuilder.get(), instanceOf(TermsQueryBuilder.class));
        TermsQueryBuilder queryBuilder = (TermsQueryBuilder) optQueryBuilder.get();
        assertThat(queryBuilder.fieldName(), is(SECURITY_CLASSIFICATION_FIELD_COL));
        assertThat(queryBuilder.values(), hasItems(PUBLIC.name(), PRIVATE.name()));
        verify(userRepository).getHighestUserClassification(jurisdictionId);
        verify(caseTypeService).getCaseType(caseTypeId);
    }

}
