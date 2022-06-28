CREATE TABLE User (
	password varchar(250) NOT NULL,
	first_name varchar(250) NOT NULL,
	last_name varchar(250) NOT NULL,
	nick_name varchar(250),
	email varchar(250) NOT NULL,
	PRIMARY KEY (email),
)

CREATE TABLE Phone(
	phone_number varchar(250),
	share_phone Number(1),
	PRIMARY KEY (phone_number),
)

CREATE TABLE Work_Phone(
	phone_number varchar(250),
	PRIMARY KEY (phone_number),
)

CREATE TABLE Mobile_Phone(
	phone_number varchar(250),
	PRIMARY KEY (phone_number),
)

CREATE TABLE Work_Phone(
	phone_number varchar(250),
	PRIMARY KEY (phone_number),
)

CREATE TABLE Address(
	postal_code varchar(250),
	city varchar(250),
    state varchar(250),
    latitude varchar(250),
    longitude varchar(250),
	PRIMARY KEY (postal_code),
)
ALTER TABLE User ADD CONSTRAINT fk_Item_user_email_User_email FOREIGN KEY (user_email) REFERENCES User (email);

ALTER TABLE HomePhone ADD CONSTRAINT fk_HomePhone_phone_number_phone_phone_number FOREIGN KEY (phone_number) REFERENCES Phone (phone_number);

ALTER TABLE WorkPhone ADD CONSTRAINT fk_WorkPhone_phone_number_phone_phone_number FOREIGN KEY (phone_number) REFERENCES Phone (phone_number);

ALTER TABLE MobilePhone ADD CONSTRAINT fk_MobilePhone_phone_number_phone_phone_number FOREIGN KEY (phone_number) REFERENCES Phone (phone_number);

ALTER TABLE User ADD CONSTRAINT fk_User_phone_number_phone_phone_number FOREIGN KEY (phone_number) REFERENCES User (phone_number);

ALTER TABLE User ADD CONSTRAINT fk_User_postal_code_address_postal_code FOREIGN KEY (postal_code) REFERENCES User (postal_code);


CREATE TABLE Item (
	item_number int(16) NOT NULL AUTO_INCREMENT,
	user_email varchar(250) NOT NULL,
	name varchar(250) NOT NULL,
	condition varchar(250) NOT NULL,
	description varchar(MAX),
	PRIMARY KEY (item_number),
)

CREATE TABLE BoardItem (
	item_number int(16) NOT NULL,
	PRIMARY KEY (item_number),
)

CREATE TABLE CardItem (
	item_number int(16) NOT NULL,
	PRIMARY KEY (item_number),
)

CREATE TABLE VideoItem (
	item_number int(16) NOT NULL,
	platform varchar(250) NOT NULL,
	media varchar(250) NOT NULL,
	PRIMARY KEY (item_number),
)

CREATE TABLE ComputerItem (
	item_number int(16) NOT NULL,
	platform varchar(250) NOT NULL,
	PRIMARY KEY (item_number),
)

CREATE TABLE JigsawItem (
	item_number int(16) NOT NULL,
	piece_count int(16) NOT NULL,
	PRIMARY KEY (item_number),
)

ALTER TABLE Item ADD CONSTRAINT fk_Item_user_email_User_email FOREIGN KEY (user_email) REFERENCES User (email);

ALTER TABLE BoardItem ADD CONSTRAINT fk_BoardItem_item_number_Item_item_number FOREIGN KEY (item_number) REFERENCES Item (item_number);

ALTER TABLE CardItem ADD CONSTRAINT fk_CardItem_item_number_Item_item_number FOREIGN KEY (item_number) REFERENCES Item (item_number);

ALTER TABLE VideoItem ADD CONSTRAINT fk_VideoItem_item_number_Item_item_number FOREIGN KEY (item_number) REFERENCES Item (item_number);

ALTER TABLE ComputerItem ADD CONSTRAINT fk_ComputerItem_item_number_Item_item_number FOREIGN KEY (item_number) REFERENCES Item (item_number);

ALTER TABLE JigsawItem ADD CONSTRAINT fk_JigsawItem_item_number_Item_item_number FOREIGN KEY (item_number) REFERENCES Item (item_number);


CREATE TABLE Swap (
	proposed_date DATE NOT NULL,
    accepted_rejected_date DATE NULL,
	status BIT NULL,
	proposed_item_number int(16) NOT NULL,
    desired_item_number int(16) NOT NULL,
    proposer_ratings INT NULL,
    counterparty_ratings INT NULL,
    PRIMARY KEY (proposed_item_number, desired_item_number)
)

ALTER TABLE Swaps ADD CONSTRAINT fk_Swaps_proposed_item_number_Item_item_number FOREIGN KEY (proposed_item_number) REFERENCES Item (item_number);

ALTER TABLE Swaps ADD CONSTRAINT fk_Swaps_desired_item_number_Item_item_number FOREIGN KEY (desired_item_number) REFERENCES Item (item_number);

