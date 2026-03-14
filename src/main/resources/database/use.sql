USE paymetv_db1;

-- ============================================================
-- PayMeTV test seed data
--
-- Schema notes (from schema.sql / JPA entities):
--   users        : id managed by users_seq     (no AUTO_INCREMENT)
--   image_face   : id managed by image_face_seq (no AUTO_INCREMENT)
--   artifact     : id managed by artifact_seq   (no AUTO_INCREMENT)
--
-- FK constraints:
--   artifact.user_id        → users.id
--   artifact.image_faces_id → image_face.id   (FK + UNIQUE)
--
-- Circular data dependency:
--   image_face.artifact_id holds the owning artifact's id,
--   but there is no FK constraint on that column, so we can
--   insert image_face first with a placeholder and UPDATE later.
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ─────────────────────────────────────────
-- 1. Users  (no FK dependencies)
-- ─────────────────────────────────────────
INSERT INTO users (username, password, email)
VALUES ('test_user', 'test_password', 'test@example.com');

-- ─────────────────────────────────────────
-- 2. ImageFace  (artifact_id back-filled in step 4)
-- ─────────────────────────────────────────
INSERT INTO image_face (artifact_id, front)
VALUES (0, 'test_front.jpg');

-- ─────────────────────────────────────────
-- 3. Artifact  (references users.id=1 and image_face.id=1)
-- ─────────────────────────────────────────
INSERT INTO artifact (name, description, model, status, user_id, image_faces_id)
VALUES (1, 'Test Artifact', 'A test artifact for development', 'test-model-v1', 1, 1, 1);

-- ─────────────────────────────────────────
-- 4. Back-fill image_face.artifact_id now that artifact.id=1 exists
-- ─────────────────────────────────────────
UPDATE image_face
SET artifact_id = 1
WHERE id = 1;

SET FOREIGN_KEY_CHECKS = 1;

-- ─────────────────────────────────────────
-- 5. Advance JPA sequence tables
--
-- Hibernate's default allocationSize = 50.
-- With next_val = N, IDs allocated = (N-1)*50+1 .. N*50
-- Setting next_val = 2 means the app will allocate IDs 51-100
-- on its next startup, never touching the manually inserted ID=1.
-- ─────────────────────────────────────────
UPDATE users_seq     SET next_val = 2;
UPDATE image_face_seq SET next_val = 2;
UPDATE artifact_seq   SET next_val = 2;