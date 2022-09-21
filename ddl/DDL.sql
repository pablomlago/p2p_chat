CREATE DATABASE p2p WITH OWNER = "postgres";
GRANT ALL PRIVILEGES ON DATABASE p2p to "postgres";

CREATE TABLE usuarios (
    nome character varying(30) NOT NULL,
    contrasinal character(60) NOT NULL,
    estado character varying(100) NOT NULL,
    key bytea,
    PRIMARY KEY(nome)
);

CREATE TABLE solicitudes (
    emisor character varying(30) NOT NULL,
    receptor character varying(30) NOT NULL,
 
    FOREIGN KEY (receptor) REFERENCES usuarios(nome) 
    	ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (emisor) REFERENCES usuarios(nome) 
    	ON UPDATE CASCADE ON DELETE CASCADE,
    
    PRIMARY KEY(emisor, receptor)
);


CREATE TABLE amigos (
    usuario character varying(30) NOT NULL,
    amigo character varying(30) NOT NULL,
    
    FOREIGN KEY (amigo) REFERENCES usuarios(nome) 
    	ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (usuario) REFERENCES usuarios(nome) 
    	ON UPDATE CASCADE ON DELETE CASCADE,
  
    PRIMARY KEY(usuario, amigo)
);
