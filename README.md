# XML and CSV file parser

This program takes a list of XML and CSV files as a parameter and parses them to extract orders in the following format:

CSV:

```
Client_Id,Request_id,Name,Quantity,Price
1,1,Bułka,1,10.00
1,1,Chleb,2,15.00
1,2,Chleb,5,15.00
2,1,Chleb,1,10.00
```


XML:

```
<requests>
  <request>
    <clientId>1</clientId>
    <requestId>1</requestId>
    <name>Bułka</name>
    <quantity>1</quantity>
    <price>10.00</price>
  </request>
</requests>
```

Orders are then added to in-memory database (created on program start) and user can generate following raports:

1. total number of all orders in database
2. number of orders to a client of certain id
3. total value of all orders in database
4. total value of orders to a client of certain id
5. list of all orders in database
6. list of all orders to a client of certain id
7. average value of an order in a database
8. average value of an order to a client of certain id

Reports can be then displayed on screen or written to a file.
