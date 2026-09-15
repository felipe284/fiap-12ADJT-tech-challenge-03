CREATE DATABASE IF NOT EXISTS healthcare_db;

USE healthcare_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    full_name VARCHAR(255),
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    doctor_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    appointment_date DATETIME NOT NULL,
    status VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES users(id),
    FOREIGN KEY (patient_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS medical_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    appointment_id BIGINT NOT NULL,
    diagnosis TEXT,
    treatment TEXT,
    medications TEXT,
    observations TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    appointment_id BIGINT NOT NULL,
    message TEXT,
    status VARCHAR(50),
    sent_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(id)
);

INSERT INTO users (username, password, email, full_name, role)
VALUES 
    ('doctor1', '$2b$10$Gjb2y5DHkWg4hVXa08kGT.e.4Y2/pUzMNDdLXJQmbwHPMokMS.lgi', 'doctor1@hospital.com', 'Dr. João Silva', 'DOCTOR'),
    ('nurse1', '$2b$10$Gjb2y5DHkWg4hVXa08kGT.e.4Y2/pUzMNDdLXJQmbwHPMokMS.lgi', 'nurse1@hospital.com', 'Enf. Maria Santos', 'NURSE'),
    ('patient1', '$2b$10$Gjb2y5DHkWg4hVXa08kGT.e.4Y2/pUzMNDdLXJQmbwHPMokMS.lgi', 'patient1@hospital.com', 'Pedro Costa', 'PATIENT');

INSERT INTO appointments (doctor_id, patient_id, appointment_date, status, notes)
VALUES 
    (1, 3, '2024-12-20 10:00:00', 'PENDING', 'Consulta de rotina'),
    (1, 3, '2024-12-21 14:30:00', 'CONFIRMED', 'Acompanhamento pós-consulta'),
    (2, 3, '2024-12-22 09:00:00', 'PENDING', 'Avaliação da enfermagem');

INSERT INTO medical_history (patient_id, appointment_id, diagnosis, treatment, medications, observations)
VALUES 
    (3, 1, 'Hipertensão leve', 'Repouso e alimentação balanceada', 'Losartana 50mg', 'Paciente apresenta histórico de hipertensão familiar'),
    (3, 2, 'Acompanhamento hipertensão', 'Continuar com dieta e exercício', 'Losartana 50mg', 'Pressão arterial controlada após primeira consulta');
