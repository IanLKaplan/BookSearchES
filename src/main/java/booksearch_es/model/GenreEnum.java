/** \file
 * 
 * Apr 24, 2018
 *
 * Copyright Ian Kaplan 2018
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package booksearch_es.model;

/**
 * <h4>
 * GenreEnum
 * </h4>
 * <p>
 * An enumeration for book genre.
 * </p>
 * May 7, 2018
 * 
 * @author Ian Kaplan, iank@bearcave.com
 */
public enum GenreEnum {
    BAD_ENUM("Bad Enum"),
    SCIENCE_FICTION("Science Fiction"),
    FICTION("Fiction"),
    HISTORY("History"),
    COMPUTER_SCIENCE("Computer Science"),
    FINANCE("Finance"),
    MATHEMATICS("Mathematics"),
    CURRENT_EVENTS("Current Events"),
    SCIENCE("Science"),
    COOKING("Cooking"),
    TRAVEL("Travel"),
    NONFICTION("Nonfiction")
    ;
    
    private final String name;
    
    private GenreEnum(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static GenreEnum stringToEnum(String name) {
        GenreEnum enumVal = BAD_ENUM;
        if (name.equals(SCIENCE_FICTION.toString())) {
            enumVal = SCIENCE_FICTION;
        } else if (name.equals(FICTION.toString())) {
            enumVal = FICTION;
        } else if (name.equals(HISTORY.toString())) {
            enumVal = HISTORY;
        } else if (name.equals(COMPUTER_SCIENCE.toString())) {
            enumVal = COMPUTER_SCIENCE;
        } else if (name.equals(FINANCE.toString())) {
            enumVal = FINANCE;
        } else if (name.equals(MATHEMATICS.toString())) {
            enumVal = MATHEMATICS;
        } else if (name.equals(CURRENT_EVENTS.toString())) {
            enumVal = CURRENT_EVENTS;
        } else if (name.equals(SCIENCE.toString())) {
            enumVal = SCIENCE;
        } else if (name.equals(COOKING.toString())) {
            enumVal = COOKING;
        } else if (name.equals(TRAVEL.toString())) {
            enumVal = TRAVEL;
        } else if (name.equals(NONFICTION.toString())) {
            enumVal = NONFICTION;
        }
        return enumVal;
    }

    @Override
    public String toString() {
        return this.name;
    }
}