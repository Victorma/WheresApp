package com.wheresapp.util;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by Sergio on 31/12/2014.
 */


public class MaskedFormatter {

    // Potential values in mask.
    private static final char DIGIT_KEY = '#';
    private static final char LITERAL_KEY = '\'';
    private static final char UPPERCASE_KEY = 'U';
    private static final char LOWERCASE_KEY = 'L';
    private static final char ALPHA_NUMERIC_KEY = 'A';
    private static final char CHARACTER_KEY = '?';
    private static final char ANYTHING_KEY = '*';
    private static final char HEX_KEY = 'H';

    /** The user specified mask. */
    private String mask;

    /** Indicates if the value contains the literal characters. */
    private boolean containsLiteralChars;

    private static final MaskCharacter[] EmptyMaskChars = new MaskCharacter[0];

    /** List of valid characters. */
    private String validCharacters;

    /** List of invalid characters. */
    private String invalidCharacters;

    /** String used to represent characters not present. */
    private char placeholder;

    /** String used for the passed in value if it does not completely
     * fill the mask. */
    private String placeholderString;

    private transient MaskCharacter[] maskChars;


    /** Indicates if the value being edited must match the mask. */
    @SuppressWarnings("unused")
    private boolean allowsInvalid;


    /**
     * Creates a MaskFormatter with no mask.
     */
    public MaskedFormatter() {
        setAllowsInvalid(false);
        containsLiteralChars = true;
        maskChars = EmptyMaskChars;
        placeholder = ' ';
    }

    /**
     * Creates a <code>MaskFormatter</code> with the specified mask.
     * A <code>ParseException</code>
     * will be thrown if <code>mask</code> is an invalid mask.
     *
     * @throws ParseException if mask does not contain valid mask characters
     */
    public MaskedFormatter(String mask) throws ParseException {
        this();
        setMask(mask);
    }

    /**
     * Sets the mask dictating the legal characters.
     * This will throw a <code>ParseException</code> if <code>mask</code> is
     * not valid.
     *
     * @throws ParseException if mask does not contain valid mask characters
     */
    public void setMask(String mask) throws ParseException {
        this.mask = mask;
        updateInternalMask();
    }

    /**
     * Returns the formatting mask.
     *
     * @return Mask dictating legal character values.
     */
    public String getMask() {
        return mask;
    }

    /**
     * Updates the internal representation of the mask.
     */
    private void updateInternalMask() throws ParseException {
        String mask = getMask();
        ArrayList<MaskCharacter> fixed = new ArrayList<MaskCharacter>();
        ArrayList<MaskCharacter> temp = fixed;

        if (mask != null) {
            for (int counter = 0, maxCounter = mask.length();
                 counter < maxCounter; counter++) {
                char maskChar = mask.charAt(counter);

                switch (maskChar) {
                    case DIGIT_KEY:
                        temp.add(new DigitMaskCharacter());
                        break;
                    case LITERAL_KEY:
                        if (++counter < maxCounter) {
                            maskChar = mask.charAt(counter);
                            temp.add(new LiteralCharacter(maskChar));
                        }
                        // else: Could actually throw if else
                        break;
                    case UPPERCASE_KEY:
                        temp.add(new UpperCaseCharacter());
                        break;
                    case LOWERCASE_KEY:
                        temp.add(new LowerCaseCharacter());
                        break;
                    case ALPHA_NUMERIC_KEY:
                        temp.add(new AlphaNumericCharacter());
                        break;
                    case CHARACTER_KEY:
                        temp.add(new CharCharacter());
                        break;
                    case ANYTHING_KEY:
                        temp.add(new MaskCharacter());
                        break;
                    case HEX_KEY:
                        temp.add(new HexCharacter());
                        break;
                    default:
                        temp.add(new LiteralCharacter(maskChar));
                        break;
                }
            }
        }
        if (fixed.size() == 0) {
            maskChars = EmptyMaskChars;
        }
        else {
            maskChars = new MaskCharacter[fixed.size()];
            fixed.toArray(maskChars);
        }
    }


    /**
     * Sets whether or not the value being edited is allowed to be invalid
     * for a length of time (that is, <code>stringToValue</code> throws
     * a <code>ParseException</code>).
     * It is often convenient to allow the user to temporarily input an
     * invalid value.
     *
     * @param allowsInvalid Used to indicate if the edited value must always
     *        be valid
     */
    public void setAllowsInvalid(boolean allowsInvalid) {
        this.allowsInvalid = allowsInvalid;
    }


    /**
     * Allows for further restricting of the characters that can be input.
     * Only characters specified in the mask, not in the
     * <code>invalidCharacters</code>, and in
     * <code>validCharacters</code> will be allowed to be input. Passing
     * in null (the default) implies the valid characters are only bound
     * by the mask and the invalid characters.
     *
     * @param validCharacters If non-null, specifies legal characters.
     */
    public void setValidCharacters(String validCharacters) {
        this.validCharacters = validCharacters;
    }

    /**
     * Returns the valid characters that can be input.
     *
     * @return Legal characters
     */
    public String getValidCharacters() {
        return validCharacters;
    }

    /**
     * Allows for further restricting of the characters that can be input.
     * Only characters specified in the mask, not in the
     * <code>invalidCharacters</code>, and in
     * <code>validCharacters</code> will be allowed to be input. Passing
     * in null (the default) implies the valid characters are only bound
     * by the mask and the valid characters.
     *
     * @param invalidCharacters If non-null, specifies illegal characters.
     */
    public void setInvalidCharacters(String invalidCharacters) {
        this.invalidCharacters = invalidCharacters;
    }

    /**
     * Returns the characters that are not valid for input.
     *
     * @return illegal characters.
     */
    public String getInvalidCharacters() {
        return invalidCharacters;
    }

    /**
     * If true, the returned value and set value will also contain the literal
     * characters in mask.
     * <p>
     * For example, if the mask is <code>'(###) ###-####'</code>, the
     * current value is <code>'(415) 555-1212'</code>, and
     * <code>valueContainsLiteralCharacters</code> is
     * true <code>stringToValue</code> will return
     * <code>'(415) 555-1212'</code>. On the other hand, if
     * <code>valueContainsLiteralCharacters</code> is false,
     * <code>stringToValue</code> will return <code>'4155551212'</code>.
     *
     * @param containsLiteralChars Used to indicate if literal characters in
     *        mask should be returned in stringToValue
     */
    public void setValueContainsLiteralCharacters(
            boolean containsLiteralChars) {
        this.containsLiteralChars = containsLiteralChars;
    }

    /**
     * Returns true if <code>stringToValue</code> should return literal
     * characters in the mask.
     *
     * @return True if literal characters in mask should be returned in
     *         stringToValue
     */
    public boolean getValueContainsLiteralCharacters() {
        return containsLiteralChars;
    }

    /**
     * Sets the character to use in place of characters that are not present
     * in the value, ie the user must fill them in. The default value is
     * a space.
     * <p>
     * This is only applicable if the placeholder string has not been
     * specified, or does not completely fill in the mask.
     *
     * @param placeholder Character used when formatting if the value does not
     *        completely fill the mask
     */
    public void setPlaceholderCharacter(char placeholder) {
        this.placeholder = placeholder;
    }

    /**
     * Returns the character to use in place of characters that are not present
     * in the value, ie the user must fill them in.
     *
     * @return Character used when formatting if the value does not
     *        completely fill the mask
     */
    public char getPlaceholderCharacter() {
        return placeholder;
    }

    /**
     * Sets the string to use if the value does not completely fill in
     * the mask. A null value implies the placeholder char should be used.
     *
     * @param placeholder String used when formatting if the value does not
     *        completely fill the mask
     */
    public void setPlaceholder(String placeholder) {
        this.placeholderString = placeholder;
    }

    /**
     * Returns the String to use if the value does not completely fill
     * in the mask.
     *
     * @return String used when formatting if the value does not
     *        completely fill the mask
     */
    public String getPlaceholder() {
        return placeholderString;
    }
    /**
     * Returns a String representation of the Object <code>value</code>
     * based on the mask.  Refer to
     * {@link #setValueContainsLiteralCharacters} for details
     * on how literals are treated.
     *
     * @throws ParseException if there is an error in the conversion
     * @param value Value to convert
     * @see #setValueContainsLiteralCharacters
     * @return String representation of value
     */
    public String valueToString(Object value) throws ParseException {
        String sValue = (value == null) ? "" : value.toString();
        StringBuilder result = new StringBuilder();
        String placeholder = getPlaceholder();
        int[] valueCounter = { 0 };

        append(result, sValue, valueCounter, placeholder, maskChars);
        return result.toString();
    }

    /**
     * Invokes <code>append</code> on the mask characters in
     * <code>mask</code>.
     */
    private void append(StringBuilder result, String value, int[] index,
                        String placeholder, MaskCharacter[] mask)
            throws ParseException {
        for (int counter = 0, maxCounter = mask.length;
             counter < maxCounter; counter++) {
            mask[counter].append(result, value, index, placeholder);
        }
    }

    //
    // Interal classes used to represent the mask.
    //
    private class MaskCharacter {
        /**
         * Subclasses should override this returning true if the instance
         * represents a literal character. The default implementation
         * returns false.
         */
        public boolean isLiteral() {
            return false;
        }

        /**
         * Returns true if <code>aChar</code> is a valid reprensentation of
         * the receiver. The default implementation returns true if the
         * receiver represents a literal character and <code>getChar</code>
         * == aChar. Otherwise, this will return true is <code>aChar</code>
         * is contained in the valid characters and not contained
         * in the invalid characters.
         */
        public boolean isValidCharacter(char aChar) {
            if (isLiteral()) {
                return (getChar(aChar) == aChar);
            }

            aChar = getChar(aChar);

            String filter = getValidCharacters();

            if (filter != null && filter.indexOf(aChar) == -1) {
                return false;
            }
            filter = getInvalidCharacters();
            if (filter != null && filter.indexOf(aChar) != -1) {
                return false;
            }
            return true;
        }

        /**
         * Returns the character to insert for <code>aChar</code>. The
         * default implementation returns <code>aChar</code>. Subclasses
         * that wish to do some sort of mapping, perhaps lower case to upper
         * case should override this and do the necessary mapping.
         */
        public char getChar(char aChar) {
            return aChar;
        }

        /**
         * Appends the necessary character in <code>formatting</code> at
         * <code>index</code> to <code>buff</code>.
         */
        public void append(StringBuilder buff, String formatting, int[] index,
                           String placeholder)
                throws ParseException {

            boolean inString = index[0] < formatting.length();
            char aChar = inString ? formatting.charAt(index[0]) : 0;

            if (isLiteral()) {
                buff.append(getChar(aChar));
                if (getValueContainsLiteralCharacters()) {
                    if (inString && aChar != getChar(aChar)) {
                        throw new ParseException("Invalid character: " +
                                aChar, index[0]);
                    }
                    index[0] = index[0] + 1;
                }
            }
            else if (index[0] >= formatting.length()) {
                if (placeholder != null && index[0] < placeholder.length()) {
                    buff.append(placeholder.charAt(index[0]));
                }
                else {
                    buff.append(getPlaceholderCharacter());
                }
                index[0] = index[0] + 1;
            }
            else if (isValidCharacter(aChar)) {
                buff.append(getChar(aChar));
                index[0] = index[0] + 1;
            }
            else {
                throw new ParseException("Invalid character: " + aChar,
                        index[0]);
            }
        }
    }


    /**
     * Used to represent a fixed character in the mask.
     */
    private class LiteralCharacter extends MaskCharacter {
        private char fixedChar;

        public LiteralCharacter(char fixedChar) {
            this.fixedChar = fixedChar;
        }

        public boolean isLiteral() {
            return true;
        }

        public char getChar(char aChar) {
            return fixedChar;
        }
    }


    /**
     * Represents a number, uses <code>Character.isDigit</code>.
     */
    private class DigitMaskCharacter extends MaskCharacter {
        public boolean isValidCharacter(char aChar) {
            return (Character.isDigit(aChar) &&
                    super.isValidCharacter(aChar));
        }
    }


    /**
     * Represents a character, lower case letters are mapped to upper case
     * using <code>Character.toUpperCase</code>.
     */
    private class UpperCaseCharacter extends MaskCharacter {
        public boolean isValidCharacter(char aChar) {
            return (Character.isLetter(aChar) &&
                    super.isValidCharacter(aChar));
        }

        public char getChar(char aChar) {
            return Character.toUpperCase(aChar);
        }
    }


    /**
     * Represents a character, upper case letters are mapped to lower case
     * using <code>Character.toLowerCase</code>.
     */
    private class LowerCaseCharacter extends MaskCharacter {
        public boolean isValidCharacter(char aChar) {
            return (Character.isLetter(aChar) &&
                    super.isValidCharacter(aChar));
        }

        public char getChar(char aChar) {
            return Character.toLowerCase(aChar);
        }
    }


    /**
     * Represents either a character or digit, uses
     * <code>Character.isLetterOrDigit</code>.
     */
    private class AlphaNumericCharacter extends MaskCharacter {
        public boolean isValidCharacter(char aChar) {
            return (Character.isLetterOrDigit(aChar) &&
                    super.isValidCharacter(aChar));
        }
    }


    /**
     * Represents a letter, uses <code>Character.isLetter</code>.
     */
    private class CharCharacter extends MaskCharacter {
        public boolean isValidCharacter(char aChar) {
            return (Character.isLetter(aChar) &&
                    super.isValidCharacter(aChar));
        }
    }


    /**
     * Represents a hex character, 0-9a-fA-F. a-f is mapped to A-F
     */
    private class HexCharacter extends MaskCharacter {
        public boolean isValidCharacter(char aChar) {
            return ((aChar == '0' || aChar == '1' ||
                    aChar == '2' || aChar == '3' ||
                    aChar == '4' || aChar == '5' ||
                    aChar == '6' || aChar == '7' ||
                    aChar == '8' || aChar == '9' ||
                    aChar == 'a' || aChar == 'A' ||
                    aChar == 'b' || aChar == 'B' ||
                    aChar == 'c' || aChar == 'C' ||
                    aChar == 'd' || aChar == 'D' ||
                    aChar == 'e' || aChar == 'E' ||
                    aChar == 'f' || aChar == 'F') &&
                    super.isValidCharacter(aChar));
        }

        public char getChar(char aChar) {
            if (Character.isDigit(aChar)) {
                return aChar;
            }
            return Character.toUpperCase(aChar);
        }
    }


}