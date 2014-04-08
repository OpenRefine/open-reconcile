package com.googlecode.openreconcile.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReconcileMatching {
	

	// This is a constant for the minimum maximum number of characters not in common in
	// the last-ditch matching effort, this is not used if the string is longer
	// instead it is 1/3rd of the characters not matching 
	private static final int THRESHOLD_LEV = 3;	
	
	// This is a constant for the max number of results returned
	private static final int MAX_RESULTS = 5;
	
	private static String nameOfLogger = ReconcileMatching.class.getName();
	private static Logger myLogger = Logger.getLogger(nameOfLogger); 

    /**
     * Finds results for a given query
     * 
     * @param query
     *            a Query object that the JSON query request will be parsed into
     * @param myVocab
     *            aVocabManager object which contains information about the
     *            vocabulary against which the query term will be compared.
     * 
     @return A List of Result objects, which will be parsed into a JSON response
     * 
     */
	public static List<Result> findMatches(Query query, DataManager myVocab){

		myLogger.setLevel(Level.INFO); 

		// get the term we're going to search for and
		// take out any leading or trailing whitespaces
		String querystr = query.getQuery();
		querystr = querystr.trim();
		String uncleaned = querystr;
		
		// This is in case of numeric entries. Google Refine doesn't seem
		// to have int cell types, instead it adds an invisible .0 to all
		// numbers. This fixes that issue, as it sometimes causes false negatives.
		if (querystr.endsWith(".0")){
		      Pattern p = Pattern.compile("[^0-9\\.]+");
		      Matcher m = p.matcher(querystr);
		      boolean result = m.find();
		      if (!result){
		    	  querystr = querystr.substring(0, querystr.length()-2);
		      }
		}
		// see if it's in the synonyms map, if it is
		// replace it with the appropriate term, if it's
		// not, don't do anything. 

		if (myVocab.subMap.get(querystr)!=null){
			querystr = myVocab.subMap.get(querystr);
			query.setQuery(querystr);
		}
		
		// Clean up the query string if it isn't case/punctuation sensitive
		if (!myVocab.capsSensitive()){		
			querystr = querystr.toLowerCase();
		}
		if (! myVocab.punctSensitive()){		
			querystr = querystr.replaceAll("[\\W_]", "");
		}
		
		// see if it's in the synonyms map, if it is
		// replace it with the appropriate term, if it's
		// not, don't do anything. 
		if(myVocab.subMap.get(querystr)!=null){
			querystr = myVocab.subMap.get(querystr);
			query.setQuery(querystr);
		}

		String type = query.getType();

		// This List is the results that are going to be returned. 
		List<Result> results = getDirectMatches(myVocab, querystr, uncleaned, type);
		
		// If there's a perfect match return it.
		if (results.size() == 1 && results.get(0).match){
			return results;
		}else{
			// Otherwise, add the initial ones and try matching
			// based on distance to vocabulary terms.
			results.addAll(distanceMatching(query, myVocab));
			
			// Split the original query term by space and non-alphanumeric characters 
			// to find how many words there are.
			//querystr = query.getQuery().replaceAll("[\\W_]", " ");
			String [] termList = querystr.split(" ");
			
			// if there's more than one, run bagOfWords
			// which tries to find a match for each of the words.
			if (termList.length > 1){
				results.addAll(bagOfWords(query, myVocab));
			}
		}
		
		// Clean the results: no duplicates
		// no extra results to return, and sorted
		// them by score before returning them
		results = removeDuplicates(results);
		
		// They do not need to be reduced in 
		// number if there are fewer than 
		// the max results.
		// The pruneResults sorts them
		// by score already.
		if(query.getLimit()!=null){
			results = pruneResults(results,Integer.parseInt(query.getLimit()));
		}else{
			results = pruneResults(results,MAX_RESULTS);
		}
			
		results = sortByScore(results);
		for (int i = 0; i< results.size(); i++){
//			myLogger.log(Level.SEVERE,results.get(i).getScore()+ " is bigger than 100?");
			if(results.get(i).getScore() > 100){
				results.get(i).setScore(100 - (results.get(i).getScore()-100));
//				myLogger.log(Level.SEVERE,results.get(i).getScore()+" is bigger than 100! and was set to "+
//						((double)100 - (results.get(i).getScore()-(double)100)));
			}
		}
		return results;
	}
	
    /**
     * This function does matching via direct matching
     * 
     * @param querystr
     *            a string of the query term
     * @param myVocab
     *            A VocabManager object (for the vocabulary)
     * @param originalQuerystr
     *            Because the query may be modified, it's useful to get original
     *            query term
     * @param type
     *            The vocab type.
     * 
     @return A List of Result objects that are closer than the maxScore
     *         allowable.
     * 
     **/
	private static List<Result> getDirectMatches(DataManager myVocab, String querystr, 
			String originalQuerystr, String type) {
		// perfectMatch is A List for a Result where the match
		// is to be considered an exact match.
		List<Result> perfectMatch = new ArrayList<Result>();
		
		// imperfectResults are for all non-100% matches
		List<Result> imperfectResults = new ArrayList<Result>();
		// Loop through the whole vocabulary list, quit if there's a perfect match
		for (int i = 0; i<myVocab.vocab.size() && perfectMatch.size()<1; i++){
			String vocabTerm = myVocab.vocab.get(i).trim();
			// Clean up the vocabulary term string if it isn't case/punctuation sensitive
			if (!myVocab.capsSensitive()){		
				vocabTerm = vocabTerm.toLowerCase();
			}
			if (! myVocab.punctSensitive()){		
				vocabTerm = vocabTerm.replaceAll("[\\W_]", "");
			}
			// The VocabID returned is always type/exact term
			String vocabID = type+"/"+myVocab.vocab.get(i);
			
			// Don't check for matching conditions if there's already a
			// perfect match, just get out of the loop ASAP, as there's nothing
			// to be gained after that point.
			if ((perfectMatch.size()<1 )&&(vocabTerm.equals(querystr))
					|| (myVocab.vocab.get(i).equals(originalQuerystr))){				
				Result match = new Result(vocabID,myVocab.vocab.get(i),type, 100.0,true);
				perfectMatch.add(match);
			}else if(myVocab.capsSensitive()||myVocab.punctSensitive()){
				// if there's a matching value that just differs in 
				// punctuation/capitalization or both, put it on the top
				// of the list
				if(myVocab.capsSensitive() && myVocab.vocab.get(i).toLowerCase().equals(querystr.toLowerCase())){
					Result possibleMatch = new Result(vocabID,myVocab.vocab.get(i),type, 99.0,false);
					imperfectResults.add(possibleMatch);
				}
				if(myVocab.punctSensitive() && myVocab.vocab.get(i).replaceAll("[\\W_]", "").equals(querystr.replaceAll("[\\W_]", ""))){
					Result possibleMatch = new Result(vocabID,myVocab.vocab.get(i),type, 99.0,false);
					imperfectResults.add(possibleMatch);
				}
				if(myVocab.punctSensitive() && myVocab.capsSensitive() && myVocab.vocab.get(i).replaceAll("[\\W_]", "").toLowerCase().equals(querystr.replaceAll("[\\W_]", "").toLowerCase())){
					Result possibleMatch = new Result(vocabID,myVocab.vocab.get(i),type, 99.0,false);
					imperfectResults.add(possibleMatch);
				}
				//to do finish this!
			}else if ((perfectMatch.size()<1 )&&(vocabTerm.contains(querystr) || querystr.contains(vocabTerm))
					||  (myVocab.vocab.get(i).contains(originalQuerystr)) ||  (originalQuerystr.contains(myVocab.vocab.get(i)))){
				double score=0;
				if (querystr.length()>vocabTerm.length()){
					score = ((double)vocabTerm.length()/(double)querystr.length())*100;
				}else{
					score = ((double)querystr.length()/(double)vocabTerm.length())*100;
				}
				if (!myVocab.capsSensitive() && !myVocab.punctSensitive() && score >= 100){
					Result match = new Result(vocabID,myVocab.vocab.get(i),type, 100.0,true);
					perfectMatch.add(match);
				}
				Result possibleMatch = new Result(vocabID,myVocab.vocab.get(i),type, score,false);
				imperfectResults.add(possibleMatch);
			}
		}
		if (perfectMatch.size()>0) {
            return perfectMatch;
        } else {
            return imperfectResults;
        }
	}

    /**
     * This function does matching via edit distance. It is useful in finding
     * typos
     * 
     * @param query
     *            A Query object (for the query term)
     * @param myVocab
     *            a VocabManager object (for the vocabulary)
     * 
     @return A List of Result objects that are closer than the maxScore
     *         allowable.
     * 
     **/ 
	private static List<Result> distanceMatching(Query query, DataManager myVocab) {
	    String queryterm = query.getQuery();
	    int maxScore;
	    if (queryterm.length()<9) {
	        maxScore= queryterm.length()-THRESHOLD_LEV;
	    } else {
	        maxScore = queryterm.length()-(queryterm.length()/THRESHOLD_LEV);
	    }
	    List<Result> fuzzyResults = new ArrayList<Result>();
	    for (int i = 0; i < myVocab.vocab.size(); i++){
	        String vocabterm = myVocab.vocab.get(i).trim();
	        int distance = levenshteinDistance(vocabterm, queryterm);
	        // subtract the distance that's just the length difference
	        // to only take into account the character substitution
	        distance = distance - (Math.abs(vocabterm.length() - queryterm.length()));
	        if (distance < maxScore){
	            double score;
	            if (vocabterm.length() < queryterm.length()) {
	                score = ((double)(vocabterm.length())/(double)(queryterm.length()+distance))*99;
	            } else {
	                score = (((double)queryterm.length()/(double)(vocabterm.length()+distance)))*99;
	            }
	            fuzzyResults.add(new Result(query.getType()+"/"+myVocab.vocab.get(i), myVocab.vocab.get(i), query.getType(),score, false));
	        }
	    }
	    return fuzzyResults;
	}

    /**
     * This function returns the number that is the minimum of three ints. it is
     * used by the LevenshteinDistance function
     * 
     * @param one
     *            an integer to be compared to two others
     * @param two
     *            an integer to be compared to two others
     * @param three
     *            an integer to be compared to two others
     * 
     @return The integer of the three which is the smallest
     * 
     **/
    private static int min(int one, int two, int three) {
        if (two < one) {
            one = two;
        }
        if (three < one) {
            one = three;
        }
        return one;
    }
	
    /**
     * This function calculates a Levenshtein Distance Matrix and returns a
     * number for the match value between one vocabulary term and a query term.
     * It is based off of the pseudocode found at:
     * http://en.wikipedia.org/wiki/Levenshtein_distance
     * 
     * @param vocabTerm
     *            A string to be compared to the second string
     * @param queryTerm
     *            A second string to be compared to the first string
     * 
     @return The distance score for the two strings
     * 
     **/		
    private static int levenshteinDistance(String vocabTerm, String queryTerm) {
        int dMatrix[][] = new int[vocabTerm.length() + 1][queryTerm.length() + 1];

        for (int i = 0; i < vocabTerm.length() + 1; i++) {
            dMatrix[i][0] = i;
        }
        for (int i = 0; i < queryTerm.length() + 1; i++) {
            dMatrix[0][i] = i;
        }
        for (int i = 1; i < queryTerm.length() + 1; i++) {
            for (int j = 1; j < vocabTerm.length() + 1; j++) {
                if (vocabTerm.charAt(j - 1) == queryTerm.charAt(i - 1)) {
                    dMatrix[j][i] = dMatrix[j - 1][i - 1];
                } else {
                    dMatrix[j][i] = min(dMatrix[j - 1][i - 1],
                            dMatrix[j][i - 1], dMatrix[j - 1][i]) + 1;
                }
            }
        }
        return dMatrix[vocabTerm.length()][queryTerm.length()];
    }


    /**
     * A function that decreases the number of result objects to be returned for
     * a particular query. Google Refine, in particular, hard-codes a limit of
     * results that will be displayed. This allows for response queries to be
     * only as long as will be used.
     * 
     * @param pruneThis
     *            A List of Result objects.
     * @param limit
     *            The limit in the query request. It is hardcoded in Google
     *            Refine as 3.
     * 
     @return A List of Result objects with a max number of entries.
     * 
     **/
    private static List<Result> pruneResults(List<Result> pruneThis, int limit) {
        List<Result> sortednodups = sortByScore(pruneThis);
        List<Result> pruned = new ArrayList<Result>();
        for (int i = 0; i < limit && i < pruneThis.size(); i++) {
            pruned.add(sortednodups.get(i));
        }
        return pruned;
    }
	
	
    /**
     * A function that checks the List of result objects for duplicates. Because
     * of the overlap between some of the methods, a positive result may be
     * found for the same vocabulary term twice. This function deletes all but
     * one entry for each term and it reports the highest score
     * 
     * @param toDeDup
     *            A List of Result objects.
     * 
     @return A List of Result objects without any duplicates
     * 
     **/	
    private static List<Result> removeDuplicates(List<Result> toDeDup){
        // This is a hash map that keeps track of the result term
        // and the index at which it is found.
        Map<String, Integer> hashm = new HashMap<String, Integer>();
        List<Result> noDups = new ArrayList<Result>();
        for(int i = 0; i < toDeDup.size(); i++){
            // Random bug where scores > 100 sometimes
            if (toDeDup.get(i).getScore()>100){
                toDeDup.get(i).setScore(100-(toDeDup.get(i).getScore()-100));
            }
            // If the result term isn't already in the hash map add it
            if(!hashm.containsKey(toDeDup.get(i).getName().trim())){
                hashm.put(toDeDup.get(i).getName().trim(), i);
            }else{
                // If the result term is already in the hash map
                // Compare the scores between the current term and the
                // one already in the Map. Add the highest.
                if (toDeDup.get(i).getScore() > toDeDup.get(hashm.get(toDeDup.get(i).getName().trim())).getScore()) {
                    hashm.put(toDeDup.get(i).getName().trim(), i);
                }
            }
        }
        // Go through the hash map and add all of the index's to the 
        // List of results to return
        for (String key: hashm.keySet()){
            noDups.add(toDeDup.get(hashm.get(key)));
        }
        return noDups;
    }
	
    /**
     * This function breaks a query term down into individual words and tries to
     * find a result based on each word in the query term. The scores are
     * decremented based on how many words are in the original Query term.
     * 
     * @param query
     *            A Query
     * @param myVocab
     *            A VocabManager
     * 
     @return A List of Result objects from the search.
     * 
     **/	
	
    private static List<Result> bagOfWords(Query query, DataManager myVocab) {
        String queryname = query.getQuery();
        // split on space and punctuation
        String[] termList = queryname.split(" ");
        List<Result> bagResults = new ArrayList<Result>();
        for (String element : termList) {
            Query newQuery = new Query(element, query.getLimit(), query.getType(), query.getTypeStrict(), query.properties()) ;
            List<Result> tempResults = new ArrayList<Result>(findMatches(newQuery, myVocab));
            for(int j=0; j<tempResults.size(); j++){

                tempResults.get(j).setMatch(false);
                tempResults.get(j).decreaseScore(termList.length);
                if(tempResults.get(j).getScore()>100){
                    tempResults.get(j).setScore(99.0);
                }else if (tempResults.get(j).getScore()<1){
                    tempResults.get(j).setScore(tempResults.get(j).getScore()*10);
                }
            }
            bagResults.addAll(tempResults);
        }
        return bagResults;
    }

    /**
     * A function that users Collections.sort function to sort the Result
     * objects by their descending score.
     * 
     * @param sortThis
     *            A List of Result objects not sorted.
     * 
     * @return A sorted List of Result objects
     * 
     **/
    private static List<Result> sortByScore(List<Result> sortThis) {
        List<Result> sorted = sortThis;
        Collections.sort(sorted, new Comparator<Result>() {

            public int compare(Result r1, Result r2) {
                Double Score1 = new Double(r1.getScore());
                Double Score2 = new Double(r2.getScore());
                // Invert comparison for descending sort
                return Score2.compareTo(Score1);
            }
        });

        return sorted;
    }
}
