insert into demo_user(name)
values
	('admin');

insert into demo_user(name)
values
	('user');

insert into demo_org(id, name)
values
	(1, 'org1');
insert into demo_org(id, name)
values
	(2, 'org2');

insert into demo_user_org(id, user_id, org_id)
values
	(1, 1, 1);
insert into demo_user_org(id, user_id, org_id)
values
	(2, 1, 2);
insert into demo_user_org(id, user_id, org_id)
values
	(3, 2, 1);
insert into demo_user_org(id, user_id, org_id)
values
	(4, 2, 2);

