/** \file
 * 
 * Jun 7, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package util;

import com.amazonaws.regions.Regions;

public interface IDynamoDB {
 // The AWS region for the DynamoDB instance.
    // Frankfurt Germany
    final static Regions region = Regions.EU_CENTRAL_1;
    // Read-only IAM keys for DynamoDB
    public final static String dynamoDBReadOnlyID = "Your DynamoDB ReadOnly ID goes here";
    public final static String dynamoDBReadOnlyKey = "Your DynamoDB ReadOnly Secret Key goes here";
}
