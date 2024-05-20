insert into users (id,first_name,last_name,phone,email) values ('2b484b54-3c49-46b8-82cb-2b1fa8aa00fe',
'user1','user1','123123','user1@email.ru');
insert into users (id,first_name,last_name,phone,email) values ('57876609-ec7e-4f47-8286-dd6c1b3b65e4',
'user2','user2','123123','user2@email.ru');
insert into banks (id, bik_code, name_bank) values ('ecf7c2bb-8e11-4abb-8de2-28dfda4867ba',22222,'bank');

insert into accounts (id, account_number, balance,create_at,user_id,bik_code_bank)
values ('57876609-ec7e-4f47-8286-dd6c1b3b65e4',11111,'1000','2024-05-15','2b484b54-3c49-46b8-82cb-2b1fa8aa00fe',22222);

insert into accounts (id, account_number, balance,create_at,user_id,bik_code_bank)
values ('ecf7c2bb-8e11-4abb-8de2-28dfda4867db',22222,'1000','2024-05-15','57876609-ec7e-4f47-8286-dd6c1b3b65e4',22222);

