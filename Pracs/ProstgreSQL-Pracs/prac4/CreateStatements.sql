DROP SCHEMA IF EXISTS public CASCADE;

CREATE SCHEMA public;

CREATE DOMAIN date_of_birth AS DATE CHECK (VALUE <= CURRENT_DATE);

CREATE DOMAIN study_year AS INTEGER CHECK (
    0 < VALUE
    AND VALUE < 8
);

CREATE DOMAIN student_number AS BIGINT CHECK (
    VALUE >= 100000
    AND VALUE <= 999999
);

CREATE TYPE postgraduate_category AS ENUM('Part-Time', 'Full-Time');

CREATE TYPE title_type AS ENUM(
    'Ms',
    'Mev',
    'Miss',
    'Mrs',
    'Mr',
    'Mnr',
    'Dr',
    'Prof'
);

CREATE TYPE name_type AS (title title_type, first_name TEXT, last_name TEXT);

CREATE SEQUENCE student_seq START
WITH
    1;

CREATE SEQUENCE degree_seq START
WITH
    1;

CREATE SEQUENCE course_seq START
WITH
    1;

-- Table: course
-- DROP TABLE IF EXISTS course;
CREATE TABLE
    course (
        course_key integer NOT NULL DEFAULT nextval('course_seq'::regclass),
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
        degree_key integer NOT NULL DEFAULT nextval('degree_seq'::regclass),
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
        student_key integer NOT NULL DEFAULT nextval('student_seq'::regclass),
        student_number student_number NOT NULL,
        student_name name_type NOT NULL,
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
        supervisor_name name_type NOT NULL
    ) INHERITS (student);

CREATE TABLE
    deleted_undergraduate_student (
        student_key integer NOT NULL,
        student_number student_number NOT NULL,
        student_name name_type NOT NULL,
        date_of_birth date_of_birth NOT NULL,
        degree_code text NOT NULL,
        year_of_study study_year NOT NULL,
        courses text[] NOT NULL,
        deleted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        user_id BIGINT,
        CONSTRAINT del_undergrad_pkey PRIMARY KEY (student_key)
    );

CREATE TABLE
    deleted_postgraduate_student (
        student_key integer NOT NULL,
        student_number student_number NOT NULL,
        student_name name_type NOT NULL,
        date_of_birth date_of_birth NOT NULL,
        degree_code text NOT NULL,
        year_of_study study_year NOT NULL,
        category postgraduate_category NOT NULL,
        supervisor_name name_type NOT NULL,
        deleted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        user_id BIGINT,
        CONSTRAINT del_postgrad_pkey PRIMARY KEY (student_key)
    );

-- STUDENT FUNCTIONS
CREATE
OR REPLACE FUNCTION get_full_name (full_name name_type) RETURNS TEXT AS $$
DECLARE
    full_name_string TEXT;
BEGIN
    SELECT
        (full_name).title || ' ' || (full_name).first_name || ' ' || (full_name).last_name
    INTO
        full_name_string;

    RETURN full_name_string;
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
OR REPLACE FUNCTION is_valid_degree_code (degree_code_in TEXT) RETURNS BOOLEAN AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM degree_program WHERE degree_program.degree_code = degree_code_in) THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END;
$$ LANGUAGE plpgsql;

CREATE
OR REPLACE FUNCTION is_valid_course (course_code TEXT) RETURNS BOOLEAN AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM course WHERE course.course_code = course_code) THEN
        RETURN TRUE;
    ELSE
        RETURN FALSE;
    END IF;
END;
$$ LANGUAGE plpgsql;

CREATE
OR REPLACE FUNCTION has_valid_courses (courses text[]) RETURNS boolean AS $$
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

CREATE
OR REPLACE FUNCTION has_duplicate_courses (courses text[]) RETURNS BOOLEAN AS $$
DECLARE
    course_code TEXT;
    course_codes TEXT[];
BEGIN
    SELECT
        ARRAY_AGG(DISTINCT course)
    INTO
        course_codes
    FROM
        UNNEST(courses) AS course;

    IF ARRAY_LENGTH(courses, 1) = ARRAY_LENGTH(course_codes, 1) THEN
        RETURN FALSE;
    ELSE
        RETURN TRUE;
    END IF;
END;
$$ LANGUAGE plpgsql;

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
OR REPLACE FUNCTION course_code_frequency (course_code TEXT, course_codes TEXT[]) RETURNS INTEGER AS $$
DECLARE
    course_code_freq INTEGER := 0;
    registration TEXT;
BEGIN
    FOREACH registration IN ARRAY course_codes
    LOOP
        IF registration = course_code THEN
            course_code_freq := course_code_freq + 1;
        END IF;
    END LOOP;
    RETURN course_code_freq;
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

-- Trigger Functions
CREATE
OR REPLACE FUNCTION check_valid_degree_code () RETURNS TRIGGER AS $$
BEGIN
    IF NOT is_valid_degree_code(NEW.degree_code) THEN
        RAISE EXCEPTION 'Invalid degree code: %', NEW.degree_code;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE
OR REPLACE FUNCTION check_valid_course_codes () RETURNS TRIGGER AS $$
BEGIN
    IF NOT has_valid_courses(NEW.courses) THEN
        RAISE EXCEPTION 'Invalid course codes: %', NEW.courses;
    END IF;
    IF has_duplicate_courses(NEW.courses) THEN
        RAISE EXCEPTION 'Duplicate course codes %', NEW.courses;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE
OR REPLACE FUNCTION record_delete_undergrad () RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO deleted_undergraduate_student
    SELECT OLD.*;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE
OR REPLACE FUNCTION record_delete_postgrad () RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO deleted_postgraduate_student
    SELECT OLD.*;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Triggers
CREATE
OR REPLACE TRIGGER check_valid_degree BEFORE INSERT
OR
UPDATE ON student FOR EACH ROW
EXECUTE FUNCTION check_valid_degree_code ();

CREATE
OR REPLACE TRIGGER check_valid_degree BEFORE INSERT
OR
UPDATE ON undergraduate_student FOR EACH ROW
EXECUTE FUNCTION check_valid_degree_code ();

CREATE
OR REPLACE TRIGGER check_valid_degree BEFORE INSERT
OR
UPDATE ON postgraduate_student FOR EACH ROW
EXECUTE FUNCTION check_valid_degree_code ();

CREATE
OR REPLACE TRIGGER check_valid_course_registration BEFORE INSERT
OR
UPDATE ON undergraduate_student FOR EACH ROW
EXECUTE FUNCTION check_valid_course_codes ();

CREATE
OR REPLACE TRIGGER audit_delete_undergrad
AFTER DELETE ON undergraduate_student FOR EACH ROW
EXECUTE FUNCTION record_delete_undergrad ();

CREATE
OR REPLACE TRIGGER audit_delete_postgrad
AFTER DELETE ON postgraduate_student FOR EACH ROW
EXECUTE FUNCTION record_delete_postgrad ();