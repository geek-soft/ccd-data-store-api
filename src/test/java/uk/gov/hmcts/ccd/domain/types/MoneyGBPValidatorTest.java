package uk.gov.hmcts.ccd.domain.types;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.ccd.data.definition.CaseDefinitionRepository;
import uk.gov.hmcts.ccd.domain.model.definition.CaseField;
import uk.gov.hmcts.ccd.test.CaseFieldBuilder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@DisplayName("MoneyGBPValidator")
class MoneyGBPValidatorTest {
    private static final String FIELD_ID = "TEST_FIELD_ID";
    private static final JsonNodeFactory NODE_FACTORY = JsonNodeFactory.instance;

    @Mock
    private BaseType moneyGbpBaseType;

    @Mock
    private CaseDefinitionRepository definitionRepository;

    private MoneyGBPValidator validator;
    private CaseField caseField;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(definitionRepository.getBaseTypes()).thenReturn(Collections.emptyList());
        BaseType.setCaseDefinitionRepository(definitionRepository);
        BaseType.initialise();

        when(moneyGbpBaseType.getType()).thenReturn(MoneyGBPValidator.TYPE_ID);
        BaseType.register(moneyGbpBaseType);

        validator = new MoneyGBPValidator();

        caseField = caseField().build();
    }

    @Test
    void validMoney() {
        final List<ValidationResult> result01 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("177978989700"),
                                                                   caseField);
        assertEquals(0, result01.size());

        final List<ValidationResult> result02 = validator.validate(FIELD_ID, NODE_FACTORY.textNode("-100"), caseField);
        assertEquals(0, result02.size());
    }

    @Test
    void nullMoney() {
        final List<ValidationResult> result01 = validator.validate(FIELD_ID, null, caseField);
        assertEquals(0, result01.size(), "Did not catch null");
    }

    @Test
    void invalidMoney() {
        final List<ValidationResult> result01 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("3321M1 1AA"),
                                                                   caseField);
        assertEquals(1, result01.size(), result01.toString());

        final List<ValidationResult> result02 = validator.validate(FIELD_ID, NODE_FACTORY.textNode("100.1"), caseField);
        assertEquals(1, result01.size(), result02.toString());
    }

    @Test
    void checkMaxMin_BothBelow1GBP() {
        final CaseField minMaxCaseField = caseField().withMin(5)
                                                     .withMax(10)
                                                     .build();

        // Test valid max min
        final List<ValidationResult> result01 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("5"),
                                                                   minMaxCaseField);
        assertEquals(0, result01.size());

        final List<ValidationResult> result02 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("10"),
                                                                   minMaxCaseField);
        assertEquals(0, result02.size());

        final List<ValidationResult> result03 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("7"),
                                                                   minMaxCaseField);
        assertEquals(0, result03.size());

        // Test invalid max min
        final List<ValidationResult> result04 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("4"),
                                                                   minMaxCaseField);
        assertEquals(1, result04.size());
        assertEquals(result04.get(0).getErrorMessage(), "Should be more than or equal to £0.05");

        final List<ValidationResult> result05 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("11"),
                                                                   minMaxCaseField);
        assertEquals(1, result05.size());
        assertEquals(result05.get(0).getErrorMessage(), "Should be less than or equal to £0.10");
    }

    @Test
    void checkMaxMin_BothAbove1GBP() {
        final CaseField minMaxCaseField = caseField().withMin(123)
                                                     .withMax(123456)
                                                     .build();

        // Test invalid max min
        final List<ValidationResult> result01 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("123457"),
                                                                   minMaxCaseField);
        assertEquals(1, result01.size());
        assertEquals(result01.get(0).getErrorMessage(), "Should be less than or equal to £1,234.56");

        final List<ValidationResult> result02 = validator.validate(FIELD_ID,
                                                                   NODE_FACTORY.textNode("122"),
                                                                   minMaxCaseField);
        assertEquals(1, result02.size());
        assertEquals(result02.get(0).getErrorMessage(), "Should be more than or equal to £1.23");
    }

    @Test
    void getType() {
        assertEquals(validator.getType(), BaseType.get("MoneyGBP"), "Type is incorrect");
    }

    private CaseFieldBuilder caseField() {
        return new CaseFieldBuilder(FIELD_ID).withType(MoneyGBPValidator.TYPE_ID);
    }
}
