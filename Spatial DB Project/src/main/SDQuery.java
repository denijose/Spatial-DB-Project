package main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;

public class SDQuery {

	public Connection conn; 
	
	public SDQuery(Connection conn){
		this.conn = conn;
	}
	
	public void windowQuery(String objectType,ArrayList<Integer> coOrdinates) throws SQLException{
		String x1 = coOrdinates.get(0).toString();
		String x2 = coOrdinates.get(1).toString();
		String x3 = coOrdinates.get(2).toString();
		String x4 = coOrdinates.get(3).toString();
		String coOrdinateString = x1 + "," + x2 + "," + x3 + "," + x4;
		Statement stmt = conn.createStatement();
		String query = null;
		if(objectType.equalsIgnoreCase("building"))
			query = "SELECT O.ID FROM BUILDING O WHERE SDO_RELATE (LOCATION, SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,3),SDO_ORDINATE_ARRAY(" + coOrdinateString + ")),'MASK=INSIDE+COVEREDBY') = 'TRUE' AND O.ID NOT IN (SELECT ID FROM FIREBUILDING)";
		else if(objectType.equalsIgnoreCase("firebuilding"))
			query = "SELECT O.ID FROM BUILDING O WHERE SDO_RELATE (LOCATION, SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,3),SDO_ORDINATE_ARRAY(" + coOrdinateString + ")),'MASK=INSIDE+COVEREDBY') = 'TRUE' AND O.ID IN (SELECT ID FROM FIREBUILDING)";
		else if(objectType.equalsIgnoreCase("firehydrant"))
			query = "SELECT O.ID FROM FIREHYDRANT O WHERE SDO_RELATE (LOCATION, SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,3),SDO_ORDINATE_ARRAY(" + coOrdinateString + ")),'MASK=INSIDE+COVEREDBY') = 'TRUE'";
		else{
				System.out.println("The query type can only be one of building,firebuilding,firehydrant ");
				System.exit(0);			
		}
		System.out.println("The query generated -\n" + query);
		ResultSet rs = stmt.executeQuery(query);
		System.out.println("\n" + objectType.toUpperCase() + " ID");
		while(rs.next()){
			System.out.println(rs.getString("ID"));
		}
		stmt.close();
	}
	
	public void demoQuery(String demoNum) throws SQLException{
		Statement stmt = conn.createStatement();
		String query = null;
		String createView = null;
		if(demoNum.equals("1")){
			query = "SELECT B.NAME FROM BUILDING B WHERE B.ID NOT IN (SELECT B2.ID FROM FIREBUILDING B2) AND B.NAME LIKE 'S%'";
			System.out.println("The query(s) generated - \n" + query);
			ResultSet rs = stmt.executeQuery(query);
			System.out.println("\n" + "Building Name");
			while(rs.next()){
				System.out.println(rs.getString("NAME"));
			}
		}
		if(demoNum.equals("2")){
			query = "SELECT B.NAME,H.ID FROM BUILDING B, FIREHYDRANT H WHERE sdo_nn(H.LOCATION,B.LOCATION,'SDO_NUM_RES=5') = 'TRUE' AND B.ID IN (SELECT ID FROM FIREBUILDING)";
			System.out.println("The query(s) generated - \n" + query);
			ResultSet rs = stmt.executeQuery(query);
			System.out.println("\n" + "FireBuilding Name   FireHydrant ID");
			while(rs.next()){
				System.out.println(rs.getString("NAME")+ "                 "+ rs.getString("ID"));
			}
		}
		if(demoNum.equals("3")){
			createView = "CREATE OR REPLACE VIEW DEMO3 AS SELECT F.ID ,COUNT(*) AS COUNT FROM FIREHYDRANT F, BUILDING B WHERE  SDO_WITHIN_DISTANCE(B.LOCATION, F.LOCATION,'DISTANCE=120') = 'TRUE' GROUP BY F.ID"; 
			query = "SELECT ID FROM DEMO3 WHERE COUNT=(SELECT MAX(COUNT) FROM DEMO3)";
			System.out.println("The query(s) generated - \n" + createView + "\n" + query);
			if(createView!=null)
				stmt.executeQuery(createView);
			ResultSet rs = stmt.executeQuery(query);
			System.out.println("\n" + "FireHydrant ID");
			while(rs.next()){
				System.out.println(rs.getString("ID"));
			}
		}
		if(demoNum.equals("4")){
			createView = "CREATE OR REPLACE VIEW DEMO4 AS SELECT F.ID AS F_ID, B.ID AS B_ID FROM FIREHYDRANT F, BUILDING B WHERE sdo_nn(F.LOCATION,B.LOCATION, 'sdo_num_res=1') = 'TRUE'";
			query = "SELECT F_ID, COUNT FROM (SELECT  COUNT(B_ID) AS COUNT, F_ID  FROM DEMO4 GROUP BY F_ID ORDER BY COUNT DESC) WHERE ROWNUM <=5";
			System.out.println("The query(s) generated - \n" + createView + "\n" + query);
			if(createView!=null)
				stmt.executeQuery(createView);
			ResultSet rs = stmt.executeQuery(query);
			System.out.println("\n" +"FireHydrant ID   Num Of Revers Nearest Neighbors");
			while(rs.next()){
				System.out.println(rs.getString("F_ID") + "              " + rs.getString("COUNT"));
			}
		}
		if(demoNum.equals("5")){
			query = "SELECT SDO_AGGR_MBR(LOCATION) FROM BUILDING WHERE NAME LIKE '%HE'";
			System.out.println("The query(s) generated - \n" + query);
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				 STRUCT st = (oracle.sql.STRUCT) rs.getObject(1);
			     //convert STRUCT into geometry
			     JGeometry j_geom = JGeometry.load(st);
			     double[] a = j_geom.getMBR();
			     System.out.println("\nThe Bottom Left Co-ordinates-\nx1:"+a[0]+", y1:"+a[1]+"\nThe Top Right Co-ordinates-\nx2:"+a[2]+", y2:"+a[3]);
			}
		}
		

		stmt.close();		
	}
	
	
	public void withinQuery(String objectType,String buildingName, String distance) throws SQLException{
		Statement stmt = conn.createStatement();
		String query = null;
		if(objectType.equalsIgnoreCase("building"))
		   query = "SELECT O.ID FROM  BUILDING O, BUILDING B WHERE B.NAME = '"+ buildingName + "' AND SDO_WITHIN_DISTANCE(O.LOCATION,B.LOCATION,'DISTANCE=" + distance + "') = 'TRUE' AND O.ID NOT IN (SELECT ID FROM FIREBUILDING) AND O.ID<>B.ID";
		else if(objectType.equalsIgnoreCase("firebuilding"))
			query = "SELECT O.ID FROM BUILDING O, BUILDING B WHERE B.NAME = '"+ buildingName + "' AND SDO_WITHIN_DISTANCE(O.LOCATION,B.LOCATION,'DISTANCE=" + distance + "') = 'TRUE' AND O.ID IN (SELECT ID FROM FIREBUILDING) AND O.ID<>B.ID";
		else if(objectType.equalsIgnoreCase("firehydrant"))
			query = "SELECT O.ID FROM FIREHYDRANT O, BUILDING B WHERE B.NAME = '"+ buildingName + "' AND SDO_WITHIN_DISTANCE(O.LOCATION,B.LOCATION,'DISTANCE=" + distance + "') = 'TRUE'";		
		else{
			System.out.println("The query type can only be one of building,firebuilding,firehydrant ");
			System.exit(0);			
	    }
		System.out.println("The query generated -\n" + query);
		ResultSet rs = stmt.executeQuery(query);
		System.out.println("\n" + objectType.toUpperCase() + " ID");
		while(rs.next()){
			System.out.println(rs.getString("ID"));
		}
		stmt.close();
	}
	
	
	public void nnQuery(String objectType, String buildingID, String nn) throws SQLException{
		Statement stmt = conn.createStatement();	
		String query =  null;
		if(objectType.equalsIgnoreCase("building"))
		   query = "SELECT O.ID FROM BUILDING O, BUILDING B2 WHERE B2.ID = '" + buildingID + "' AND sdo_nn(O.LOCATION,B2.LOCATION,'SDO_NUM_RES=" + nn + "') = 'TRUE' AND O.ID NOT IN (SELECT ID FROM FIREBUILDING) AND O.ID<>B2.ID";
		else if(objectType.equalsIgnoreCase("firebuilding"))
			query = "SELECT O.ID FROM FIREBUILDING O, BUILDING B2 WHERE B2.ID = '" + buildingID + "' AND sdo_nn(O.LOCATION,B2.LOCATION,'SDO_NUM_RES=" + nn + "') = 'TRUE' AND O.ID<>B2.ID";
		else if(objectType.equalsIgnoreCase("firehydrant"))
			query = "SELECT O.ID FROM FIREHYDRANT O, BUILDING B2 WHERE B2.ID = '" + buildingID + "' AND sdo_nn(O.LOCATION,B2.LOCATION,'SDO_NUM_RES=" + nn + "') = 'TRUE'";
		
		System.out.println("The query generated -\n" + query);
		ResultSet rs = stmt.executeQuery(query);
		System.out.println("\n" + objectType.toUpperCase() + " ID");
		while(rs.next())
			System.out.println(rs.getString("ID"));		
		stmt.close();
	}
	
	
}
