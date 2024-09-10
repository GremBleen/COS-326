-- ##################PRAC 3 QUERIES##################
-- 1. student personal details
SELECT
    student_key,
    student_number,
    get_full_name (student_name) as student_name,
    age_in_years (date_of_birth)
FROM
    student;

-- 2. undergraduate student registration details
SELECT
    student_key,
    student_number,
    get_full_name (student_name) as student_name,
    degree_code,
    year_of_study,
    courses
FROM
    undergraduate_student;

-- 3. postgraduate student registration details
SELECT
    student_key,
    student_number,
    get_full_name (student_name) as student_name,
    degree_code,
    year_of_study,
    category,
    get_full_name (supervisor_name) as supervisor_name
FROM
    postgraduate_student;

-- 4. undergraduate student registration details for final year students
SELECT
    student_key,
    student_number,
    get_full_name (student_name) as student_name,
    degree_code,
    year_of_study,
    courses
FROM
    undergraduate_student
WHERE
    is_final_year_student (student_number);

-- 5. undergraduate student registration details for students studying COS326
SELECT
    student_key,
    student_number,
    get_full_name (student_name) as student_name,
    degree_code,
    year_of_study,
    courses
FROM
    undergraduate_student
WHERE
    is_registered_for ('COS326', student_number);

-- 6. postgraduate student registration details for students who are full-time
SELECT
    student_key,
    student_number,
    get_full_name (student_name) as student_name,
    degree_code,
    year_of_study,
    category,
    get_full_name (supervisor_name) as supervisor_name
FROM
    postgraduate_student
WHERE
    is_full_time (student_number);

-- 7. postgraduate student registration details for part-time students
SELECT
    student_key,
    student_number,
    get_full_name (student_name) as student_name,
    degree_code,
    year_of_study,
    category,
    get_full_name (supervisor_name) as supervisor_name
FROM
    postgraduate_student
WHERE
    is_part_time (student_number);

-- ##################PRAC 4 QUERIES##################
-- 1. students who are registered for COS326
SELECT
    student_number,
    get_full_name (student_name) as student_name,
    age_in_years (date_of_birth),
    degree_code
FROM
    student
WHERE
    is_registered_for ('COS326', student_number);

-- 2.1 has_valid_courses returns true
SELECT
    has_valid_courses (ARRAY['COS326', 'COS301', 'MTH301', 'PHL301']);

-- 2.2 has_valid_courses returns false
SELECT
    has_valid_courses (ARRAY['COS326', 'COS330', 'MTH301', 'PHL301']);

-- 3.1 has_duplicate_courses returns true
SELECT
    has_duplicate_courses (ARRAY['COS326', 'COS326', 'MTH301', 'PHL301']);

-- 3.2 has_duplicate_courses returns false
SELECT
    has_duplicate_courses (ARRAY['COS326', 'COS301', 'MTH301', 'PHL301']);