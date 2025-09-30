create table t_demo_test01
(
	id   number(22) generated always as identity,
	name varchar2(30),
	primary key (id)
);
create table t_demo_test02
(
	id      number(22) generated always as identity,
	name    varchar2(30),
	age     number(3),
	sex     varchar2(1) default 'M',
	intro   varchar2(500),
	deleted number(1) default 0,
	version number(22) default 0,
	crt_dt  date default sysdate,
	upt_dt  date default sysdate,
	primary key (id)
);
create sequence seq_demo_test01 start with 1 increment by 1 cache 20 maxvalue 9999999999999999999999999999;
create table t_demo_test03
(
	id   number(22),
	name varchar2(30),
	primary key (id)
);
