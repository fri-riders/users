CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO users (uuid, email, password, first_name, last_name, is_active, is_confirmed, created_at, updated_at, deleted_at) VALUES (uuid_generate_v4(), 'user1@example.com', '123456', 'User', 'One', TRUE, TRUE, TIMESTAMP '2017-01-12 12:00:00', NULL, NULL);
INSERT INTO users (uuid, email, password, first_name, last_name, is_active, is_confirmed, created_at, updated_at, deleted_at) VALUES (uuid_generate_v4(), 'user2@example.com', '123456', 'User', 'Two', TRUE, TRUE, TIMESTAMP '2017-01-12 12:00:00', NULL, NULL);
INSERT INTO users (uuid, email, password, first_name, last_name, is_active, is_confirmed, created_at, updated_at, deleted_at) VALUES (uuid_generate_v4(), 'user3@example.com', '123456', 'User', 'Three', TRUE, TRUE, TIMESTAMP '2017-01-12 12:00:00', NULL, NULL);
INSERT INTO users (uuid, email, password, first_name, last_name, is_active, is_confirmed, created_at, updated_at, deleted_at) VALUES (uuid_generate_v4(), 'user4@example.com', '123456', 'User', 'Four', TRUE, FALSE, TIMESTAMP '2017-01-12 12:00:00', NULL, NULL);