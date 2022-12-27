-- sqlite queries
select * from note 
; 

SELECT * FROM NOTE WHERE (author='' or shared) and upper(category)=upper('Weather ')
;
update note set shared = 1 where author is null
;

SELECT * FROM NOTE WHERE (author='user' or shared) and (upper(text) like upper('test') or upper(name) like upper('test'))
;
 
-- postgres queries
SELECT current_database();
;
create database notes
;
create schema n
;
select * from n.note
;
