-- EduHub Database Creation Script
-- Ensure all extensions are available
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Create Table: users
CREATE TABLE IF NOT EXISTS public.users
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    first_name character varying(50) NOT NULL,
    last_name character varying(50) NOT NULL,
    email character varying(100) NOT NULL,
    role character varying(20),
    birthdate timestamp without time zone NOT NULL DEFAULT now(),
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_email_key UNIQUE (email)
);

-- 2. Create Table: course
CREATE TABLE IF NOT EXISTS public.course
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    name character varying(255) NOT NULL,
    description text,
    total_hours integer NOT NULL,
    CONSTRAINT course_pkey PRIMARY KEY (id)
);

-- 3. Create Table: subject
CREATE TABLE IF NOT EXISTS public.subject
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    name character varying(255) NOT NULL,
    description text,
    CONSTRAINT subject_pkey PRIMARY KEY (id)
);

-- 4. Create Table: auth
CREATE TABLE IF NOT EXISTS public.auth
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    user_id uuid NOT NULL,
    email character varying(100) NOT NULL,
    password character varying(255) NOT NULL,
    CONSTRAINT auth_pkey PRIMARY KEY (id),
    CONSTRAINT auth_email_key UNIQUE (email),
    CONSTRAINT auth_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES public.users (id) ON DELETE CASCADE
);

-- 5. Create Table: attendance
CREATE TABLE IF NOT EXISTS public.attendance
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    student_id uuid NOT NULL,
    course_id uuid NOT NULL,
    attendance_date timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_present boolean NOT NULL DEFAULT false,
    subject_id uuid NOT NULL,
    CONSTRAINT attendance_pkey PRIMARY KEY (id),
    CONSTRAINT attendance_course_id_fkey FOREIGN KEY (course_id)
        REFERENCES public.course (id) ON DELETE CASCADE,
    CONSTRAINT attendance_student_id_fkey FOREIGN KEY (student_id)
        REFERENCES public.users (id) ON DELETE CASCADE,
    CONSTRAINT attendance_subject_id_fkey FOREIGN KEY (subject_id)
        REFERENCES public.subject (id) ON UPDATE CASCADE
);

-- 6. Create Table: file
CREATE TABLE IF NOT EXISTS public.file
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    file_name character varying(255) NOT NULL,
    path text NOT NULL,
    course_id uuid,
    teacher_id uuid,
    upload_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT file_pkey PRIMARY KEY (id),
    CONSTRAINT file_course_id_fkey FOREIGN KEY (course_id)
        REFERENCES public.course (id) ON UPDATE CASCADE,
    CONSTRAINT file_teacher_id_fkey FOREIGN KEY (teacher_id)
        REFERENCES public.users (id) ON DELETE CASCADE
);

-- 7. Create Table: enrollment
CREATE TABLE IF NOT EXISTS public.enrollment
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    user_id uuid NOT NULL,
    course_id uuid NOT NULL,
    enrollment_date timestamp without time zone NOT NULL DEFAULT now(),
    CONSTRAINT enrollment_pkey PRIMARY KEY (id),
    CONSTRAINT enrollment_unique UNIQUE (user_id, course_id),
    CONSTRAINT fk_course FOREIGN KEY (course_id)
        REFERENCES public.course (id) ON DELETE CASCADE,
    CONSTRAINT fk_user FOREIGN KEY (user_id)
        REFERENCES public.users (id) ON DELETE CASCADE
);

-- 8. Create Table: test
CREATE TABLE IF NOT EXISTS public.test
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    course_id uuid NOT NULL,
    subject_id uuid NOT NULL,
    title character varying(255) NOT NULL,
    available_minutes integer NOT NULL,
    CONSTRAINT test_pkey PRIMARY KEY (id),
    CONSTRAINT test_course_id_fkey FOREIGN KEY (course_id)
        REFERENCES public.course (id),
    CONSTRAINT test_subject_id_fkey FOREIGN KEY (subject_id)
        REFERENCES public.subject (id)
);

-- 9. Create Table: question
CREATE TABLE IF NOT EXISTS public.question
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    test_id uuid NOT NULL,
    question_text text NOT NULL,
    points double precision NOT NULL,
    CONSTRAINT question_pkey PRIMARY KEY (id),
    CONSTRAINT question_test_id_fkey FOREIGN KEY (test_id)
        REFERENCES public.test (id) ON DELETE CASCADE
);

-- 10. Create Table: answer
CREATE TABLE IF NOT EXISTS public.answer
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    question_id uuid NOT NULL,
    answer_text text NOT NULL,
    is_correct boolean NOT NULL,
    CONSTRAINT answer_pkey PRIMARY KEY (id),
    CONSTRAINT answer_question_id_fkey FOREIGN KEY (question_id)
        REFERENCES public.question (id) ON DELETE CASCADE
);

-- 11. Create Table: subject_course
CREATE TABLE IF NOT EXISTS public.subject_course
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    subject_id uuid NOT NULL,
    course_id uuid NOT NULL,
    scheduled_day timestamp without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT subject_course_pkey PRIMARY KEY (id),
    CONSTRAINT subject_course_course_id_fkey FOREIGN KEY (course_id)
        REFERENCES public.course (id) ON DELETE CASCADE,
    CONSTRAINT subject_course_subject_id_fkey FOREIGN KEY (subject_id)
        REFERENCES public.subject (id) ON DELETE CASCADE
);

-- 12. Create Table: message
CREATE TABLE IF NOT EXISTS public.message
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    sender_id uuid NOT NULL,
    receiver_id uuid NOT NULL,
    message_subject character varying(255),
    text text NOT NULL,
    sent_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT message_pkey PRIMARY KEY (id),
    CONSTRAINT message_receiver_id_fkey FOREIGN KEY (receiver_id)
        REFERENCES public.users (id) ON DELETE CASCADE,
    CONSTRAINT message_sender_id_fkey FOREIGN KEY (sender_id)
        REFERENCES public.users (id) ON DELETE CASCADE
);

-- 13. Create Table: test_result
CREATE TABLE IF NOT EXISTS public.test_result
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    test_id uuid NOT NULL,
    student_id uuid NOT NULL,
    score double precision NOT NULL,
    test_length_in_seconds integer NOT NULL,
    title character varying(250) NOT NULL,
    course_id uuid NOT NULL,
    CONSTRAINT test_result_pkey PRIMARY KEY (id),
    CONSTRAINT test_result_course_id_fkey FOREIGN KEY (course_id)
        REFERENCES public.course (id) ON DELETE CASCADE,
    CONSTRAINT test_result_student_id_fkey FOREIGN KEY (student_id)
        REFERENCES public.users (id) ON DELETE SET NULL,
    CONSTRAINT test_result_test_id_fkey FOREIGN KEY (test_id)
        REFERENCES public.test (id) ON DELETE CASCADE
);

-- 14. Create Table: test_result_question
CREATE TABLE IF NOT EXISTS public.test_result_question
(
    id uuid NOT NULL DEFAULT uuid_generate_v4(),
    test_result_id uuid NOT NULL,
    question_id uuid NOT NULL,
    score double precision NOT NULL,
    CONSTRAINT test_result_question_pkey PRIMARY KEY (id),
    CONSTRAINT test_result_question_question_id_fkey FOREIGN KEY (question_id)
        REFERENCES public.question (id) ON DELETE CASCADE,
    CONSTRAINT test_result_question_test_result_id_fkey FOREIGN KEY (test_result_id)
        REFERENCES public.test_result (id) ON DELETE CASCADE
);

-- 15. Create Table: test_result_answers
CREATE TABLE IF NOT EXISTS public.test_result_answers
(
    id uuid NOT NULL,
    test_result_id uuid NOT NULL,
    question_id uuid NOT NULL,
    answer_id uuid NOT NULL,
    CONSTRAINT test_result_answers_pkey PRIMARY KEY (id),
    CONSTRAINT test_result_answers_answer_id_fkey FOREIGN KEY (answer_id)
        REFERENCES public.answer (id),
    CONSTRAINT test_result_answers_question_id_fkey FOREIGN KEY (question_id)
        REFERENCES public.question (id),
    CONSTRAINT test_result_answers_test_result_id_fkey FOREIGN KEY (test_result_id)
        REFERENCES public.test_result (id)
);
