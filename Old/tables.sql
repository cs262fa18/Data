--
-- This SQL script builds a database, deleting any pre-existing version.
--
-- @author tjv37
--

-- Drop previous versions of the tables if they they exist, in reverse order of foreign keys.
DROP TABLE IF EXISTS Times;
DROP TABLE IF EXISTS Projects;
DROP TABLE IF EXISTS Employees;

-- Create the schema.
CREATE TABLE Employees (
	ID int primary key,
	name varchar(128)
);

CREATE TABLE Projects (
	ID int primary key,
	name varchar(128),
	managerID int references Employees(ID)
);

CREATE TABLE Times (
	ID int primary key,
	startTime timestamp,
	endTime timestamp,
	employeeID int references Employees(ID),
	projectID int references Projects(ID)
);
	

-- Allow users to select data from the tables.
GRANT SELECT ON Employees TO PUBLIC;
GRANT SELECT ON Projects TO PUBLIC;
GRANT SELECT ON Times TO PUBLIC;

-- Add sample records.
INSERT INTO Employees VALUES (1, 'Joe');
INSERT INTO Employees VALUES (2, 'John');
INSERT INTO Employees VALUES (3, 'Jay');
INSERT INTO Employees VALUES (4, 'Jordan');
INSERT INTO Employees VALUES (5, 'Joe');

INSERT INTO Projects VALUES (1, 'Alpha', 3);
INSERT INTO Projects VALUES (2, 'Beta', 3);
INSERT INTO Projects VALUES (3, 'Gamma', 5);
INSERT INTO Projects VALUES (4, 'Zeta', 5);

INSERT INTO Times VALUES (1, '2018-10-25 10:23:54', 
			     '2018-10-25 12:23:54', 2, 1);
INSERT INTO Times VALUES (2, '2018-10-25 10:23:54', 
			     '2018-10-25 12:23:54', 1, 1);
INSERT INTO Times VALUES (3, '2018-10-25 10:23:54', 
			     '2018-10-25 12:23:54', 4, 1);
INSERT INTO Times VALUES (4, '2018-10-25 13:23:54', 
			     '2018-10-25 15:23:54', 2, 1);

