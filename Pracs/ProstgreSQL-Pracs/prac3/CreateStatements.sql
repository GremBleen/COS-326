DROP SCHEMA IF EXISTS public CASCADE;

CREATE SCHEMA public;

CREATE DOMAIN date_of_birth AS DATE CHECK (VALUE <= CURRENT_DATE);

CREATE DOMAIN study_year AS INTEGER CHECK (
    0 < VALUE
    AND VALUE < 8
);

CREATE TYPE postgraduate_category AS ENUM('Part-Time', 'Full-Time');

CREATE TYPE student_title AS ENUM('Ms', 'Mev', 'Miss', 'Mr', 'Mrs', 'Mnr');

CREATE TYPE supervisor_title AS ENUM(
    'Dr',
    'Prof',
    'Mr',
    'Mrs',
    'Ms',
    'Mev',
    'Miss',
    'Mnr'
);

CREATE TYPE student_name AS (
    title student_title,
    first_name TEXT,
    last_name TEXT
);

CREATE TYPE supervisor_name AS (
    title supervisor_title,
    first_name TEXT,
    last_name TEXT
);

CREATE SEQUENCE key_seq START
WITH
    1;

CREATE SEQUENCE student_number_seq START
WITH
    1;

-- Table: course
-- DROP TABLE IF EXISTS course;
CREATE TABLE
    course (
        course_key integer NOT NULL DEFAULT nextval('key_seq'::regclass),
        course_code text NOT NULL,
        course_name text NOT NULL,
        department text NOT NULL,
        course_credits integer NOT NULL,
        CONSTRAINT course_pkey PRIMARY KEY (course_key),
        CONSTRAINT course_code_unq UNIQUE (course_code)
    );

-- Table: degree_program
-- DROP TABLE IF EXISTS degree_program;
CREATE TABLE
    degree_program (
        degree_key integer NOT NULL DEFAULT nextval('key_seq'::regclass),
        degree_code text NOT NULL,
        degree_name text NOT NULL,
        number_of_years study_year NOT NULL,
        faculty text NOT NULL,
        CONSTRAINT degree_program_pkey PRIMARY KEY (degree_key),
        CONSTRAINT degree_code_unq UNIQUE (degree_code)
    );

-- Table: student
-- DROP TABLE IF EXISTS student;
CREATE TABLE
    student (
        student_key integer NOT NULL DEFAULT nextval('student_number_seq'::regclass),
        student_number bigint NOT NULL,
        student_name student_name NOT NULL,
        date_of_birth date_of_birth NOT NULL,
        degree_code text NOT NULL,
        year_of_study study_year NOT NULL,
        CONSTRAINT students_pkey PRIMARY KEY (student_key),
        CONSTRAINT degree_code_fkey FOREIGN KEY (degree_code) REFERENCES degree_program (degree_code) ON UPDATE CASCADE ON DELETE CASCADE
    );

-- Table: undergraduate_student
-- DROP TABLE IF EXISTS undergraduate_student;
CREATE TABLE
    undergraduate_student (
        -- Inherited from table student: student_key integer NOT NULL DEFAULT nextval('students_student_key_seq'::regclass),
        -- Inherited from table student: student_number bigint NOT NULL DEFAULT nextval('student_number_seq'::regclass),
        -- Inherited from table student: student_name student_name NOT NULL,
        -- Inherited from table student: date_of_birth date_of_birth NOT NULL,
        -- Inherited from table student: degree_code text NOT NULL,
        -- Inherited from table student: year_of_study study_year NOT NULL,
        courses text[] NOT NULL
    ) INHERITS (student);

-- Table: postgraduate_student
-- DROP TABLE IF EXISTS postgraduate_student;
CREATE TABLE
    postgraduate_student (
        -- Inherited from table student: student_key integer NOT NULL DEFAULT nextval('students_student_key_seq'::regclass),
        -- Inherited from table student: student_number bigint NOT NULL DEFAULT nextval('student_number_seq'::regclass),
        -- Inherited from table student: student_name student_name NOT NULL,
        -- Inherited from table student: date_of_birth date_of_birth NOT NULL,
        -- Inherited from table student: degree_code text NOT NULL,
        -- Inherited from table student: year_of_study study_year NOT NULL,
        category postgraduate_category NOT NULL,
        supervisor_name supervisor_name NOT NULL
    ) INHERITS (student);

-- STUDENT FUNCTIONS
CREATE
OR REPLACE FUNCTION get_student_name (student_num BIGINT) RETURNS TEXT AS $$
DECLARE
    student_name_string TEXT;
BEGIN
    SELECT
        (student_name).title || ' ' || (student_name).first_name || ' ' || (student_name).last_name
    INTO
        student_name_string
    FROM
        student
    WHERE
        student.student_number = student_num;
    RETURN student_name_string;
END;
$$ LANGUAGE plpgsql;

CREATE
OR REPLACE FUNCTION get_student_supervisor_name (student_num BIGINT) RETURNS TEXT AS $$
DECLARE
    supervisor_name_string TEXT;
BEGIN 
    SELECT
        (supervisor_name).title || ' ' || (supervisor_name).first_name || ' ' || (supervisor_name).last_name
    INTO
        supervisor_name_string
    FROM
        postgraduate_student
    WHERE
        postgraduate_student.student_number = student_num;
    RETURN supervisor_name_string;
END;
$$ LANGUAGE plpgsql;

CREATE
OR REPLACE FUNCTION age_in_years (date_of_birth DATE) RETURNS INT AS $$
DECLARE
    age INT;
BEGIN
    SELECT
        EXTRACT(YEAR FROM AGE(CURRENT_DATE, date_of_birth))
    INTO
        age;
    RETURN age;
END;
$$ LANGUAGE plpgsql;

-- UNDERGRADUATE STUDENT FUNCTIONS
CREATE
OR REPLACE FUNCTION check_courses_exist (courses text[]) RETURNS boolean AS $$
DECLARE
    course_code_element text;
BEGIN
    FOREACH course_code_element IN ARRAY courses
    LOOP
        IF NOT EXISTS (SELECT 1 FROM course WHERE course.course_code = course_code_element) THEN
            RETURN FALSE;
        END IF;
    END LOOP;
    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;

ALTER TABLE undergraduate_student
ADD CONSTRAINT check_courses_exist CHECK (check_courses_exist (courses));

CREATE
OR REPLACE FUNCTION is_registered_for (course_code TEXT, student_num BIGINT) RETURNS BOOLEAN AS $$
DECLARE
    crs_code TEXT;
BEGIN
    SELECT
        course_code = ANY (courses)
    INTO
        crs_code
    FROM
        undergraduate_student AS us
    WHERE
        us.student_number = student_num;

    IF crs_code THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END;
$$ LANGUAGE plpgsql;

CREATE
OR REPLACE FUNCTION is_final_year_student (student_num BIGINT) RETURNS BOOLEAN AS $$
DECLARE
    curr_year study_year;
    deg_years study_year;
BEGIN
    SELECT
        us.year_of_study,
        dp.number_of_years
    INTO
        curr_year, deg_years
    FROM
        undergraduate_student AS us
    JOIN
        degree_program AS dp ON us.degree_code = dp.degree_code
    WHERE
        us.student_number = student_num;

    IF curr_year = deg_years THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- POSTGRADUATE STUDENT FUNCTIONS
CREATE
OR REPLACE FUNCTION is_full_time (student_num BIGINT) RETURNS BOOLEAN AS $$
DECLARE
    stu_category postgraduate_category;
BEGIN
    SELECT
        postgraduate_student.category
    INTO
        stu_category
    FROM
        postgraduate_student
    WHERE
        postgraduate_student.student_number = student_num;

    IF stu_category = 'Full-Time' THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END;
$$ LANGUAGE plpgsql;

-- POSTGRADUATE STUDENT FUNCTIONS
CREATE
OR REPLACE FUNCTION is_part_time (student_num BIGINT) RETURNS BOOLEAN AS $$
DECLARE
    stu_category postgraduate_category;
BEGIN
    SELECT
        postgraduate_student.category
    INTO
        stu_category
    FROM
        postgraduate_student
    WHERE
        postgraduate_student.student_number = student_num;

    IF stu_category = 'Part-Time' THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END;
$$ LANGUAGE plpgsql;