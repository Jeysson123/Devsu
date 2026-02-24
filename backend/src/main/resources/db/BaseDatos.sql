create table accounts (initial_balance float(53) not null, status bit not null, client_id bigint not null, id bigint not null auto_increment, account_number varchar(20) not null, account_type varchar(255) not null, primary key (id)) engine=InnoDB;
create table clients (age integer, status bit not null, id bigint not null auto_increment, phone varchar(15), client_id varchar(20), identification varchar(20), password varchar(20) not null, name varchar(100) not null, address varchar(200), gender varchar(255), primary key (id)) engine=InnoDB;
create table movements (amount float(53) not null, balance float(53), account_id bigint not null, date datetime(6), id bigint not null auto_increment, movement_type varchar(255) not null, primary key (id)) engine=InnoDB;
alter table accounts add constraint UK6kplolsdtr3slnvx97xsy2kc8 unique (account_number);
alter table accounts add constraint FKgymog7firrf8bnoiig61666ob foreign key (client_id) references clients (id);
alter table movements add constraint FK1a6nru7corjv5b2vidld4ef5r foreign key (account_id) references accounts (id);
