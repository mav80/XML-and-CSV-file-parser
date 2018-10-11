package pl.mav80;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;



public class MainProgram {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("Program CoreServices bootcamp - zadanie 01.");
		
		if(args.length > 0) {
			System.out.println("Dostarczona liczba argumentow " + args.length);
//			for(String arg : args) {
//				System.out.print(arg + ", ");
//			}	
		} else {
			System.out.println("Nie dostalem zadnych argumentow!");
			System.exit(1);
		}
		
		System.out.println("Usuwamy z listy pliki o nieznanych/nieobslugiwanych rozszerzeniach...");
		List<String> finalArgs = removeUnsupportedFilesFromList(args);
		
		if(finalArgs.size() > 0) {
			//System.out.println("Final list of arguments (total: " + finalArgs.size() + ") is:");
//			for(String arg : args) {
//				System.out.print(arg + ", ");
//			}
			System.out.println("Koncowa lista argumentow: " + finalArgs.size() + ", " + (args.length - finalArgs.size()) + " plikow zostalo usunietych z listy.");
		} else {
			System.out.println("Nie znalazlem zadnych obslugiwanych formatow plikow!");
			System.exit(1);
		}
		
		

		
		
		
		//let's parse every file on the list using the method suitable for it's format
		System.out.println("Parsujemy pliki...");
		
		List<Order> orders = new ArrayList<>();
		Pattern patternCSV = Pattern.compile("\\.csv$");
		Pattern patternXML = Pattern.compile("\\.xml$");
		int numberOfFilesParsed = 0;
		
		for(String arg : finalArgs) {
				
			Matcher matcherCSV = patternCSV.matcher(arg.toLowerCase());
			Matcher matcherXML = patternXML.matcher(arg.toLowerCase());
			Boolean resultCSV = matcherCSV.find();
			Boolean resultXML = matcherXML.find();
			//System.out.println(result);
			
			if(resultCSV == true) {
				//System.out.println("Found CSV file, sending for parsing.");
				List ordersParsed = parseCSVfileWithHeaders(arg);
				if(ordersParsed != null) {
					orders.addAll(ordersParsed);
				}
				numberOfFilesParsed++;
			} else 	if(resultXML == true) {
				//System.out.println("Found XML file, sending for parsing.");
				List ordersParsed = parseXMLfile(arg);
				if(ordersParsed != null) {
					orders.addAll(ordersParsed);
				}
				numberOfFilesParsed++;
			} else {
				System.out.println("Nieznany format pliku, pomijam.");
			}
		}
		
		System.out.println("Znaleziono: " + orders.size() + " zamowien w " +  numberOfFilesParsed + " plikach.");
		
		
		
		
		
		
		
		//save orders to database, if any
		if(orders.size() > 0 ) {
			System.out.print("Zapisuje zamowienia do bazy...");
			
			dbConnection connection = new dbConnection();
			
			for(Order order : orders) {			
				try {
					order.addOrderToDB(connection.getConnection());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println("Wystapil blad podczas zapisu do bazy.");
					e.printStackTrace();
				} finally {
					connection.closeConnection();
				}

			}	
			System.out.println(" zrobione.");
			
		}
		

		

		
		
		
		//if there are any orders in our database, let's continue
		if(orders.size() < 1 ) {
			System.out.println("Do bazy nie dodano zadnych zamowien, program konczy dzialanie.");
			System.exit(1);
		}
			
		int userInput = -1;
		
		while (userInput != 0) {
			
			displayProgramMenu();
			userInput = getNumberFromConsole();
			
			if (userInput == 1) {
				dbConnection connection = new dbConnection();
				try {
					int numberOfOrders = Order.getNumberOfOrdersInDB(connection.getConnection());
					//System.out.println("W bazie jest " + numberOfOrders + " zamowien. (Number of orders in database: " + numberOfOrders + ")");
					ArrayList<String> data = new ArrayList<>();
					data.add("Total_number_of_orders_in_database");
					data.add(Integer.toString(numberOfOrders));
					displayOrWriteTofile(data);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					connection.closeConnection();
				}
				
			} else if (userInput == 2) {
				String userId = getStringFromConsole();
				dbConnection connection = new dbConnection();
				try {
					int numberOfOrders = Order.getNumberOfOrdersForASingleClient(connection.getConnection(), userId);
					//System.out.println("Uzytkownik o id  " + userId  + " ma " + numberOfOrders + " zamowien. (User of id " + userId  + " has " + numberOfOrders + " orders)");
					ArrayList<String> data = new ArrayList<>();
					data.add("Client_Id,Number_of_user_orders_in_database");
					data.add(userId+","+Integer.toString(numberOfOrders));
					displayOrWriteTofile(data);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					connection.closeConnection();
				}
				
			} else if (userInput == 3) {
				dbConnection connection = new dbConnection();
				try {
					double valueOfAllOrders = Order.getTotalValueOfOrdersInDB(connection.getConnection());
					//System.out.println("Laczna kwota zamowien w bazie: " + round(valueOfAllOrders, 2) + ". (Total value of orders in database: " + valueOfAllOrders + ")");
					ArrayList<String> data = new ArrayList<>();
					data.add("Total_value_of_orders_in_database");
					data.add(Double.toString(valueOfAllOrders));
					displayOrWriteTofile(data);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					connection.closeConnection();
				}
				
			} else if (userInput == 4) {
				System.out.println("Podaj nazwe (ClientId) uzytkownika.");
				String userId = getStringFromConsole();
				dbConnection connection = new dbConnection();
				try {
					double valueOfAllOrdersForASingleClient = Order.getTotalValueOfOrdersForASingleClient(connection.getConnection(), userId);
					//System.out.println("Laczna kwota zamowien dla klienta o id " + userId + " wynosi "+ round(valueOfAllOrdersForASingleClient, 2) + ". (Total value of orders for a client of id " + userId + 
					//		" equals " + round(valueOfAllOrdersForASingleClient, 2) + ")");
					ArrayList<String> data = new ArrayList<>();
					data.add("Client_Id,Total_value_of_single_user_orders");
					data.add(userId+","+Double.toString(valueOfAllOrdersForASingleClient));
					displayOrWriteTofile(data);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					connection.closeConnection();
				}
				
			} else if (userInput == 5) {
				dbConnection connection = new dbConnection();
				try {
					ArrayList<Order> allOrders = Order.loadAllOrders(connection.getConnection());
					//System.out.println("Oto wszystkie zamownienia znajdujace sie w bazie (Here are all the orders in database):");
					if(!allOrders.isEmpty()) {
						ArrayList<String> data = new ArrayList<>();
						data.add("ClientId,Requestid,Name,Quantity,Price");
						
						for(Order order : allOrders) {
							//System.out.println(order.toString());
							data.add(order.getClientId()+","+order.getRequestId()+","+order.getName()+","+order.getQuantity()+","+order.getPrice());
						}
						
						displayOrWriteTofile(data);
					} else {
						System.out.println("Brak zamowien w bazie (No orders in database).");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					connection.closeConnection();
				}
				
			} else if (userInput == 6) {
				System.out.println("Podaj nazwe (ClientId) uzytkownika.");
				String userId = getStringFromConsole();
				dbConnection connection = new dbConnection();
				try {
					ArrayList<Order> allOrdersForASingleUser = Order.loadAllOrdersForASingleUser(connection.getConnection(), userId);
					//System.out.println("Oto wszystkie zamownienia dla klienta o id " + userId + " (Here are all the orders for a client of id " + userId +"):");
					if(!allOrdersForASingleUser.isEmpty()) {
						ArrayList<String> data = new ArrayList<>();
						data.add("ClientId,Requestid,Name,Quantity,Price");
						for(Order order : allOrdersForASingleUser) {
							//System.out.println(order.toString());
							data.add(order.getClientId()+","+order.getRequestId()+","+order.getName()+","+order.getQuantity()+","+order.getPrice());
						} 
						
						displayOrWriteTofile(data);
					} else {
						System.out.println("Brak zamowien w bazie (No orders in database).");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					connection.closeConnection();
				}
				
			} else if (userInput == 7) {
				dbConnection connection = new dbConnection();
				try {
					double averageValueOfAnOrder = Order.countAverageValueOfAllOrders(connection.getConnection());
					//System.out.println("Srednia wartosc zamowienia: " + round(averageValueOfAnOrder, 2) + ". (Average value of an order: " + round(averageValueOfAnOrder, 2)  + ")");
					ArrayList<String> data = new ArrayList<>();
					data.add("Average_value_of_orders_in_database");
					data.add(Double.toString(round(averageValueOfAnOrder, 2)));
					displayOrWriteTofile(data);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					connection.closeConnection();
				}
				
			} else if (userInput == 8) {
				System.out.println("Podaj nazwe (ClientId) uzytkownika.");
				String userId = getStringFromConsole();
				dbConnection connection = new dbConnection();
				try {
					double averageValueOfAnOrder = Order.countAverageValueOfOrdersOfASingleUser(connection.getConnection(), userId);
					//System.out.println("Srednia wartosc zamowienia uzytkownika o id: " + userId + " wynosi " + round(averageValueOfAnOrder, 2) + ". (Average value of an order for user of an id  " + userId +
					//		 " is " +round(averageValueOfAnOrder, 2)  + ")");
					ArrayList<String> data = new ArrayList<>();
					data.add("Client_Id,Average_user_order_value");
					data.add(userId+","+Double.toString(round(averageValueOfAnOrder, 2)));
					displayOrWriteTofile(data);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					connection.closeConnection();
				}
				
			} else if (userInput == 0) {
				System.out.println("\nKoniec programu.");
				
			} else {
				System.out.println("\nNieprawidłowe polecenie - wpisz jeszcze raz..");
			}
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

	} //end of main method
	
	
	
	
	
	public static List<String> removeUnsupportedFilesFromList(String[] args) {
		
		List<String> finalArgs = new ArrayList<>();
			
		Pattern pattern = Pattern.compile("\\.csv$|\\.xml$");
		//System.out.println("Checking arguments...");
		
		for(String arg : args) {
			//System.out.print(arg + ", ");
			Matcher matcher = pattern.matcher(arg.toLowerCase());
			Boolean result = matcher.find();
			//System.out.println(result);
			
			if(result == true) {
				finalArgs.add(arg);
			}
		}
		
		return finalArgs;
		
	}
	
	
	
	
	
	
	
	public static List<Order> parseCSVfileWithHeaders(String pathToFile) {
		
		List<Order> parsedOrders = new ArrayList<>();
		
		try (
	            Reader reader = Files.newBufferedReader(Paths.get(pathToFile));
	            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
	                    .withFirstRecordAsHeader()
	                    .withIgnoreHeaderCase()
	                    .withTrim());
	        ) {
	            for (CSVRecord csvRecord : csvParser) {
	                // Accessing values by Header names
	                String ClientId = csvRecord.get("Client_Id");
	                String RequestId = csvRecord.get("Request_Id");
	                String Name = csvRecord.get("Name");
	                String Quantity = csvRecord.get("Quantity");
	                String Price = csvRecord.get("Price");

//	                System.out.println("Record No - " + csvRecord.getRecordNumber());
//	                System.out.println("---------------");
//	                System.out.println("ClientId : " + ClientId);
//	                System.out.println("RequestId : " + RequestId);
//	                System.out.println("Name : " + Name);
//	                System.out.println("Quantity : " + Quantity);
//	                System.out.println("Price : " + Price);
//	                System.out.println("---------------\n\n");
	                
	                Order order = new Order();
	                
	                if(ClientId.length() > 6 || ClientId.length() < 1 ||ClientId.contains(" ")) {
	                	System.out.println("Bledne pole ClientId podczas parsowania pliku CSV, zamowienie nie zostanie dodane do bazy.");
	                	continue;
                	 	//order.setClientId(null);
	                } else {
	                	order.setClientId(ClientId);
	                }
	                
	                                
	                	
	                
	                if(!RequestId.isEmpty()) {
	                	int fieldValue = -1;
	                	
	                	try {
							fieldValue = Integer.parseInt(RequestId);
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
	                	
	                	if(fieldValue < 1) {
	                		System.out.println("Bledne pole RequestId podczas parsowania pliku CSV, zamowienie nie zostanie dodane do bazy.");
	                		continue;
	                		//order.setRequestId(0);
	                	} else {
	                		order.setRequestId(fieldValue);
	                	}
	                } else {
                		System.out.println("Bledne pole RequestId podczas parsowania pliku CSV, zamowienie nie zostanie dodane do bazy.");
                		continue;
                		//order.setRequestId(0);
	                }
	                
	                
	                
	                
	                
	                if(Name.isEmpty() || Name.length() > 255) {
                		System.out.println("Bledne pole Name podczas parsowania pliku CSV, zamowienie nie zostanie dodane do bazy.");
                		continue;
                		//order.setName(null);
	                } else {
	                	order.setName(Name);
	                }
	                
	                
	                                
	                
	                	                
	                if(!Quantity.isEmpty()) {
	                	int fieldValue = -1;
	                	
	                	try {
							fieldValue = Integer.parseInt(Quantity);
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
	                	
	                	if(fieldValue < 1) {
	                		System.out.println("Bledne pole Quantity podczas parsowania pliku CSV, zamowienie nie zostanie dodane do bazy.");
	                		continue;
	                		//order.setQuantity(0);
	                	} else {
	                		order.setQuantity(fieldValue);
	                	}
	                } else {
                		System.out.println("Bledne pole Quantity podczas parsowania pliku CSV, zamowienie nie zostanie dodane do bazy.");
                		continue;
                		//order.setQuantity(0);
	                }
	                
	                            
	                
	                
	                
	                if(!Price.isEmpty()) {
	                	double fieldValue = -1;
	                	
	                	try {
							fieldValue = Double.parseDouble(Price);
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
	                	
	                	if(fieldValue < 1) {
	                		System.out.println("Bledne pole Price podczas parsowania pliku CSV, zamowienie nie zostanie dodane do bazy.");
	                		continue;
	                		//order.setPrice(0);
	                	} else {
	                		order.setPrice(fieldValue);
	                	}
	                } else {
                		System.out.println("Bledne pole Price podczas parsowania pliku CSV, zamowienie nie zostanie dodane do bazy.");
                		continue;
                		//order.setPrice(0);
	                }
	                
	                parsedOrders.add(order);
	                
	                
	            }
	        } catch (IOException e) {
				// TODO Auto-generated catch block
	        	System.out.println("Error parsing CSV file " + pathToFile + " - doesn't exist?");
				//e.printStackTrace();
			} 
		

		
		return parsedOrders;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static List<Order> parseXMLfile(String pathToFile) {
		
		List<Order> xmlOrders = null;
		Order xmlOrder = null;
		String text = null;


		XMLInputFactory factory = XMLInputFactory.newInstance();
		try {
			XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(new File(pathToFile)));
			
	          while (reader.hasNext()) {
	        	  
	        		  	
	               int Event = reader.next();

	               switch (Event) {
	                    case XMLStreamConstants.START_ELEMENT: {
	
	                         if ("request".equals(reader.getLocalName())) {
	                        	 xmlOrder = new Order();
	                         }

	                         if ("requests".equals(reader.getLocalName()))
	                        	 xmlOrders = new ArrayList<>();
	                         break;
	                    }
	
	                    case XMLStreamConstants.CHARACTERS: {
	                         text = reader.getText().trim();
	                         break;
	                    }
	
	                    case XMLStreamConstants.END_ELEMENT: {
	                         switch (reader.getLocalName()) {
	
	                              case "request": {
	                            	  //xmlOrders.add(xmlOrder);
	                                  break;
	                              }
	
	                              case "clientId": {        	  
	              	                if(text.length() > 6 || text.length() < 1 || text.contains(" ")) {
	            	                	System.out.println("Bledne pole ClientId podczas parsowania pliku XML, zamowienie nie zostanie dodane do bazy.");
	            	                } else {
	            	                	xmlOrder.setClientId(text);
	            	                }
	              	                break;
	                              }
	
	                              case "requestId": {
                            	  try {
										xmlOrder.setRequestId(Long.parseLong(text));
									} catch (NumberFormatException e) {
										// TODO Auto-generated catch block
										//e.printStackTrace();
									}
                            	  
                            	  if(xmlOrder.getRequestId() < 1) {
                            		  System.out.println("Bledne pole RequestId podczas parsowania pliku XML, zamowienie nie zostanie dodane do bazy.");
                            	  }
	                                break;
	                              }
	
	                              case "name": {
	              	                if(text.isEmpty() || text.length() > 255) {
	                            		System.out.println("Bledne pole Name podczas parsowania pliku XML, zamowienie nie zostanie dodane do bazy.");
	            	                } else {
	            	                	xmlOrder.setName(text);
	            	                }
	              	                break;
	                              }
	
	                              case "quantity": {
	                            	try {
										xmlOrder.setQuantity(Integer.parseInt(text));
									} catch (NumberFormatException e) {
										//e.printStackTrace();
									}
	                            	  
	                            	  if(xmlOrder.getQuantity() < 1) {
	                            		  System.out.println("Bledne pole Quantity podczas parsowania pliku XML, zamowienie nie zostanie dodane do bazy.");
	                            	  }
	                            	break;
	                              }
	                              
	                              case "price": {
	                            	  try {
										xmlOrder.setPrice(Double.parseDouble(text));
									} catch (NumberFormatException e) {
										//e.printStackTrace();
									}
	                            	  
	                            	  if(xmlOrder.getPrice() == 0) {
	                            		  System.out.println("Bledne pole Price podczas parsowania pliku XML, zamowienie nie zostanie dodane do bazy.");
	                            	  }
	                            	  
	                            	  
	                            	  //check if the order doesn't have any errors, add to the list if it does not
	                            	  if(xmlOrder.getPrice() == 0 || xmlOrder.getQuantity() < 1 || xmlOrder.getName() == null || xmlOrder.getName().length() < 1 || xmlOrder.getName().length() > 255 || xmlOrder.getRequestId() < 1 
	                            			  || xmlOrder.getClientId() == null || xmlOrder.getClientId().length() > 6 || xmlOrder.getClientId().length() < 1 || xmlOrder.getClientId().contains(" ")) {
	                            		  System.out.println("Blad podczas parsowania pliku XML, zamowienie nie zostanie dodane do bazy.");
	                            		  break;
	                            	  } else {
	                            		  xmlOrders.add(xmlOrder);
	                            	  }
	                            	  
	                            	  
	                            	  
	                            	break;
	                              }
	
	                         }
	                         break;
	                    }
	               }
	          }
		} catch (FileNotFoundException e) {
			System.out.println("Error parsing XML file " + pathToFile + " - doesn't exist?");
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (XMLStreamException e) {
			System.out.println("Parsing exception of XML file.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return xmlOrders; 
	}
	
	
	
	
	
	
	
	
	
	
	
	public static void displayProgramMenu() {
		System.out.println("\nProgram menu:\n");
		System.out.println("1 - ilosc zamowien lacznie");
		System.out.println("2 - ilosc zamowien do klienta o wskazanym identyfikatorze");
		System.out.println("3 - laczna kwota zamowien (total sum of orders)");
		System.out.println("4 - laczna kwota zamowien do klienta o wskazanym identyfikatorze");
		System.out.println("5 - lista wszystkich zamowien (list of all orders)");
		System.out.println("6 - lista zamowien do klienta o wskazanym identyfikatorze");
		System.out.println("7 - srednia wartosc zamowienia ");
		System.out.println("8 - srednia wartosc zamowienia do klienta o wskazanym identyfikatorze");
		System.out.println("\n0 - koniec programu\n");
		
	}
	
	
	static int getNumberFromConsole() {
		 
 		@SuppressWarnings("resource")
 		Scanner myScanner = new Scanner(System.in);
 		int number;
 		System.out.println("Wpisz liczbe:");
 
 		try {
 			number = myScanner.nextInt();
 		} catch (Exception e) {
 			System.out.println("To nie jest liczba!");
 			number = getNumberFromConsole();
 		}
 		return number;
 	}
	
	
	static String getStringFromConsole() {
		Scanner myScanner = new Scanner(System.in);
		System.out.println("Wpisz tekst:");
		String string = myScanner.nextLine();
		return string;
	}
	
	
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	
	
	
	
	public static void displayOrWriteTofile(ArrayList<String> data) {		
		int userInput = -1;
		
		while (userInput != 0) { 
			System.out.println("\nRaport jest gotowy, co chcesz teraz zrobic? 1 - wyświetlic na ekranie, 2 - zapisac do pliku, 0 - powrot do menu");
			userInput = getNumberFromConsole();
			
			if(userInput == 1) {
				System.out.println("RAPORT:\n\n");
				for(String line: data) {
					System.out.println(line);
				}
				System.out.println("\n\n");
			} 
			
			else if (userInput == 2) {
					
				String fileName = "";
				
				while(fileName.isEmpty() || fileName.contains(" ")) {
					System.out.println("\nPodaj nazwe pliku do zapisu. Nazwa nie moze byc pusta i nie moze zawierac spacji. Format zapisywanych danych to CSV.");
					fileName = getStringFromConsole();
				}
				writeToFile(fileName, data);
				
			}

			
		}
		
		
	}
	
	
	
	
	public static void writeToFile(String fileName, ArrayList<String> data) {
		Path pathToFile = Paths.get(fileName);
		try {
			Files.write(pathToFile, data);
			System.out.println("Zapis do pliku udany.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Wystapil blad podczas zapisu do pliku");
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}




















































//let's test adding to database

		/*
		Order order = new Order("Clnt 1", 1, "Mutton", 7, 10);
		order.setId(2);
		
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
		*/
		
		//works fine
		
















//for a file with no header - works

//String SAMPLE_CSV_FILE_PATH = "/home/mav/workspace/WorkInterviewTasks/CoreServices/testFile01noheader.csv";
//
//try (
//        Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
//        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
//    ) {
//        for (CSVRecord csvRecord : csvParser) {
//            // Accessing Values by Column Index
//            String ClientId = csvRecord.get(0);
//            String RequestId = csvRecord.get(1);
//            String Name = csvRecord.get(2);
//            String Quantity = csvRecord.get(3);
//            String Price = csvRecord.get(4);
//
//            System.out.println("Record No - " + csvRecord.getRecordNumber());
//            System.out.println("---------------");
//            System.out.println("ClientId : " + ClientId);
//            System.out.println("RequestId : " + RequestId);
//            System.out.println("Name : " + Name);
//            System.out.println("Quantity : " + Quantity);
//            System.out.println("Price : " + Price);
//            System.out.println("---------------\n\n");
//        }
//    } catch (IOException e) {
//		// TODO Auto-generated catch block
//    	System.out.println("Error reading file without header - doesn't exist?");
//		e.printStackTrace();
//	}






///for a file with headers - works also


//System.out.println("------------------------------------------- and now file with headers -----------------------");
//
//
//SAMPLE_CSV_FILE_PATH = "/home/mav/workspace/WorkInterviewTasks/CoreServices/testFile01.csv";
//
//
//try (
//        Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
//        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
//                .withFirstRecordAsHeader()
//                .withIgnoreHeaderCase()
//                .withTrim());
//    ) {
//        for (CSVRecord csvRecord : csvParser) {
//            // Accessing values by Header names
//            String ClientId = csvRecord.get("Client_Id");
//            String RequestId = csvRecord.get("Request_Id");
//            String Name = csvRecord.get("Name");
//            String Quantity = csvRecord.get("Quantity");
//            String Price = csvRecord.get("Price");
//
//            System.out.println("Record No - " + csvRecord.getRecordNumber());
//            System.out.println("---------------");
//            System.out.println("ClientId : " + ClientId);
//            System.out.println("RequestId : " + RequestId);
//            System.out.println("Name : " + Name);
//            System.out.println("Quantity : " + Quantity);
//            System.out.println("Price : " + Price);
//            System.out.println("---------------\n\n");
//        }
//    } catch (IOException e) {
//		// TODO Auto-generated catch block
//    	System.out.println("Error reading file with header - doesn't exist?");
//		e.printStackTrace();
//	}




















//here we fiddle with xml parsing
//works, now let's turn it into a method
/*	
List<Order> xmlOrders = null;
Order xmlOrder = null;
String text = null;


XMLInputFactory factory = XMLInputFactory.newInstance();
try {
	XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(new File("/home/mav/workspace/WorkInterviewTasks/CoreServices/testFile02.Xml")));
	
	
      while (reader.hasNext()) {

           int Event = reader.next();



           switch (Event) {

                case XMLStreamConstants.START_ELEMENT: {

                     if ("request".equals(reader.getLocalName())) {
                    	 xmlOrder = new Order();
                     }

                     if ("requests".equals(reader.getLocalName()))
                    	 xmlOrders = new ArrayList<>();

                     break;

                }

                case XMLStreamConstants.CHARACTERS: {
                     text = reader.getText().trim();
                     break;

                }

                case XMLStreamConstants.END_ELEMENT: {

                     switch (reader.getLocalName()) {

                          case "request": {
                        	  xmlOrders.add(xmlOrder);
                              break;
                          }

                          case "clientId": {
                        	  
          	                if(text.length() > 6 || text.length() < 1 ||text.contains(" ")) {
        	                	System.out.println("Wrong value of the field ClientId while parsing XML order, field will have null value.");
        	                	xmlOrder.setClientId(null);
        	                } else {
        	                	xmlOrder.setClientId(text);
        	                }

          	                break;
                          }

                          case "requestId": {
                        	  try {
								xmlOrder.setRequestId(Long.parseLong(text));
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                              break;
                          }

                          case "name": {
                        	  
          	                if(text.isEmpty() || text.length() > 255) {
                        		System.out.println("Wrong value of the field Name while parsing XML order, field will have null value.");
                        		xmlOrder.setName(null);
        	                } else {
        	                	xmlOrder.setName(text);
        	                }

          	                break;
                          }

                          case "quantity": {
                        	  try {
								xmlOrder.setQuantity(Integer.parseInt(text));
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                        	  break;
                          }
                          
                          case "price": {
                        	  try {
								xmlOrder.setPrice(Double.parseDouble(text));
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
                        	  break;
                          }

                     }

                     break;

                }

           }

      }
      
      //print all orders found
      
      dbConnection connection = new dbConnection();
      
      
      for(Order order : xmlOrders) {
			System.out.println(order.toString());
			System.out.println("Saving order to database.");
			
			try {
				order.addOrderToDB(connection.getConnection());
				System.out.println("XML order scuccessfully added to database.");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("Error occured when adding XML order to database.");
				e.printStackTrace();
			} finally {
				connection.closeConnection();
			}
			
      }


	
} catch (FileNotFoundException e) {
	System.out.println("XML file not found!");
	// TODO Auto-generated catch block
	e.printStackTrace();
} catch (XMLStreamException e) {
	System.out.println("Parsing exception of XML file.");
	// TODO Auto-generated catch block
	e.printStackTrace();
}
*/






























//
//System.out.println("---------------------------------- here we parse csv  file -------------------------------------");
//
//
//
//
//
//System.out.println("Sending CSV file to method for parsing...");
//
//String SAMPLE_CSV_FILE_PATH = "/home/mav/workspace/WorkInterviewTasks/CoreServices/testFile01.csv";
//
//List<Order> orders = new ArrayList<>();
//orders = parseCSVfileWithHeaders(SAMPLE_CSV_FILE_PATH);
//
//if(orders.size()  > 0) {
//	System.out.println("...parsed. Returned " + orders.size() + " orders. Here they are:");
//	
//	dbConnection connection = new dbConnection();
//	
//	for(Order order : orders) {
//		System.out.println(order.toString());
//		System.out.println("Saving CSV order to database.");
//		
//		try {
//			order.addOrderToDB(connection.getConnection());
//			System.out.println("Order scuccessfully added to database.");
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			System.out.println("Error occured when adding CSV order to database.");
//			e.printStackTrace();
//		} finally {
//			connection.closeConnection();
//		}
//
//	}	
//} else {
//	System.out.println("Parsing CSV didn't return any valid orders!");
//}
//
//
//
//
//
//
//
//
//
//
//
//
//System.out.println("---------------------------------- here we parse xml file -------------------------------------");
//
//System.out.println("Sending XML file to method for parsing...");
//
//
//List<Order> XMLorders = new ArrayList<>();
//XMLorders = parseXMLfile("/home/mav/workspace/WorkInterviewTasks/CoreServices/testFile02.Xml");
//
//if(XMLorders.size()  > 0) {
//	System.out.println("...parsed. Returned " + XMLorders.size() + " orders. Here they are:");
//	
//	dbConnection connection = new dbConnection();
//	
//	for(Order order : XMLorders) {
//		System.out.println(order.toString());
//		System.out.println("Saving XML order to database.");
//		
//		try {
//			order.addOrderToDB(connection.getConnection());
//			System.out.println("XML order scuccessfully added to database.");
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			System.out.println("Error occured when adding XML order to database.");
//			e.printStackTrace();
//		} finally {
//			connection.closeConnection();
//		}
//
//	}	
//} else {
//	System.out.println("Parsing XML didn't return any valid orders!");
//}



