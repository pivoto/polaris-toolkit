insert into demo_user(id, name)
values
	(seq_demo_user.nextval,'admin');

insert into demo_user(id, name)
values
	(seq_demo_user.nextval,'user');

insert into demo_org(id, name,intro)
values
	(seq_demo_org.nextval, 'org1', '{}');
insert into demo_org(id, name)
values
	(seq_demo_org.nextval, 'org2');

insert into demo_user_org(id, user_id, org_id)
values
	(seq_demo_user_org.nextval, 1, 1);
insert into demo_user_org(id, user_id, org_id)
values
	(seq_demo_user_org.nextval, 1, 2);
insert into demo_user_org(id, user_id, org_id)
values
	(seq_demo_user_org.nextval, 2, 1);
insert into demo_user_org(id, user_id, org_id)
values
	(seq_demo_user_org.nextval, 2, 2);

