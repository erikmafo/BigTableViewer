# Bigtable Viewer

[![Build Status](https://travis-ci.org/erikmafo/BigtableViewer.svg?branch=master)](https://travis-ci.org/erikmafo/BigtableViewer)

Bigtable viewer is an application that lets you view and query the contents of Google Bigtable tables.

## Installation

Download and run the [installer](https://github.com/erikmafo/BigtableViewer/releases/latest) 
for your operating system

Set the environment variable GOOGLE_APPLICATION_CREDENTIALS to the path of a file containing the 
credentials for a service account with access to your Bigtable tables.

## Usage

When the application has started, click on the 'Add Bigtable instance' button

![Add Bigtable instance](https://user-images.githubusercontent.com/11388438/85906811-bf328200-b80f-11ea-9bf3-13fd426f83ba.png)

Enter the projectId and the instanceId of a Bigtable instance. The application 
should display all tables of the instance:

![Display tables](https://user-images.githubusercontent.com/11388438/85920814-4535e380-b877-11ea-9f95-81924eb57691.png)

Select the table that you would like to view. The application should display the first few rows of the table

![Display rows](https://user-images.githubusercontent.com/11388438/85920819-49fa9780-b877-11ea-9df8-d87f9fd3f50c.png)

By clicking on the 'Configure value types' button you can configure how the application should interpret 
the columns in your table

![Configure value types](https://user-images.githubusercontent.com/11388438/85920859-abbb0180-b877-11ea-9d9e-2833feb1f8e1.png)

Click OK and the application updates the view of the table

![Display rows with value types](https://user-images.githubusercontent.com/11388438/85920860-ad84c500-b877-11ea-91c1-bb16ad1d51aa.png)

## Licence

See [licence file](LICENSE).
