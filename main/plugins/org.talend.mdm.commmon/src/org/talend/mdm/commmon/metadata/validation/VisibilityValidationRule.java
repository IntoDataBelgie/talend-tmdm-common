package org.talend.mdm.commmon.metadata.validation;

import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.metadata.*;
import org.w3c.dom.Element;

/**
 * This rule checks whether a field is mandatory (field or all parents). If there's a visibility rule UI may
 */
public class VisibilityValidationRule implements ValidationRule {

    private final FieldMetadata field;

    public VisibilityValidationRule(FieldMetadata field) {
        this.field = field;
    }

    @Override
    public boolean perform(ValidationHandler handler) {
        if(StringUtils.isNotEmpty(field.getVisibilityRule()) && isMandatory(field)) {
            handler.warning(field, "Mandatory field may not visible during record edition dur to visibility rule.",
                    field.<Element>getData(MetadataRepository.XSD_DOM_ELEMENT),
                    field.<Integer>getData(MetadataRepository.XSD_LINE_NUMBER),
                    field.<Integer>getData(MetadataRepository.XSD_COLUMN_NUMBER),
                    ValidationError.MANDATORY_FIELD_MAY_NOT_BE_VISIBLE);
        }
        return true;
    }

    private static boolean isMandatory(FieldMetadata field) {
        if(field.isMandatory()) {
            return true;
        }
        ComplexTypeMetadata entity = field.getContainingType().getEntity();
        ComplexTypeMetadata containingType = field.getContainingType();
        while(!containingType.equals(entity)) {
            FieldMetadata container = containingType.getContainer();
            if(!container.isMandatory()) {
                return false;
            }
            containingType = (ComplexTypeMetadata) container.getType();
        }
        return true;
    }

    @Override
    public boolean continueOnFail() {
        return false;
    }
}
