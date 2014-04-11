package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FormulateSQLQueries {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//CreateFireHydrantQueries("C:\\D Drive\\KNOWLEDGE IS POWER\\DB\\HW2\\cs585-hw2 Spring 2014\\hydrant.xy");
		CreateBuildingQueries("C:\\D Drive\\KNOWLEDGE IS POWER\\DB\\HW2\\cs585-hw2 Spring 2014\\building.xy");
		

	}

	public static void CreateFireHydrantQueries(String fileName) throws IOException{
		File inputFile = new File(fileName);
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		String line = null;
		while((line=br.readLine())!=null){
			String tokens[] = line.split(" ");
			String id = tokens[0].substring(0, tokens[0].length()-1);
			String x = tokens[1].substring(0, tokens[1].length()-1);
			String y = tokens[2];
			String query = "INSERT INTO FIREHYDRANT VALUES ('" + id + "', SDO_GEOMETRY(2001,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1,1),SDO_ORDINATE_ARRAY(" + x +"," + y + ")));"; 
			System.out.println(query);
		}
		br.close();		
	}
	
	public static void CreateBuildingQueries(String fileName) throws IOException{
		File inputFile = new File(fileName);
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		String line = null;
		while((line=br.readLine())!=null){
			String tokens[] = line.split(",");
			String id = tokens[0];
			String name = tokens[1];
			name = name.split(" ")[1];
			String noOfCoordinates = tokens[2];
			String coOrdinates = new String();
			for(int i=3;i<tokens.length-1;i++)
			  coOrdinates += tokens[i]+",";
			coOrdinates += tokens[tokens.length-1];
			String query = "INSERT INTO BUILDING VALUES ('" + id + "','" + name +"', SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY("+ coOrdinates +")));"; 
			System.out.println(query);
		}
		br.close();		
	}
}
