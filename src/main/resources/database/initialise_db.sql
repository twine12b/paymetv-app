INSERT INTO paymetv_db1.users (username, password, email) VALUES ('test user', 'password', 'test@test.com');
INSERT INTO paymetv_db1.image_face(artifact_id, front) VALUES (1, "test image face front");
INSERT INTO paymetv_db1.artifact (name, model, description, user_id, status) VALUES ("test artifact name", "some test model","test description", 1, 1);
