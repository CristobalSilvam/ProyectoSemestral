CREATE DATABASE "defaultdb" /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE "cancha" (
  "id_cancha" bigint NOT NULL AUTO_INCREMENT,
  "es_techada" bit(1) DEFAULT NULL,
  "nombre_interno" varchar(50) NOT NULL,
  "tipo_superficie" varchar(30) DEFAULT NULL,
  "deporte_id" bigint NOT NULL,
  "recinto_id" bigint NOT NULL,
  PRIMARY KEY ("id_cancha"),
  KEY "FKooe24dhm3io8jhkdcq9uh9c3d" ("deporte_id"),
  KEY "FK1xsfk675d3wnbrdfdg814wuf2" ("recinto_id"),
  CONSTRAINT "FK1xsfk675d3wnbrdfdg814wuf2" FOREIGN KEY ("recinto_id") REFERENCES "recinto" ("id"),
  CONSTRAINT "FKooe24dhm3io8jhkdcq9uh9c3d" FOREIGN KEY ("deporte_id") REFERENCES "deporte" ("id_deporte")
);

CREATE TABLE "comuna" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "nombre" varchar(50) NOT NULL,
  "region_id" bigint NOT NULL,
  PRIMARY KEY ("id"),
  KEY "FKtop2papyj2urkhnpghehayki4" ("region_id"),
  CONSTRAINT "FKtop2papyj2urkhnpghehayki4" FOREIGN KEY ("region_id") REFERENCES "region" ("id")
);

CREATE TABLE "coordenada" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "latitud" decimal(10,8) NOT NULL,
  "longitud" decimal(11,8) NOT NULL,
  PRIMARY KEY ("id")
);

CREATE TABLE "deporte" (
  "id_deporte" bigint NOT NULL AUTO_INCREMENT,
  "nombre" varchar(50) NOT NULL,
  PRIMARY KEY ("id_deporte")
);

CREATE TABLE "direccion" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "calle" varchar(100) NOT NULL,
  "numero" int NOT NULL,
  "comuna_id" bigint NOT NULL,
  "coordenada_id" bigint NOT NULL,
  PRIMARY KEY ("id"),
  KEY "FKdh63uja7g08nw6eeu6diqiwa" ("comuna_id"),
  KEY "FK12q1b6l0ixo5pnh3cxsn686pm" ("coordenada_id"),
  CONSTRAINT "FK12q1b6l0ixo5pnh3cxsn686pm" FOREIGN KEY ("coordenada_id") REFERENCES "coordenada" ("id"),
  CONSTRAINT "FKdh63uja7g08nw6eeu6diqiwa" FOREIGN KEY ("comuna_id") REFERENCES "comuna" ("id")
);

CREATE TABLE "email" (
  "id_email" bigint NOT NULL AUTO_INCREMENT,
  "correo" varchar(100) NOT NULL,
  "es_principal" bit(1) DEFAULT NULL,
  "recinto_id" bigint DEFAULT NULL,
  "usuario_id" bigint DEFAULT NULL,
  PRIMARY KEY ("id_email"),
  KEY "FK70bxiuc3m2s11ncsm55sxxa6i" ("recinto_id"),
  KEY "FKkcch1rfv3cge9f9odplk9ouem" ("usuario_id"),
  CONSTRAINT "FK70bxiuc3m2s11ncsm55sxxa6i" FOREIGN KEY ("recinto_id") REFERENCES "recinto" ("id"),
  CONSTRAINT "FKkcch1rfv3cge9f9odplk9ouem" FOREIGN KEY ("usuario_id") REFERENCES "usuario" ("id")
);

CREATE TABLE "estado_reserva" (
  "id_estado" bigint NOT NULL AUTO_INCREMENT,
  "descripcion" varchar(50) NOT NULL,
  PRIMARY KEY ("id_estado")
);

CREATE TABLE "horario_especial" (
  "id_he" bigint NOT NULL AUTO_INCREMENT,
  "esta_bloqueado" bit(1) DEFAULT NULL,
  "fecha" date NOT NULL,
  "motivo" varchar(100) DEFAULT NULL,
  "cancha_id" bigint NOT NULL,
  PRIMARY KEY ("id_he"),
  KEY "FKnvav1rbu43le2n9mj43xr5xr8" ("cancha_id"),
  CONSTRAINT "FKnvav1rbu43le2n9mj43xr5xr8" FOREIGN KEY ("cancha_id") REFERENCES "cancha" ("id_cancha")
);

CREATE TABLE "imagen" (
  "id_img" bigint NOT NULL AUTO_INCREMENT,
  "url" varchar(255) NOT NULL,
  "recinto_id" bigint NOT NULL,
  PRIMARY KEY ("id_img"),
  KEY "FKawn5t8yc6cvb9ve86cnfyil75" ("recinto_id"),
  CONSTRAINT "FKawn5t8yc6cvb9ve86cnfyil75" FOREIGN KEY ("recinto_id") REFERENCES "recinto" ("id")
);

CREATE TABLE "recinto" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "nombre" varchar(100) NOT NULL,
  "rut_empresa" varchar(20) NOT NULL,
  "direccion_id" bigint NOT NULL,
  PRIMARY KEY ("id"),
  KEY "FKbg1shetjonyhlbo6ij9pub14r" ("direccion_id"),
  CONSTRAINT "FKbg1shetjonyhlbo6ij9pub14r" FOREIGN KEY ("direccion_id") REFERENCES "direccion" ("id")
);

CREATE TABLE "region" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "nombre" varchar(50) NOT NULL,
  PRIMARY KEY ("id")
);

CREATE TABLE "reserva" (
  "id_reserva" bigint NOT NULL AUTO_INCREMENT,
  "fecha_uso" date NOT NULL,
  "hora_fin" time(6) NOT NULL,
  "hora_inicio" time(6) NOT NULL,
  "monto_total" decimal(10,2) NOT NULL,
  "cancha_id" bigint NOT NULL,
  "estado_id" bigint NOT NULL,
  "review_id" bigint DEFAULT NULL,
  "usuario_id" bigint NOT NULL,
  PRIMARY KEY ("id_reserva"),
  UNIQUE KEY "UKetllbgf3vaeslpsxial3j9hxn" ("review_id"),
  KEY "FKlmdd0lnuxry7bjp5ba2vvry8" ("cancha_id"),
  KEY "FK3rq0dmocpwgflkhe8ot47lln2" ("estado_id"),
  KEY "FKiad9w96t12u3ms2ul93l97mel" ("usuario_id"),
  CONSTRAINT "FK3rq0dmocpwgflkhe8ot47lln2" FOREIGN KEY ("estado_id") REFERENCES "estado_reserva" ("id_estado"),
  CONSTRAINT "FKhmppt2yqbw4hohodqbbgomjed" FOREIGN KEY ("review_id") REFERENCES "review" ("id_review"),
  CONSTRAINT "FKiad9w96t12u3ms2ul93l97mel" FOREIGN KEY ("usuario_id") REFERENCES "usuario" ("id"),
  CONSTRAINT "FKlmdd0lnuxry7bjp5ba2vvry8" FOREIGN KEY ("cancha_id") REFERENCES "cancha" ("id_cancha")
);

CREATE TABLE "review" (
  "id_review" bigint NOT NULL AUTO_INCREMENT,
  "comentario" text,
  "puntaje" tinyint NOT NULL,
  PRIMARY KEY ("id_review")
);

CREATE TABLE "rol" (
  "id_rol" bigint NOT NULL AUTO_INCREMENT,
  "nombre" varchar(20) NOT NULL,
  PRIMARY KEY ("id_rol")
);

CREATE TABLE "tarifa" (
  "id_tarifa" bigint NOT NULL AUTO_INCREMENT,
  "dia_semana" tinyint NOT NULL,
  "hora_fin" time(6) NOT NULL,
  "hora_inicio" time(6) NOT NULL,
  "precio" decimal(10,2) NOT NULL,
  "cancha_id" bigint NOT NULL,
  PRIMARY KEY ("id_tarifa"),
  KEY "FK5igfwy6eeg8y1ejjb5g24k6x4" ("cancha_id"),
  CONSTRAINT "FK5igfwy6eeg8y1ejjb5g24k6x4" FOREIGN KEY ("cancha_id") REFERENCES "cancha" ("id_cancha")
);

CREATE TABLE "telefono" (
  "id_telefono" bigint NOT NULL AUTO_INCREMENT,
  "numero" varchar(20) NOT NULL,
  "tipo" varchar(20) DEFAULT NULL,
  "recinto_id" bigint DEFAULT NULL,
  "usuario_id" bigint DEFAULT NULL,
  PRIMARY KEY ("id_telefono"),
  KEY "FK5u3kua58cjkcyj89ty4ob0m1w" ("recinto_id"),
  KEY "FKpi2c7iq0lw09d1ovc7bn86f85" ("usuario_id"),
  CONSTRAINT "FK5u3kua58cjkcyj89ty4ob0m1w" FOREIGN KEY ("recinto_id") REFERENCES "recinto" ("id"),
  CONSTRAINT "FKpi2c7iq0lw09d1ovc7bn86f85" FOREIGN KEY ("usuario_id") REFERENCES "usuario" ("id")
);

CREATE TABLE "usuario" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "apellido_materno" varchar(50) DEFAULT NULL,
  "apellido_paterno" varchar(50) NOT NULL,
  "email_principal" varchar(100) NOT NULL,
  "nombre" varchar(50) NOT NULL,
  "password_hash" varchar(255) NOT NULL,
  "rut" varchar(15) NOT NULL,
  "segundo_nombre" varchar(50) DEFAULT NULL,
  "rol_id" bigint NOT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "UKrxe7ma3vfytpv5hn6gogabkc7" ("email_principal"),
  UNIQUE KEY "UKjx61a01wwidax9iafoa3xj22i" ("rut"),
  KEY "FKshkwj12wg6vkm6iuwhvcfpct8" ("rol_id"),
  CONSTRAINT "FKshkwj12wg6vkm6iuwhvcfpct8" FOREIGN KEY ("rol_id") REFERENCES "rol" ("id_rol")
);