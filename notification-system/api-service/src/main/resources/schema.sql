create table notifications(
	id serial primary key,
	channel varchar(20) not null,
	recipient varchar(255) not null,
	template int foreign key references templates(id),
	payload JSONB,
	status varchar(30) not null,
	retry_count int default 0,
	idempotency_key varchar(255),
	schedule_at TIMESTAMP,
	sent_at TIMESTAMP,
	delivered_at TIMESTAMP,
	failed_at TIMESTAMP,
	failure_reason text,
	created_at TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP not null default CURRENT_TIMESTAMP
);

CREATE TABLE notification_events (
    id serial PRIMARY KEY,
    notification_id int foreign key notifications(id),
    event_type VARCHAR(50) NOT NULL,
    payload JSONB,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE templates (
    id serial PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    channel VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);