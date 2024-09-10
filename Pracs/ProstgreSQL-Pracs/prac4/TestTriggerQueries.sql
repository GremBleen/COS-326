-- 1. INSERTS
-- 1.1 Undergradute with an invalid degree code
INSERT INTO
    undergraduate_student (
        student_number,
        student_name,
        date_of_birth,
        degree_code,
        year_of_study,
        courses
    )
VALUES
    (
        140020,
        ROW ('Mr', 'John', 'Doe'),
        TO_DATE('10-01-1996', 'DD-MM-YYYY'),
        'BEng',
        3,
        ARRAY['COS301', 'COS326', 'MTH301']
    );

-- 1.2 Undergraduate with an invalid course code
INSERT INTO
    undergraduate_student (
        student_number,
        student_name,
        date_of_birth,
        degree_code,
        year_of_study,
        courses
    )
VALUES
    (
        140025,
        ROW ('Miss', 'Alice', 'Smith'),
        TO_DATE('25-05-1995', 'DD-MM-YYYY'),
        'BSc',
        3,
        ARRAY['COS301', 'PHL301', 'MTH301', 'COS330']
    );

-- 1.3 Postgraduate with an invalid degree code
INSERT INTO
    postgraduate_student (
        student_number,
        student_name,
        date_of_birth,
        degree_code,
        year_of_study,
        category,
        supervisor_name
    )
VALUES
    (
        101125,
        ROW ('Mr', 'Aidan', 'Chapman'),
        TO_DATE('10-01-1996', 'DD-MM-YYYY'),
        'MSc',
        1,
        'Full-Time',
        ROW ('Dr', 'John', 'Doe')
    );

-- 2. UPDATES
-- 2.1 Update the degree code of an undergraduate student to an invalid degree code
UPDATE undergraduate_student
SET
    degree_code = 'BEng'
WHERE
    student_number = 140010;

-- 2.2 Update the course codes of an undergraduate student to include an invalid course code
UPDATE undergraduate_student
SET
    courses = ARRAY['COS301', 'PHL301', 'MTH301', 'COS330']
WHERE
    student_number = 140015;

-- 2.3 Update the degree code of a postgraduate student to an invalid degree code
UPDATE postgraduate_student
SET
    degree_code = 'MSc'
WHERE
    student_number = 101122;

-- 3. DELETES
-- 3.1 Delete an undergraduate student
DELETE FROM undergraduate_student
WHERE
    student_number = 140010;

-- 3.2 Delete a postgraduate student
DELETE FROM postgraduate_student
WHERE
    student_number = 101122;