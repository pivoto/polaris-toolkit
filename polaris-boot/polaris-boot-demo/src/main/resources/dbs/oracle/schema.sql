create table demo_user
(
	id      number(22) generated always AS IDENTITY,
	name    varchar2(64),
	age     number(3),
	intro   varchar2(500),
	deleted number(1) default 0 not null,
	version number(22) default 0,
	crt_dt  date,
	upt_dt  date,
	primary key (id)
);

create table demo_org
(
	id      number(22),
	name    varchar2(64),
	intro   varchar2(500),
	deleted number(1) default 0 not null,
	version number(22) default 0,
	crt_dt  date,
	upt_dt  date,
	primary key (id)
);

create table demo_user_org
(
	id      number(22),
	user_id number(22),
	org_id  number(22),
	deleted number(1) default 0 not null,
	version number(22) default 0,
	crt_dt  date,
	upt_dt  date,
	primary key (id)
);
