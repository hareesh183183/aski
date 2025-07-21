DROP TABLE IF EXISTS roles

CREATE table roles(
id SERIAL PRIMARY KEY,
name VARCHAR(100) NOT NULL,
created_on TIMESTAMP NOT NULL,
updated_on TIMESTAMP NOT NULL
)
INSERT INTO roles (name) values('ADMIN'), ('SUPPLIER'), ('CUSTOMER')

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
created_on TIMESTAMP NOT NULL,
updated_on TIMESTAMP NOT NULL,
last_login_on TIMESTAMP NOT NULL
)

ALTER TABLE users ADD FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE SET NULL