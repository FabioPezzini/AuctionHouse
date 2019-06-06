use progettok19;

drop TABLE IF EXISTS LOT;
drop TABLE IF EXISTS BID;
drop TABLE IF EXISTS FAVORITES;
drop TABLE IF EXISTS USER;
drop TABLE IF EXISTS TIMER;
drop TABLE IF EXISTS AUCTION;


CREATE TABLE USER
( username CHAR(20),
  pass CHAR(40),
  loggedstatus BOOLEAN,
  PRIMARY KEY(username));

CREATE TABLE AUCTION
( id INTEGER AUTO_INCREMENT,
  closingdate DATETIME,
  higheroffer INTEGER,
  closed BOOLEAN,
  PRIMARY KEY(id));

CREATE TABLE FAVORITES
( Idfav INTEGER AUTO_INCREMENT,
  username CHAR(20),
  id INTEGER,
  PRIMARY KEY (Idfav),
  FOREIGN KEY (username) REFERENCES USER(username),
  FOREIGN KEY (id) REFERENCES AUCTION(id));


CREATE TABLE LOT
( title CHAR(20),
  vendor CHAR(20),
  winner CHAR(20),
  baseprice INTEGER,
  auctionid INTEGER AUTO_INCREMENT,
  PRIMARY KEY(auctionid),
  FOREIGN KEY (vendor) REFERENCES USER(username),
  FOREIGN KEY (winner) REFERENCES USER(username),
  FOREIGN KEY (auctionid) REFERENCES AUCTION(id));

CREATE TABLE BID
(
    id INTEGER AUTO_INCREMENT,
    offerer CHAR(20),
    amount INTEGER,
    auctionid INTEGER,
    PRIMARY KEY (id),
    FOREIGN KEY (offerer) REFERENCES USER(username),
    FOREIGN KEY(auctionid) REFERENCES AUCTION(id));

CREATE TABLE TIMER
( id INTEGER,
  millis LONG,
  auction INTEGER,
  PRIMARY KEY (id),
  FOREIGN KEY(id) REFERENCES AUCTION(id));


SET @@global.time_zone = '+02:00';
SET @@session.time_zone = '+02:00';

ALTER DATABASE `progettok19` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;