DROP TABLE IF EXISTS roles

CREATE table roles(
                      id SERIAL PRIMARY KEY,
                      name VARCHAR(100) NOT NULL,
                      created_on TIMESTAMP NOT NULL,
                      updated_on TIMESTAMP default now()
)
    INSERT INTO roles (name, created_on) values('ADMIN', now()), ('SUPPLIER', now()), ('CUSTOMER', now())

------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS users
create table users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(200) NOT NULL,
                       country VARCHAR(100),
                       state VARCHAR(100),
                       city VARCHAR(100),
                       email VARCHAR(100) NOT NULL,
                       role_id INTEGER NOT NULL,
                       enabled BOOLEAN NOT NULL DEFAULT true,
                       password VARCHAR(100),
                       created_on TIMESTAMP NOT NULL,
                       updated_on TIMESTAMP,
                       last_login_on TIMESTAMP,

)
ALTER TABLE USERS ADD CONSTRAINT UQ_Email UNIQUE(email);

ALTER TABLE users ADD FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE SET NULL;