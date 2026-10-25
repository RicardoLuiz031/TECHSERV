CREATE DATABASE TECHSERV;
USE TECHSERV;

CREATE TABLE chamados(
id_chamados int auto_increment primary key,
solicitante varchar(200) not null,
descricao text not null,
prioridade enum('baixa' , 'media' , 'alta') not null,
status enum('aberto' , 'em_atendimento' , 'concluido') not null, 
data_abertura datetime not null DEFAULT CURRENT_TIMESTAMP
);