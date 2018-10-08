package pl.mav80;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainProgram {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("Program CoreServices bootcamp - zadanie 1.");
		
		if(args.length > 0) {
			System.out.println("Oto lista argumentów które dostałem (łącznie: " + args.length + ") :");
			for(String arg : args) {
				System.out.print(arg + ", ");
			}	
		} else {
			System.out.println("Nie otrzymałem żadnych argumentów podczas uruchomienia!");
			System.exit(1);
		}
		
		System.out.println("Usuwam z argumentów pliki o nieznanych lub nieobsługiwanych rozszerzeniach...");
		List<String> finalArgs = removeUnsupportedFilesFromList(args);
		
		
		if(finalArgs.size() > 0) {
			System.out.println("Ostateczna lista argumentów (łącznie: " + finalArgs.size() + ") wygląda następująco:");
			for(String arg : args) {
				System.out.print(arg + ", ");
			}
			System.out.println("odsialiśmy " + (args.length - finalArgs.size()) + " nazw plików.");
		} else {
			System.out.println("Na liście nie było żadnych obsługiwanych typów plików!");
			System.exit(1);
		}
		
		
		
		
		
		//let's test adding to database
		
		Order order = new Order("Clnt 1", 1, "Bread", 7, 10);
		
		dbConnection connection = new dbConnection();
		try {
			order.addOrderToDB(connection.getConnection());
			System.out.println("Order scuccessfully added to database.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error occured when adding order to database.");
			e.printStackTrace();
		} finally {
			connection.closeConnection();
		}
		
		
		
		
		
		

	} //end of main method
	
	
	
	
	
	public static List<String> removeUnsupportedFilesFromList(String[] args) {
		
		List<String> finalArgs = new ArrayList<>();
		
		//if(args.length > 0) { //długość listy argumentów zawsze będzie większa od 0 bo sprawdzamy to na samym początku
			
		Pattern pattern = Pattern.compile("\\.csv$|\\.xml$");
		System.out.println("Sprawdzam argumenty...");
		
		for(String arg : args) {
			System.out.print(arg + ", ");
			Matcher matcher = pattern.matcher(arg.toLowerCase());
			Boolean result = matcher.find();
			System.out.println(result);
			
			if(result == true) {
				finalArgs.add(arg);
			}
		}	
		//} else {
		//	System.out.println("Nie otrzymałem żadnych argumentów podczas uruchomienia!");
		//	System.exit(1);
		//}
		
		return finalArgs;
		
	}

}
