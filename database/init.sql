CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE ekyc_records (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id VARCHAR(100) NOT NULL,
    id_card_number_encrypted TEXT NOT NULL, 
    full_name VARCHAR(255) NOT NULL,
    dob DATE,
    gender VARCHAR(20),
    face_match_score NUMERIC(5,2), 
    liveness_status VARCHAR(50),   
    kyc_status VARCHAR(50) DEFAULT 'PENDING', 
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_customer_kyc ON ekyc_records(customer_id);