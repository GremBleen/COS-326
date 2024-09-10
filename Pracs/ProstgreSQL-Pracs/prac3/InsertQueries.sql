DELETE FROM degree_program;

INSERT INTO
    degree_program (
        degree_code,
        degree_name,
        number_of_years,
        faculty
    )
VALUES
    ('BSc', 'Bachelor of Science', 3, 'EBIT'),
    ('BIT', 'Bachelor of IT', 4, 'EBIT'),
    ('PhD', 'Philosophiae Doctor', 5, 'EBIT');

DELETE FROM COURSE;

INSERT INTO
    course (
        course_code,
        course_name,
        department,
        course_credits
    )
VALUES
    (
        'COS301',
        'Software Engineering',
        'Computer Science',
        40
    ),
    (
        'COS326',
        'Database Systems',
        'Computer Science',
        20
    ),
    (
        'MTH301',
        'Discrete Mathematics',
        'Mathematics',
        15
    ),
    ('PHL301', 'Logical Reasoning', 'Philosophy', 15);

DELETE FROM student;

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
        140010,
        ROW ('Mr', 'John', 'Doe'),
        TO_DATE('10-01-1996', 'DD-MM-YYYY'),
        'BSc',
        3,
        ARRAY['COS301', 'COS326', 'MTH301']
    ),
    (
        140015,
        ROW ('Miss', 'Alice', 'Smith'),
        TO_DATE('25-05-1995', 'DD-MM-YYYY'),
        'BSc',
        3,
        ARRAY['COS301', 'PHL301', 'MTH301']
    ),
    (
        131120,
        ROW ('Ms', 'Rachel', 'Boulle'),
        TO_DATE('30-01-1995', 'DD-MM-YYYY'),
        'BIT',
        3,
        ARRAY['COS301', 'COS326', 'PHL301']
    ),
    (
        131140,
        ROW ('Mr', 'Ben', 'Boulle'),
        TO_DATE('20-02-1996', 'DD-MM-YYYY'),
        'BIT',
        4,
        ARRAY['COS301', 'COS326', 'MTH301', 'PHL301']
    );

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
        101122,
        ROW ('Mr', 'Aidan', 'Chapman'),
        TO_DATE('15-06-1987', 'DD-MM-YYYY'),
        'PhD',
        2,
        'Full-Time',
        ROW ('Prof', 'Thambo', 'Nyathi')
    ),
    (
        121101,
        ROW ('Mr', 'Graeme', 'Blain'),
        TO_DATE('27-04-1985', 'DD-MM-YYYY'),
        'PhD',
        3,
        'Part-Time',
        ROW ('Prof', 'Nelishia', 'Pillay')
    );