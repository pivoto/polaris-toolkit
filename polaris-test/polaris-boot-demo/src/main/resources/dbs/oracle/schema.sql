create sequence seq_demo_user start with 1 increment by 1 cache 20 maxvalue 999999999999;
create sequence seq_demo_org start with 1 increment by 1 cache 20 maxvalue 999999999999;
create sequence seq_demo_user_org start with 1 increment by 1 cache 20 maxvalue 999999999999;

create table demo_user
(
-- 	id      number(22) generated always AS IDENTITY,
	id      number(22),
	name    varchar2(64),
	age     number(3),
	intro   varchar2(500),
	deleted number(1) default 0 not null,
	version number(22) default 0,
	crt_dt  date default sysdate,
	upt_dt  date default sysdate,
	primary key (id)
);

create table demo_org
(
	id      number(22),
	name    varchar2(64),
	intro   varchar2(500),
	deleted number(1) default 0 not null,
	version number(22) default 0,
	crt_dt  date default sysdate,
	upt_dt  date default sysdate,
	primary key (id)
);

create table demo_user_org
(
	id      number(22),
	user_id number(22),
	org_id  number(22),
	deleted number(1) default 0 not null,
	version number(22) default 0,
	crt_dt  date default sysdate,
	upt_dt  date default sysdate,
	primary key (id)
);
