-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: searchsport-mysql-searchsport.k.aivencloud.com    Database: defaultdb
-- ------------------------------------------------------
-- Server version	8.4.8

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ 'ffe6f6b4-4fc6-11f1-96b3-22a61362dfa8:1-114';

--
-- Table structure for table `cancha`
--

DROP TABLE IF EXISTS `cancha`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cancha` (
  `id_cancha` bigint NOT NULL AUTO_INCREMENT,
  `es_techada` bit(1) DEFAULT NULL,
  `nombre_interno` varchar(50) NOT NULL,
  `tipo_superficie` varchar(30) DEFAULT NULL,
  `deporte_id` bigint NOT NULL,
  `recinto_id` bigint NOT NULL,
  PRIMARY KEY (`id_cancha`),
  KEY `FKooe24dhm3io8jhkdcq9uh9c3d` (`deporte_id`),
  KEY `FK1xsfk675d3wnbrdfdg814wuf2` (`recinto_id`),
  CONSTRAINT `FK1xsfk675d3wnbrdfdg814wuf2` FOREIGN KEY (`recinto_id`) REFERENCES `recinto` (`id`),
  CONSTRAINT `FKooe24dhm3io8jhkdcq9uh9c3d` FOREIGN KEY (`deporte_id`) REFERENCES `deporte` (`id_deporte`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cancha`
--

LOCK TABLES `cancha` WRITE;
/*!40000 ALTER TABLE `cancha` DISABLE KEYS */;
INSERT INTO `cancha` VALUES (1,_binary '\0','Cancha 1','Sintética',1,1),(2,_binary '','Cancha 2','Sintética',1,1),(3,_binary '\0','Cancha Principal','Pasto natural',1,2),(4,_binary '','Cancha Futbolito','Sintética',1,3);
/*!40000 ALTER TABLE `cancha` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comuna`
--

DROP TABLE IF EXISTS `comuna`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comuna` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `region_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtop2papyj2urkhnpghehayki4` (`region_id`),
  CONSTRAINT `FKtop2papyj2urkhnpghehayki4` FOREIGN KEY (`region_id`) REFERENCES `region` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=347 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comuna`
--

LOCK TABLES `comuna` WRITE;
/*!40000 ALTER TABLE `comuna` DISABLE KEYS */;
INSERT INTO `comuna` VALUES (1,'Arica',1),(2,'Camarones',1),(3,'Putre',1),(4,'General Lagos',1),(5,'Iquique',2),(6,'Alto Hospicio',2),(7,'Pozo Almonte',2),(8,'Camiña',2),(9,'Colchane',2),(10,'Huara',2),(11,'Pica',2),(12,'Antofagasta',3),(13,'Mejillones',3),(14,'Sierra Gorda',3),(15,'Taltal',3),(16,'Calama',3),(17,'Ollagüe',3),(18,'San Pedro de Atacama',3),(19,'Tocopilla',3),(20,'María Elena',3),(21,'Copiapó',4),(22,'Caldera',4),(23,'Tierra Amarilla',4),(24,'Chañaral',4),(25,'Diego de Almagro',4),(26,'Vallenar',4),(27,'Alto del Carmen',4),(28,'Freirina',4),(29,'Huasco',4),(30,'La Serena',5),(31,'Coquimbo',5),(32,'Andacollo',5),(33,'La Higuera',5),(34,'Paiguano',5),(35,'Vicuña',5),(36,'Illapel',5),(37,'Canela',5),(38,'Los Vilos',5),(39,'Salamanca',5),(40,'Ovalle',5),(41,'Combarbalá',5),(42,'Monte Patria',5),(43,'Punitaqui',5),(44,'Río Hurtado',5),(45,'Valparaíso',6),(46,'Casablanca',6),(47,'Concón',6),(48,'Juan Fernández',6),(49,'Puchuncaví',6),(50,'Quintero',6),(51,'Viña del Mar',6),(52,'Isla de Pascua',6),(53,'Los Andes',6),(54,'Calle Larga',6),(55,'Rinconada',6),(56,'San Esteban',6),(57,'La Ligua',6),(58,'Cabildo',6),(59,'Papudo',6),(60,'Petorca',6),(61,'Zapallar',6),(62,'Quillota',6),(63,'Calera',6),(64,'Hijuelas',6),(65,'La Cruz',6),(66,'Nogales',6),(67,'San Antonio',6),(68,'Algarrobo',6),(69,'Cartagena',6),(70,'El Quisco',6),(71,'El Tabo',6),(72,'Santo Domingo',6),(73,'San Felipe',6),(74,'Catemu',6),(75,'Llaillay',6),(76,'Panquehue',6),(77,'Putaendo',6),(78,'Santa María',6),(79,'Quilpué',6),(80,'Limache',6),(81,'Olmué',6),(82,'Villa Alemana',6),(83,'Rancagua',7),(84,'Codegua',7),(85,'Coinco',7),(86,'Coltauco',7),(87,'Doñihue',7),(88,'Graneros',7),(89,'Las Cabras',7),(90,'Machalí',7),(91,'Malloa',7),(92,'Mostazal',7),(93,'Olivar',7),(94,'Peumo',7),(95,'Pichidegua',7),(96,'Quinta de Tilcoco',7),(97,'Rengo',7),(98,'Requínoa',7),(99,'San Vicente',7),(100,'Pichilemu',7),(101,'La Estrella',7),(102,'Litueche',7),(103,'Marchihue',7),(104,'Navidad',7),(105,'Paredones',7),(106,'San Fernando',7),(107,'Chépica',7),(108,'Chimbarongo',7),(109,'Lolol',7),(110,'Nancagua',7),(111,'Palmilla',7),(112,'Peralillo',7),(113,'Placilla',7),(114,'Pumanque',7),(115,'Santa Cruz',7),(116,'Talca',8),(117,'Constitución',8),(118,'Curepto',8),(119,'Empedrado',8),(120,'Maule',8),(121,'Pelarco',8),(122,'Pencahue',8),(123,'Río Claro',8),(124,'San Clemente',8),(125,'San Rafael',8),(126,'Cauquenes',8),(127,'Chanco',8),(128,'Pelluhue',8),(129,'Curicó',8),(130,'Hualañé',8),(131,'Licantén',8),(132,'Molina',8),(133,'Rauco',8),(134,'Romeral',8),(135,'Sagrada Familia',8),(136,'Teno',8),(137,'Vichuquén',8),(138,'Linares',8),(139,'Colbún',8),(140,'Longaví',8),(141,'Parral',8),(142,'Retiro',8),(143,'San Javier',8),(144,'Villa Alegre',8),(145,'Yerbas Buenas',8),(146,'Cobquecura',9),(147,'Coelemu',9),(148,'Ninhue',9),(149,'Portezuelo',9),(150,'Quirihue',9),(151,'Ránquil',9),(152,'Treguaco',9),(153,'Bulnes',9),(154,'Chillán Viejo',9),(155,'Chillán',9),(156,'El Carmen',9),(157,'Pemuco',9),(158,'Pinto',9),(159,'Quillón',9),(160,'San Ignacio',9),(161,'Yungay',9),(162,'Coihueco',9),(163,'Ñiquén',9),(164,'San Carlos',9),(165,'San Fabián',9),(166,'San Nicolás',9),(167,'Concepción',10),(168,'Coronel',10),(169,'Chiguayante',10),(170,'Florida',10),(171,'Hualqui',10),(172,'Lota',10),(173,'Penco',10),(174,'San Pedro de la Paz',10),(175,'Santa Juana',10),(176,'Talcahuano',10),(177,'Tomé',10),(178,'Hualpén',10),(179,'Lebu',10),(180,'Arauco',10),(181,'Cañete',10),(182,'Contulmo',10),(183,'Curanilahue',10),(184,'Los Álamos',10),(185,'Tirúa',10),(186,'Los Ángeles',10),(187,'Antuco',10),(188,'Cabrero',10),(189,'Laja',10),(190,'Mulchén',10),(191,'Nacimiento',10),(192,'Negrete',10),(193,'Quilaco',10),(194,'Quilleco',10),(195,'San Rosendo',10),(196,'Santa Bárbara',10),(197,'Tucapel',10),(198,'Yumbel',10),(199,'Alto Biobío',10),(200,'Temuco',11),(201,'Carahue',11),(202,'Cunco',11),(203,'Curarrehue',11),(204,'Freire',11),(205,'Galvarino',11),(206,'Gorbea',11),(207,'Lautaro',11),(208,'Loncoche',11),(209,'Melipeuco',11),(210,'Nueva Imperial',11),(211,'Padre las Casas',11),(212,'Perquenco',11),(213,'Pitrufquén',11),(214,'Pucón',11),(215,'Saavedra',11),(216,'Teodoro Schmidt',11),(217,'Toltén',11),(218,'Vilcún',11),(219,'Villarrica',11),(220,'Cholchol',11),(221,'Angol',11),(222,'Collipulli',11),(223,'Curacautín',11),(224,'Ercilla',11),(225,'Lonquimay',11),(226,'Los Sauces',11),(227,'Lumaco',11),(228,'Purén',11),(229,'Renaico',11),(230,'Traiguén',11),(231,'Victoria',11),(232,'Valdivia',12),(233,'Corral',12),(234,'Lanco',12),(235,'Los Lagos',12),(236,'Máfil',12),(237,'Mariquina',12),(238,'Paillaco',12),(239,'Panguipulli',12),(240,'La Unión',12),(241,'Futrono',12),(242,'Lago Ranco',12),(243,'Río Bueno',12),(244,'Puerto Montt',13),(245,'Calbuco',13),(246,'Cochamó',13),(247,'Fresia',13),(248,'Frutillar',13),(249,'Los Muermos',13),(250,'Llanquihue',13),(251,'Maullín',13),(252,'Puerto Varas',13),(253,'Castro',13),(254,'Ancud',13),(255,'Chonchi',13),(256,'Curaco de Vélez',13),(257,'Dalcahue',13),(258,'Puqueldón',13),(259,'Queilén',13),(260,'Quellón',13),(261,'Quemchi',13),(262,'Quinchao',13),(263,'Osorno',13),(264,'Puerto Octay',13),(265,'Purranque',13),(266,'Puyehue',13),(267,'Río Negro',13),(268,'San Juan de la Costa',13),(269,'San Pablo',13),(270,'Chaitén',13),(271,'Futaleufú',13),(272,'Hualaihué',13),(273,'Palena',13),(274,'Coyhaique',14),(275,'Lago Verde',14),(276,'Aysén',14),(277,'Cisnes',14),(278,'Guaitecas',14),(279,'Cochrane',14),(280,'O’Higgins',14),(281,'Tortel',14),(282,'Chile Chico',14),(283,'Río Ibáñez',14),(284,'Punta Arenas',15),(285,'Laguna Blanca',15),(286,'Río Verde',15),(287,'San Gregorio',15),(288,'Cabo de Hornos',15),(289,'Antártica',15),(290,'Porvenir',15),(291,'Primavera',15),(292,'Timaukel',15),(293,'Natales',15),(294,'Torres del Paine',15),(295,'Cerrillos',16),(296,'Cerro Navia',16),(297,'Conchalí',16),(298,'El Bosque',16),(299,'Estación Central',16),(300,'Huechuraba',16),(301,'Independencia',16),(302,'La Cisterna',16),(303,'La Florida',16),(304,'La Granja',16),(305,'La Pintana',16),(306,'La Reina',16),(307,'Las Condes',16),(308,'Lo Barnechea',16),(309,'Lo Espejo',16),(310,'Lo Prado',16),(311,'Macul',16),(312,'Maipú',16),(313,'Ñuñoa',16),(314,'Pedro Aguirre Cerda',16),(315,'Peñalolén',16),(316,'Providencia',16),(317,'Pudahuel',16),(318,'Quilicura',16),(319,'Quinta Normal',16),(320,'Recoleta',16),(321,'Renca',16),(322,'Santiago',16),(323,'San Joaquín',16),(324,'San Miguel',16),(325,'San Ramón',16),(326,'Vitacura',16),(327,'Puente Alto',16),(328,'Pirque',16),(329,'San José de Maipo',16),(330,'Colina',16),(331,'Lampa',16),(332,'Tiltil',16),(333,'San Bernardo',16),(334,'Buin',16),(335,'Calera de Tango',16),(336,'Paine',16),(337,'Melipilla',16),(338,'Alhué',16),(339,'Curacaví',16),(340,'María Pinto',16),(341,'San Pedro',16),(342,'Talagante',16),(343,'El Monte',16),(344,'Isla de Maipo',16),(345,'Padre Hurtado',16),(346,'Peñaflor',16);
/*!40000 ALTER TABLE `comuna` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coordenada`
--

DROP TABLE IF EXISTS `coordenada`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coordenada` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `latitud` decimal(10,8) NOT NULL,
  `longitud` decimal(11,8) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coordenada`
--

LOCK TABLES `coordenada` WRITE;
/*!40000 ALTER TABLE `coordenada` DISABLE KEYS */;
INSERT INTO `coordenada` VALUES (1,-33.44889000,-70.66926500),(2,-33.40878300,-70.56781800),(3,-33.42628000,-70.61709000);
/*!40000 ALTER TABLE `coordenada` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `deporte`
--

DROP TABLE IF EXISTS `deporte`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deporte` (
  `id_deporte` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id_deporte`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deporte`
--

LOCK TABLES `deporte` WRITE;
/*!40000 ALTER TABLE `deporte` DISABLE KEYS */;
INSERT INTO `deporte` VALUES (1,'Fútbol');
/*!40000 ALTER TABLE `deporte` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `direccion`
--

DROP TABLE IF EXISTS `direccion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `direccion` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `calle` varchar(100) NOT NULL,
  `numero` int NOT NULL,
  `comuna_id` bigint NOT NULL,
  `coordenada_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdh63uja7g08nw6eeu6diqiwa` (`comuna_id`),
  KEY `FK12q1b6l0ixo5pnh3cxsn686pm` (`coordenada_id`),
  CONSTRAINT `FK12q1b6l0ixo5pnh3cxsn686pm` FOREIGN KEY (`coordenada_id`) REFERENCES `coordenada` (`id`),
  CONSTRAINT `FKdh63uja7g08nw6eeu6diqiwa` FOREIGN KEY (`comuna_id`) REFERENCES `comuna` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `direccion`
--

LOCK TABLES `direccion` WRITE;
/*!40000 ALTER TABLE `direccion` DISABLE KEYS */;
INSERT INTO `direccion` VALUES (1,'Avenida Matta',850,322,1),(2,'Avenida Las Condes',12450,307,2),(3,'Avenida Providencia',2150,316,3);
/*!40000 ALTER TABLE `direccion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `email`
--

DROP TABLE IF EXISTS `email`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `email` (
  `id_email` bigint NOT NULL AUTO_INCREMENT,
  `correo` varchar(100) NOT NULL,
  `es_principal` bit(1) DEFAULT NULL,
  `recinto_id` bigint DEFAULT NULL,
  `usuario_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id_email`),
  KEY `FK70bxiuc3m2s11ncsm55sxxa6i` (`recinto_id`),
  KEY `FKkcch1rfv3cge9f9odplk9ouem` (`usuario_id`),
  CONSTRAINT `FK70bxiuc3m2s11ncsm55sxxa6i` FOREIGN KEY (`recinto_id`) REFERENCES `recinto` (`id`),
  CONSTRAINT `FKkcch1rfv3cge9f9odplk9ouem` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `email`
--

LOCK TABLES `email` WRITE;
/*!40000 ALTER TABLE `email` DISABLE KEYS */;
/*!40000 ALTER TABLE `email` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `estado_reserva`
--

DROP TABLE IF EXISTS `estado_reserva`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `estado_reserva` (
  `id_estado` bigint NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(50) NOT NULL,
  PRIMARY KEY (`id_estado`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `estado_reserva`
--

LOCK TABLES `estado_reserva` WRITE;
/*!40000 ALTER TABLE `estado_reserva` DISABLE KEYS */;
INSERT INTO `estado_reserva` VALUES (1,'PENDIENTE'),(2,'CONFIRMADA'),(3,'CANCELADA');
/*!40000 ALTER TABLE `estado_reserva` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `horario_especial`
--

DROP TABLE IF EXISTS `horario_especial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `horario_especial` (
  `id_he` bigint NOT NULL AUTO_INCREMENT,
  `esta_bloqueado` bit(1) DEFAULT NULL,
  `fecha` date NOT NULL,
  `motivo` varchar(100) DEFAULT NULL,
  `cancha_id` bigint NOT NULL,
  PRIMARY KEY (`id_he`),
  KEY `FKnvav1rbu43le2n9mj43xr5xr8` (`cancha_id`),
  CONSTRAINT `FKnvav1rbu43le2n9mj43xr5xr8` FOREIGN KEY (`cancha_id`) REFERENCES `cancha` (`id_cancha`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `horario_especial`
--

LOCK TABLES `horario_especial` WRITE;
/*!40000 ALTER TABLE `horario_especial` DISABLE KEYS */;
/*!40000 ALTER TABLE `horario_especial` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `imagen`
--

DROP TABLE IF EXISTS `imagen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `imagen` (
  `id_img` bigint NOT NULL AUTO_INCREMENT,
  `url` varchar(255) NOT NULL,
  `recinto_id` bigint NOT NULL,
  PRIMARY KEY (`id_img`),
  KEY `FKawn5t8yc6cvb9ve86cnfyil75` (`recinto_id`),
  CONSTRAINT `FKawn5t8yc6cvb9ve86cnfyil75` FOREIGN KEY (`recinto_id`) REFERENCES `recinto` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `imagen`
--

LOCK TABLES `imagen` WRITE;
/*!40000 ALTER TABLE `imagen` DISABLE KEYS */;
/*!40000 ALTER TABLE `imagen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recinto`
--

DROP TABLE IF EXISTS `recinto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recinto` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `rut_empresa` varchar(20) NOT NULL,
  `direccion_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbg1shetjonyhlbo6ij9pub14r` (`direccion_id`),
  CONSTRAINT `FKbg1shetjonyhlbo6ij9pub14r` FOREIGN KEY (`direccion_id`) REFERENCES `direccion` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recinto`
--

LOCK TABLES `recinto` WRITE;
/*!40000 ALTER TABLE `recinto` DISABLE KEYS */;
INSERT INTO `recinto` VALUES (1,'Complejo Deportivo Santiago Centro','76123456-7',1),(2,'Cancha Futbolito Las Condes','76234567-8',2),(3,'Club Deportivo Providencia','76345678-9',3);
/*!40000 ALTER TABLE `recinto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `region`
--

DROP TABLE IF EXISTS `region`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `region` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `region`
--

LOCK TABLES `region` WRITE;
/*!40000 ALTER TABLE `region` DISABLE KEYS */;
INSERT INTO `region` VALUES (1,'Arica y Parinacota'),(2,'Tarapacá'),(3,'Antofagasta'),(4,'Atacama'),(5,'Coquimbo'),(6,'Valparaíso'),(7,'Región del Libertador Gral. Bernardo O’Higgins'),(8,'Región del Maule'),(9,'Región de Ñuble'),(10,'Región del Biobío'),(11,'Región de la Araucanía'),(12,'Región de Los Ríos'),(13,'Región de Los Lagos'),(14,'Región de Aysén'),(15,'Región de Magallanes'),(16,'Región Metropolitana de Santiago');
/*!40000 ALTER TABLE `region` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reserva`
--

DROP TABLE IF EXISTS `reserva`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reserva` (
  `id_reserva` bigint NOT NULL AUTO_INCREMENT,
  `fecha_uso` date NOT NULL,
  `hora_fin` time(6) NOT NULL,
  `hora_inicio` time(6) NOT NULL,
  `monto_total` decimal(10,2) NOT NULL,
  `cancha_id` bigint NOT NULL,
  `estado_id` bigint NOT NULL,
  `review_id` bigint DEFAULT NULL,
  `usuario_id` bigint NOT NULL,
  PRIMARY KEY (`id_reserva`),
  UNIQUE KEY `UKetllbgf3vaeslpsxial3j9hxn` (`review_id`),
  KEY `FKlmdd0lnuxry7bjp5ba2vvry8` (`cancha_id`),
  KEY `FK3rq0dmocpwgflkhe8ot47lln2` (`estado_id`),
  KEY `FKiad9w96t12u3ms2ul93l97mel` (`usuario_id`),
  CONSTRAINT `FK3rq0dmocpwgflkhe8ot47lln2` FOREIGN KEY (`estado_id`) REFERENCES `estado_reserva` (`id_estado`),
  CONSTRAINT `FKhmppt2yqbw4hohodqbbgomjed` FOREIGN KEY (`review_id`) REFERENCES `review` (`id_review`),
  CONSTRAINT `FKiad9w96t12u3ms2ul93l97mel` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`),
  CONSTRAINT `FKlmdd0lnuxry7bjp5ba2vvry8` FOREIGN KEY (`cancha_id`) REFERENCES `cancha` (`id_cancha`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reserva`
--

LOCK TABLES `reserva` WRITE;
/*!40000 ALTER TABLE `reserva` DISABLE KEYS */;
INSERT INTO `reserva` VALUES (1,'2026-05-16','22:00:00.000000','21:00:00.000000',0.00,1,3,NULL,1);
/*!40000 ALTER TABLE `reserva` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id_review` bigint NOT NULL AUTO_INCREMENT,
  `comentario` text,
  `puntaje` tinyint NOT NULL,
  PRIMARY KEY (`id_review`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol`
--

DROP TABLE IF EXISTS `rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol` (
  `id_rol` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(20) NOT NULL,
  PRIMARY KEY (`id_rol`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol`
--

LOCK TABLES `rol` WRITE;
/*!40000 ALTER TABLE `rol` DISABLE KEYS */;
INSERT INTO `rol` VALUES (1,'CLIENTE'),(2,'DUENO'),(3,'ADMIN');
