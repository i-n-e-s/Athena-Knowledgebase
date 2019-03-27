# Athena Knowledgebase

## Overview

![Project structure](https://github.com/bl4ckscor3/BP-Athena/raw/master/DatabaseDoc/Program%20scheme.png)

The following sections intend to outline features/facts of this program

### Access
- Query the database via our REST API
- Programme options options to scrape only selected conferences

### Backend
- Based on JPA via Hibernate
- Largely database agnostic

### Wiki
- Found at https://github.com/bl4ckscor3/BP-Athena/wiki
- Well documented, containing explanations of all functionality, including the API
- Database schematic

### Data
- Supports scraping the ACL anthology
- Provides the data structure necessary for storing information on the authors and papers
- General schedule data as well as information of the Institution and conference itself is stored
- The necessary database structure for Semantic Scholar data (see below) is present

### Special API accesses
For information not suited for immediate storage in the database, a special API process has been included, providing the following functionality:

- OpenStreeMap data
	- Query locations of interest close to you (depends on locatoin data)
- Semantic Scholar
	- On demand: Find out and store information about authors and papers not provided by t he default scraping functionality, such as:
		- An author's top 5 influences
		- More robust author identification
		- Get all other papers of an author
		- Paper abstracts
		- Find out how often a paper has been cited
		- ...

## Code Conventions:
 
All submitted code should follow the Google Java Guidelines, except for the following changes:

- (Section 2.3.1)	Tabs are used for indentation and should be represented with 4 whitespace characters
- (Section 4.2) 	Each time a new block or block-like construct is opened, the indent increases by one Tab
- (Section 4.4) 	The column limit doesn't have to be followed //This is done because some of the frameworks have very long names, so you would often have to rip apart your code without adding readability