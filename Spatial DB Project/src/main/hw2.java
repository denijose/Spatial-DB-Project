package main;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class hw2 {

	/**
	 * @param args
	 */
	
	private static String QUERYTYPE;
	private static String OBJECTTYPE;
	private static String DEMONUM;
	private static String BUILDNGID;
	private static String BUILDNGNAME;
	private static String DISTANCE;
	private static String NNNUM;
	private static ArrayList<Integer> COORDINATES;
	
	public static void main(String[] args) {
		
		parseArgs(args);
		
		try{
		Connection conn =  DataBaseConn.getConnection("denis","localhost", "1525", "SYS as SYSDBA", "123DJimagine");
		SDQuery q = new SDQuery(conn);
		
		if(QUERYTYPE.equalsIgnoreCase("window"))
			q.windowQuery(OBJECTTYPE, COORDINATES);
		else if(QUERYTYPE.equalsIgnoreCase("within"))
			q.withinQuery(OBJECTTYPE,BUILDNGNAME,DISTANCE);
		else if(QUERYTYPE.equalsIgnoreCase("nn"))
			q.nnQuery(OBJECTTYPE,BUILDNGID,NNNUM);
		else if(QUERYTYPE.equalsIgnoreCase("demo"))			
		    q.demoQuery(DEMONUM);

		
		DataBaseConn.closeConnection(conn);
		}
		catch(SQLException e){
			System.out.println("ERROR -\n" + e.getMessage() + "\nexiting...");
		}
		
	}
	
	private static void parseArgs(String[] args){
		try{
		QUERYTYPE = args[0];
		if(QUERYTYPE.equalsIgnoreCase("demo")){
			DEMONUM = args[1];
		}
		else{
			OBJECTTYPE = args[1];
			if(QUERYTYPE.equalsIgnoreCase("window"))
				getCoordinates(args);
			else if(QUERYTYPE.equalsIgnoreCase("within")){
				BUILDNGNAME = args[2];
				DISTANCE = args[3];
			}				
			else if(QUERYTYPE.equalsIgnoreCase("nn")){
				BUILDNGID = args[2];
				NNNUM = args[3];
			}
			else {
				System.out.println("The query type can only be one of window,within,nn,demo ");
				System.exit(0);
			}
		}
		}
		catch(Exception e){
			System.out.println("Usage - \njava -jar hw2.jar query type [object type other parameters|demo number]\n");
			System.exit(0);
			}
		}
	
	
	private static void getCoordinates(String[] args){
		COORDINATES = new ArrayList<Integer>();
		for(int i=2;i<args.length;i++)
			COORDINATES.add(Integer.parseInt(args[i]));
	}

}
