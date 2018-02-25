#Dikshya Acharya
#ATM Database Management System

CREATE DATABASE db_ATM;
GRANT ALL PRIVILEGES ON db_ATM.* TO 'student'@'localhost'identified by'student';
FLUSH PRIVILEGES;
use db_ATM;

# Customer table holding Cutomer info and password
CREATE TABLE Customer(
	userId VARCHAR(25) PRIMARY KEY,
    lName VARCHAR(50) NOT NULL,
    fName VARCHAR(50) NOT NULL,
    phoneNo VARCHAR(25),
    pincode VARCHAR(200) NOT NULL
);

#Account type table holding id for savings and checking account along with minimum balance required in each account type
CREATE TABLE AccountType(
	typeId CHAR(1) PRIMARY KEY,
    accountName VARCHAR(10), 
    minBalance DECIMAL(5,2)
);

#Account table holding account and balance details
CREATE TABLE Account(
	accNo INTEGER AUTO_INCREMENT PRIMARY KEY,
    userId VARCHAR(25),
    typeId CHAR(5),
    balance DECIMAL(13,2),
    FOREIGN KEY (userId) REFERENCES Customer(userId) ON DELETE CASCADE,
    FOREIGN KEY (typeId) REFERENCES AccountType(typeId)
);
ALTER TABLE Account AUTO_INCREMENT = 101;

#Transaction table holding info about all the activities done in atm
CREATE TABLE Trans(
	transId INTEGER AUTO_INCREMENT PRIMARY KEY,
    transDate DATETIME,
    accNo INTEGER,
    transType VARCHAR(25),
    amount DECIMAL(13,2),
    FOREIGN KEY (accNo) REFERENCES Account(accNo)
);
ALTER TABLE Trans AUTO_INCREMENT = 2000;

#creates a horizontal view for eachUser to view their bank statement
CREATE VIEW BankStatement(fName, lName, phoneNo, userId, account, accType, balance)
AS SELECT fName, lName, phoneNo, a.userId, accNo, typeId, balance
FROM Customer c, Account a
WHERE c.userId = a.userId;
                               
#trigger that creates a checking account for customers.
delimiter #
CREATE TRIGGER createAct_trigger AFTER INSERT ON Customer
FOR EACH ROW
	BEGIN
		INSERT INTO account (userId, typeId, balance) values (new.userId,'c',0);
        END#
delimiter ;

#insert required info into AccountType
INSERT INTO AccountType VALUES ('c', 'checking', 0), ('s', 'savings', 200);

#insert some customers into the Customer table
INSERT INTO Customer VALUES 
		('jdoe', 'Doe', 'John', '719-3478-5667', SHA('jdoe')),
		('rivy', 'Ivimey', 'Rosella', '224-718-8226', SHA('rivy')),
		('sburres', 'Burres', 'Shaw', '536-497-5625', SHA('sburres'));

#insert into Account the account details for Customers
#Customers can have multiple accounts, but they will have atleast one checking account, savings account is optional.
#For every new customers inserted into the Customer table, createAct_trigger creates 
#an automated checking account for the customer with initial balance of 0.0
#Therefore we are inserting/creating only savings accounts for customers.

INSERT INTO Account (userId, typeId, balance) VALUES 
								('jdoe', 's', 200),
                                ('rivy', 's', 500);
                                                                