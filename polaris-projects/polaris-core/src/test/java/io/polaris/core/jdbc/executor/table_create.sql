create table demo_test01
(
	id   number generated always as identity,
	name varchar2(30),
	primary key (id)
);
create table demo_test02
(
	id   number generated always as identity,
	name varchar2(30),
	age number(3),
	sex varchar2(1) default 'M',
	intro varchar2(500),
	deleted number(1)  default 0,
	version number(22) default 0,
	crt_dt date default sysdate,
	upt_dt date default sysdate,
	primary key (id)
);
