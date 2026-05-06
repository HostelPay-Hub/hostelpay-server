-- V1__init_schema.sql
-- Complete schema for HostelPay Hub multi-tenant SaaS
-- Includes: users, hostels, rooms, students, lease_contracts, payments, audit_logs

-- Create ENUM types
CREATE TYPE user_role AS ENUM ('OWNER', 'SUPER_ADMIN');
CREATE TYPE payment_term AS ENUM ('ADVANCE', 'ARREARS');
CREATE TYPE payment_method AS ENUM ('CASH', 'UPI', 'BANK_TRANSFER');
CREATE TYPE payment_status AS ENUM ('COMPLETED', 'PENDING', 'REVERSED');
CREATE TYPE audit_action AS ENUM ('CREATE', 'UPDATE', 'DELETE');

-- ============================================================
-- 1. USERS TABLE (root entity — no hostel_id)
-- ============================================================
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(15) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role user_role NOT NULL DEFAULT 'OWNER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_email_not_empty CHECK (email <> '')
);

CREATE INDEX idx_users_email ON users(email);

-- ============================================================
-- 2. HOSTELS TABLE (multi-tenant root)
-- ============================================================
CREATE TABLE hostels (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    subscription_active BOOLEAN NOT NULL DEFAULT true,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_hostel_name_not_empty CHECK (name <> '')
);

CREATE INDEX idx_hostels_owner_id ON hostels(owner_id);
CREATE INDEX idx_hostels_is_active ON hostels(is_active);

-- ============================================================
-- 3. ROOMS TABLE (multi-tenant)
-- ============================================================
CREATE TABLE rooms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    hostel_id UUID NOT NULL REFERENCES hostels(id) ON DELETE CASCADE,
    room_number VARCHAR(50) NOT NULL,
    capacity INTEGER NOT NULL DEFAULT 1,
    default_price DECIMAL(10, 2) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(hostel_id, room_number),
    CONSTRAINT chk_capacity_positive CHECK (capacity > 0),
    CONSTRAINT chk_price_non_negative CHECK (default_price >= 0)
);

CREATE INDEX idx_rooms_hostel_id_active ON rooms(hostel_id, is_active);

-- ============================================================
-- 4. STUDENTS TABLE (multi-tenant)
-- ============================================================
CREATE TABLE students (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    hostel_id UUID NOT NULL REFERENCES hostels(id) ON DELETE CASCADE,
    full_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    dob DATE,
    aadhar_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_student_name_not_empty CHECK (full_name <> '')
);

CREATE INDEX idx_students_hostel_id_active ON students(hostel_id, is_active);
CREATE INDEX idx_students_phone ON students(phone_number);

-- ============================================================
-- 5. LEASE CONTRACTS TABLE (multi-tenant)
-- ============================================================
CREATE TABLE lease_contracts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    hostel_id UUID NOT NULL REFERENCES hostels(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    room_id UUID NOT NULL REFERENCES rooms(id) ON DELETE RESTRICT,
    start_date DATE NOT NULL,
    end_date DATE,
    agreed_monthly_rent DECIMAL(10, 2) NOT NULL,
    billing_anchor_date INTEGER NOT NULL,
    payment_term payment_term NOT NULL DEFAULT 'ARREARS',
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_end_after_start CHECK (end_date IS NULL OR end_date >= start_date),
    CONSTRAINT chk_rent_positive CHECK (agreed_monthly_rent > 0),
    CONSTRAINT chk_billing_anchor_valid CHECK (billing_anchor_date >= 1 AND billing_anchor_date <= 31)
);

CREATE INDEX idx_lease_hostel_active ON lease_contracts(hostel_id, is_active);
CREATE INDEX idx_lease_student ON lease_contracts(student_id);
CREATE INDEX idx_lease_room ON lease_contracts(room_id);

-- ============================================================
-- 6. PAYMENTS TABLE (multi-tenant financial ledger)
-- ============================================================
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    hostel_id UUID NOT NULL REFERENCES hostels(id) ON DELETE CASCADE,
    student_id UUID NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    amount DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method payment_method NOT NULL DEFAULT 'CASH',
    status payment_status NOT NULL DEFAULT 'COMPLETED',
    reference_notes VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_amount_positive CHECK (amount > 0)
);

CREATE INDEX idx_payments_hostel_active ON payments(hostel_id, is_active);
CREATE INDEX idx_payments_student ON payments(student_id);
CREATE INDEX idx_payments_date ON payments(payment_date);

-- ============================================================
-- 7. AUDIT LOGS TABLE (immutable — no soft delete)
-- ============================================================
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_name VARCHAR(100) NOT NULL,
    entity_id UUID NOT NULL,
    action audit_action NOT NULL,
    performed_by VARCHAR(255) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_entity ON audit_logs(entity_name, entity_id);
CREATE INDEX idx_audit_performed_by ON audit_logs(performed_by);
CREATE INDEX idx_audit_created_at ON audit_logs(created_at);
