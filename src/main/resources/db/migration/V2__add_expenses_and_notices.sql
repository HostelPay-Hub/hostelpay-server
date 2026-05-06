CREATE TABLE expenses (
    id UUID PRIMARY KEY,
    hostel_id UUID NOT NULL REFERENCES hostels(id) ON DELETE CASCADE,
    category VARCHAR(50) NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    description TEXT,
    expense_date DATE NOT NULL,
    receipt_url VARCHAR(255),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_expenses_hostel_id ON expenses(hostel_id);
CREATE INDEX idx_expenses_date ON expenses(expense_date);

CREATE TABLE notices (
    id UUID PRIMARY KEY,
    hostel_id UUID NOT NULL REFERENCES hostels(id) ON DELETE CASCADE,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    priority VARCHAR(20) DEFAULT 'NORMAL',
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notices_hostel_id ON notices(hostel_id);
