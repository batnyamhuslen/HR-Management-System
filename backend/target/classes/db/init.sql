-- =============================================
-- HR MANAGEMENT SYSTEM — Updated DDL Script
-- =============================================

-- \c hr_db дээр ажиллуулна

-- =============================================
-- 1. ENUM TYPES (аль хэдийн байвал алгасна)
-- =============================================

DO $$ BEGIN
    CREATE TYPE role_type AS ENUM ('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE leave_type AS ENUM ('ANNUAL', 'SICK', 'UNPAID');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

DO $$ BEGIN
    CREATE TYPE leave_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');
EXCEPTION WHEN duplicate_object THEN NULL; END $$;

-- =============================================
-- 2. DEPARTMENTS
-- =============================================

CREATE TABLE IF NOT EXISTS departments (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 3. POSITIONS
-- =============================================

CREATE TABLE IF NOT EXISTS positions (
    id            SERIAL PRIMARY KEY,
    title         VARCHAR(100) NOT NULL,
    department_id INT REFERENCES departments(id) ON DELETE SET NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 4. USERS
-- =============================================

CREATE TABLE IF NOT EXISTS users (
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       role_type    NOT NULL DEFAULT 'EMPLOYEE',
    enabled    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 5. EMPLOYEES
-- =============================================

CREATE TABLE IF NOT EXISTS employees (
    id              SERIAL PRIMARY KEY,
    employee_id     VARCHAR(20)  NOT NULL UNIQUE,
    full_name       VARCHAR(150) NOT NULL,
    age             INT          CHECK (age BETWEEN 16 AND 70),
    phone           VARCHAR(20),
    email           VARCHAR(100) NOT NULL UNIQUE,
    address         TEXT,
    hire_date       DATE         NOT NULL,
    department_id   INT          REFERENCES departments(id) ON DELETE SET NULL,
    position_id     INT          REFERENCES positions(id)   ON DELETE SET NULL,
    profile_picture VARCHAR(255),
    user_id         INT          REFERENCES users(id)       ON DELETE SET NULL,
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 6. EMPLOYEE DEPARTMENT HISTORY
-- =============================================

CREATE TABLE IF NOT EXISTS employee_department_history (
    id            SERIAL PRIMARY KEY,
    employee_id   INT  NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    department_id INT  REFERENCES departments(id) ON DELETE SET NULL,
    position_id   INT  REFERENCES positions(id)   ON DELETE SET NULL,
    start_date    DATE NOT NULL,
    end_date      DATE,
    note          TEXT
);

-- =============================================
-- 7. LEAVE BALANCES
-- =============================================

CREATE TABLE IF NOT EXISTS leave_balances (
    id            SERIAL PRIMARY KEY,
    employee_id   INT NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    year          INT NOT NULL,
    annual_total  INT NOT NULL DEFAULT 14,
    annual_used   INT NOT NULL DEFAULT 0,
    sick_total    INT NOT NULL DEFAULT 30,
    sick_used     INT NOT NULL DEFAULT 0,
    UNIQUE (employee_id, year)
);

-- =============================================
-- 8. LEAVES
-- =============================================

CREATE TABLE IF NOT EXISTS leaves (
    id           SERIAL PRIMARY KEY,
    employee_id  INT          NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    leave_type   leave_type   NOT NULL,
    start_date   DATE         NOT NULL,
    end_date     DATE         NOT NULL,
    days_count   INT          GENERATED ALWAYS AS (end_date - start_date + 1) STORED,
    reason       TEXT,
    status       leave_status NOT NULL DEFAULT 'PENDING',
    approved_by  INT          REFERENCES users(id) ON DELETE SET NULL,
    requested_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    note         TEXT,
    CONSTRAINT valid_dates CHECK (end_date >= start_date)
);

-- =============================================
-- 9. ATTENDANCE
-- =============================================

CREATE TABLE IF NOT EXISTS attendance (
    id             SERIAL PRIMARY KEY,
    employee_id    INT  NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    date           DATE NOT NULL,
    check_in       TIME,
    check_out      TIME,
    late_minutes   INT  NOT NULL DEFAULT 0,
    worked_hours   NUMERIC(5,2),
    overtime_hours NUMERIC(5,2) DEFAULT 0,
    note           TEXT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (employee_id, date)
);

-- =============================================
-- 10. TRIGGERS
-- =============================================

CREATE OR REPLACE FUNCTION calc_attendance()
RETURNS TRIGGER AS $$
DECLARE
    work_start   TIME    := '09:00:00';
    standard_hrs NUMERIC := 8.0;
    worked       NUMERIC;
BEGIN
    IF NEW.check_in IS NOT NULL AND NEW.check_out IS NOT NULL THEN
        worked := EXTRACT(EPOCH FROM (NEW.check_out - NEW.check_in)) / 3600.0;
        NEW.worked_hours   := ROUND(worked::NUMERIC, 2);
        NEW.overtime_hours := ROUND(GREATEST(worked - standard_hrs, 0)::NUMERIC, 2);
    END IF;
    IF NEW.check_in IS NOT NULL AND NEW.check_in > work_start THEN
        NEW.late_minutes := EXTRACT(EPOCH FROM (NEW.check_in - work_start)) / 60;
    ELSE
        NEW.late_minutes := 0;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_calc_attendance ON attendance;
CREATE TRIGGER trg_calc_attendance
BEFORE INSERT OR UPDATE ON attendance
FOR EACH ROW EXECUTE FUNCTION calc_attendance();

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at := CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_users_updated_at ON users;
CREATE TRIGGER trg_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

DROP TRIGGER IF EXISTS trg_employees_updated_at ON employees;
CREATE TRIGGER trg_employees_updated_at
BEFORE UPDATE ON employees
FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE OR REPLACE FUNCTION update_leave_balance()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.status = 'APPROVED' AND OLD.status != 'APPROVED' THEN
        IF NEW.leave_type = 'ANNUAL' THEN
            UPDATE leave_balances
            SET annual_used = annual_used + (NEW.end_date - NEW.start_date + 1)
            WHERE employee_id = NEW.employee_id
              AND year = EXTRACT(YEAR FROM NEW.start_date);
        ELSIF NEW.leave_type = 'SICK' THEN
            UPDATE leave_balances
            SET sick_used = sick_used + (NEW.end_date - NEW.start_date + 1)
            WHERE employee_id = NEW.employee_id
              AND year = EXTRACT(YEAR FROM NEW.start_date);
        END IF;
    END IF;
    IF NEW.status = 'REJECTED' AND OLD.status = 'APPROVED' THEN
        IF NEW.leave_type = 'ANNUAL' THEN
            UPDATE leave_balances
            SET annual_used = annual_used - (NEW.end_date - NEW.start_date + 1)
            WHERE employee_id = NEW.employee_id
              AND year = EXTRACT(YEAR FROM NEW.start_date);
        ELSIF NEW.leave_type = 'SICK' THEN
            UPDATE leave_balances
            SET sick_used = sick_used - (NEW.end_date - NEW.start_date + 1)
            WHERE employee_id = NEW.employee_id
              AND year = EXTRACT(YEAR FROM NEW.start_date);
        END IF;
    END IF;
    NEW.processed_at := CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_leave_balance ON leaves;
CREATE TRIGGER trg_leave_balance
BEFORE UPDATE ON leaves
FOR EACH ROW EXECUTE FUNCTION update_leave_balance();

-- =============================================
-- 11. INDEXES
-- =============================================

CREATE INDEX IF NOT EXISTS idx_employees_email      ON employees(email);
CREATE INDEX IF NOT EXISTS idx_employees_department ON employees(department_id);
CREATE INDEX IF NOT EXISTS idx_attendance_emp_date  ON attendance(employee_id, date);
CREATE INDEX IF NOT EXISTS idx_leaves_employee      ON leaves(employee_id);
CREATE INDEX IF NOT EXISTS idx_leaves_status        ON leaves(status);

-- =============================================
-- 12. SAMPLE DATA
-- =============================================

-- Хэлтэс
INSERT INTO departments (name, description) VALUES
('Технологи',   'Програм хангамж, дэд бүтэц'),
('Хүний нөөц',  'Ажилтны удирдлага'),
('Санхүү',      'Нягтлан бодох бүртгэл'),
('Борлуулалт',  'Борлуулалт, маркетинг')
ON CONFLICT (name) DO NOTHING;

-- Албан тушаал
INSERT INTO positions (title, department_id) VALUES
('Ахлах хөгжүүлэгч',     1),
('Хөгжүүлэгч',           1),
('HR менежер',            2),
('HR мэргэжилтэн',        2),
('Санхүүч',               3),
('Борлуулалтын менежер',  4)
ON CONFLICT DO NOTHING;

-- =============================================
-- 13. USERS
-- Нууц үг: Admin@123
-- bcrypt hash (strength 10):
-- $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- =============================================

INSERT INTO users (username, password, role, enabled) VALUES
('admin',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN',    TRUE),
('hr_mgr',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'HR',       TRUE),
('manager1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MANAGER',  TRUE),
('emp001',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'EMPLOYEE', TRUE)
ON CONFLICT (username) DO UPDATE
    SET password = EXCLUDED.password,
        role     = EXCLUDED.role,
        enabled  = EXCLUDED.enabled;

-- =============================================
-- 14. EMPLOYEES
-- =============================================

INSERT INTO employees
    (employee_id, full_name, age, phone, email, address, hire_date, department_id, position_id, user_id, is_active)
VALUES
('EMP001', 'Батболд Дорж',    32, '99001122', 'batbold@company.mn',     'УБ, Сүхбаатар дүүрэг',  '2020-03-01', 1, 1, 4,    TRUE),
('EMP002', 'Энхзул Гантулга', 28, '99223344', 'enkhzul@company.mn',     'УБ, Баянзүрх дүүрэг',   '2021-06-15', 1, 2, NULL, TRUE),
('EMP003', 'Оюунцэцэг Нямаа', 35, '99445566', 'oyuntsetseg@company.mn', 'УБ, Чингэлтэй дүүрэг',  '2019-01-10', 2, 3, 2,    TRUE),
('EMP004', 'Admin Хэрэглэгч', 30, '99000000', 'admin@company.mn',       'УБ, Хан-Уул дүүрэг',    '2018-01-01', 1, 1, 1,    TRUE),
('EMP005', 'Менежер Нэгдүгээр',38, '99001234', 'manager1@company.mn',   'УБ, Баянгол дүүрэг',    '2019-05-01', 1, 1, 3,    TRUE)
ON CONFLICT (employee_id) DO NOTHING;

-- =============================================
-- 15. LEAVE BALANCES (2025, 2026 он)
-- =============================================

INSERT INTO leave_balances (employee_id, year, annual_total, annual_used, sick_total, sick_used)
SELECT e.id, y.year, 14, 0, 30, 0
FROM employees e
CROSS JOIN (VALUES (2025), (2026)) AS y(year)
ON CONFLICT (employee_id, year) DO NOTHING;

-- Зарим ажилтанд ашигласан амралт нэмэх
UPDATE leave_balances SET annual_used = 3 WHERE employee_id = 1 AND year = 2026;
UPDATE leave_balances SET sick_used   = 2 WHERE employee_id = 2 AND year = 2026;
UPDATE leave_balances SET annual_used = 7 WHERE employee_id = 3 AND year = 2026;

-- =============================================
-- 16. ATTENDANCE SAMPLES
-- =============================================

INSERT INTO attendance (employee_id, date, check_in, check_out)
VALUES
(1, CURRENT_DATE - 4, '09:05', '18:10'),
(1, CURRENT_DATE - 3, '08:55', '18:00'),
(1, CURRENT_DATE - 2, '09:15', '18:00'),
(1, CURRENT_DATE - 1, '09:00', '17:55'),
(2, CURRENT_DATE - 4, '09:20', '18:00'),
(2, CURRENT_DATE - 3, '09:00', '17:45'),
(2, CURRENT_DATE - 2, '08:50', '18:10'),
(2, CURRENT_DATE - 1, '09:30', '18:00'),
(3, CURRENT_DATE - 3, '09:00', '18:00'),
(3, CURRENT_DATE - 2, '09:10', '17:50'),
(3, CURRENT_DATE - 1, '09:00', '18:00')
ON CONFLICT (employee_id, date) DO NOTHING;

-- =============================================
-- 17. LEAVE SAMPLES
-- =============================================

INSERT INTO leaves (employee_id, leave_type, start_date, end_date, reason, status)
VALUES
(2, 'ANNUAL', '2026-07-01', '2026-07-05', 'Жилийн ээлжийн амралт', 'PENDING'),
(1, 'SICK',   '2026-06-10', '2026-06-11', 'Цасан ханиад',           'APPROVED'),
(3, 'ANNUAL', '2026-08-01', '2026-08-07', 'Гэр бүлийн амралт',      'APPROVED')
ON CONFLICT DO NOTHING;

-- =============================================
-- 18. VIEWS
-- =============================================

CREATE OR REPLACE VIEW vw_employee_detail AS
SELECT
    e.id,
    e.employee_id,
    e.full_name,
    e.age,
    e.phone,
    e.email,
    e.hire_date,
    d.name  AS department,
    p.title AS position,
    u.role,
    u.username,
    e.is_active
FROM employees e
LEFT JOIN departments d ON e.department_id = d.id
LEFT JOIN positions   p ON e.position_id   = p.id
LEFT JOIN users       u ON e.user_id       = u.id;

CREATE OR REPLACE VIEW vw_monthly_attendance AS
SELECT
    e.employee_id,
    e.full_name,
    DATE_TRUNC('month', a.date)::DATE          AS month,
    COUNT(*)                                    AS total_days,
    SUM(a.late_minutes)                         AS total_late_min,
    ROUND(SUM(a.worked_hours)::NUMERIC, 2)      AS total_worked_hrs,
    ROUND(SUM(a.overtime_hours)::NUMERIC, 2)    AS total_overtime_hrs
FROM attendance a
JOIN employees e ON a.employee_id = e.id
GROUP BY e.employee_id, e.full_name, DATE_TRUNC('month', a.date);

CREATE OR REPLACE VIEW vw_leave_balance AS
SELECT
    e.employee_id,
    e.full_name,
    lb.year,
    lb.annual_total,
    lb.annual_used,
    (lb.annual_total - lb.annual_used) AS annual_remaining,
    lb.sick_total,
    lb.sick_used,
    (lb.sick_total  - lb.sick_used)    AS sick_remaining
FROM leave_balances lb
JOIN employees e ON lb.employee_id = e.id;

-- =============================================
-- ШАЛГАХ QUERY-УД
-- =============================================
-- SELECT * FROM vw_employee_detail;
-- SELECT * FROM vw_leave_balance WHERE year = 2026;
-- SELECT username, role, enabled FROM users;
