CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


CREATE TABLE shared_album (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    album_id TEXT,
    album_enabled BOOLEAN,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE processed_url (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    album_id UUID NOT NULL REFERENCES shared_album(id),
    file_id TEXT,
    status TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

create TABLE processed_url_error_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    processed_url_id UUID NOT NULL REFERENCES processed_url(id),
    stack_trace TEXT,
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
)