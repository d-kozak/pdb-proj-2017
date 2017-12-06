ALTER SESSION SET NLS_DATE_FORMAT='DD-MM-YYYY';

DROP TABLE SpatialEntity CASCADE CONSTRAINT;
DROP TABLE Picture;
DROP TABLE Description;
DROP TABLE BlindMapResult;

CREATE TABLE SpatialEntity (
  id NUMBER NOT NULL,
  name VARCHAR(30) NOT NULL,
  geometry SDO_GEOMETRY NOT NULL,
  validFrom DATE NULL,
  validTo DATE NULL,
  entityType VARCHAR(10) CHECK(entityType in ('country', 'river', 'place')),

  CHECK(validTo >= validFrom),
  CONSTRAINT PKSpatialEntity PRIMARY KEY (id)
);

CREATE TABLE Picture (
  id NUMBER NOT NULL,
  description VARCHAR(255),
  pictureType VARCHAR(10) CHECK( pictureType in ('normal', 'flag')),
  createdAt DATE,
  spatialEntityId NUMBER,
  img ORDSYS.ORDIMAGE,
  img_si ORDSYS.SI_STILLIMAGE,
  img_ac ORDSYS.SI_AVERAGECOLOR,
  img_ch ORDSYS.SI_COLORHISTOGRAM,
  img_pc ORDSYS.SI_POSITIONALCOLOR,
  img_tx ORDSYS.SI_TEXTURE,

  CONSTRAINT PKPicture PRIMARY KEY (id),
  CONSTRAINT FKPictureSpatialEntity FOREIGN KEY (spatialEntityId) REFERENCES SpatialEntity(id)
);

CREATE TABLE Description (
  id NUMBER NOT NULL,
  description VARCHAR(255),
  validFrom DATE NULL,
  validTo DATE NULL,
  spatialEntityId NUMBER,

  CHECK(validTo >= validFrom),
  CONSTRAINT PKDescription PRIMARY KEY(id),
  CONSTRAINT FKDescriptionSpatialEntity FOREIGN KEY (spatialEntityId) REFERENCES SpatialEntity(id)
);

CREATE TABLE BlindMapResult (
 id NUMBER NOT NULL,
 person VARCHAR(30) NOT NULL,
 points NUMBER NOT NULL,

 CONSTRAINT PKBlindMapResult PRIMARY KEY (id)
);

DELETE FROM USER_SDO_GEOM_METADATA WHERE
	TABLE_NAME = 'SPATIALENTITY' AND COLUMN_NAME = 'GEOMETRY';

INSERT INTO USER_SDO_GEOM_METADATA VALUES (
	'SPATIALENTITY', 'geometry',
	SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 1000, 0.001), SDO_DIM_ELEMENT('Y', 0, 1000, 0.001)),
	NULL
);
CREATE INDEX SP_INDEX_SpatialEntitiesGeometry ON SpatialEntity (geometry) indextype is MDSYS.SPATIAL_INDEX ;

COMMIT;

INSERT INTO SpatialEntity(id, name, geometry, validFrom, validTo, entityType) VALUES (
    1,
    'Brno',
    SDO_GEOMETRY(2001, NULL,
	    SDO_POINT_TYPE(142, 142, NULL),
		NULL, NULL
	),
	TO_DATE('27-10-1500', 'dd-mm-yyyy'),
    TO_DATE('27-10-2200', 'dd-mm-yyyy'),
    'place'
);

INSERT INTO Description(id, description, validFrom, validTo, spatialEntityId) VALUES (
    1,
    'The best city on Pandora!',
    TO_DATE('11-11-1500', 'dd-mm-yyyy'),
    TO_DATE('11-11-2200', 'dd-mm-yyyy'),
    1
);

INSERT INTO SpatialEntity(id, name, geometry, validFrom, validTo, entityType) VALUES (
    2,
    'Praha',
    SDO_GEOMETRY(2001, NULL,
	    SDO_POINT_TYPE(242, 142, NULL),
		NULL, NULL
	),
	TO_DATE('27-10-1100', 'dd-mm-yyyy'),
    TO_DATE('27-10-2300', 'dd-mm-yyyy'),
    'place'
);

INSERT INTO Description(id, description, validFrom, validTo, spatialEntityId) VALUES (
    2,
    'Unknow village beyond the horizont.',
    TO_DATE('11-11-1500', 'dd-mm-yyyy'),
    TO_DATE('11-11-2200', 'dd-mm-yyyy'),
    2
);

INSERT INTO SpatialEntity(id, name, geometry, validFrom, validTo, entityType) VALUES (
	3,
	'Some river',
	SDO_GEOMETRY(2002, NULL, NULL,
		SDO_ELEM_INFO_ARRAY(1, 2, 1),
		SDO_ORDINATE_ARRAY(0, 40, 50, 70, 120, 150)
	),
	TO_DATE('3-11-1120', 'dd-mm-yyyy'),
	TO_DATE('3-11-2116', 'dd-mm-yyyy'),
	'river'
);

INSERT INTO Description(id, description, validFrom, validTo, spatialEntityId) VALUES (
    3,
    'Large river from somewhere to nowhere',
    TO_DATE('11-11-1700', 'dd-mm-yyyy'),
    TO_DATE('11-11-2200', 'dd-mm-yyyy'),
    3
);

INSERT INTO SpatialEntity(id, name, geometry, validFrom, validTo, entityType) VALUES (
	4,
	'Czech Republic',
	SDO_GEOMETRY(2003, NULL, NULL,
		SDO_ELEM_INFO_ARRAY(1, 1003, 1),
		SDO_ORDINATE_ARRAY(10, 10, 20, 100, 150, 150, 250, 170, 350, 120, 250, 50, 10, 10)
	),
	TO_DATE('1-1-1993', 'dd-mm-yyyy'),
	TO_DATE('1-1-3000', 'dd-mm-yyyy'),
	'country'
);

INSERT INTO Description(id, description, validFrom, validTo, spatialEntityId) VALUES (
    4,
    'Some republic',
    TO_DATE('11-11-1700', 'dd-mm-yyyy'),
    TO_DATE('11-11-2200', 'dd-mm-yyyy'),
    4
);

COMMIT;
