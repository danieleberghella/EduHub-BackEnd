-- Insert courses
INSERT INTO course (id, name, description, total_hours) VALUES
('1e6247d1-4701-4f26-b318-381f51a05639', 'Cyber Security', 'Understand cybersecurity principles and counter cyber threats.', 1200),
('4c4b207a-aca3-4a54-98cd-c54ad995f708', 'Graphic & Multimedia Design', 'Learn graphic and multimedia design skills for professional applications.', 1200),
('624996dc-be63-4b24-87ea-f756fbb2bc58', 'System Engineering', 'Explore system architecture and engineering methodologies.', 1200),
('885ac7e4-003c-41d8-90ff-93f16b943bce', 'Interior Meta Design', 'Master interior design principles with a futuristic approach.', 1200),
('88f330bb-08fd-47cd-852d-ea39db6a6f50', 'Software Development', 'In-depth knowledge of software engineering and programming.', 1200),
('91137788-fe5f-4741-ba9c-6b0a5666a32d', 'Web Design & Development', 'Comprehensive training on web design and development technologies.', 1200);


-- Insert subjects
INSERT INTO subject (id, name, description) VALUES
('a5bcd9e7-c0f5-4328-93b9-4a2f5f9e1cdd', 'Node.js', 'Introduction to server-side JavaScript programming.'),
('b2d8b0f5-6de2-4f1b-9ad8-c3f4f4e4e90c', 'UX/UI Design', 'Learn principles of user experience and interface design.'),
('b3e61e4a-75d2-4562-acde-32dcc6da242e', 'English', 'English courses from A1 to B2'),
('b8c9a2b3-a9c1-6e4b-bccd-5b4f6a6d2a22', 'Cloud Computing', 'Introduction to cloud technologies and architectures.'),
('c8c1e5f7-39e5-4924-bb23-2d9a4f9c1f44', 'Python', 'Programming course for Python basics and advanced topics.'),
('d2e9f6b4-48d3-4299-99ab-8a9f3e7d3a44', 'Ethical Hacking', 'Introduction to penetration testing and ethical hacking.'),
('d5b6b2d3-79f4-4f2b-8cbb-2a1f5f3c1b11', 'System Architecture', 'Study principles of system and network architecture.'),
('d6e7a1e2-89d3-4c4b-9dab-3a2f6f4b2a00', 'Advanced Java', 'Explore advanced Java concepts and frameworks.'),
('e3f6d4b7-5be9-4624-bf99-7c2f9d3e2a33', 'Data Structures', 'Master data structures for efficient programming.'),
('e7d8b0c1-99e2-5d3b-abbc-4a3f5e5c1a11', 'Multimedia Production', 'Create and edit multimedia content effectively.'),
('f4f5c3e6-69d4-421b-aabb-1b9f4d2e3b22', 'Cyber Forensics', 'Learn techniques to investigate digital crimes.');


-- Insert users
INSERT INTO users (id, first_name, last_name, email, role, birthdate) VALUES
('11cdcb49-2720-445f-b775-7210a5e07b09', 'Will', 'Benson', 'will.benson@example.com', 'STUDENT', '2025-01-07 20:59:44.855164'),
('1cf73746-f15a-4d58-8049-a98244bd5ca2', 'Mike', 'Fraser', 'mike.fraser@example.com', 'STUDENT', '2025-01-07 20:31:12.49124'),
('6f030217-cdfc-4f19-9995-8f8b00655b2b', 'PROFILE', 'ADMIN', 'admin@example.com', 'ADMIN', '2024-12-29 20:04:30.753166'),
('73677dde-e268-41e8-aa94-782d2827528b', 'Jill', 'Plum', 'jill.plum@example.com', 'STUDENT', '2025-01-07 20:58:02.652822'),
('ae5870e6-2fc5-4cb6-94e5-c668208854ea', 'Jane', 'Smith', 'jane.smith@example.com', 'TEACHER', '2024-12-24 19:20:18.812092'),
('bdbede6d-6b0b-4755-ad73-65e07219ef34', 'Vinny', 'Felton', 'vinni.felton@example.com', 'STUDENT', '2025-01-07 21:01:47.274597'),
('cc5f4073-6fe7-4db0-8044-9a3be8cb37ca', 'Brody', 'Fox', 'brody.fox@example.com', 'STUDENT', '2025-01-06 13:00:26.211408');


-- Insert auth
INSERT INTO auth (id, user_id, email, password) VALUES
('4d6fdb07-73ab-4179-b908-38e122c2ffba', '6f030217-cdfc-4f19-9995-8f8b00655b2b', 'admin@example.com', '$2a$10$MLsQywJsN6UokGHWjb5KIeXOsmG6b6iruLB.wksCEfmuLpE0kVGUu'),
('51cc1819-6053-440d-a594-bbe9887c1ec6', '11cdcb49-2720-445f-b775-7210a5e07b09', 'will.benson@example.com', '$2a$10$744iZqu.yhGvjf6AY.RUAOC.ttdEjdRJbXzICbx9I58NrirxqFCWi'),
('6c6b83c4-7905-46e3-901d-41b4b9424764', 'ae5870e6-2fc5-4cb6-94e5-c668208854ea', 'jane.smith@example.com', '$2a$10$k7F5nn/k8xar33ZpdWzbYubfFRKn0cOW3PNicCcWAqxlowmgHbmRu'),
('75f842a2-bc67-4247-bdbc-e126aca762e4', '1cf73746-f15a-4d58-8049-a98244bd5ca2', 'mike.fraser@example.com', '$2a$10$744iZqu.yhGvjf6AY.RUAOC.ttdEjdRJbXzICbx9I58NrirxqFCWi'),
('cac0e6d3-7748-4217-acc7-cc5605bd0529', 'cc5f4073-6fe7-4db0-8044-9a3be8cb37ca', 'brody.fox@example.com', '$2a$10$uF80asFuFLdoujADFlLQQOXnuL8UQpvS09ZhKjBT.9GEsZOMjtJbS'),
('de007cf8-3209-4204-a533-fa8914e3e2f2', '73677dde-e268-41e8-aa94-782d2827528b', 'jill.plum@example.com', '$2a$10$744iZqu.yhGvjf6AY.RUAOC.ttdEjdRJbXzICbx9I58NrirxqFCWi'),
('e39c93a8-3cec-457b-8678-eb64c8e377f2', 'bdbede6d-6b0b-4755-ad73-65e07219ef34', 'vinni.felton@example.com', '$2a$10$744iZqu.yhGvjf6AY.RUAOC.ttdEjdRJbXzICbx9I58NrirxqFCWi');



-- Insert subject_course
INSERT INTO subject_course (id, subject_id, course_id, scheduled_day) VALUES
('1f47e6a0-848a-4c2b-a5f5-b6e01531bd65', 'b2d8b0f5-6de2-4f1b-9ad8-c3f4f4e4e90c', '885ac7e4-003c-41d8-90ff-93f16b943bce', '2024-03-08 00:00:00'),
('2f6ae038-1a4e-45f7-b42e-e5b21f7d2bdd', 'e7d8b0c1-99e2-5d3b-abbc-4a3f5e5c1a11', '885ac7e4-003c-41d8-90ff-93f16b943bce', '2024-03-01 00:00:00'),
('3c81e8d2-d1df-4866-ae02-535e22e68a60', 'f4f5c3e6-69d4-421b-aabb-1b9f4d2e3b22', '1e6247d1-4701-4f26-b318-381f51a05639', '2024-05-08 00:00:00'),
('40c58407-adfd-48ca-b74c-4cb1a95c9c4f', 'd2e9f6b4-48d3-4299-99ab-8a9f3e7d3a44', '1e6247d1-4701-4f26-b318-381f51a05639', '2024-05-01 00:00:00'),
('4a6a791b-9ea9-48ed-8ad6-020a6b3f8622', 'b3e61e4a-75d2-4562-acde-32dcc6da242e', '4c4b207a-aca3-4a54-98cd-c54ad995f708', '2025-01-03 23:18:07.380035'),
('69c4d05c-efdb-4f65-aee3-b7d151b375a4', 'a5bcd9e7-c0f5-4328-93b9-4a2f5f9e1cdd', '91137788-fe5f-4741-ba9c-6b0a5666a32d', '2025-01-03 22:43:05.506829'),
('861fcb01-9af7-4564-9a52-7727567f73c2', 'd5b6b2d3-79f4-4f2b-8cbb-2a1f5f3c1b11', '624996dc-be63-4b24-87ea-f756fbb2bc58', '2024-06-01 00:00:00'),
('888ee6f0-49e7-4833-9853-9e4e7e3d393c', 'b3e61e4a-75d2-4562-acde-32dcc6da242e', '91137788-fe5f-4741-ba9c-6b0a5666a32d', '2025-01-03 22:43:05.50187'),
('8bf3d80f-d9d3-4475-b524-f7233957ce1c', 'c8c1e5f7-39e5-4924-bb23-2d9a4f9c1f44', '88f330bb-08fd-47cd-852d-ea39db6a6f50', '2024-04-01 00:00:00'),
('a33436a7-c216-4e50-b8e6-c022f189be55', 'b2d8b0f5-6de2-4f1b-9ad8-c3f4f4e4e90c', '91137788-fe5f-4741-ba9c-6b0a5666a32d', '2025-01-03 22:43:05.507595'),
('abb2a2bb-b35d-4b49-9d61-57c38d19c867', 'c8c1e5f7-39e5-4924-bb23-2d9a4f9c1f44', '91137788-fe5f-4741-ba9c-6b0a5666a32d', '2025-01-04 17:43:02.767792'),
('ad41c502-098c-448d-a16f-640a533171f4', 'd6e7a1e2-89d3-4c4b-9dab-3a2f6f4b2a00', '88f330bb-08fd-47cd-852d-ea39db6a6f50', '2024-04-08 00:00:00'),
('baf2fd73-17ce-406d-a09d-ce25d7334bd6', 'b8c9a2b3-a9c1-6e4b-bccd-5b4f6a6d2a22', '624996dc-be63-4b24-87ea-f756fbb2bc58', '2024-06-08 00:00:00');
