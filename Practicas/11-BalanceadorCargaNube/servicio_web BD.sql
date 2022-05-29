create database servicio_web;
use servicio_web;

create table usuarios(
	id_usuario integer auto_increment primary key,
    email varchar(256) not null,
    nombre varchar(100) not null,
    apellido_paterno varchar(100) not null,
    apellido_materno varchar(100),
    fecha_nacimiento datetime not null,
    telefono varchar(20),
    genero char(1)
);

create table fotos_usuarios(
	id_foto integer auto_increment primary key,
    foto longblob,
    id_usuario integer not null
);

alter table fotos_usuarios add foreign key (id_usuario) references usuarios(id_usuario);
create unique index usuarios_1 on usuarios(email);

create user hugo identified by 'qwerty_1';
grant all on servicio_web.* to hugo;

select * from usuarios;

insert into usuarios (email, nombre, apellido_paterno, apellido_materno, fecha_nacimiento, telefono, genero) 
values ("max@ejemplo.com", "Maximiliano", "Cazares", "Martinez", "2000-01-13 10:00:00", "5512789641", "M");