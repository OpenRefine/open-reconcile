package com.googlecode.openreconcile.server;

public class Result {

    String id;
    String name;
    String[] type;
    private Double score;
    Boolean match;

    /**
     * Constructor - it sets values to nothing, the score to 0 and the match to
     * false, basically it's nothing
     */
    Result() {
        id = "";
        name = "";
        type = new String[1];
        type[0] = "";
        score = 0.0;
        match = false;
    }

    /**
     * Returns the score for a particular result
     * 
     * @return a double value that is the score of the match
     */
    public double getScore() {
        return score;
    }

    /**
     * Returns the result term for a particular result
     * 
     * @return a string value of the matched term for this result
     */
    public String getName() {
        return name;
    }

    /**
     * Decreases a score. This is used with the bag of words method. The factor
     * is generally the number of terms. This function decrease the score
     * slightly more than it needs to, so that these results don't show up too
     * high.
     * 
     * @param factor
     *            - the integer factor by which to decrease a score
     */
    public void decreaseScore(int factor) {
        score = (score * 0.95) / (factor);
        if (score > 100) {
            score = (double) 99;
        }
    }

    /**
     * Constructor with only type specified. This is used when Google Refine
     * sends out the query to get a suggestion, since this doesn't support the
     * suggest API.
     * 
     * @param retType
     *            the return type to be set for the result
     * 
     */
    Result(String retType) {
        id = null;
        name = null;
        type = new String[1];
        type[0] = retType;
        score = null;
        match = null;
    }

    /**
     * Constructor
     * 
     * @param nID
     *            the ID for the result
     * @param nName
     *            the term matched
     * @param nType
     *            the type of the match
     * @param nScore
     *            the score of the match
     * @param nMatch
     *            the Boolean value to indicate if it's a perfect match or not
     */
    Result(String nID, String nName, String nType, Double nScore, Boolean nMatch) {
        id = nID;
        name = nName;
        type = new String[1];
        type[0] = nType;
        score = Math.abs(nScore);
        if (score > 100) {
            score = (double) 99;
        }
        match = nMatch;
    }

    /**
     * Sets the match value to true or false. This is used in the bag-of-words
     * method, because a particular word in the term may have a perfect match
     * that is not the perfect match for the whole query term
     * 
     * @param bMatch
     *            a boolean value for what match should be set to
     * 
     */
    public void setMatch(boolean bMatch) {
        match = bMatch;
    }

    /**
     * Manually sets the score value
     * 
     * @param d
     *            the desired score
     */
    public void setScore(double d) {
        score = d;
        if (score > 100) {
            score = (double) 99;
        }

    }
}