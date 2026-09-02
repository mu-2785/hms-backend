-- Seed a handful of departments so downstream modules (doctors, staff, etc.)
-- have real foreign keys to work against while you build them.
INSERT INTO departments (name, description) VALUES
    ('Cardiology', 'Diagnosis and treatment of heart-related conditions'),
    ('Orthopedics', 'Musculoskeletal system: bones, joints, ligaments'),
    ('Neurology', 'Disorders of the nervous system'),
    ('Pediatrics', 'Medical care for infants, children and adolescents'),
    ('General Medicine', 'Primary/general adult healthcare'),
    ('Emergency', 'Acute and emergency care');
