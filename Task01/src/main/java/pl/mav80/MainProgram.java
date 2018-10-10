package pl.mav80;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
		
		System.out.println("Program CoreServices bootcamp - task 1.");
		
		if(args.length > 0) {
			System.out.println("Number of the arguments I got: " + args.length);
//			for(String arg : args) {
//				System.out.print(arg + ", ");
//			}	
		} else {
			System.out.println("I didn't get any arguments when executing program!");
			System.exit(1);
		}
		
		System.out.println("Now attempting to remove from the arguments list files with unknown or unsupported extensions...");
		List<String> finalArgs = removeUnsupportedFilesFromList(args);
		
		if(finalArgs.size() > 0) {
			//System.out.println("Final list of arguments (total: " + finalArgs.size() + ") is:");
//			for(String arg : args) {
//				System.out.print(arg + ", ");
//			}
			System.out.println("Final list of arguments: " + finalArgs.size() + ", " + (args.length - finalArgs.size()) + " filenames were removed.");
		} else {
			System.out.println("No supported files were found on arguments list!");
			System.exit(1);
		}
		
		

		
		
		
		
		
		
		//let's parse every file on the list using the method suitable for it's format
		System.out.println("Parsing files from arguments...");
		
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
				System.out.println("Unknown file format, skipping file.");
			}
			
			
			
		}
		
		System.out.println("Found: " + orders.size() + " orders in " +  numberOfFilesParsed + " files.");
		
		if(orders.size() > 0 ) {
			System.out.print("Writing orders to database...");
			
			dbConnection connection = new dbConnection();
			
			for(Order order : orders) {			
				try {
					order.addOrderToDB(connection.getConnection());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println("Error occured when adding order to database.");
					e.printStackTrace();
				} finally {
					connection.closeConnection();
				}

			}	
			System.out.println(" done.");
		}
		
		
		if(orders.size() < 1 ) {
			System.out.println("0 orders were added to database, program will now terminate.");
			System.exit(1);
		}
		System.out.println("\nProgram menu:");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

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
	                	System.out.println("Wrong value of the field ClientId while parsing order, field will have null value.");
                	 	order.setClientId(null);
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
	                		System.out.println("Wrong value of the field RequestId while parsing order, field will have 0 value.");
	                		order.setRequestId(0);
	                	} else {
	                		order.setRequestId(fieldValue);
	                	}
	                } else {
                		System.out.println("Wrong value of the field RequestId while parsing order, field will have 0 value.");
                		order.setRequestId(0);
	                }
	                
	                
	                
	                
	                
	                if(Name.isEmpty() || Name.length() > 255) {
                		System.out.println("Wrong value of the field Name while parsing order, field will have null value.");
                		order.setName(null);
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
	                		System.out.println("Wrong value of the field Quantity while parsing order, field will have 0 value.");
	                		order.setQuantity(0);
	                	} else {
	                		order.setQuantity(fieldValue);
	                	}
	                } else {
                		System.out.println("Wrong value of the field Quantity while parsing order, field will have 0 value.");
                		order.setQuantity(0);
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
	                		System.out.println("Wrong value of the field Price while parsing order, field will have 0 value.");
	                		order.setPrice(0);
	                	} else {
	                		order.setPrice(fieldValue);
	                	}
	                } else {
                		System.out.println("Wrong value of the field Price while parsing order, field will have 0 value.");
                		order.setPrice(0);
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
		
		System.out.println(pathToFile);
		
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
		} catch (FileNotFoundException e) {
			System.out.println("Error parsing CSV file " + pathToFile + " - doesn't exist?");
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (XMLStreamException e) {
			System.out.println("Parsing exception of XML file.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return xmlOrders; 
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



