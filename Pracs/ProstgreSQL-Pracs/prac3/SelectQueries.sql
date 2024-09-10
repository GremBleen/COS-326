-- 1. student personal details
SELECT
    student_key,
    student_number,
    get_student_name (student_number),
    age_in_years (date_of_birth)
FROM
    student;

-- 2. undergraduate student registration details
SELECT
    student_key,
    student_number,
    get_student_name (student_number),
    degree_code,
    year_of_study,
    courses
FROM
    undergraduate_student;

-- 3. postgraduate student registration details
SELECT
    student_key,
    student_number,
    get_student_name (student_number),
    degree_code,
    year_of_study,
    category,
    get_student_supervisor_name (student_number)
FROM
    postgraduate_student;

-- 4. undergraduate student registration details for final year students
SELECT
    student_key,
    student_number,
    get_student_name (student_number),
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
    get_student_name (student_number),
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
    get_student_name (student_number),
    degree_code,
    year_of_study,
    category,
    get_student_supervisor_name (student_number)
FROM
    postgraduate_student
WHERE
    is_full_time (student_number);

-- 7. postgraduate student registration details for part-time students
SELECT
    student_key,
    student_number,
    get_student_name (student_number),
    degree_code,
    year_of_study,
    category,
    get_student_supervisor_name (student_number)
FROM
    postgraduate_student
WHERE
    is_part_time (student_number);