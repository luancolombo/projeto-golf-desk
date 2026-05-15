-- Development-only seed user for local testing.
-- Email: manager@golfoffice.dev
-- Password: admin123
INSERT INTO app_user (
    name,
    email,
    password,
    role,
    active,
    created_at,
    updated_at
)
SELECT
    'Development Manager',
    'manager@golfoffice.dev',
    '{bcrypt}$2a$10$5RMJ4dPo3IMoBlMNlFJ9QejjL94.u9QxhPuVHQQgTfJxgUjcb3BXu',
    'MANAGER',
    TRUE,
    CURRENT_TIMESTAMP(6),
    CURRENT_TIMESTAMP(6)
WHERE NOT EXISTS (
    SELECT 1
    FROM app_user
    WHERE email = 'manager@golfoffice.dev'
);
