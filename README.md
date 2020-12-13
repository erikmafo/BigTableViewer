# Bigtable Viewer

[![Build Actions Status](https://github.com/erikmafo/BigtableViewer/workflows/Build/badge.svg)](https://github.com/erikmafo/BigtableViewer/actions)
[![CodeQL Analysis Actions Status](https://github.com/erikmafo/BigtableViewer/workflows/CodeQL/badge.svg)](https://github.com/erikmafo/BigtableViewer/actions)

Bigtable viewer is an application that lets you view and query the contents of Google Bigtable tables with SQL. 

## Installation

Download and run the [installer](https://github.com/erikmafo/BigtableViewer/releases/latest) 
for your operating system

Set the environment variable GOOGLE_APPLICATION_CREDENTIALS to the path of a file containing the 
credentials for a service account with access to your Bigtable tables.

## Basic Usage

When the application has started, click on the 'Add instance' button

Enter the project and instance id of a Bigtable instance. The application should display all the tables of the instance.

Click on the table that you would like to query. A default query, selecting the first 1000 rows of the selected table, 
should appear in the query box. Click 'Execute' to execute the query.

To use different credentials than GOOGLE_APPLICATION_CREDENTIALS, click on 'File'->'Set credentials'

## Table schema

Bigtable viewer works best with tables where the data in each column corresponds to a standard data type. By clicking 
on 'Table settings' you can configure how the application should interpret the columns in your table.

## Query examples

Use 'KEY' to reference the row key:
```sql
SELECT * FROM 'table-0' WHERE KEY LIKE 'row-.*' LIMIT 1000
```
Use "." to reference a column in a column family:
```sql
SELECT * FROM 'table-0' WHERE myFamily.myIntColumn > 0 LIMIT 1000
```
Filter rows based on the value of a column or row key by using standard operators '>', '<', '>=', '<=', '=', '!=', e.g.
```sql
SELECT * FROM 'table-0' WHERE myFamily.myStringColumn = 'foo' LIMIT 1000
```
Use 'AND' to combine filters:
```sql
SELECT * FROM 'table-0' WHERE KEY LIKE 'rowkey' AND myFamily.myStringColumn = 'foo' LIMIT 1000
```
Only select data from a specific column family:
```sql
SELECT myFamily FROM 'table-0' LIMIT 1000
```
or list of columns:
```sql
SELECT myFamily.column1, myFamily.column2  FROM 'table-0' LIMIT 1000
```
To work with reverse row keys, use the built-in REVERSE function:
```sql
SELECT * FROM 'table-0' WHERE KEY LIKE REVERSE('yekwor') LIMIT 1000
```
in combination with CONCAT:
```sql
SELECT * FROM 'table-0' WHERE KEY LIKE CONCAT(REVERSE('yekwor'), '#', '2020-12.*') LIMIT 1000
```
## Licence

See [licence file](LICENSE).
