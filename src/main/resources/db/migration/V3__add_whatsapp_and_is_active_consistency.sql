-- V3__add_whatsapp_and_is_active_consistency.sql
-- Synchronizing schema with entity updates

-- 1. Add whatsapp_group_url to hostels
ALTER TABLE hostels ADD COLUMN IF NOT EXISTS whatsapp_group_url VARCHAR(500);

-- 2. Ensure all tables have is_active for BaseEntity support (if missing)
-- (They were mostly there but good for consistency)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='users' AND column_name='is_active') THEN
        ALTER TABLE users ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT true;
    END IF;
END $$;
