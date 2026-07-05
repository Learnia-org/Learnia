drop database if exists Learnia_db_in5bm;
create database Learnia_db_in5bm;
use Learnia_db_in5bm;

create table usuario(
                        id_usuario bigint auto_increment primary key,
                        nombre_usuario varchar(100) not null,
                        correo_usuario varchar(100) not null unique,
                        contrasena varchar(255) not null,
                        fecha_registro datetime,
                        rol enum("ADMIN", "PROFESOR", "MODERADOR", "ESTUDIANTE") not null,
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
                         oculta boolean default false,
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
                          oculta boolean default false,
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

-- Planes de estudio generados por el estudiante (fecha de examen + días de repaso)
create table plan_estudio(
                             id_plan bigint auto_increment primary key,
                             titulo varchar(255),
                             id_usuario bigint not null,
                             id_categoria bigint not null,
                             fecha_examen date not null,
                             fecha_creacion datetime,
                             activo boolean default true,
                             constraint fk_plan_usuario
                                 foreign key (id_usuario) references usuario(id_usuario) on delete cascade,
                             constraint fk_plan_categoria
                                 foreign key (id_categoria) references categoria(id_categoria) on delete cascade
);

-- Días individuales de cada plan de estudio
create table plan_estudio_dia(
                                 id_dia bigint auto_increment primary key,
                                 id_plan bigint not null,
                                 numero_dia int not null,
                                 fecha date not null,
                                 id_recurso bigint null,
                                 titulo_tema varchar(200) not null,
                                 completado boolean default false,
                                 constraint fk_dia_plan
                                     foreign key (id_plan) references plan_estudio(id_plan) on delete cascade,
                                 constraint fk_dia_recurso
                                     foreign key (id_recurso) references recurso(id_recurso) on delete set null
);

-- Progreso del estudiante por recurso (usado en la ruta de aprendizaje adaptativa)
create table recurso_progreso(
                                 id_progreso bigint auto_increment primary key,
                                 id_usuario bigint not null,
                                 id_recurso bigint not null,
                                 completado boolean default false,
                                 nivel_dominio int default 0,
                                 fecha_actualizacion datetime,
                                 constraint fk_progreso_usuario
                                     foreign key (id_usuario) references usuario(id_usuario) on delete cascade,
                                 constraint fk_progreso_recurso
                                     foreign key (id_recurso) references recurso(id_recurso) on delete cascade,
                                 constraint uq_usuario_recurso_progreso unique (id_usuario, id_recurso)
);

-- ================================================================
-- INSERTS DE PRUEBA
-- NOTA: el hash bcrypt de abajo corresponde a la contraseña "123456"
-- para maria@learnia.com, carlos@learnia.com y profesor@learnia.com.
--
-- Para crear tu propio ADMIN:
--   1. Regístrate normalmente en /register con tu correo
--   2. Luego ejecuta: UPDATE usuario SET rol='ADMIN' WHERE correo_usuario='tu@correo.com';
--
-- Para convertir cualquier cuenta ya registrada en PROFESOR (se ve como
-- "Moderador" en la interfaz, pero el rol en la BD sigue siendo PROFESOR):
--   UPDATE usuario SET rol='PROFESOR' WHERE correo_usuario='tu@correo.com';
-- ================================================================

INSERT INTO usuario (nombre_usuario, correo_usuario, contrasena, fecha_registro, rol, foto, en_linea, bloqueado)
VALUES
    ('María González', 'maria@learnia.com',    '$2a$10$xVe4Y0I/AYbpTbimI6HH8uKpqVqYHr4bGb2FwOaFdFq1.fFq0YYJW', NOW(), 'ESTUDIANTE', NULL, false, false),
    ('Carlos Pérez',   'carlos@learnia.com',    '$2a$10$xVe4Y0I/AYbpTbimI6HH8uKpqVqYHr4bGb2FwOaFdFq1.fFq0YYJW', NOW(), 'ESTUDIANTE', NULL, false, false),
    (

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

INSERT INTO recurso (titulo_recurso, descripcion_recurso, tipo_recurso, url_archivo, id_usuario, id_categoria) VALUES
                                                                                                                   ('Ecuaciones lineales',   'Guía introductoria de ecuaciones de primer grado.', 'DOCUMENTO', '/uploads/matematica/ecuaciones-lineales.pdf', 3, 1),
                                                                                                                   ('Ecuaciones cuadráticas','Fórmula general y método de factorización.',        'DOCUMENTO', '/uploads/matematica/ecuaciones-cuadraticas.pdf', 3, 1),
                                                                                                                   ('Funciones y gráficas',  'Introducción a funciones, dominio y rango.',         'DOCUMENTO', '/uploads/matematica/funciones-graficas.pdf', 3, 1);

select * from usuario;