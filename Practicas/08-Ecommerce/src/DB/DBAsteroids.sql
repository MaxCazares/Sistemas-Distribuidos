create database asteroids;
use asteroids;

CREATE TABLE articulos(
	id_articulo integer auto_increment primary key,
	nombre varchar(500) not null,
	descripcion varchar(500) not null,
	precio double not null,
	cantidad_almacen integer not null
);

CREATE TABLE foto_articulos(
	id_foto integer auto_increment primary key,
	foto longblob,
	id_articulo integer not null
);

CREATE TABLE carrito_compra(
	id_carrito_articulo integer auto_increment primary key,
	id_articulo integer not null,
	cantidad integer not null	
);

alter table foto_articulos add foreign key (id_articulo) references articulos(id_articulo);
alter table carrito_compra add foreign key (id_articulo) references articulos(id_articulo);