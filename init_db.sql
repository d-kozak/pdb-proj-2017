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