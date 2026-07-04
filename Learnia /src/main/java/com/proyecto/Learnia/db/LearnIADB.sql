drop database if exists Learnia_db_in5bm;
create database Learnia_db_in5bm;
use Learnia_db_in5bm;

create table usuario(
                        id_usuario bigint auto_increment primary key,
                        nombre_usuario varchar(100) not null,
                        correo_usuario varchar(100) not null unique,
                        contrasena varchar(255) not null,
                        fecha_registro datetime,
                        rol enum("ADMIN", "MODERADOR", "ESTUDIANTE") not null,
                        foto varchar(255) null,
                        en_linea boolean default false,
                        bloqueado boolean default false,
                        ultimo_acceso datetime null
);

create table categoria(
                          id_categoria bigint auto_increment primary key,
                          nombre varchar(100) not null,
                          descripcion text,
                          activa boolean default true
);

create table recurso(
                        id_recurso bigint auto_increment primary key,
                        titulo_recurso varchar(200) not null,
                        descripcion_recurso text,
                        tipo_recurso enum("DOCUMENTO","PRESENTACION","AUDIO") not null,
                        url_archivo varchar(255) not null,
                        fecha_subida datetime default current_timestamp,
                        id_usuario bigint not null,
                        id_categoria bigint not null,
                        constraint fk_id_usuario
                            foreign key (id_usuario) references usuario(id_usuario) on delete cascade,
                        constraint fk_id_categoria
                            foreign key (id_categoria) references categoria(id_categoria) on delete cascade
);

create table pregunta(
                         id_pregunta bigint auto_increment primary key,
                         titulo varchar(200) not null,
                         descripcion text not null,
                         fecha_publicacion datetime default current_timestamp,
                         id_usuario bigint not null,
                         id_categoria bigint not null,
                         constraint fk_id_usuarios
                             foreign key (id_usuario) references usuario(id_usuario) on delete cascade,
                         constraint fk_id_categorias
                             foreign key (id_categoria) references categoria(id_categoria) on delete cascade
);

create table respuesta(
                          id_respuesta bigint auto_increment primary key,
                          contenido text not null,
                          fecha_respuesta datetime default current_timestamp,
                          id_usuario bigint not null,
                          id_pregunta bigint not null,
                          constraint fk_respuesta_usuario
                              foreign key (id_usuario) references usuario(id_usuario) on delete cascade,
                          constraint fk_respuesta_pregunta
                              foreign key (id_pregunta) references pregunta(id_pregunta) on delete cascade
);

create table comentario(
                           id_comentario bigint auto_increment primary key,
                           contenido text not null,
                           fecha datetime default current_timestamp,
                           id_usuario bigint not null,
                           id_recurso bigint null,
                           id_respuesta bigint null,
                           constraint fk_usuario
                               foreign key (id_usuario) references usuario(id_usuario) on delete cascade,
                           constraint fk_respuesta
                               foreign key (id_respuesta) references respuesta(id_respuesta) on delete cascade,
                           constraint fk_recurso
                               foreign key (id_recurso) references recurso(id_recurso) on delete cascade
);

create table voto(
                     id_voto bigint auto_increment primary key,
                     tipo enum('like','dislike') not null,
                     id_usuario bigint not null,
                     id_respuesta bigint not null,
                     constraint fk_idusuario
                         foreign key (id_usuario) references usuario(id_usuario) on delete cascade,
                     constraint fk_id_respuesta
                         foreign key (id_respuesta) references respuesta(id_respuesta) on delete cascade
);


INSERT INTO usuario (nombre_usuario, correo_usuario, contrasena, fecha_registro, rol, foto, en_linea, bloqueado)
VALUES
    ('María González', 'maria@learnia.com', '$2a$10$xVe4Y0I/AYbpTbimI6HH8uKpqVqYHr4bGb2FwOaFdFq1.fFq0YYJW', NOW(), 'ESTUDIANTE', NULL, false, false),
    ('Carlos Pérez',   'carlos@learnia.com', '$2a$10$xVe4Y0I/AYbpTbimI6HH8uKpqVqYHr4bGb2FwOaFdFq1.fFq0YYJW', NOW(), 'ESTUDIANTE', NULL, false, false);

INSERT INTO categoria (nombre, descripcion, activa) VALUES
                                                        ('Matemática',   'Álgebra, cálculo, estadística y todo tipo de matemáticas', true),
                                                        ('Física',       'Mecánica, termodinámica, electricidad y magnetismo', true),
                                                        ('Programación', 'Algoritmos, estructuras de datos, lenguajes de programación', true),
                                                        ('Inglés',       'Gramática, vocabulario, comprensión lectora y escritura', true),
                                                        ('Química',      'Química orgánica, inorgánica y reacciones', true),
                                                        ('Historia',     'Historia universal, de Guatemala y de América Latina', false),
                                                        ('Biología',     'Biología celular, genética, ecología y anatomía', false);

INSERT INTO pregunta (titulo, descripcion, fecha_publicacion, id_usuario, id_categoria) VALUES
                                                                                            ('¿Cómo se resuelve una ecuación cuadrática?',
                                                                                             'Necesito entender paso a paso cómo aplicar la fórmula general para resolver ecuaciones de segundo grado.',
                                                                                             NOW(), 1, 1),
                                                                                            ('¿Qué es la segunda ley de Newton y cómo se aplica?',
                                                                                             'Estoy estudiando para un examen de física y no entiendo bien cómo usar F=ma con fricción.',
                                                                                             NOW(), 2, 2),
                                                                                            ('¿Cuál es la diferencia entre una lista y un arreglo en Java?',
                                                                                             'En mi clase de programación usamos ArrayList y arreglos normales, no sé cuándo usar cada uno.',
                                                                                             NOW(), 1, 3);