/** \file
 * 
 * Apr 25, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package util;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

/**
 * <h4>
 * DynamoDBUtil
 * </h4>
 * <p>
 * Useful functions for dealing with DynamoDB
 * </p>
 * Apr 25, 2018
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public class DynamoDBUtil {


    /**
     * Use the "setter" 
     * @param methods
     * @param fieldName
     * @param attrVal
     * @throws ReflectiveOperationException 
     */
    private static void setValue(Object obj, String fieldName, String attrValStr) throws ReflectiveOperationException {
        String fieldNameLC = fieldName.toLowerCase();
        Method[] methods = obj.getClass().getMethods();
        for (Method m : methods) {
            String methodName = m.getName().toLowerCase();
            if (methodName.startsWith("set")) {
                if (methodName.endsWith(fieldNameLC)) {
                    m.invoke(obj, attrValStr);
                }
            }
        }
    } // setValue
    
    /**
     * <p>
     * Populate an object with values from a DynamoDB query.
     * </p>
     * <p>
     * Some flavors of DynamoDB queries return a list of attributes that correspond to values in the
     * DynamoDB table row. These values are used to populate the object argument. The IMPORTANT assumption
     * here is that the object that is being populated is mapped to the DynamoDB table AND that the object
     * contains fields and getters and setters that correspond to the attribute names. 
     * </p>
     * <p>
     * For example, if the DynamoDB table has an attribute named "author" this code assumes that there is a
     * setter with the name setAuthor().  Further, it is assumed that all attribute values are String values.
     * This requirement is imposed by the fact that a specific get[Type] method has to be used for the DynamoDB
     * attribute.
     * </p>
     * <p>
     * If the class meets the criteria outlined above, the "obj" argument will be populated with values
     * from the attribute map.
     * </p>
     * @param obj
     * @param dynamoDBAttributes the DynamoDB attribute map for that DynamoDB row
     * @throws ReflectiveOperationException 
     */
    public static void attributesToObject(Object obj, Map<String, AttributeValue> dynamoDBAttributes) 
            throws ReflectiveOperationException {
        if (obj != null && dynamoDBAttributes != null) {
            Set<String> fieldNames = dynamoDBAttributes.keySet();
            if (fieldNames.size() > 0) {
                for (String fieldName : fieldNames) {
                    AttributeValue attrVal = dynamoDBAttributes.get(fieldName);
                    String attrValStr = attrVal.getS();
                    setValue(obj, fieldName, attrValStr );
                } // for
            }
        }
    }

}
