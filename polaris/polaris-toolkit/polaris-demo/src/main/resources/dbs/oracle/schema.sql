create table demo_user
(
	id      number(22),
	name    varchar2(64),
	age     number(3),
	intro   varchar2(500),
	deleted number(1)  default 0 not null,
	version number(22) default 0,
	crt_dt  date,
	upt_dt  date,
	primary key (id)
);
