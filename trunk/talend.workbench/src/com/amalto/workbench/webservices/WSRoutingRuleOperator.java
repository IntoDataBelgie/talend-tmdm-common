// This class was generated by the JAXRPC SI, do not edit.
// Contents subject to change without notice.
// JAX-RPC Standard Implementation ��1.1.2_01������� R40��
// Generated source version: 1.1.2

package com.amalto.workbench.webservices;


import java.util.Map;
import java.util.HashMap;

public class WSRoutingRuleOperator {
    private java.lang.String value;
    private static Map valueMap = new HashMap();
    public static final String _CONTAINSString = "CONTAINS";
    public static final String _MATCHESString = "MATCHES";
    public static final String _STARTSWITHString = "STARTSWITH";
    public static final String _EQUALSString = "EQUALS";
    public static final String _NOT_EQUALSString = "NOT_EQUALS";
    public static final String _GREATER_THANString = "GREATER_THAN";
    public static final String _GREATER_THAN_OR_EQUALString = "GREATER_THAN_OR_EQUAL";
    public static final String _LOWER_THANString = "LOWER_THAN";
    public static final String _LOWER_THAN_OR_EQUALString = "LOWER_THAN_OR_EQUAL";
    public static final String _IS_NULLString = "IS_NULL";
    public static final String _IS_NOT_NULLString = "IS_NOT_NULL";
    
    public static final java.lang.String _CONTAINS = new java.lang.String(_CONTAINSString);
    public static final java.lang.String _MATCHES = new java.lang.String(_MATCHESString);
    public static final java.lang.String _STARTSWITH = new java.lang.String(_STARTSWITHString);
    public static final java.lang.String _EQUALS = new java.lang.String(_EQUALSString);
    public static final java.lang.String _NOT_EQUALS = new java.lang.String(_NOT_EQUALSString);
    public static final java.lang.String _GREATER_THAN = new java.lang.String(_GREATER_THANString);
    public static final java.lang.String _GREATER_THAN_OR_EQUAL = new java.lang.String(_GREATER_THAN_OR_EQUALString);
    public static final java.lang.String _LOWER_THAN = new java.lang.String(_LOWER_THANString);
    public static final java.lang.String _LOWER_THAN_OR_EQUAL = new java.lang.String(_LOWER_THAN_OR_EQUALString);
    public static final java.lang.String _IS_NULL = new java.lang.String(_IS_NULLString);
    public static final java.lang.String _IS_NOT_NULL = new java.lang.String(_IS_NOT_NULLString);
    
    public static final WSRoutingRuleOperator CONTAINS = new WSRoutingRuleOperator(_CONTAINS);
    public static final WSRoutingRuleOperator MATCHES = new WSRoutingRuleOperator(_MATCHES);
    public static final WSRoutingRuleOperator STARTSWITH = new WSRoutingRuleOperator(_STARTSWITH);
    public static final WSRoutingRuleOperator EQUALS = new WSRoutingRuleOperator(_EQUALS);
    public static final WSRoutingRuleOperator NOT_EQUALS = new WSRoutingRuleOperator(_NOT_EQUALS);
    public static final WSRoutingRuleOperator GREATER_THAN = new WSRoutingRuleOperator(_GREATER_THAN);
    public static final WSRoutingRuleOperator GREATER_THAN_OR_EQUAL = new WSRoutingRuleOperator(_GREATER_THAN_OR_EQUAL);
    public static final WSRoutingRuleOperator LOWER_THAN = new WSRoutingRuleOperator(_LOWER_THAN);
    public static final WSRoutingRuleOperator LOWER_THAN_OR_EQUAL = new WSRoutingRuleOperator(_LOWER_THAN_OR_EQUAL);
    public static final WSRoutingRuleOperator IS_NULL = new WSRoutingRuleOperator(_IS_NULL);
    public static final WSRoutingRuleOperator IS_NOT_NULL = new WSRoutingRuleOperator(_IS_NOT_NULL);
    
    protected WSRoutingRuleOperator(java.lang.String value) {
        this.value = value;
        valueMap.put(this.toString(), this);
    }
    
    public java.lang.String getValue() {
        return value;
    }
    
    public static WSRoutingRuleOperator fromValue(java.lang.String value)
        throws java.lang.IllegalStateException {
        if (CONTAINS.value.equals(value)) {
            return CONTAINS;
        } else if (MATCHES.value.equals(value)) {
            return MATCHES;
        } else if (STARTSWITH.value.equals(value)) {
            return STARTSWITH;
        } else if (EQUALS.value.equals(value)) {
            return EQUALS;
        } else if (NOT_EQUALS.value.equals(value)) {
            return NOT_EQUALS;
        } else if (GREATER_THAN.value.equals(value)) {
            return GREATER_THAN;
        } else if (GREATER_THAN_OR_EQUAL.value.equals(value)) {
            return GREATER_THAN_OR_EQUAL;
        } else if (LOWER_THAN.value.equals(value)) {
            return LOWER_THAN;
        } else if (LOWER_THAN_OR_EQUAL.value.equals(value)) {
            return LOWER_THAN_OR_EQUAL;
        } else if (IS_NULL.value.equals(value)) {
            return IS_NULL;
        } else if (IS_NOT_NULL.value.equals(value)) {
            return IS_NOT_NULL;
        }
        throw new IllegalArgumentException();
    }
    
    public static WSRoutingRuleOperator fromString(String value)
        throws java.lang.IllegalStateException {
        WSRoutingRuleOperator ret = (WSRoutingRuleOperator)valueMap.get(value);
        if (ret != null) {
            return ret;
        }
        if (value.equals(_CONTAINSString)) {
            return CONTAINS;
        } else if (value.equals(_MATCHESString)) {
            return MATCHES;
        } else if (value.equals(_STARTSWITHString)) {
            return STARTSWITH;
        } else if (value.equals(_EQUALSString)) {
            return EQUALS;
        } else if (value.equals(_NOT_EQUALSString)) {
            return NOT_EQUALS;
        } else if (value.equals(_GREATER_THANString)) {
            return GREATER_THAN;
        } else if (value.equals(_GREATER_THAN_OR_EQUALString)) {
            return GREATER_THAN_OR_EQUAL;
        } else if (value.equals(_LOWER_THANString)) {
            return LOWER_THAN;
        } else if (value.equals(_LOWER_THAN_OR_EQUALString)) {
            return LOWER_THAN_OR_EQUAL;
        } else if (value.equals(_IS_NULLString)) {
            return IS_NULL;
        } else if (value.equals(_IS_NOT_NULLString)) {
            return IS_NOT_NULL;
        }
        throw new IllegalArgumentException();
    }
    
    public String toString() {
        return value.toString();
    }
    
    private Object readResolve()
        throws java.io.ObjectStreamException {
        return fromValue(getValue());
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof WSRoutingRuleOperator)) {
            return false;
        }
        return ((WSRoutingRuleOperator)obj).value.equals(value);
    }
    
    public int hashCode() {
        return value.hashCode();
    }
}
