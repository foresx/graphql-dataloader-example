CREATE TABLE author (
	id                   serial primary key   ,
	name                 varchar(100)   ,
	age                  integer   ,
	CONSTRAINT unq_author_id UNIQUE ( id )
 );


CREATE TABLE book (
	id                   serial primary key ,
	code                 varchar   ,
	name                 varchar(100)   ,
	description          varchar   ,
	author_id            integer   ,
	CONSTRAINT unq_book_id UNIQUE ( id )
 );

ALTER TABLE book ADD CONSTRAINT fk_book_author FOREIGN KEY ( author_id ) REFERENCES author( id );


CREATE TABLE review (
	id                   serial primary key   ,
	content              varchar   ,
	book_id              integer
 );

ALTER TABLE review ADD CONSTRAINT fk_review_book FOREIGN KEY ( book_id ) REFERENCES book( id );
