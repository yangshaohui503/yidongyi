INSERT INTO users(username, password, image, enabled) VALUES ('admin', '$2a$10$OB3Ni.NElsl5i1q6Acj8sOBAoMtoM3wmHjubaX/CZrddC5y5wfQje', '', 1)
INSERT INTO authorities(username, authority) VALUES ('admin','ROLE_USER')
INSERT INTO authorities(username, authority) VALUES ('admin','ROLE_ADMIN')