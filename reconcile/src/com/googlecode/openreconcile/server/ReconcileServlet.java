package com.googlecode.openreconcile.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
// IO objects used in this servlet
// Data structures used in this servlet
// Servlet objects used in this servlet
// Other objects used in this servlet

public final class ReconcileServlet extends HttpServlet{
	/**
	 * Generated serial Version ID. 
	 */

	private static final long serialVersionUID = 510045970250283364L;

	public ReconcileMatching matcher = new ReconcileMatching();
	public Query myQuery = new Query();
	public Queries myQueries = new Queries();
	public Result myResult = new Result();
	
	/**
	 * Runs the reconciliation service, parses the request and generates the response, creating objects and calling functions as necessary
	 * 
	@param request - HttpServletRequest
	@param response - HttpServletResponse
	*
	@throws ServletException
	@throws IOException
	 *  
	 */
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		
		// Gson is used for creation and parsing of JSON throughout this program	
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		// initialize the string that will be added to the response.
		String json="";
		
		// check to see what the request contains
		// per Google Refine's Reconcile API
		// there are three cases:
		// 1.) multiple queries requested
		// 2.) a single query request
		// 3.) a request for metadata
		// These conditional statements direct the servlet
	    if(request.getParameter("queries")!=null){
			json = runQueries(request);
		}else if(request.getParameter("query")!=null){
			Query query = gson.fromJson(request.getParameter("query"),Query.class);
			ArrayList<Result> resp = runQuery(query);
			// Create an ArrayList of Result objects. 
			if(resp.size()>0){
				for(int i = 0; i<resp.size(); i++){
					json = json+gson.toJson(resp.get(i));
				}
				if (resp.size()>1){
				json = "{ \"result\":["+json+"]}";
				}
			}else{
				// return an empty set if there are no results
				json="{ \"result\":[{ }]}";
			}
		}else{
			MetaData metadata = genMetaData();
			json = gson.toJson(metadata);
		}
	    // return the information in JSONP format
	    // the difference between JSON and JSONP is
	    // the callback parameter, which needs to be
	    // included in the response. This is for security in
	    //cross-site JSON requests
		String callback = request.getParameter("callback");
		String jsonp=json;
		if (callback !=null){
			jsonp = callback+"("+json+");";
		}
		
		//set the parameters of the response
		// all JSON responses need to be encoded
		// in UTF-8 and have the type application/json
		// status 200 is "OK" 
		response.setCharacterEncoding("UTF-8");
	    response.setContentType("application/json");	    
	    response.setStatus(200);
		PrintWriter out= response.getWriter();
		out.print(jsonp);
	}

	/**
	 * Generates the MetaData which is returned for all non-query calls. The MetaData information
	 * is kept in a configuration file, which is a sqlite database on the server. 
	 *                       	                          
	@return A "MetaData" object which is converted to a JSON string with GSON
	 *  
	 */
	public MetaData genMetaData(){
		ArrayList<Library> lib = new ArrayList<Library>();
		File file = new File(DataStoreFile.DATA_FILE_NAME);
		// this name can be changed as desired
		String name = "Open Reconcile Reconciliation Service";
		if (!file.exists()){
			MetaData md = new MetaData(name,lib);
			return md;
		}

//		    Connection connection = DriverManager.getConnection("jdbc:sqlite:"+DataStoreFile.DATA_FILE_NAME);
//		    Statement stmt = connection.createStatement();
//		    ResultSet rs = stmt.executeQuery("SELECT vocabID FROM DatabaseTable");
//		    int columnCount = rs.getMetaData().getColumnCount();
//		    ArrayList<String> dbResults = new ArrayList<String>();
//		    while(rs.next())
//		    {
//		    	// this is necessary to get all of the results.
//		        String id = null;
//		        for (int i=0; i <columnCount ; i++)
//		        {
//		           id = rs.getString(i + 1);
//		        }
//		        dbResults.add(id);
//		    }
		    // the libraries are going to have VocabName and VocabID as identical.
		    // this is for ease of use, and better integration with Google Refine
		    // as Google Refine only displays the VocabID, and it may be confusing
		    // if the does not see the name they entered in, but only an ID
//		    for(int i = 0; i < dbResults.size(); i++){
//		    	lib.add(new Library (dbResults.get(i), dbResults.get(i)));
//		    }

	
		
		MetaData md = new MetaData(name,lib);
		return md;
	}

	/**
	 * Processes a QUERY request. 
	 * 
	@param  query A Query object that the JSON query request is parsed into.
	 *    
	@return An ArrayList of Result objects, which will be returned and parsed into the JSON response
	 *  
	 */
	public ArrayList<Result> runQuery(Query query){
		
		ArrayList<Result> resultList = new ArrayList<Result>();
		// Google Refine always sends an intial query without 
		// a type specified. This works with Freebase to allow
		// the database to suggest a type, to reduce the number of
		// choices the user gets. However, there is no suggest 
		// API here, so by default we're returning all of the vocabIDs
		// so the user can pick between all vocabularies entered into 
		// the configuration file.
		if (query.getType()==null){
			MetaData md = genMetaData();
			Library libs[] = md.getLibraries();
			for (Library lib : libs) {
				resultList.add(new Result(lib.getID()));
			}
		}else{
			DataManager vocabManager = new DataManager(query.getType());
			// The vocab should be more than one entry long, if it's 
			// shorter, don't bother looking for results.
			if (vocabManager.vocab.size()>2){
				resultList = ReconcileMatching.findMatches(query, vocabManager);
			}
		}
		return resultList;
		
	}
	
	/**
	 * This function parses apart queries requests and returns a string with the JSON response.
	 * It uses GSON to break apart the array of queries into individual ones, and it calls the 
	 * match finder and manually puts together the response JSON string. 
	 * 
	@param  jsonReq HttpServletRequest the original request with the JSON code
	 *            	                          
	@return  A string with the JSON response to the request
	 *  
	**/	
	public String runQueries(HttpServletRequest jsonReq){
		// Get the JSON query string out of the request
		String queryList =jsonReq.getParameter("queries");
		Gson gson = new Gson();
		// Use the Queries class to parse all the data.
		Queries queries =gson.fromJson(queryList, Queries.class);
		// Put them into an ArrayList of Query objects for easier handling
		ArrayList<Query> qList = new ArrayList<Query>();
		qList = queries.getQueries();
		// Check to see if there's a type send with the request, if there's
		// not, send one of the requests to runQuery as it has the code to handle it
		String type = qList.get(0).getType();
		DataManager vocabManager = null;
		if (type != null){
			vocabManager = new DataManager(qList.get(0).getType());
		}
		String jsonResp="{";		
		for (int i = 0; i<qList.size() && qList.get(i)!=null; i++){
			ArrayList<Result> singleQueryResultSet;
			// If the type was null, and therefore most of these will be true
			// send it to the runQuery method.
			if (vocabManager == null || vocabManager.vocab == null || vocabManager.vocab.size()<2){
				singleQueryResultSet = runQuery(qList.get(i));
			}else{
				// Otherwise, process it as normal
				singleQueryResultSet = ReconcileMatching.findMatches(qList.get(i), vocabManager);
			}
			// Each of the result sets needs to be handled 
			// individually and labeled properly, that is what 
			// this code does. It creates an array of JSON strings
			// generated by GSON for the results for each query.
			jsonResp = jsonResp+ "\"q"+Integer.toString(i)+"\": "+"{";
			if(singleQueryResultSet!=null){
				ArrayList<String> newResult= new ArrayList<String>();
				for(int j = 0; j<singleQueryResultSet.size(); j++){
					newResult.add(gson.toJson(singleQueryResultSet.get(j)));
				}
				// The results need to be all joined together and the proper formating needs
				// to go around them
				String nResult = StringUtils.join(newResult,',');
				jsonResp = jsonResp+"\"result\":["+nResult+"]},";
			}else{
				// If there aren't any results, empty result
				jsonResp=jsonResp+ "\"result\":[{ }]},";
			}
		}	
		// Take off the last trailing common and add a closing bracket
		// return it.
		return jsonResp.substring(0,jsonResp.length()-1)+"}";
	}
	
	
	/**
	 * Runs the reconciliation service, creates objects and calls functions and necessary.
	 * This one just calls the DoGet which has all the necessary code
	 * 
	@param  request HttpServletRequest
	@param  response HttpServletResponse
	 *  
	@throws ServletException
	@throws IOException
	 *  
	 */
	public void doPost(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	/**
	 * @author  lawlerr1  The following are classes used for parsing/generating JSON requests/responses. Please do not edit unless you   know what you're doing. There are some accessors and mutators, but not much else.
	 */
	
	class Queries{
		Query q0;
		Query q1;
		Query q2;
		Query q3;
		Query q4;
		Query q5;
		Query q6;
		Query q7;
		Query q8;
		Query q9;
		
		Queries(Query one,Query two,Query three,Query four,Query five,Query six,Query seven,Query eight,Query nine,Query ten){
			q0=one;
			q1=two;
			q2=three;
			q3=four;
			q4=five;
			q5=six;
			q6=seven;
			q7=eight;
			q8=nine;
			q9=ten;
		}
		
		Queries(){
			q0=q1=q2=q3=q4=q5=q6=q7=q8=q9=null;
		}
		
		public ArrayList<Query> getQueries(){
			ArrayList<Query> returnThis = new ArrayList<Query>();
			returnThis.add(q0);
			returnThis.add(q1);
			returnThis.add(q2);
			returnThis.add(q3);
			returnThis.add(q4);
			returnThis.add(q5);
			returnThis.add(q6);
			returnThis.add(q7);
			returnThis.add(q8);
			returnThis.add(q9);
			return returnThis;
		}
	}
	
	/**
	 * @author  lawlerr1
	 */
	public class MetaData{
		private String name;
		private Library[] defaultTypes;
		
		public MetaData(String cname)
		{
			name = cname;
			defaultTypes= new Library[1];
			defaultTypes[0]= new Library();
		}
		
		public MetaData(String cname, ArrayList<Library> libs){
			name = cname;	
			defaultTypes = new Library[libs.size()];
			for (int i = 0; i < libs.size(); i++){
				defaultTypes[i]= libs.get(i);
			}
		}
		public Library[] getLibraries(){
			return defaultTypes;
		}
		/**
		 * @return
		 */
		public String getName(){
			return name;
		}
	}
	
	/**
	 * @author  lawlerr1
	 */
	public class Library{
		private String id;
		private String name;
		
		Library (String cid, String cname){
			id = cid;
			name = cname;
		}
		Library(){
			id ="error no id found";
			name="error no name found";
		}
		public String getID(){
			return id;
		}
		/**
		 * @return name
		 */
		public String getName(){
			return name;
		}
	}
}